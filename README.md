# PlantAppMVI — HUBX Case, native Android

A Kotlin/Compose implementation of the HUBX developer case: an **onboarding
flow** that ends at a paywall, and a **home flow** driven by two live
endpoints. Multi-module Clean Architecture, a Props/`mapStateToProps` flavour
of MVI, Hilt, Coroutines, Retrofit and DataStore.

The brief is written for Flutter, and there is a complete Flutter build of the
same case at [`hubx-flutter-case`](https://github.com/fatihbcan/PlantAppFlutter).
This repository answers the brief natively; [`docs/FLUTTER_TO_ANDROID.md`](docs/FLUTTER_TO_ANDROID.md)
maps between the two and is the most interesting document here.

## Running it

```bash
./gradlew assembleDebug
```

Open in Android Studio and run, or `./gradlew installDebug` with a device
attached. No codegen step to remember — KSP runs as part of the build.

```bash
./gradlew runLocalTests   # 119 unit tests
./gradlew detekt          # clean
./gradlew lintDebug       # clean, warnings as errors
```

## What the app does

**Onboarding** — three intro pages, then the paywall. Tapping the paywall's
close button is what ends onboarding, per the brief: the completion flag is
persisted and the composition root picks the start destination from it, so a
user who completes the flow never sees it again — on this launch or any later
one. The splash screen is held until that flag has been read, so onboarding
never flashes up for a frame in front of someone who already finished it.

**Home** — a header band carrying the greeting, the search field and the two
painted leaves tucked behind it, a premium strip, a horizontal carousel from
`getQuestions`, a categories grid from `getCategories`, and the design's bottom
bar. Pull to refresh; search filters the grid live.

## Layout

```
app/                       manifest, Application, one Activity — nearly empty
framework/app/             composition root: routes, NavHost, navigators, the onboarding gate
core/
  util/                    pure Kotlin — dispatcher qualifiers, Logger
  presentation/            MVI base, TextResource, IconResource, preview annotations
  designsystem/            theme + tokens, shared components, the design's icons and Rubik
  build-config/            build constants
platform-apis/
  network/                 Retrofit, OkHttp, the error-translating interceptor
  datastore/               Preferences DataStore behind a narrow interface
domain/<feature>/          pure Kotlin — entities, use cases, repository interfaces, results
data/<feature>/            repository impls, data sources, DTOs, mappers
presentation/<feature>/    Compose screens + MVI
```

`domain` modules are `java-library`, not Android libraries, so `import
android.*` there is a compile error rather than a review comment. Features
never depend on features; anything crossing that line is wired in
`framework/app`.

`onboarding` covers the intro pages **and** the paywall. They are one flow, not
two features — the paywall completes onboarding and needs its use cases, so
splitting them would force one feature to import another's domain, which is the
signal that the boundary was drawn at a screen instead of a functional
requirement.

## Deliberate trade-offs

**Per-operation sealed result types, not `Either<Failure, T>` or `Result<T>`.**
`GetCategoriesResult` has `Success` plus a nested `Error` hierarchy of
`Network`, `Server(statusCode)`, `Parse` and `Unknown`. Callers use an
exhaustive `when` with no `else`, so adding a failure mode later is a compile
error at every call site rather than a silent fall-through.

**Flat state fields, not a `Loading | Success | Error` union.** `HomeScreenState`
carries `isLoading`, `questions`, `categories` and two failure fields at once. A
sealed UI-state union cannot express "showing cached categories while
refreshing, with the articles endpoint down" — which is exactly what home does.

**Two failure fields, not one.** The two endpoints fail independently. A dead
`getCategories` still leaves the articles carousel usable, and each section
offers its own retry.

**Every I/O entry point states its concurrency.** Bloc has
`transformer: droppable()`; coroutines have no such declaration, so the guards
are explicit — a job check for refresh and submit, `debounce` +
`collectLatest` for search, `launchNavigationOnce` for navigation that must
first await a write. The trade is that intent is three lines instead of one
word; what it buys is real cancellation, which a Dart `Future` cannot offer.

**Presentation logic lives in `mapStateToProps`, not in composables.** Each
screen has an `@Immutable` Props class and a pure, non-composable function
building it. `Content` takes Props only — never state, never the ViewModel — so
the whole presentation layer is unit-tested on the JVM and previews come free
from `Props.preview()`.

**Navigation is an injected interface, not a `NavController`.** Features declare
where they can go; the composition root decides how. This is also what removes
the `isFinished` / `shouldExit` flag pairs the Flutter build needs to signal
navigation through state.

**Failing open on persistence.** If the onboarding flag cannot be read, the gate
shows onboarding; if it cannot be written, the paywall still lets the user
through and records the failure. Repeating onboarding is a much milder failure
than locking someone out of the app.

**No `JsonDecodeInterceptor`.** Both endpoints return JSON under
`content-type: text/plain`. The Flutter build needs a whole interceptor for
this because Dio dispatches its decoder on that header; Retrofit picks a
converter by the declared return type and parses it regardless. The server
quirk is real, the workaround is not needed, and `NetworkModule` says so — the
instinct is to port it anyway.

**No hardcoded colours, spacing or strings.** Colours, spacing, type and shapes
come from `AppTheme`; user-facing text comes from `TextResource`. `AppTheme`
swaps to a tighter dimension scale below a 700dp viewport, so onboarding still
fits on a small phone without any screen special-casing itself.

**Artwork composed in fractions, not fixed offsets.** The pieces of each
illustration — the welcome badges, the phone mockups, the header leaves — are
placed and sized as fractions of the box they are given, and clipped where the
design cuts them off. A design measured at 360×800 then holds its proportions
on a 412dp phone or a tablet.

## Design assets

The icons and artwork are the design file's own exports rather than lookalikes,
which is what finally made the bottom bar and the paywall's feature strip read
correctly — two rounds of hand-drawn icons did not. Type is **Rubik**, bundled
in `core:designsystem` so it renders as the frames do instead of falling back to
Roboto.

They ship at 1x for the file's 360dp frame, so they live in `drawable-mdpi` and
Android scales them per density — exactly what the Flutter build does with the
same files. They soften on a dense screen; a 3x re-export is a straight file
swap into `drawable-xxhdpi` with no code change.

The viewfinder over the welcome plant and the phone's camera preview is drawn
rather than exported: the design stretches the same mark to a different aspect
on each screen, which a bitmap cannot follow without distorting its own stroke
weight.

## Testing

119 unit tests: use cases (one per result branch), DTO→entity mappers (null
collapsing, rank/order sorting), repository implementations (one per failure
translation), event reducers, state getters and `mapStateToProps`.

The parallel-fetch test uses deliberately slow stubs and asserts the elapsed
virtual time — an instantly-completing mock never overlaps with anything, so a
sequential implementation would pass it for the wrong reason.

## Known gaps

**Screenshot tests.** Golden tests are the Flutter build's genuine advantage and
it has them; there is no equivalent here yet. Paparazzi over each screen's
`Props.preview()` in both schemes is the natural next step, and cheap, because
the Props already exist.

**Bottom bar.** The design's five destinations are all present, but only Home
has a screen in this case. The other four render unselected rather than posing
as buttons that silently do nothing; the raised scan control is the bar's one
live affordance.

**Asset density.** As above — 1x only, by way of the design file's export.

## Documentation

| File | What it is for |
|---|---|
| [`AGENT.md`](AGENT.md) | The rules. What to do, and what never to do. |
| [`MEMORY.md`](MEMORY.md) | The reasoning, the recorded deviations and the debt. |
| [`docs/CASE.md`](docs/CASE.md) | The brief and its evaluation criteria. |
| [`docs/FLUTTER_TO_ANDROID.md`](docs/FLUTTER_TO_ANDROID.md) | The two implementations, side by side. |
