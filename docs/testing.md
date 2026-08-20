# Testing Strategy

## Objectives

HealthMetric testing protects deterministic health calculations, adult-use safety boundaries, privacy-sensitive Android persistence, bounded backup handling, locale/presentation behavior, and buildability across every supported application target.

A platform is not considered supported merely because a Gradle target exists. Its application entry point must compile in CI, and native package/application builds must run on the appropriate host runner.

## Shared domain tests

Located in `shared/src/commonTest/`.

Coverage includes:

- metric BMI calculation;
- imperial/metric BMI equivalence;
- adult reference boundary selection;
- evidence source metadata/review date;
- unit conversion precision;
- invalid/non-finite input rejection;
- waist-to-height calculation;
- deterministic property-style coverage over generated valid inputs.

Run:

```bash
gradle :shared:desktopTest
```

Representative target compilation:

```bash
gradle :shared:compileKotlinDesktop
gradle :shared:compileKotlinJs
gradle :shared:compileKotlinWasmJs
```

On macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

## Shared Compose client verification

`composeApp` is the application client for iOS/iPadOS, Windows, macOS, Linux, JavaScript web, and WebAssembly web.

Formatting:

```bash
gradle :composeApp:ktlintCheck
```

Desktop compile:

```bash
gradle :composeApp:compileKotlinDesktop
```

JavaScript production build:

```bash
gradle :composeApp:jsBrowserProductionWebpack
```

WebAssembly production build:

```bash
gradle :composeApp:wasmJsBrowserProductionWebpack
```

Combined browser compatibility distribution:

```bash
gradle :composeApp:composeCompatibilityBrowserDistribution
```

The shared client must preserve these invariants on every target:

- adult-use notice appears before adult reference calculators;
- metric and imperial calculators invoke the shared domain;
- invalid values cannot bypass domain validation;
- result copy remains educational and non-diagnostic;
- presentation must not add appearance rankings or body-target pressure;
- no platform-specific client may reimplement calculator formulas independently.

## Desktop packaging verification

Desktop package generation is host-specific.

Run on the destination host:

```bash
gradle :composeApp:packageDistributionForCurrentOS
```

Expected configured formats:

- Windows: MSI;
- macOS: DMG;
- Linux: DEB.

`.github/workflows/cross-platform.yml` runs this task independently on Windows, macOS, and Ubuntu and uploads the resulting package directories.

## iOS/iPadOS verification

Compile the Kotlin simulator framework on macOS:

```bash
gradle :composeApp:linkDebugFrameworkIosSimulatorArm64
```

Verify the native SwiftUI/Xcode host:

```bash
xcodebuild \
  -project iosApp/HealthMetric.xcodeproj \
  -scheme HealthMetric \
  -sdk iphonesimulator \
  -configuration Debug \
  CODE_SIGNING_ALLOWED=NO \
  build
```

The cross-platform CI workflow runs both steps on `macos-latest`.

The separate Apple shared-core workflow remains useful because it verifies the lower-level `shared` Apple targets independently of the UI framework/Xcode host.

## Browser runtime verification

Compilation is the automated gate. Release candidates should also be opened in representative modern browsers.

Build:

```bash
gradle :composeApp:composeCompatibilityBrowserDistribution
```

Manual browser checks should cover:

- page loads without a blank canvas;
- viewport resizes correctly;
- adult-use gate remains usable at narrow/mobile widths;
- text fields accept expected decimal input;
- BMI and waist-to-height results match shared-domain test values;
- keyboard navigation remains usable;
- JavaScript fallback output remains deployable when the preferred Wasm path is unavailable.

## Android JVM unit checks

Located in `androidApp/src/test/`.

Coverage includes:

- privacy-first preference defaults;
- history-retention normalization;
- bounded UTF-8 backup read/write;
- oversized backup rejection;
- locale-aware decimal validation/parsing;
- locale-aware result formatting.

Run:

```bash
gradle :androidApp:testDebugUnitTest
```

Deterministic health calculations stay in `shared`; Android tests should not duplicate the domain suite.

## Android instrumentation/UI tests

Located in `androidApp/src/androidTest/`.

Coverage includes:

- onboarding/adult-use flow;
- under-18 adult-reference unavailable behavior;
- BMI success/error journeys;
- waist-to-height success/error journeys;
- explicit history opt-in;
- retention selection;
- file/share backup actions;
- per-entry history deletion/undo;
- erase-all confirmation;
- DataStore privacy defaults;
- retention trimming;
- backup export/restore;
- unsupported/oversized/malformed backup handling;
- portable backup consent/adult-gate boundaries;
- duplicate-ID protection.

