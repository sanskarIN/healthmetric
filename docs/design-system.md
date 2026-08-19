# HealthMetric Design System

HealthMetric uses a calm, neutral design language intended to make measurement information easy to read without assigning appearance-based value or emotional pressure to results.

## Platform scope

The Android client has the project's most complete tokenized design system. The desktop client uses Compose Material 3 components and the same content/safety principles, but it intentionally does not duplicate Android-only token classes merely to force implementation symmetry.

Shared cross-platform rules:

- adult health references remain educational and non-diagnostic;
- color never carries health meaning by itself;
- errors/results include explicit text;
- controls use visible labels or accessible names;
- unavailable/recovery states explain the next action in text;
- no appearance ranking, shame/praise language, or pressure-oriented body goals;
- layouts remain readable under common scaling/resizing conditions;
- error recovery must not rely on hidden gestures or clearing application data.

## Android color

The base palette uses a teal primary family with neutral light/dark surfaces. Android dynamic color is supported where available.

Rules:

- color never communicates health meaning by itself;
- measurement bands are described with text rather than red/green success/failure scoring;
- errors use Material error styling plus explicit text;
- charts use a neutral measurement line instead of category colors that imply judgment.

## Android typography

`HealthMetricTypography` defines the application scale:

- headline large: 32sp / 40sp;
- headline medium: 28sp / 36sp;
- headline small: 24sp / 32sp;
- title medium: 16sp / 24sp;
- body large: 16sp / 24sp;
- body medium: 14sp / 20sp;
- body small: 12sp / 18sp;
- label medium: 12sp / 16sp.

The UI uses scalable Compose text units and must remain usable with Android font/display scaling.

## Android spacing

`HealthMetricSpacing` provides:

| Token | Value |
|---|---:|
| `xxs` | 4dp |
| `xs` | 8dp |
| `sm` | 12dp |
| `md` | 16dp |
| `lg` | 20dp |
| `xl` | 24dp |
| `xxl` | 32dp |
| `hero` | 48dp |

Android UI should use these tokens instead of near-duplicate spacing values without reason.

## Android shape and elevation

`HealthMetricShapes` defines reusable rounded surfaces:

- extra small: 8dp;
- small: 12dp;
- medium: 16dp;
- large: 24dp;
- extra large: 32dp.

`HealthMetricElevation` provides:

- resting: 0dp;
- raised: 2dp;
- overlay: 6dp.

Prefer tonal hierarchy and spacing over excessive shadows.

## Motion

`HealthMetricMotion` defines nominal Android timing constants:

- quick: 120ms;
- standard: 220ms;
- emphasized: 320ms.

Neither client currently depends on substantial decorative motion. If motion is introduced, it must not delay content access and should respect reduced-motion expectations where relevant.

## Android interaction states

### Adult-use gate

The adult and under-18 choices must remain visually and textually distinct without presenting the adult path as a reward or the under-18 path as a failure.

The under-18 unavailable state can show **Return to age selection** so an accidental choice has an explicit recovery route. That action does not reveal adult results; it only returns to the gate.

### About navigation

When the About destination hides bottom navigation, a visible Back action is required. The same return behavior must also be available to Android system Back.

### Destructive actions

Erase-all history, delete-all local data, and restore/replacement actions use confirmation where the effect is destructive/replacing. Per-entry deletion uses immediate Undo instead of adding a confirmation dialog to every row.

### Validation

Validation text identifies the field/unit problem directly. Error color may reinforce state but is never the only signal.

## Android icons and charts

Use Material icons for platform-consistent controls. Standalone icon buttons require content descriptions; icons paired with an explicit navigation label may be decorative.

Charts are Android-only in the current clients. They are supplementary and must retain a textual equivalent rather than conveying health interpretation through visual/color-only signals.

`ChartScale` performs finite-safe normalization before Canvas coordinates are derived. This is a rendering implementation detail that preserves visibility/stability for extreme finite imported values without assigning them different health meaning.

## Android responsive layout

Primary Android content is centered with an 840dp maximum width so tablet/wide-window layouts remain readable without stretching forms across the full window.

Long screens should scroll rather than clip critical text/actions. Large font/display scaling must be considered when arranging bottom navigation, retention choices, dialogs and age-gate recovery controls.

## Desktop layout

The desktop client uses a deliberately simple Material 3 workspace:

- branded header with a labeled session theme switch;
- startup adult-use choice as the first interaction;
- text-labeled section navigation for adult users;
- scrollable main content;
- standard labeled text fields, radio buttons, buttons, cards, and switches;
- explicit result/error text;
- About/evidence content with explicit link buttons.

Desktop layout should remain usable under high-DPI scaling and window resizing. Long content must remain reachable through scrolling rather than clipping.

Desktop currently uses Material light/dark color schemes for the session. Platform-specific design tokens may be introduced later only when they solve a real consistency/maintenance need.

## Desktop measurement forms

Metric/imperial switching must leave the active form understandable from labels, not field position alone.

Imperial height uses separate **feet** and **remaining inches** fields. Remaining inches must be in `[0, 12)`. An invalid component must produce textual feedback instead of being silently normalized into a different height.

Desktop presentation parsing accepts ordinary dot/comma decimals but rejects signed/scientific/non-finite/malformed measurement syntax. These presentation rules must remain distinct from shared adult range validation.

## Content language

All HealthMetric UI copy must:

- remain adult-only for current reference calculators;
- describe measurements as educational screening information;
- avoid diagnoses;
- avoid appearance rankings, shame, praise, or pressure-oriented goals;
- explain privacy behavior plainly;
- make under-18 unavailability explicit without presenting adult reference values;
- explain recovery/errors without blaming or judging the user.

## Testability and semantics

Stable Compose test tags are appropriate for critical automation paths such as navigation, calculators, history, privacy controls, About return, and age-gate correction. They supplement—not replace—visible text/content descriptions/semantic meaning.

A UI change should prefer a stable semantic hook when text-only automation would be brittle, while keeping the user-facing accessible name intact.

## Design review checklist

When changing Android or desktop UI, verify:

- hierarchy remains clear without relying on color;
- primary controls are visibly labeled;
- error/result meaning remains text-accessible;
- adult-use gating remains unambiguous;
- under-18 recovery does not expose adult results;
- About/other modal-like destinations retain a return path;
- fictional/example data is used in screenshots/tests;
- desktop keyboard/focus or Android touch/screen-reader behavior is not degraded;
- responsive/scaled layouts remain usable;
- desktop split-height component errors remain discoverable;
- charts remain supplementary and finite-safe.

See [`accessibility.md`](accessibility.md) for manual acceptance expectations and [`documentation-map.md`](documentation-map.md) for documentation ownership when UI contracts change.
