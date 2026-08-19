# Testing Strategy

## Objectives

HealthMetric tests focus on deterministic calculations, validation boundaries, privacy-sensitive Android persistence behavior, bounded backup handling, adult-only safety behavior, locale-aware presentation, desktop transient-state behavior, release artifact reproducibility, and primary adult user journeys.

## Current automated coverage

### Shared domain tests

Located in `shared/src/commonTest/`:

- BMI metric calculation;
- BMI imperial/metric equivalence;
- documented imperial BMI weight boundaries;
- unit-specific metric/imperial validation messages;
- adult reference boundary selection;
- evidence source metadata and review date;
- unit conversion precision;
- invalid/non-finite input rejection;
- waist-to-height calculation;
- documented imperial waist-to-height boundaries;
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
- locale-aware result formatting without grouping;
- finite-safe history-chart normalization, including extreme finite values;
- stale saved-enum restoration fallback for future navigation/filter enum changes.

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
- scientific-notation and signed-input rejection;
- whole-number parsing for imperial feet.

`DesktopCalculationsTest` covers:

- metric BMI integration with the shared adult reference profile;
- imperial BMI conversion through the shared core;
- imperial weight errors reported in pounds;
- neutral waist-to-height presentation;
- imperial waist errors reported in inches;
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

Native package tasks are host-specific:

```bash
# Linux
gradle :desktopApp:packageDeb

# Windows
gradle :desktopApp:packageMsi

# macOS
gradle :desktopApp:packageDmg
```

The dedicated `.github/workflows/desktop.yml` workflow runs desktop formatting/tests, current-OS runnable-JAR packaging, and the matching native installer build on Linux, Windows, and macOS.

### Repository/release tooling tests

Located in `scripts/tests/`:

- `test_check_release_version.py` — stable tag syntax plus Android/desktop version agreement;
- `test_stage_release_assets.py` — deterministic Android/desktop staging, all target desktop platforms, duplicate-output rejection, empty-output rejection, and invalid-tag rejection;
- `test_verify_release_assets.py` — exact eight-binary release set, missing/extra/empty rejection, deterministic SHA-256 manifest generation, and invalid-tag rejection.

Run:

```bash
python3 -m unittest discover -s scripts/tests -p "test_*.py"
```

These tests run in main CI, the tagged release preflight, and both local verification scripts.

### Android instrumentation/UI tests

Located in `androidApp/src/androidTest/`:

- `OnboardingUiTest` — fresh-install adult-use notice and both age-choice actions;
- `AdultGateUiTest` — under-18 choice dispatch, adult-reference unavailable screen, hidden return action when no callback is supplied, and correction-action dispatch when enabled;
- `CalculatorUiTest` — metric BMI success result and missing-weight validation;
- `WaistToHeightUiTest` — ratio success result and missing-waist validation;
- `SettingsUiTest` — explicit history opt-in, retention selection, save-file, and share-backup actions;
- `HistoryUiTest` — per-entry deletion and erase-all confirmation;
- `AboutNavigationUiTest` — explicit and system back navigation from About to the originating destination;
- `HealthMetricDataStoreTest` — privacy opt-in, retention trimming, portable export/restore, unsupported schemas, device-local consent/adult-gate preservation, adult-choice reset preservation, entry delete/restore, chronology after undo, malformed-record recovery, duplicate-ID handling, and invalid entry rejection;
- `ReleaseScreenshotCaptureTest` — drives the real app and captures the required eight-file Android release-evidence set with fictional/example values.

Run with a connected device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

The dedicated `.github/workflows/android-instrumentation.yml` workflow provisions an Android API 35 Pixel 7 emulator, runs the connected suite, pulls the app-scoped screenshot directory, and uploads `android-release-screenshots` plus instrumentation reports.

## Android release screenshot evidence

The required automated PNG set is:

1. `01-onboarding.png`
2. `02-bmi-metric.png`
3. `03-bmi-result.png`
4. `04-waist-ratio.png`
5. `05-history.png`
6. `06-settings.png`
7. `07-about.png`
8. `08-dark-theme.png`

Missing PNGs cause artifact upload failure. Automated capture proves that the real app rendered the expected release journey; it does not replace human visual/privacy review before permanent publication.

## Stable Android UI automation tags

Critical Android controls use constants in `HealthMetricTestTags` rather than brittle text-only selectors where a stable semantic hook improves test reliability. User-visible semantics remain present for accessibility; test tags are not used as a substitute for accessible labels.

Covered tagged journeys include:

- adult onboarding confirmation, under-18 selection, and return-to-age-selection correction action;
- bottom navigation for BMI, waist ratio, history, and settings;
- About open/back actions;
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

