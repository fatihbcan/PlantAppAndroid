# Flutter → Android: the same case, built twice

> Companion to `AGENT.md` and `MEMORY.md`.
>
> The HUBX brief is written for Flutter, and there is a complete Flutter
> implementation of it at `../hubx-flutter-case`. This app is the same product
> built natively. This document is the map between them — what transliterates,
> what changes shape, and the three places where one platform is simply better
> at the job.
>
> It is deliberately two-directional: read it left-to-right to understand this
> repository, right-to-left to understand the other one.

---

## 1. The 30-second map

| Flutter build | Here | Fidelity |
|---|---|---|
| folder per layer, one package | Gradle module per layer | ⬆️ **compiler-enforced** |
| `@injectable` / `@LazySingleton` / `@module` | `@Inject` / `@Singleton` / `@Module` | ✅ very close |
| `Bloc` | `ViewModel` + `StateStore` | ⚠️ split into two |
| `on<Event>` handler | `Event.reduce(old)` **plus** a ViewModel method | ⚠️ logic moves |
| State getters + `const` sub-widgets | `mapStateToProps` → `Props` → `Content` | ⬆️ **restored** |
| `@freezed sealed` union | `sealed interface` | ✅ near-perfect |
| `switch` with patterns, no `default:` | `when`, no `else` | ✅ near-perfect |
| `@freezed` + `copyWith()` | `data class` + `copy()` | ✅ |
| `dio` + `Interceptor` | Retrofit + OkHttp `Interceptor` | ✅ |
| `shared_preferences` | Preferences DataStore | ✅ |
| `gen_l10n` → `context.l10n.x` | `strings.xml` → `TextResource` | ✅ |
| `ThemeExtension<AppColors>` | `AppColors` + `staticCompositionLocalOf` | ✅ near-perfect |
| `auto_route` + `AutoRouteGuard` | injected `Navigator` + start-destination gate | ⚠️ same intent, different shape |
| golden tests | *(none yet — Paparazzi is the gap)* | ⬇️ **regression** |
| `build_runner` (visible, `*.g.dart`) | KSP (invisible, in `build/`) | ⬆️ one less command to forget |
| `Future` + `async` | `suspend` + structured concurrency | ⬆️ **real cancellation** |
| `bloc_concurrency` transformers | job guards / `collectLatest` | ⚠️ explicit rather than declared |
| `BlocBuilder` | `collectAsStateWithLifecycle()` | ✅ |
| widget rebuild + `const` + `identical` | recomposition + `@Immutable` + stability | ⚠️ **different model** — see §7 |

---

## 2. Project structure — getting the compiler back

The Flutter build is one Dart package with folders. Nothing stops
`presentation/home_bloc.dart` from importing `../data/home_repository_impl.dart`.
It compiles. It runs. It is wrong. Its own `AGENT.md` says so and backs the rule
with lints and discipline.

Here, `presentation:home` *physically cannot* import `data:home`, because the
Gradle dependency is not declared. The build fails.

That is the single biggest thing this port gains, and it is worth being precise
about what buys it: not Kotlin, and not Android — the **module boundary**. The
Flutter project could have had it too, via pub workspaces or melos; the brief
asked for a folder structure and it delivered one.

**The strongest version of the boundary is the domain layer.** `domain:home`
applies a `java-library` convention plugin rather than AGP, so `import
android.*` there is a compile error. There is no Dart equivalent of that at all.

---

## 3. State management — Bloc → ViewModel + StateStore + reduce

The Flutter build has two moving parts. This one has three, and the split is
the point.

### Flutter

```dart
Future<void> _onStarted(PaywallStarted event, Emitter<PaywallState> emit) async {
  emit(state.copyWith(isLoading: true, error: null));   // work AND reduction
  final result = await _getPlans();                     // both live here
  switch (result) {
    case GetPlansSuccess(:final plans):
      emit(state.copyWith(isLoading: false, plans: plans));
    case GetPlansFailure():
      emit(state.copyWith(isLoading: false, error: PaywallError.plansUnavailable));
  }
}
```

### Here

```kotlin
// The event owns the reduction — pure, local, trivially testable.
data class PlansLoaded(val plans: List<SubscriptionPlan>, val defaultPlanId: String?) :
    PaywallScreenEvent {
    override fun reduce(oldState: PaywallScreenState) = oldState.copy(
        isLoading = false,
        plans = plans,
        selectedPlanId = oldState.selectedPlanId ?: defaultPlanId,
    )
}

// The ViewModel owns the work, and nothing else.
private fun load() {
    if (loadJob?.isActive == true) return
    loadJob = viewModelScope.launch {
        sendEvent(PaywallScreenEvent.LoadStarted)
        when (val result = getPlans()) {
            is GetPlansResult.Success ->
                sendEvent(PlansLoaded(result.plans, result.plans.defaultId()))
            is GetPlansResult.Failure -> sendEvent(PlansLoadFailed)
        }
    }
}
```

