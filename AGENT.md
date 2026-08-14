# AGENT.md

> Instructions for AI coding agents working in this repository.

---

## What this project is

**PlantAppMVI** — a Kotlin Android app: **Jetpack Compose, Clean Architecture, multi-module, MVI, Hilt, Coroutines**.

Namespace `com.plantappmvi.android` · minSdk 26 · targetSdk 35 · compileSdk 35 · JDK 17

It is the HUBX developer case, built natively. There is a Flutter implementation of the same brief at `../hubx-flutter-case`; `docs/FLUTTER_TO_ANDROID.md` maps between them and `docs/CASE.md` is the source of truth for scope.

Two flows: **Intro → Paywall → Home**. Onboarding is entered once; completing it is persisted and gated.

**Compose-only. Never write Java, XML layouts, Fragments, MVP, RxJava, or LiveData.**

## Commands

```bash
./gradlew assembleDebug                # build
./gradlew runLocalTests                # all JVM unit tests
./gradlew runDomainTests               # domain only — fastest gate
./gradlew :presentation:home:testDebugUnitTest
./gradlew detekt                       # static analysis — must be clean
./gradlew lintDebug                    # lint — must be clean
```

Run `detekt` + `lintDebug` + the relevant tests before considering any change done.

## Module layout

```
app/                       Manifest, Application, single Activity. Keep nearly empty.
framework/app/             COMPOSITION ROOT — routes, NavHost, NavigatorImpls, start-destination gate
core/util                  Pure Kotlin — dispatcher qualifiers, Logger interface
core/presentation          MVI base, TextResource, IconResource, effects, preview annotations
core/designsystem          Theme + tokens, all shared components, shared icons and fonts
core/build-config          Build constants
platform-apis/network      Retrofit, OkHttp, the error-translating interceptor, AppException
platform-apis/datastore    Preferences DataStore behind a KeyValueStore interface
domain/<feature>           PURE KOTLIN — entities, use cases, repository interfaces, result types
data/<feature>             Repository impls, data sources, DTOs, mappers
presentation/<feature>     Compose screens + MVI
```

`onboarding` covers intro **and** paywall. They are one flow, not two features — the paywall calls `CompleteOnboardingUseCase`, so splitting them would force one feature to import another's domain.

## Dependency rules — never violate

1. `domain` has **zero** Android imports. Enforced by the `plantappmvi.kotlin.library` plugin (`java-library`), so a violation is a compile error.
2. `presentation → domain ← data`. **`presentation` must never depend on `data`.**
3. Feature modules **never** depend on sibling feature modules.
4. Repository interfaces in `domain`; implementations in `data`.
5. All `Navigator` implementations and all route constants live in `framework/app`.
6. `core:designsystem` must never depend on `domain`.

**One documented deviation:** each `data` module binds its own repository in its own Hilt module rather than in the composition root. See `MEMORY.md` for why — it is what lets every implementation stay `internal`.

## MVI — how every screen works

State → Event(reduce) → StateStore → ViewModel → **mapStateToProps** → Props → Content

- **State** — flat immutable `data class : ScreenState` + `companion object { fun initial() }`. Derived data is a **getter on the state**, never a computation in a composable.
- **Event** — `sealed interface : ScreenEvent<S>`; **each case implements its own pure `reduce(oldState): S`**. No central reducer.
- Event names are **past-tense facts** (`PlansLoaded`, `SearchQueryApplied`), never commands.
- **StateStore** — `<Screen>ScreenStateStore @Inject constructor() : DefaultStateStore<S, E>(initial())`.
- **ViewModel** — `@HiltViewModel`, extends `BasicViewModel<S, E>`, `override val navigator`. Translates UI intent → use case → event. No logic of its own.
- **mapStateToProps** — a pure, non-`@Composable` function merging State + callbacks into `@Immutable` Props. **All presentation logic lives here.**
- **Content** — receives **Props only**. Never State, never the ViewModel.

**There is no Effect / one-shot-event channel.** Navigation goes through the injected `Navigator`. Dialogs and messages are nullable fields in State with an explicit dismiss event.

### Concurrency

Every I/O entry point must state how it handles a second call arriving while the first is in flight. There is no transformer parameter to declare it, so it is explicit:

| Intent | Pattern | Live example |
|---|---|---|
| Refresh / retry / submit | `if (job?.isActive == true) return` | `HomeViewModel.load`, `PaywallViewModel.load` |
| Search / filter text | `MutableStateFlow` + `debounce` + `collectLatest` | `HomeViewModel.queryInput` |
| Navigate after async work | `launchNavigationOnce { … ; true }` | `PaywallViewModel.finishOnboarding` |
| Parallel fetch | `coroutineScope { async { } }` **in the use case** | `GetHomeContentUseCase` |

Launching in `viewModelScope` without one of these is a bug, not a style choice.

### Adding a screen — all 9 steps

1. `<Screen>State` + `initial()` + getters for anything derived
2. `<Screen>Event` — past-tense cases, each with `reduce`
3. `<Screen>StateStore`
4. `<Screen>Navigator` interface (in the feature)
5. `<Screen>ViewModel`
6. `<Screen>Props` + `preview()` + `mapStateToProps`
7. `<Screen>Route` (public) / `Screen` (internal) / `Content` (private) + `@DayNightPreviews`
8. **In `framework/app`:** `NavigatorImpl`, `@Binds`, route constant, `NavHost` entry
9. Tests for the reducers, the state getters and `mapStateToProps`