Run with a connected Android device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

The Android instrumentation workflow provisions an API 35 emulator for pull requests and `main` pushes.

## Stable Android UI automation tags

Critical Android controls use constants in `HealthMetricTestTags` where a stable semantic hook improves reliability. Test tags supplement accessible user-facing semantics; they do not replace accessibility labels.

Tagged journeys include:

- BMI weight/height/calculate/result;
- waist/height inputs/calculate/result;
- history list;
- privacy history switch.

## Consent/safety regression invariants

Portable Android backup must never export or restore device-local choices:

- `historyEnabled` / `history_enabled`;
- adult-use confirmation;
- onboarding completion.

Legacy schema-v1 documents containing similarly named fields must not override the current installation's choices.

Across all calculator clients, adult reference screens must remain unavailable before adult-use confirmation.

## Required regression policy

Every confirmed defect should receive a regression check at the lowest practical layer before or with the fix.

Examples:

- calculator boundary defect → shared unit test;
- JS/Wasm compilation defect → cross-platform browser CI task;
- desktop packaging defect → affected host matrix job;
- iOS bridge/Xcode defect → simulator framework + Xcode CI build;
- malformed Android backup crash → DataStore instrumentation test;
- backup size bypass → `BackupIoTest` plus restore regression;
- consent/adult-gate restore regression → DataStore instrumentation test;
- locale parsing regression → `LocalizedNumbersTest`;
- Android screen state regression → Compose UI test;
- accessibility regression → semantics test plus manual platform review.

## Validation edge cases

At minimum test:

- exact lower/upper supported measurement boundaries;
- values immediately outside boundaries;
- zero/negative values;
- NaN/infinities at domain/persistence boundaries;
- imperial feet/inches boundaries;
- reference band thresholds;
- comma/dot decimal input where presentation supports it;
- corrupted/unsupported Android backup schema;
- oversized Android backup payloads;
- malformed/duplicate history records;
- legacy backups containing non-portable consent/safety fields;
- empty/disabled history;
- retention trimming;
- delete/restore behavior while future history saving is disabled.

## Accessibility verification

Automated checks are only one layer. Release candidates should be reviewed with platform-appropriate accessibility tools and input modes.

Android checks include TalkBack, large fonts, display scaling, themes, keyboard/DPAD use, chart descriptions, deletion labels/undo, confirmation dialogs, and locale formatting.

iOS/desktop/web release checks should also cover:

- screen-reader/VoiceOver semantics where supported;
- keyboard focus order;
- text scaling/window resizing;
- high-contrast/system theme behavior where applicable;
- touch targets on iPhone/iPad/mobile browser sizes;
- no color-only health interpretation.

See [`accessibility.md`](accessibility.md).

## CI quality gates

### Main CI

Fails on:

- repository invariant audit;
- internal Markdown-link audit;
- shared/Android formatting failures;
- shared JVM test failures;
- Android JVM unit-test failures;
- Android release-lint failures;
- Android debug/release assembly failures.

### Cross-platform CI

Fails on:

- shared/Compose formatting failures;
- shared JVM test failures;
- JS production build failure;
- Wasm production build failure;
- compatibility browser distribution failure;
- Windows/macOS/Linux desktop compilation or native packaging failure;
- Kotlin iOS simulator framework failure;
- Xcode iOS simulator application failure.

### Additional workflows

Also fail independently on:

- connected Android emulator failures;
- Apple shared-domain compilation failures;
- CodeQL findings/workflow errors;
- prohibited dependency-review findings;
- repository-history secret-scan findings.

## Local release-candidate verification

Unix-like systems:

```bash
bash scripts/verify.sh
```

Windows:

```powershell
.\scripts\verify.ps1
```

Then run Android instrumentation where the Android SDK/device is available:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS additionally run the Xcode host build:

```bash
xcodebuild -project iosApp/HealthMetric.xcodeproj -scheme HealthMetric -sdk iphonesimulator -configuration Debug CODE_SIGNING_ALLOWED=NO build
```

Finally perform manual accessibility/device/browser checks and the release checklist in [`release.md`](release.md).

For the full platform command matrix, see [`cross-platform.md`](cross-platform.md).
