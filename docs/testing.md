# Testing Strategy

## Objectives

HealthMetric verification focuses on:

- deterministic calculations and validation boundaries;
- adult-only calculator eligibility;
- Android privacy-sensitive persistence behavior;
- bounded Android backup handling;
- locale-aware Android presentation;
- reusable desktop/web/iOS UI compilation;
- native host integration;
- primary adult user journeys;
- security and repository integrity.

## Test pyramid

```text
manual platform/accessibility/release review
                   ▲
        native host/build verification
                   ▲
       Android instrumentation/UI tests
                   ▲
 Android JVM policy + persistence tests
                   ▲
     shared deterministic domain tests
```

The calculation engine belongs at the bottom of the pyramid and should receive the broadest deterministic coverage. Platform UI tests should verify wiring and user journeys rather than duplicate every calculation case.

## Shared domain tests

Located in `shared/src/commonTest/`.

Coverage includes:

- BMI metric calculation;
- BMI imperial/metric equivalence;
- adult reference boundary selection;
- evidence source metadata and review date;
- unit conversion precision;
- invalid/non-finite input rejection;
- waist-to-height calculation;
- deterministic property-style coverage over large valid input sets;
- `HealthMetricEngine` adult eligibility boundary;
- façade routing to the shared BMI and waist-to-height calculators.

Run:

```bash
gradle :shared:desktopTest
```

Compile browser targets:

```bash
gradle :shared:compileKotlinJs :shared:compileKotlinWasmJs
```

Compile Apple targets on macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

## Shared UI verification

`sharedUI` is primarily verified through compilation on every supported target plus native host builds.

Linux/Windows/macOS-capable local checks:

```bash
gradle :sharedUI:compileKotlinDesktop
gradle :sharedUI:compileKotlinJs
gradle :sharedUI:compileKotlinWasmJs
```

Apple framework checks on macOS:

```bash
gradle :sharedUI:linkDebugFrameworkIosSimulatorArm64
gradle :sharedUI:linkDebugFrameworkIosArm64
```

Current shared UI invariants:

- an age value below 18 never enters the adult calculator content;
- `HealthMetricEngine` independently repeats age eligibility;
- measurement values/results remain transient in desktop/web/iOS beta clients;
- neutral educational copy is used;
- no appearance scoring or body-target output is introduced.

Future behavior-heavy common UI should receive Compose Multiplatform UI tests where the target APIs are stable and the tests add value beyond host/integration coverage.

## Desktop verification

Compile:

```bash
gradle :desktopApp:compileKotlin
```

Run interactively:

```bash
gradle :desktopApp:run
```

Build the current OS native distribution:

```bash
gradle :desktopApp:packageDistributionForCurrentOS
```

CI should verify native packaging independently on Windows, macOS, and Linux so package configuration does not silently regress.

## Web verification

Production bundle checks:

```bash
gradle :webApp:jsBrowserProductionWebpack
gradle :webApp:wasmJsBrowserProductionWebpack
```

Interactive development servers:

```bash
gradle :webApp:jsBrowserDevelopmentRun
gradle :webApp:wasmJsBrowserDevelopmentRun
```

Cross-platform CI uploads both production bundles as artifacts. Release/manual browser review should cover at least current Chromium, Firefox, and Safari-compatible behavior where the selected Wasm/JS runtime supports it; the JavaScript build remains the compatibility fallback.

## iOS verification

Framework-only checks:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
gradle :sharedUI:linkDebugFrameworkIosSimulatorArm64 :sharedUI:linkDebugFrameworkIosArm64
```

Generated host check:

```bash
cd iosApp
xcodegen generate
cd ..
xcodebuild \
  -project iosApp/HealthMetricIOS.xcodeproj \
  -scheme HealthMetric \
  -configuration Debug \
  -sdk iphonesimulator \
  -destination 'generic/platform=iOS Simulator' \
  CODE_SIGNING_ALLOWED=NO \
  build
