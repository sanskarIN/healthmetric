# HealthMetric Design System

HealthMetric uses a calm, neutral design system intended to make measurement information easy to read without assigning appearance-based value or emotional pressure to results.

## Color

The base palette uses a teal primary family with neutral light/dark surfaces. Android dynamic color is supported where available.

Rules:

- color never communicates health meaning by itself;
- measurement bands are described with text rather than red/green success/failure scoring;
- errors use Material error styling plus explicit text;
- charts use a single neutral measurement line instead of category colors that imply judgment.

## Typography

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

## Spacing

`HealthMetricSpacing` provides the shared spacing scale:

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

New UI should use these tokens instead of introducing near-duplicate spacing values without reason.

## Shape

`HealthMetricShapes` defines reusable rounded surfaces:

- extra small: 8dp;
- small: 12dp;
- medium: 16dp;
- large: 24dp;
- extra large: 32dp.

These values are installed into `MaterialTheme` so Material components use a consistent shape language.

## Elevation

`HealthMetricElevation` provides:

- resting: 0dp;
- raised: 2dp;
- overlay: 6dp.

Prefer tonal hierarchy and spacing over excessive shadows.

## Motion

`HealthMetricMotion` defines nominal timing constants:

- quick: 120ms;
- standard: 220ms;
- emphasized: 320ms.

The initial app deliberately does not add decorative animation. If motion is introduced, it must not delay content and should respect reduced-motion expectations.

## Icons

Use Material icons for platform-consistent controls. Standalone icon buttons require content descriptions; icons paired with an explicit navigation label may be decorative.

## Responsive layout

Primary app content is centered with an 840dp maximum width so tablet/wide-window layouts remain readable without stretching forms across the full window.

## Content language

HealthMetric copy must:

- remain adult-only for the current reference calculators;
- describe measurements as educational screening information;
- avoid diagnoses;
- avoid appearance rankings, shame, praise, or pressure-oriented goals;
- explain privacy behavior in plain language.