| | Flutter | Here |
|---|---|---|
| Who does async work | the `on<Event>` handler | the ViewModel |
| Who computes the next state | the same handler, via `emit` | `Event.reduce` |
| Is the reduction pure? | no — handlers are `async` and side-effecting | **yes, by construction** |
| Event naming | user **intents** (`RefreshRequested`) | past-tense **facts** (`PlansLoaded`) |
| Testing the reduction | `bloc_test` — fast, but async | a plain function call |

The naming flip is what trips people up moving either way. In Bloc an event
means *"the user wants this, go do it"*. Here it means *"this already happened,
fold it in"*. `HomeRefreshRequested` is idiomatic Bloc; here the equivalent is a
ViewModel method `onRefresh()` and an event called `ContentLoaded`.

---

## 4. What comes back: the Props layer

The Flutter build **deliberately dropped** `mapStateToProps`, and its own
documentation argues the case well: it is a React idiom, a Flutter reviewer
reads it as imported from another ecosystem, and — the load-bearing reason —
**the performance argument does not transfer**. A props object holding Dart
closures has a different `==` on every rebuild, so nothing is skipped.

In Compose it does transfer. `@Immutable` Props plus the compiler's lambda
memoisation let the runtime skip recomposition for real, and `Props.preview()`
seeds a preview for free.

So the pattern is back, and the whole of each screen's presentation logic lives
in one pure non-composable function:

```kotlin
internal fun mapStateToProps(
    state: HomeScreenState,
    onRefresh: () -> Unit = {},
): HomeScreenProps = HomeScreenProps(
    categories = state.visibleCategories.map { it.toProps() }.toImmutableList(),
    categoriesErrorMessage = state.categoriesFailure.toMessage(R.string.home_categories_error),
    emptySearchMessage = if (state.hasNoSearchResults) { … } else null,
    …
)
```

What the Flutter build kept — **state getters** for derived data — is kept here
too, and for the same reason: `visibleCategories` and `hasNoSearchResults` are
unit-tested with no widget tree and no Compose runtime. Getters recover most of
the value on both platforms; Props recover the rest on one of them.

---

## 5. Navigation — and the flag that disappears

The Flutter build's onboarding rule ("users who complete this flow must not
re-enter it") is an `AutoRouteGuard` consulted on every navigation into the
onboarding branch. Here it is resolved once, at startup, in the composition
root:

```kotlin
internalStartRoute.value = when (getOnboardingStatus()) {
    is OnboardingStatusResult.Completed -> AppRoutes.HOME
    is OnboardingStatusResult.Pending,
    is OnboardingStatusResult.Unavailable -> AppRoutes.INTRO
}
```

with the splash screen held until it resolves, so onboarding never flashes up
for a frame in front of someone who already finished it.

**The bigger difference is what stops existing.** A Bloc cannot navigate, so the
Flutter build signals navigation through state:

```dart
@Default(false) bool isFinished,     // intro
@Default(false) bool shouldExit,     // paywall
```

each with a `BlocListener` to notice it and a `finishConsumed` / `exitConsumed`
event to clear it, or the screen re-navigates on every rebuild.

All four are gone here. The ViewModel holds an injected `Navigator` and calls
`navigator.home()`. Two state fields, two events, two listeners and two classes
of "forgot to clear the flag" bug, deleted — not by cleverness, but because the
architecture has somewhere for navigation to go.

---

## 6. Concurrency — the biggest genuine upgrade

This is where the Flutter document is most candid: *"Kotlin coroutines give you
structured concurrency. Dart Futures do not. This is the one place Flutter is
meaningfully weaker."*

| Flutter | Here |
|---|---|
| `droppable()` on refresh/submit | `if (job?.isActive == true) return` |
| `restartable()` + `debounce` on search | `MutableStateFlow` + `debounce` + `collectLatest` |
| `sequential()` | a `Channel`, or nothing — most cases do not need it |
| `(a(), b()).wait` | `coroutineScope { async { … } }` in the use case |
| `if (emit.isDone) return` — required | **not needed** |
| `CancelToken` plumbed through `dio` | `viewModelScope` cancels the request itself |

Bloc's transformers are more *declarative* — one word per handler, visible at
registration. That is a real ergonomic win and the trade is worth naming: here
the same intent is a hand-written guard, so `AGENT.md` has a table making the
expectation explicit rather than relying on a package to carry it.

What the guards buy back is cancellation that actually cancels. In the Flutter
build a `Future` cannot be cancelled once started; if the Bloc closes while a
request is in flight, the response still arrives and `emit` throws — hence
`emit.isDone` guards and a `CancelToken` on every request. None of that
scaffolding exists here.

---

