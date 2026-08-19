# Testing Strategy

## Objectives

HealthMetric tests focus on deterministic calculations, validation boundaries, privacy-sensitive persistence behavior, bounded backup handling, adult-only safety behavior, locale-aware presentation, desktop transient-state behavior, and primary adult user journeys.

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

### Desktop JVM tests

Located in `desktopApp/src/test/`:

`DesktopNumbersTest` covers:

- dot-decimal parsing;
- comma-decimal parsing;
- whitespace trimming;
- malformed decimal rejection;
- NaN/infinity rejection;
- whole-number parsing for imperial feet.

`DesktopCalculationsTest` covers:

- metric BMI integration with the shared adult reference profile;
- imperial BMI conversion through the shared core;
- neutral waist-to-height presentation;
- field-specific invalid-text feedback;
- preservation of shared validation limits.

Run:

```bash
gradle :desktopApp:ktlintCheck :desktopApp:test
```

Build the current-OS runnable JAR as an additional integration/configuration check:

```bash
gradle :desktopApp:packageUberJarForCurrentOS
```

The dedicated `.github/workflows/desktop.yml` workflow runs desktop formatting, tests, and current-OS runnable-JAR packaging on Linux, Windows, and macOS.

### Android instrumentation/UI tests

Located in `androidApp/src/androidTest/`:

- `OnboardingUiTest` — fresh-install adult-use notice and both age-choice actions;
- `AdultGateUiTest` — under-18 choice dispatch and adult-reference unavailable screen;
- `CalculatorUiTest` — metric BMI success result and missing-weight validation;
- `WaistToHeightUiTest` — ratio success result and missing-waist validation;
- `SettingsUiTest` — explicit history opt-in, retention selection, save-file, and share-backup actions;
- `HistoryUiTest` — per-entry deletion and erase-all confirmation;
- `HealthMetricDataStoreTest` — privacy opt-in, retention trimming, portable export/restore, unsupported schemas, device-local consent/adult-gate preservation, entry delete/restore, chronology after undo, malformed-record recovery, duplicate-ID handling, and invalid entry rejection.

Run with a connected device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

The dedicated `.github/workflows/android-instrumentation.yml` workflow provisions an Android API 35 emulator and runs this connected test suite for pull requests and `main` pushes.

## Stable Android UI automation tags

Critical Android controls use constants in `HealthMetricTestTags` rather than brittle text-only selectors where a stable semantic hook improves test reliability. User-visible semantics remain present for accessibility; test tags are not used as a substitute for accessible labels.

Covered tagged journeys include:

- BMI weight, height, calculate, and result;
- waist/height ratio inputs, calculate, and result;
- history list;
- privacy history switch.

## Consent/safety regression invariants

Tests must prevent regressions where imported or restored data could change device-local choices.

A portable Android backup must not export or restore:

- `historyEnabled` / `history_enabled` consent state;
- adult-use confirmation;
- onboarding completion.

Legacy schema-v1 documents containing similarly named fields must not override the current installation's values.

Desktop has no persistence layer, so its adult-use choice is process-local and resets when the application closes.

Both user-facing clients must keep the under-18 path separate from adult BMI/waist reference results.

## Chronology regression invariant

Android history is displayed newest-first. Adding, importing, deleting, and undoing entries must preserve descending timestamp order before applying the selected retention limit.

A confirmed regression where undoing an older deleted entry moved it to the top of the list now has dedicated DataStore instrumentation coverage.

## Required regression policy

Every confirmed defect should receive a regression test at the lowest practical layer before or with the fix.

Examples:

- calculation boundary defect → shared unit test;
- malformed backup crash → DataStore instrumentation test;
- backup size bypass → `BackupIoTest` plus restore test where relevant;
- consent/adult-gate restore regression → DataStore instrumentation test;
- Android locale parsing regression → `LocalizedNumbersTest`;
- desktop text parsing regression → `DesktopNumbersTest`;
- desktop shared-core integration regression → `DesktopCalculationsTest`;
- Android screen state regression → Compose UI test;
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
- corrupted/unsupported Android backup schema;
- oversized Android backup payloads;
- malformed and duplicate Android history records;
- legacy Android backups containing non-portable consent/safety fields;
- empty Android history;
- disabled Android history;
- Android retention-limit trimming;
- Android delete/restore behavior while history saving is disabled;
- Android chronological ordering after delete/undo;
- desktop malformed text input;
- desktop whole-number feet parsing.

## Property/fuzz testing

The shared module uses seeded property-style loops for large sets of valid inputs. Keep seeds deterministic so failures reproduce exactly in CI.

Android backup parsing is intentionally bounded before JSON parsing and validates each history record independently. If backup schemas become more complex, add dedicated parser fuzz/property tooling rather than relying only on example tests.

## Accessibility verification

Automated tests are only one layer. Release candidates should also be reviewed with:

### Android

- TalkBack;
- large font/display scaling;
- dark/light/dynamic themes;
- keyboard/DPAD navigation where relevant;
- chart content descriptions;
- deletion button labels and undo action;
- restore/destructive confirmation dialogs;
- non-color-only status interpretation;
- dot- and comma-decimal locale presentation.

### Desktop

- keyboard traversal through adult gate, section navigation, fields, theme switch, and actions;
- visible focus indication;
- operating-system screen reader naming;
- display scaling;
- light/dark contrast;
- external-link actions;
- under-18 path isolation from adult calculators;
- non-color-only error/result meaning.

See [`accessibility.md`](accessibility.md) and [`desktop.md`](desktop.md).

## CI quality gates

The main CI workflow fails on:

- repository invariant failures;
- internal Markdown-link failures;
- shared/Android/desktop ktlint failures;
- shared JVM test failures;
- desktop JVM test failures;
- desktop runnable-JAR packaging failures;
- Android JVM unit test failures;
- Android release lint failures;
- Android debug assembly failures;
- Android unsigned release APK failures;
- Android unsigned release AAB failures.

Additional workflows fail on:

- connected Android emulator test failures;
- desktop Linux/Windows/macOS formatting, test, or runnable-JAR packaging failures;
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

Then run Android connected tests:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS also run:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

For desktop, manually launch the application on every distribution platform that will be published even when cross-platform CI is green.

Finally perform the manual accessibility/device checks documented in [`accessibility.md`](accessibility.md), [`desktop.md`](desktop.md), and the release checklist in [`release.md`](release.md).