**Step 8 is the one that gets forgotten. Skipping it compiles fine and crashes at runtime.**

## Domain rules

- One use case = one operation: `suspend operator fun invoke()`, `@Inject constructor`.
- Errors are **sealed result types per operation**, with a nested `sealed interface Error`. Never a generic `Result<T>`, never `Either`, never a thrown exception crossing out of `data`.
- Consume with an exhaustive `when` and **no `else` branch**, so a new failure mode is a compile error at every call site.
- Entities are plain immutable `data class`es — no `@Serializable`, no `@Entity`.
- Never hardcode `Dispatchers.X`; inject via the qualifiers in `core:util`.

## Data rules

- DTOs never leave the data layer. Mappers convert at the repository boundary and are tested.
- Nullable wire fields collapse to safe defaults in the mapper, so nothing downstream reasons about nulls from the API.
- Repository impls are thin: call the source, map, translate exceptions into result cases.
- Status-code validation and error translation live in `ErrorInterceptor`, which throws a typed `AppException`. The repository is the only thing that catches it.

## Compose rules

- `Route` (public) → `Screen` (internal, wiring) → `Content` (private, pure).
- Wrap content in `BasicScreen(viewModel) { … }`.
- Collect with `collectAsStateWithLifecycle()`. Never bare `collectAsState()`.
- Component signature: `fun X(props: XProps, modifier: Modifier = Modifier)` — props first, modifier second and defaulted.
- Props are `@Immutable` and use `ImmutableList`, never `List`.
- All text via `TextResource`. **No hardcoded strings.**
- All styling via `AppTheme.colors / typography / dimens / shapes`. **No literal colours, no magic dimensions.**
- Artwork is composed in **fractions of the box it is given**, never fixed offsets — that is what holds the design's proportions from a small phone to a tablet.
- Every component and screen gets a `@DayNightPreviews` preview driven by `Props.preview()`.

## Reusable components

> Generic, or needed by a second feature, or a design-system decision → `core:designsystem`. Otherwise it stays in the feature.

- **Never** define a button, dialog, text field, or loader inside a feature module.
- A shared component must not take a domain type. Give it Props of primitives / `TextResource` / `IconResource`, and let the feature own the `Domain → Props` mapper.
- Don't extract on speculation. A **second real caller** is the trigger.

## Testing

**Mandatory:** domain use cases (one test per result branch), data mappers, repository implementations.
**Also expected, and cheap:** event reducers and `mapStateToProps` — both are pure functions needing no Compose runtime.

Stack: JUnit 4 + MockK + Kotest assertions + `kotlinx-coroutines-test`. Descriptive backtick names, no network, no disk, no real time.

**Use slow stubs where concurrency is the thing under test.** An instantly-completing mock never overlaps with anything, so a job guard has nothing to guard and the test passes for the wrong reason.

## Common mistakes — do not make these

| ❌ | ✅ |
|---|---|
| `presentation` depending on `data` | Depend on `domain` use cases |
| Feature A importing feature B | Route via `domain` or the composition root |
| Public `MutableStateFlow` | Expose immutable `StateFlow` |
| `NavController` / `Context` in a ViewModel | Injected `Navigator` interface |
| Passing State or the ViewModel into `Content` | Pass **Props** |
| Sealed `UiState.Loading/Success/Error` | Independent `isLoading` / `error` / `items` fields |
| Side effects inside `reduce` | Keep reducers pure; act in the ViewModel |
| Command-style event names (`LoadPlans`) | Past-tense facts (`PlansLoaded`) |
| Generic `Result<T>` or `Either` | Per-operation sealed result |
| `List<T>` in Props | `ImmutableList<T>` |
| Hardcoded string / colour / dimension | `TextResource` / `AppTheme.*` |
| Launching I/O with no concurrency guard | Job guard, `collectLatest`, or `launchNavigationOnce` |
| Adding a screen without wiring `framework/app` | Complete all 9 steps |
| `collectAsState()` | `collectAsStateWithLifecycle()` |
| kapt | KSP |
| Hardcoded dependency version | The version catalog |

## Never introduce

Java · XML layouts · Fragments as screens · MVP/MVC · RxJava · LiveData · ViewBinding/DataBinding · mutable singleton state · one Activity per screen · kapt · `GlobalScope` · `TODO`/`FIXME` in merged work.

## Conventions

- Branches `feature/<short-description>`; commits are imperative and grouped by functionality.
- Prefer `internal`. A feature's public surface is its use cases, its `Route`, and its `Navigator`.
- The version catalog is a compatibility-verified set — Kotlin↔KSP, AGP↔Gradle and Hilt↔KSP are coupled. Do not bump one in isolation.

## Reference material

- Scope, evaluation criteria, Figma link and API endpoints: `docs/CASE.md`
- Why each rule looks the way it does, with the Flutter equivalent: `docs/FLUTTER_TO_ANDROID.md`
- Architectural reasoning and recorded deviations: `MEMORY.md`
