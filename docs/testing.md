# Testing Strategy

## Objectives

HealthMetric tests focus on deterministic calculations, validation boundaries, privacy-sensitive persistence behavior, bounded backup handling, and primary adult user journeys.

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

### Android JVM unit checks

Located in `androidApp/src/test/`:

- privacy-first preference defaults;
- supported history-retention normalization;
- bounded UTF-8 backup read/write round trips;
- oversized backup read/write rejection.

Run:

```bash
gradle :androidApp:testDebugUnitTest
```

Most deterministic health calculations remain in `shared`, so Android JVM tests intentionally target Android-module utilities and preference policies rather than duplicating domain tests.

### Android instrumentation/UI tests

Located in `androidApp/src/androidTest/`:

- `OnboardingUiTest` — fresh-install adult-use notice and both age-choice actions;
- `CalculatorUiTest` — metric BMI success result and missing-weight validation;
- `WaistToHeightUiTest` — ratio success result and missing-waist validation;
- `SettingsUiTest` — explicit history opt-in, retention selection, save-file, and share-backup actions;
- `HistoryUiTest` — per-entry deletion and erase-all confirmation;
- `HealthMetricDataStoreTest` — privacy opt-in, retention trimming, export/restore round trip, unsupported schemas, entry delete/restore, malformed-record recovery, duplicate-ID handling, and invalid entry rejection.

Run with a connected device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

The dedicated `.github/workflows/android-instrumentation.yml` workflow provisions an Android API 35 emulator and runs this connected test suite for pull requests and `main` pushes.

## Stable UI automation tags

Critical controls use constants in `HealthMetricTestTags` rather than brittle text-only selectors where a stable semantic hook improves test reliability. User-visible semantics remain present for accessibility; test tags are not used as a substitute for accessible labels.

Covered tagged journeys include:

- BMI weight, height, calculate, and result;
- waist/height ratio inputs, calculate, and result;
- history list;
- privacy history switch.

## Required regression policy

Every confirmed defect should receive a regression test at the lowest practical layer before or with the fix.

Examples:

- calculation boundary defect → shared unit test;
- malformed backup crash → DataStore instrumentation test;
- backup size bypass → `BackupIoTest` plus restore test where relevant;
- screen state regression → Compose UI test;
- accessibility label regression → Compose semantics test/manual accessibility check.

## Validation edge cases

Test at minimum:

- exact lower/upper supported measurement boundaries;
- values immediately outside boundaries;
- zero/negative values;
- NaN and infinities at domain/persistence boundaries;
- imperial feet/inches normalization boundaries;
- reference band thresholds;
- corrupted/unsupported backup schema;
- oversized backup payloads;
- malformed and duplicate history records;
- empty history;
- disabled history;
- retention-limit trimming;
- delete/restore behavior while history saving is disabled.

## Property/fuzz testing

The shared module uses seeded property-style loops for large sets of valid inputs. Keep seeds deterministic so failures reproduce exactly in CI.

Backup parsing is intentionally bounded before JSON parsing and validates each history record independently. If backup schemas become more complex, add dedicated parser fuzz/property tooling rather than relying only on example tests.

## Accessibility verification

Automated semantics tests are only one layer. Release candidates should also be reviewed with:

- TalkBack;
- large font/display scaling;
- dark/light themes;
- keyboard/DPAD navigation where relevant;
- chart content descriptions;
- deletion button labels;
- non-color-only status interpretation.

See [`accessibility.md`](accessibility.md).

## CI quality gates

The main CI workflow fails on:

- ktlint style failures;
- shared JVM test failures;
- Android JVM unit test failures;
- Android release lint failures;
- Android debug assembly failures;
- Android unsigned release assembly failures.

The Android instrumentation workflow fails on connected emulator test failures. Separate workflows perform CodeQL analysis, pull-request dependency review, and full-history secret scanning.

## Release candidate checklist

Before tagging:

```bash
gradle :shared:ktlintCheck :androidApp:ktlintCheck
gradle :shared:desktopTest
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintRelease
gradle :androidApp:assembleDebug
gradle :androidApp:assembleRelease
```

Then run:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

Finally perform the manual accessibility/device checks documented in [`accessibility.md`](accessibility.md) and the release checklist in [`release.md`](release.md).
