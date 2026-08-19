# Testing Strategy

## Objectives

HealthMetric tests focus on deterministic calculations, validation boundaries, privacy-sensitive persistence behavior, and primary adult user journeys.

## Current automated coverage

### Shared domain tests

Located in `shared/src/commonTest/`:

- BMI metric calculation;
- BMI imperial/metric equivalence;
- adult reference boundary selection;
- unit conversion precision;
- invalid/non-finite input rejection;
- waist-to-height calculation;
- deterministic property-style coverage over 1,000 generated valid inputs per calculator.

Run:

```bash
gradle :shared:desktopTest
```

### Android unit checks

Run:

```bash
gradle :androidApp:testDebugUnitTest
```

The initial Android implementation keeps most deterministic business rules in `shared`, so Android local tests should concentrate on future presentation adapters and non-framework utilities rather than duplicate domain tests.

### Android instrumentation/UI tests

`OnboardingUiTest` verifies the fresh-install adult-use notice and both age-choice actions are visible.

Run with a connected device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

## Required regression policy

Every confirmed defect should receive a regression test at the lowest practical layer before or with the fix.

Examples:

- calculation boundary defect → shared unit test;
- malformed backup crash → persistence instrumentation test;
- screen state regression → Compose UI test;
- accessibility label regression → Compose semantics test/manual accessibility check.

## Validation edge cases

Test at minimum:

- exact lower/upper supported input boundaries;
- values immediately outside boundaries;
- zero/negative values;
- NaN and infinities at domain level;
- imperial feet/inches normalization boundaries;
- reference band thresholds;
- corrupted/unsupported backup schema;
- empty history;
- disabled history.

## Property/fuzz testing

The shared module uses seeded property-style loops for large sets of valid inputs. Keep seeds deterministic so failures reproduce exactly in CI.

If a parser becomes more complex, add dedicated fuzz/property tooling rather than relying only on example tests.

## Accessibility verification

Automated semantics tests are only one layer. Release candidates should also be reviewed with:

- TalkBack;
- large font/display scaling;
- dark/light themes;
- keyboard/DPAD navigation where relevant;
- chart content descriptions;
- non-color-only status interpretation.

See [`accessibility.md`](accessibility.md).

## CI quality gates

The CI workflow fails on:

- ktlint style failures;
- shared JVM test failures;
- Android unit test failures;
- Android lint failures;
- Android debug assembly failures.

Separate workflows perform CodeQL analysis and pull-request dependency review.

## Release candidate checklist

Before tagging:

```bash
gradle :shared:ktlintCheck :androidApp:ktlintCheck
gradle :shared:desktopTest
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintRelease
gradle :androidApp:assembleRelease
```

Then run connected UI tests and manually verify the primary flows on at least one emulator/device.
