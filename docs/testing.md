# Testing Strategy

## Objectives

HealthMetric tests focus on deterministic calculations, validation boundaries, privacy-sensitive persistence behavior, bounded backup handling, adult-only safety behavior, locale-aware presentation, navigation resilience, chronological history integrity, release packaging, and primary adult user journeys.

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
- `AboutNavigationUiTest` — About opens from multiple origins and returns to the correct origin through the explicit back action;
- `HealthMetricDataStoreTest` — privacy opt-in, retention trimming, portable export/restore, unsupported schemas, device-local consent/adult-gate preservation, canonical newest-first ordering, entry delete/restore, malformed-record recovery, duplicate-ID handling, and invalid entry rejection;
- `ReleaseScreenshotCaptureTest` — deterministic real-app release evidence for onboarding, BMI, ratio, history, settings, About, and dark theme.

Run with a connected device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

The dedicated `.github/workflows/android-instrumentation.yml` workflow provisions an Android API 35 Pixel 7 emulator, runs this connected test suite for pull requests and `main` pushes, pulls the generated PNG set from app-scoped external storage, and uploads it as the `android-release-screenshots` artifact.

## Release screenshot evidence

`ReleaseScreenshotCaptureTest` resets local app state before capture and uses fictional/example measurements only. It produces:

- `01-onboarding.png`;
- `02-bmi-metric.png`;
- `03-bmi-result.png`;
- `04-waist-ratio.png`;
- `05-history.png`;
- `06-settings.png`;
- `07-about.png`;
- `08-dark-theme.png`.

CI treats missing screenshot files as an error when uploading the release-evidence artifact. Generated images still require a final human visual/privacy review before permanent README/store publication.

## Stable UI automation tags

Critical controls use constants in `HealthMetricTestTags` rather than brittle text-only selectors where a stable semantic hook improves test reliability. User-visible semantics remain present for accessibility; test tags are not used as a substitute for accessible labels.

Covered tagged journeys include:

- bottom navigation for BMI, ratio, history, and settings;
- About open/back navigation;
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

## History regression invariants

History is canonical newest-first by `timestampEpochMillis` after:

- adding a new result;
- replacing an entry with the same ID;
- restoring a deleted entry through Undo;
- importing a valid backup.

Retention is applied after canonical ordering. This prevents an older undone item from jumping to the top and prevents arbitrary JSON array ordering from deciding which records survive a retention cap.

Newly recorded entries use UUID identifiers. Imported IDs remain bounded/validated/deduplicated by the persistence layer.

## Required regression policy

Every confirmed defect should receive a regression test at the lowest practical layer before or with the fix.

Examples:

- calculation boundary defect → shared unit test;
- malformed backup crash → DataStore instrumentation test;
- backup size bypass → `BackupIoTest` plus restore test where relevant;
- consent/adult-gate restore regression → DataStore instrumentation test;
- history-order regression → DataStore instrumentation test;
- locale parsing regression → `LocalizedNumbersTest`;
- screen/navigation state regression → Compose UI test;
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
- out-of-order imported history records;
- legacy backups containing non-portable consent/safety fields;
- empty history;
- disabled history;
- retention-limit trimming;
- delete/restore behavior while history saving is disabled;
- About navigation from multiple originating screens.

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
- About back-navigation label;
- restore/destructive confirmation dialogs;
- non-color-only status interpretation;
- dot- and comma-decimal locale presentation.

See [`accessibility.md`](accessibility.md).

## CI quality gates

The main CI workflow fails on:

- repository invariant failures;
- broken internal Markdown links;
- ktlint style failures;
- shared JVM test failures;
- Android JVM unit test failures;
- Android release lint failures;
- Android debug APK assembly failures;
- Android unsigned release APK assembly failures;
- Android unsigned release App Bundle assembly failures;
- missing expected APK/AAB artifacts.

Additional workflows fail on:

- connected Android emulator test failures;
- missing release screenshot evidence files;
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

The scripts include `:androidApp:bundleRelease` in addition to APK builds.

Then run:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS also run:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

Finally inspect the CI-generated `android-release-screenshots` artifact, perform the manual accessibility/device checks documented in [`accessibility.md`](accessibility.md), and complete the release checklist in [`release.md`](release.md).
