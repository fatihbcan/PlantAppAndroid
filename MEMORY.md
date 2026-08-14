# MEMORY.md

> Persistent architectural knowledge for sessions working on **PlantAppMVI**.
> `AGENT.md` says **what to do**. This file explains **how the system is built and why** — read it when a decision needs context rather than a rule.

---

## Project identity

| | |
|---|---|
| Name | PlantAppMVI |
| Package | `com.plantappmvi.android` |
| Domain | The HUBX developer case: a plant-identification app with an onboarding flow ending at a paywall, and a home screen driven by two live endpoints |
| Started | 2026-08-14 |
| Stack | Kotlin 2.0.21, Compose BOM 2024.10.01, Hilt 2.52, Coroutines 1.9.0, AGP 8.7.3, Gradle 8.10.2, JDK 17 |
| SDK | min 26 · target 35 · compile 35 |
| API | `https://dummy-api-jtg6bessta-ey.a.run.app/` — `getCategories`, `getQuestions` |

## Architecture in one picture

```
                    app  (manifest, Application, one Activity)
                     │
              framework/app  ── COMPOSITION ROOT
              │  route constants + NavHost
              │  every NavigatorImpl
              │  the onboarding start-destination gate
              ├──────────────┬──────────────┐
              ▼              ▼              ▼
       presentation/*     data/*        core/*
       Compose + MVI    impls, DTOs,   util, presentation,
              │          mappers        designsystem, build-config
              └──────┬───────┘               │
                     ▼                 platform-apis/*
                 domain/*              network, datastore
          PURE KOTLIN — no Android
```

## Module inventory

### Core
| Module | Owns |
|---|---|
| `core:util` | Dispatcher qualifiers, the `Logger` interface. No Android. |
| `core:presentation` | MVI base (`ScreenState`, `ScreenEvent`, `StateStore`, `BasicViewModel`, `BasicScreen`), `TextResource`, `IconResource`, lifecycle/back effects, `@DayNightPreviews` |
| `core:designsystem` | `AppTheme` + tokens, shared components, the design's icons and the Rubik family |
| `core:build-config` | Build constants |

### Platform APIs
| Module | Owns |
|---|---|
| `platform-apis:network` | Retrofit + OkHttp, `ErrorInterceptor`, `AppException` |
| `platform-apis:datastore` | Preferences DataStore behind `KeyValueStore`, `StorageException` |

### Features
| Module | Owns |
|---|---|
| `domain:onboarding` | `SubscriptionPlan`, the three onboarding use cases, three result types |
| `domain:home` | `Category`, `Question`, `GetHomeContentUseCase` and its two halves |
| `data:onboarding` | Local data source (the plan catalogue and the completion flag), mapper, repository |
| `data:home` | `HomeApi`, remote data source, mappers, repository |
| `presentation:onboarding` | The intro pager **and** the paywall — one flow, two screens |
| `presentation:home` | The home screen |

## Key architectural decisions

### 1. `domain` purity is compiler-enforced

Domain modules apply `plantappmvi.kotlin.library`, which is `java-library` — not AGP. `import android.*` is a **compile error**, not a review comment.

*Rules out:* Parcelable entities, `Context` in use cases, Android-dependent domain tests.

### 2. `framework/app` is the single composition root

It owns the `NavHost`, every route constant, every `NavigatorImpl`, and the start-destination gate. It is the only module in the build that knows both features exist — which is exactly what lets `presentation:onboarding` and `presentation:home` stay ignorant of each other.

### 3. Events own their reduction

`ScreenEvent<S>.reduce(oldState): S`. No central reducer, no growing `when`.

*Requires:* `reduce` is strictly pure — `copy` only. Side effects belong in the ViewModel.

### 4. `mapStateToProps` — the UI never sees State

Every screen has `@Immutable` Props and a pure, non-`@Composable` `mapStateToProps`. `Content` takes Props only.

*Why it is here and not in the Flutter build:* in Compose, `@Immutable` Props plus compiler-memoised lambdas let the runtime skip recomposition, and `Props.preview()` gives a preview for free. In Flutter the same pattern buys nothing — a props object holding Dart closures fails `==` on every rebuild, so nothing is skipped. The pattern is worth its weight here and was not there.

### 5. No Effect / one-shot-event channel

Navigation goes through injected `Navigator` interfaces. Transient UI is a nullable field in State with an explicit dismiss event.

*This is where the port gains the most.* The Flutter build needs an `isFinished` flag plus a `finishConsumed` event on the intro screen, and a `shouldExit` flag plus `exitConsumed` on the paywall, purely because a Bloc cannot navigate. Both pairs are gone: the ViewModel holds a `Navigator` and calls it.

### 6. Sealed result types per operation

Not `Result<T>`, not `Either`, not exceptions across layers. The compiler enumerates exactly the failures *this* operation has, so `when` is exhaustive and adding a failure mode breaks the build at every call site.

### 7. State is flat, not a sealed `UiState`