## 7. The rebuild model — where intuitions mislead in both directions

**Flutter:** the lever is *object identity*. A rebuild constructs new widget
objects; Flutter asks whether each child is `identical()` to the old one and
skips the subtree if so. This is why `const` matters so much there — a `const`
constructor is canonicalised, so it is the same instance every time.

**Compose:** the lever is *type stability*. The compiler analyses whether a
composable's parameters are stable and skips the call if none changed.
`@Immutable` is you telling it to trust a type; `ImmutableList` exists because
`List` is unstable to the compiler and one `List` parameter defeats skipping for
the whole composable.

Neither model translates. `const` has no Compose equivalent, and `@Immutable`
has no Flutter one. The practical rules that fall out are correspondingly
different — `prefer_const_constructors` treated as an error there,
`ImmutableList`-only Props here — and copying either rule to the other platform
achieves nothing.

---

## 8. Testing

| Flutter | Here |
|---|---|
| `flutter_test` + `mocktail` | JUnit 4 + MockK |
| `bloc_test` `expect:` on the whole emission sequence | Turbine, or a direct `reduce` call |
| `blocTest` for reducer behaviour | **a plain function call** — reducers are pure |
| golden tests, light + dark | *(gap — Paparazzi is the natural fit)* |

The reducer tests are the clearest illustration of §3. In the Flutter build,
testing a state transition means building a Bloc, stubbing its use cases, adding
an event, and asserting an async sequence. Here it is:

```kotlin
val new = HomeScreenEvent.SearchCleared.reduce(old)
new.appliedQuery shouldBe ""
```

No coroutines, no mocks, no dispatcher.

**One test discipline carries over unchanged, and it matters:** where
concurrency is the thing under test, use *slow* stubs. An instantly-completing
mock never overlaps with anything, so a job guard has nothing to guard and the
test passes for the wrong reason. `GetHomeContentUseCaseTest` delays both
endpoints by 500ms and asserts the total is 500ms, not 1000ms — which a
sequential implementation would fail.

**Where this port is behind:** golden tests are Flutter's genuine advantage and
the Flutter build uses them. There is no equivalent here yet. It is cheap to
close, because every screen already has a `Props.preview()` for Paparazzi to
render, and it is recorded in `MEMORY.md` as debt rather than quietly omitted.

---

## 9. Small things that changed shape

**The `text/plain` interceptor disappears.** Both endpoints return JSON under
`content-type: text/plain`. Dio dispatches its decoder on that header, so the
Flutter build needs a `JsonDecodeInterceptor` to fix the body before anything
else sees it. Retrofit picks a converter by the declared *return type*, so
kotlinx-serialization parses it regardless. One file that does not need porting
— and `NetworkModule` says so in a comment, because the obvious instinct is to
port it anyway.

**Two `toEntities()` become two names.** Dart happily hosts
`List<CategoryDto>.toEntities()` and `List<QuestionDto>.toEntities()` as
separate extensions. On the JVM both erase to
`toEntities(List): List` and the compiler rejects the clash, so they are
`toCategoryEntities()` and `toQuestionEntities()`. A small thing, but the kind
of small thing that only shows up at compile time.

**The search field grows a second state field.** In Flutter the `TextField`
owns a controller, so typed text appears immediately while only the filter is
debounced. Compose text fields are driven by state, so debouncing the state
would visibly lag the field. The state therefore carries `query` (what the field
shows, updated per keystroke) and `appliedQuery` (what the grid filters by,
updated after the debounce) — modelling honestly what the Flutter version got
for free by keeping one of them outside its state.

**Assets go to `drawable-mdpi`.** Flutter treats an asset with no `2x`/`3x`
variant as 1x logical pixels; `drawable-mdpi` is the exact analogue, where 1px
= 1dp. Same files, same softness on a dense screen, same one-line fix (a 3x
re-export into `drawable-xxhdpi`).

---

## 10. Scorecard

Things this port does better:

1. Layer boundaries are compile errors, not conventions — and domain purity most of all.
2. Navigation is a call, which deletes four fields, four events and two listeners.
3. Cancellation is real, which deletes the `emit.isDone` / `CancelToken` scaffolding.
4. Reducers are pure functions, so state-transition tests need no async machinery.
5. `mapStateToProps` earns its keep here, where in Flutter it could not.

Things the Flutter build does better:

1. **Golden tests.** Committed PNGs that fail CI on UI drift, built in. This repo has no equivalent yet.
2. **Declarative concurrency.** `transformer: droppable()` states the intent in one word at the point of registration; a job guard is three lines of prose.
3. **Fewer moving parts per screen.** Two files where this has three, and for a screen with no meaningful state transitions that is genuinely less to read.

Neither list is a verdict. They are the trades, and knowing which side of each
one you are on is most of what "picking a stack" actually means.
