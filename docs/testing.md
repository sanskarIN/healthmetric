# Testing Strategy

## Objectives

HealthMetric tests focus on deterministic calculations, validation boundaries, privacy-sensitive persistence behavior, bounded backup handling, adult-only safety behavior, locale-aware presentation, and primary adult user journeys.

## Current automated coverage

### Shared domain tests

Located in `shared/src/commonTest/`:

- BMI metric calculation;
- BMI imperial/metric equivalence;
- adult reference boundary selection;
- evidence source metadata and review date;
- unit conversion precision;
- invalid/non-finite input rejection;
- waist-to-height calculation;
- deterministic property-style coverage over 1,000 generated valid inputs per calculator.

Run:

```bash
gradle :shared:desktopTest
```

On macOS, compile both Apple targets:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

### Android JVM unit checks

Located in `androidApp/src/test/`:

- privacy-first preference defaults;
- supported history-retention normalization;
- bounded UTF-8 backup read/write round trips;
- oversized backup read/write rejection;
- locale-aware decimal input validation;
- comma/dot decimal parsing across representative locales;
- locale-aware result formatting without grouping.

Run:

```bash
gradle :androidApp:testDebugUnitTest
```

Most deterministic health calculations remain in `shared`, so Android JVM tests intentionally target Android-module utilities and preference/presentation policies rather than duplicating domain tests.

### Android instrumentation/UI tests

Located in `androidApp/src/androidTest/`:

- `OnboardingUiTest` — fresh-install adult-use notice and both age-choice actions;
- `AdultGateUiTest` — under-18 choice dispatch and adult-reference unavailable screen;
- `CalculatorUiTest` — metric BMI success result and missing-weight validation;
- `WaistToHeightUiTest` — ratio success result and missing-waist validation;
- `SettingsUiTest` — explicit history opt-in, retention selection, save-file, and share-backup actions;
- `HistoryUiTest` — per-entry deletion and erase-all confirmation;
- `HealthMetricDataStoreTest` — privacy opt-in, retention trimming, portable export/restore, unsupported schemas, device-local consent/adult-gate preservation, entry delete/restore, malformed-record recovery, duplicate-ID handling, and invalid entry rejection.

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

## Consent/safety regression invariants

Tests must prevent regressions where imported or restored data could change device-local choices.

A portable backup must not export or restore:

- `historyEnabled` / `history_enabled` consent state;
- adult-use confirmation;
- onboarding completion.

Legacy schema-v1 documents containing similarly named fields must not override the current installation's values.

## Required regression policy

Every confirmed defect should receive a regression test at the lowest practical layer before or with the fix.

Examples:

- calculation boundary defect → shared unit test;
- malformed backup crash → DataStore instrumentation test;
- backup size bypass → `BackupIoTest` plus restore test where relevant;
- consent/adult-gate restore regression → DataStore instrumentation test;
- locale parsing regression → `LocalizedNumbersTest`;
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
- comma and dot decimal presentation input;
- corrupted/unsupported backup schema;
- oversized backup payloads;
- malformed and duplicate history records;
- legacy backups containing non-portable consent/safety fields;
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
- dark/light/dynamic themes;
- keyboard/DPAD navigation where relevant;
- chart content descriptions;
- deletion button labels and undo action;
- restore/destructive confirmation dialogs;
- non-color-only status interpretation;
- dot- and comma-decimal locale presentation.

See [`accessibility.md`](accessibility.md).

## CI quality gates

The main CI workflow fails on:

- ktlint style failures;
- shared JVM test failures;
- Android JVM unit test failures;
- Android release lint failures;
- Android debug assembly failures;
- Android unsigned release assembly failures.

Additional workflows fail on:

- connected Android emulator test failures;
- iOS device/simulator shared-core compilation failures on macOS;
- CodeQL analysis failures;
- high-severity pull-request dependency review findings;
- repository-history secret scan findings.

## Release candidate checklist

Run the complete non-device suite:

```bash
bash scripts/verify.sh
```

or the Windows equivalent:

```powershell
.\scripts\verify.ps1
```

Then run:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS also run:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

Finally perform the manual accessibility/device checks documented in [`accessibility.md`](accessibility.md) and the release checklist in [`release.md`](release.md).