Home is routinely refreshing *while* showing a populated grid *while* the articles endpoint is down. A `Loading | Success | Error` hierarchy cannot represent that without lying.

The same argument produces **two failure fields on home, not one**: the endpoints fail independently, and a dead grid should not blank a working carousel.

### 8. Design system speaks Props, not domain

`core:designsystem` never depends on `domain`. Shared components take primitives / `TextResource` / `IconResource` / `ImmutableList`; features own the `Domain → Props` mappers.

### 9. The responsive scale is a token, not a per-screen decision

`AppTheme` swaps `RegularAppDimens` for `CompactAppDimens` below a 700dp viewport. Every screen tightens at once and no screen special-cases itself.

### 10. Concurrency is hand-rolled, and better for it

The Flutter build declares a `bloc_concurrency` transformer per I/O event because Bloc processes events concurrently by default. Here the same two behaviours are explicit — a job guard for droppable, `debounce` + `collectLatest` for restartable — and `viewModelScope` adds real cancellation, which Dart `Future`s cannot offer. All the "is the sink still open" guarding the Flutter version needs simply does not exist here.

### 11. The `text/plain` workaround does not exist here

Both endpoints answer with `content-type: text/plain` despite returning JSON. Dio dispatches its decoder on that header, so the Flutter build needs a whole `JsonDecodeInterceptor`. Retrofit chooses a converter by the declared return type, so kotlinx-serialization parses them regardless. The server quirk is real; the workaround is not needed. This is noted in `NetworkModule` so nobody ports it back in out of habit.

## Deviations from the blueprint

| Date | Rule | Deviation | Why |
|---|---|---|---|
| 2026-08-14 | All domain→data `@Binds` live in `framework/app` | Each `data` module binds its own repository and data source | Kotlin visibility. A `@Binds` in the composition root forces the impl public, and then every type in its constructor signature public too — the DTOs included. Binding in the module keeps the whole chain `internal`, so the property the rule protects (nothing outside `data` knows an implementation exists) is enforced by the compiler instead of by convention. The composition root still owns every route and every `Navigator`. |
| 2026-08-14 | `lint { checkDependencies = true }` | Not enabled | It re-analyses every dependency from the app module on each run, for a build this size a large cost with nothing new found — each module already lints itself with `warningsAsErrors`. |
| 2026-08-14 | `MatchingDeclarationName` | Disabled in detekt | A component and its Props class are one declaration in two halves. The blueprint's own component convention puts them in one file. |

## Known debt

| Item | Impact | Notes |
|---|---|---|
| Icons and artwork ship at 1x only (`drawable-mdpi`) | Soften on dense screens | The design file exports at 1x for its 360dp frame. A 3x re-export is a straight file swap into `drawable-xxhdpi`, no code change. Lint's `IconMissingDensityFolder` is set to informational for this reason. |
| AGP 8.7.3 while 9.x exists | None today | The catalog is a compatibility-verified set; AGP↔Gradle↔Kotlin↔KSP all move together. The lint "newer version" checks are informational so a build cannot start failing purely with the passage of time. |
| No screenshot tests | UI drift is uncaught by CI | Paparazzi over each screen's `Props.preview()` in both schemes is the natural next step, and cheap because the Props already exist. |
| Four of the five bottom-bar destinations have no screen | — | Only Home is in the case's scope. They render unselected rather than posing as buttons that silently do nothing. |
| The scan control is inert | — | Same reason. It is the bar's one live affordance in the design, so it is drawn live. |

## Recurring patterns worth knowing

### Delegation over inheritance
`class XNavigatorImpl(...) : XNavigator, BasicNavigator by basicNavigator` — `back()` implemented once, globally. Same trick for `BasicViewModel … StateStore<S,E> by stateStore`.

### Fail open on persistence
If the onboarding flag cannot be read, the gate shows onboarding. If it cannot be written, the paywall still lets the user through and records the failure. Repeating onboarding is a much milder failure than locking someone out of the app.

### Artwork in fractions
Every illustration is placed and sized as a fraction of the box it is given, taken from the design's own proportions. A composition measured at 360×800 then holds on a 412dp phone or a tablet instead of drifting apart at fixed offsets.

### `fullBleed`
A page has one gutter, declared once as the list's content padding. Bands that must reach the screen edges — the home header's leaves, the article carousel — escape it with `Modifier.fullBleed(gutter)` rather than the page giving up and using a second scroll container.

## For future sessions

- Read `AGENT.md` for rules; this file for reasoning; `docs/FLUTTER_TO_ANDROID.md` when a decision only makes sense against the other implementation.
- Before adding a feature, open `presentation/onboarding/paywall` — it is the fullest slice (load, select, submit, navigate, fail) and the one to mirror.
- **Never** finish a feature without wiring `framework/app`. It compiles and then crashes at runtime.
- Run `./gradlew detekt lintDebug runLocalTests assembleDebug` before declaring anything done.