```

The Apple workflow performs these checks on macOS. Physical-device signing remains a protected/manual release concern.

## Android JVM unit checks

Located in `androidApp/src/test/`.

Coverage includes:

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

Most deterministic health calculations remain in `shared`; Android JVM tests intentionally target Android-module utilities and policy/presentation behavior rather than duplicate domain tests.

## Android instrumentation/UI tests

Located in `androidApp/src/androidTest/`.

Coverage includes:

- `OnboardingUiTest` — fresh-install adult-use notice and age-choice actions;
- `AdultGateUiTest` — under-18 dispatch and adult-reference unavailable screen;
- `CalculatorUiTest` — metric BMI success result and missing-weight validation;
- `WaistToHeightUiTest` — ratio success result and missing-waist validation;
- `SettingsUiTest` — explicit history opt-in, retention, file save, and share actions;
- `HistoryUiTest` — per-entry deletion and erase-all confirmation;
- `HealthMetricDataStoreTest` — privacy opt-in, retention trimming, export/restore, unsupported schemas, device-local consent/adult-gate preservation, entry delete/restore, malformed-record recovery, duplicate-ID handling, and invalid-entry rejection.

Run:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

`.github/workflows/android-instrumentation.yml` provisions an API 35 emulator for pull requests and `main` pushes.

## Stable Android automation tags

Critical controls use `HealthMetricTestTags` constants rather than brittle text-only selectors where a stable semantic hook improves reliability. User-visible semantics remain present for accessibility.

Tagged journeys include:

- BMI weight/height/calculate/result;
- waist/height-ratio inputs/calculate/result;
- history list;
- privacy history switch.

## Consent/safety regression invariants

Portable Android backup must not export or restore:

- `historyEnabled` / `history_enabled` consent state;
- adult-use confirmation;
- onboarding completion.

Legacy schema-v1 documents containing similarly named fields must not override the current installation's values.

The reusable cross-platform `HealthMetricEngine` must continue to reject age values below 18 before calculation routing.

## Required regression policy

Every confirmed defect should receive a regression test at the lowest practical layer before or with the fix.

Examples:

- calculation boundary defect → shared unit test;
- age façade defect → `HealthMetricEngineTest`;
- malformed Android backup crash → DataStore instrumentation test;
- backup size bypass → `BackupIoTest` plus restore test where relevant;
- consent/adult-gate restore regression → DataStore instrumentation test;
- locale parsing regression → `LocalizedNumbersTest`;
- Android screen-state regression → Compose UI test;
- desktop/web/iOS build integration regression → platform CI build plus unit test when logic is involved;
- accessibility label regression → semantics test/manual accessibility check.

## Validation edge cases

At minimum test:

- age 17 rejected and age 18 accepted at the adult façade boundary;
- exact lower/upper supported measurement boundaries;
- values immediately outside boundaries;
- zero/negative values;
- NaN and infinities at domain/persistence boundaries;
- imperial feet/inches normalization boundaries;
- reference band thresholds;
- comma/dot decimal presentation input;
- corrupted/unsupported Android backup schema;
- oversized backup payloads;
- malformed and duplicate history records;
- legacy backups containing non-portable consent/safety fields;
- empty/disabled history;
- retention-limit trimming;
- delete/restore behavior while history saving is disabled.

## Property/fuzz testing

The shared module uses seeded property-style loops for large valid input sets. Keep seeds deterministic so failures reproduce exactly in CI.

Backup parsing is bounded before JSON parsing and validates each history record independently. If the schema becomes more complex, add dedicated parser fuzz/property tooling rather than relying only on examples.

## Accessibility verification

Automated semantics checks are only one layer. Release candidates should also be reviewed with platform accessibility tools.

Android:

- TalkBack;
- large font/display scaling;
- dark/light/dynamic themes;
- keyboard/DPAD navigation where relevant;
- chart content descriptions;
- deletion/undo labels;
- restore/destructive confirmation dialogs;
- non-color-only status interpretation;
- dot/comma locale presentation.

Desktop/web/iOS beta clients:

- keyboard/focus navigation where available;
- screen-reader/VoiceOver semantics;
- large/dynamic type behavior;
- responsive window sizing;
- light/dark system behavior;
- neutral copy review.

See [`accessibility.md`](accessibility.md).

## CI quality gates

### `ci.yml`

Fails on:

- repository invariant failure;
- ktlint failure;
- shared JVM test failure;
- Android JVM unit failure;
- Android release lint failure;
- Android debug/release assembly failure.

### `cross-platform.yml`

Fails on:

- cross-platform formatting failure;
- shared JVM/domain test failure;
- shared JS/Wasm compilation failure;
- sharedUI JVM/JS/Wasm compilation failure;
- desktop application compilation failure;
- JS/Wasm production web build failure.

### `android-instrumentation.yml`

Fails on connected Android emulator regression failures.

### `apple-shared.yml`

Fails on:

- shared Apple compilation failures;
- sharedUI iOS framework link failures;
- XcodeGen generation failures;
- generated SwiftUI iOS simulator host build failures.

### Security workflows

- CodeQL;
- high-severity pull-request dependency review;
- repository-history secret scan.

## Complete local release-candidate checks

Unix-like systems:

```bash
bash scripts/verify.sh
```

Windows:

```powershell
.\scripts\verify.ps1
```

Then Android connected tests:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS also run Apple framework and Xcode-host checks described above.

Finally perform manual accessibility/device/browser checks and the release checklist in [`release.md`](release.md).
