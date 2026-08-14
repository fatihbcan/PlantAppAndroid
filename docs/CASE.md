# HUBX Developer Case — requirements

> Distilled from the HUBX Recruitment Assignment brief.
>
> The brief is written for Flutter; this repository answers it natively in
> Kotlin/Compose. Where a requirement names a Flutter package, the Android
> equivalent it maps onto is noted in `FLUTTER_TO_ANDROID.md`.
> This is the source of truth for scope. `AGENT.md` is the source of truth for *how*.

## Environments

- **Figma:** https://www.figma.com/design/PReH5pLDAK4wi1FQbzVoGO/Android-Case?node-id=0-1
  *(titled "Android-Case" — HUBX reuses the design across platform cases)*
- **API**
  - Categories: `https://dummy-api-jtg6bessta-ey.a.run.app/getCategories`
  - Questions: `https://dummy-api-jtg6bessta-ey.a.run.app/getQuestions`

## Requirements

**1 — UI implementation**
- Flutter widgets, pixel-perfect and responsive against the Figma.
- Custom widgets plus layout widgets (`Column`, `Row`, `Stack`, `Expanded`, `Flexible`) for modular, reusable code.
- Material **or** Cupertino widget sets only, chosen by platform context.
- Follow Flutter layout/theming practice: `MediaQuery`, `LayoutBuilder`, `Theme.of(context)`.

**2 — Architecture**
- Bloc-like state management, preferably `flutter_bloc`, with Cubit or Bloc as appropriate for event/state separation.
- Scalable structured folders (features, bloc, models, views) and reusable components.
- Apply Clean Architecture where possible — domain / data / presentation separation.

**3 — Networking**
- HTTP via a client such as `dio` (interceptors, cancellation, timeouts, file downloads).
- JSON via code generation, preferably `json_serializable`.
- Immutable models, e.g. via `freezed` (equality + copy).
- Proper error handling, status-code validation, custom exception management.

**4 — Application flows** — two of them:
- **Onboarding flow** — ends when the close button on the **paywall** screen is tapped, then navigates to home. **Users who complete this flow must not re-enter it.**
- **Home flow** — the main screen after onboarding.

**5 — Pixel-perfect & responsive** — must match Figma across multiple screen sizes and resolutions.

**6 — Data handling** — JSON from the API endpoints; fetch, parse, and display all required data accurately.

## Evaluation criteria

1. **Structure** — modular, Bloc-based, Clean Architecture; clear feature-first folders (`features/onboarding`, `features/home`, `core`, `shared`).
2. **Styling** — consistent Dart/Flutter conventions (naming, formatting, widget-tree readability); reusable design via `ThemeData`, `TextStyle`, `ColorScheme`, custom components; **light/dark mode and accessibility** where applicable.
3. **Pixel perfection** — matches Figma using Material/Cupertino; responsive via `MediaQuery`, `LayoutBuilder`, `Flexible`, `Expanded`; **no hardcoded values** — scalable spacing via `EdgeInsets`, `SizedBox`, design constants.
4. **Logic & code quality** — meaningful names; clean widget composition separating presentation from business logic; strong error handling in **both** UI and network layers; Bloc-style state management.
5. **Application flow** — smooth onboarding leading into home; Bloc managing the onboarding↔home transition; declarative routing such as `auto_route`.
6. **Git** — clean, consistent commit history with meaningful messages; feature-based branching with PRs, or commits grouped by functionality.
7. **Bonus** — unit and widget tests (`flutter_test`, `bloc_test`, `mocktail`); performance work (lazy loading, efficient rebuilds, animated transitions via `AnimatedContainer`, `Hero`); static analysis via `flutter_lints`.

## Reading the criteria

Two things are graded that are easy to under-serve because they aren't features:

- **Git history is a criterion.** Commit in feature-sized increments from the first commit onward. A single "initial commit" with the finished app scores zero here and cannot be fixed retroactively.
- **"Consistent Dart/Flutter coding conventions" is a criterion.** Idiomatic beats clever. Where this project diverges from common Flutter practice (per-operation sealed results instead of `Either<Failure, T>`), the README must say why.

HUBX states the case exists *"to gain insight into your perspective, approach, and strengths"* — so a short README section on deliberate trade-offs is part of the deliverable, not padding.