Resetting an accidental adult-use choice removes only adult/onboarding state; unrelated history consent, retention, theme, and saved history remain intact.

Desktop has no persistence layer, so its adult-use choice is process-local and resets when the application closes.

Both user-facing clients must keep the under-18 path separate from adult BMI/waist reference results.

## Chronology and identity regression invariants

Android history is canonical newest-first. Adding, importing, deleting, and undoing entries must preserve descending timestamp order before applying the selected retention limit.

A confirmed regression where undoing an older deleted entry moved it to the top of the list has dedicated DataStore instrumentation coverage. Import tests also verify that serialized JSON array order does not determine which chronologically newest records survive retention.

New locally recorded Android entries use UUID identifiers. Imported schema-v1 IDs remain backward-compatible but must continue through trim/non-blank/length/deduplication validation.

## Release-integrity regression invariants

The tagged release pipeline must fail closed unless:

- the tag uses stable `vMAJOR.MINOR.PATCH` form;
- the tag version matches Android `versionName` and the desktop project version;
- the tag commit is the current `main` commit;
- staging finds exactly one non-empty expected Android APK/AAB and exactly one non-empty JAR/native package per desktop host;
- the final downloaded binary set contains exactly the eight expected files and no unexpected files;
- SHA-256 checksums are generated from the verified binary set before publication.

The release workflow remains read-only until the final publish job.

## Required regression policy

Every confirmed defect should receive a regression test at the lowest practical layer before or with the fix.

Examples:

- calculation boundary defect → shared unit test;
- imperial unit/error regression → shared test plus presentation-layer test when user-visible;
- malformed backup crash → DataStore instrumentation test;
- backup size bypass → `BackupIoTest` plus restore test where relevant;
- consent/adult-gate restore regression → DataStore instrumentation test;
- adult-choice correction regression → DataStore plus Compose instrumentation test;
- chronology/undo defect → DataStore instrumentation test;
- Android About navigation trap → full-app Compose instrumentation test;
- Android locale parsing regression → `LocalizedNumbersTest`;
- Android history chart arithmetic regression → `ChartScaleTest`;
- stale saved Android enum value → `SavedEnumTest`;
- desktop text parsing regression → `DesktopNumbersTest`;
- desktop shared-core integration regression → `DesktopCalculationsTest`;
- Android screen state regression → Compose UI test;
- accessibility label regression → Compose semantics test/manual accessibility check;
- release artifact drift → repository invariant plus release-tooling unit test/workflow verification.

## Validation edge cases

Test at minimum:

- exact lower/upper supported measurement boundaries;
- values immediately outside boundaries;
- zero/negative values;
- NaN and infinities at domain/persistence boundaries;
- imperial feet/inches normalization boundaries;
- imperial pound/inch error messages and documented boundary acceptance;
- reference band thresholds;
- comma and dot decimal presentation input;
- desktop scientific/signed/mixed-separator input rejection;
- corrupted/unsupported Android backup schema;
- oversized Android backup payloads;
- malformed and duplicate Android history records;
- legacy Android backups containing non-portable consent/safety fields;
- extreme finite imported history values through chart normalization;
- empty Android history;
- disabled Android history;
- Android retention-limit trimming;
- Android delete/restore behavior while history saving is disabled;
- Android chronological ordering after delete/undo;
- out-of-order Android backup history;
- desktop malformed text input;
- desktop whole-number feet parsing;
- missing/duplicate/empty/unexpected release artifacts.

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
- About return navigation;
- adult-gate correction control;
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
- repository/release tooling unit-test failures;
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
- missing Android release screenshot evidence;
- desktop Linux/Windows/macOS formatting or test failures;
- desktop runnable-JAR failures;
- Linux DEB, Windows MSI, or macOS DMG packaging failures on matching hosts;
- iOS device/simulator shared-core compilation failures on macOS;
- CodeQL analysis failures;
- high-severity pull-request dependency review findings;
- repository-history secret scan findings;
- invalid release tag/version/main-commit preflight;
- ambiguous or empty release build outputs;
- incomplete/unexpected/empty final release asset sets.

## Release candidate checklist

Run the complete non-device suite:

```bash
bash scripts/verify.sh
```

or the Windows equivalent:

```powershell
.\scripts\verify.ps1
```

Both local scripts run repository/docs audits, the Python release-tooling regression suite, Kotlin formatting/tests, desktop JAR packaging, Android unit/lint checks, and Android debug/release APK/AAB assembly.

Then run Android connected tests:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS also run:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

For desktop, build and manually launch the runnable JAR and matching native package on every distribution platform that will be published even when cross-platform CI is green.

Finally perform the manual accessibility/device checks documented in [`accessibility.md`](accessibility.md), [`desktop.md`](desktop.md), and the release checklist in [`release.md`](release.md).
