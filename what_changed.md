# HealthMetric — Work Handoff

Last updated: 2026-08-20

## Current milestone

HealthMetric **2.0.12 cross-platform application support**.

Active branch:

```text
feature/full-cross-platform
```

Open pull request:

```text
PR #15 — feat: make HealthMetric cross-platform
```

Base branch:

```text
main
```

This continuation changes HealthMetric from an Android application with a reusable shared calculation core into a repository that contains buildable application clients for Android, iPhone/iPad, Windows, macOS, Linux, JavaScript browsers, and WebAssembly browsers.

## Platform matrix after this continuation

| Platform | Application client | Packaging / output | CI verification |
|---|---|---|---|
| Android | Jetpack Compose Android app | APK + AAB | Main CI + Android emulator instrumentation |
| iPhone / iPad | SwiftUI host + Compose Multiplatform calculator UI | Xcode app; protected Apple signing for production | Kotlin framework + Xcode simulator build on macOS |
| Windows | Compose Desktop | MSI | Windows runner |
| macOS | Compose Desktop | DMG | macOS runner |
| Linux | Compose Desktop | DEB | Ubuntu runner |
| WebAssembly web | Compose Multiplatform Wasm | Browser distribution | Ubuntu web job |
| JavaScript web | Compose Multiplatform JS | Browser distribution / fallback | Ubuntu web job |
| ChromeOS | Android client where Android applications are supported | APK/store install | Covered by Android build path |

## Feature-parity boundary

Cross-platform support does **not** falsely claim that every platform currently exposes every Android-only integration.

All first-class calculator clients provide:

- adult-use confirmation before adult BMI/waist reference calculators;
- metric BMI calculation;
- imperial BMI calculation;
- metric waist-to-height calculation;
- imperial waist-to-height calculation;
- the same Kotlin Multiplatform domain validation;
- the same deterministic calculation rules;
- neutral educational/non-diagnostic result presentation;
- local calculator execution without requiring a remote health API.

The Android client remains the most feature-complete platform-specific client and additionally provides:

- opt-in persistent calculation history;
- configurable retention of 50, 100, 250, or 500 records;
- Android DataStore persistence;
- per-entry deletion/undo;
- accessible history chart;
- JSON file/share backup and defensive restore;
- Android document-picker/share integrations;
- Android dynamic color and Android-specific theme behavior;
- Android settings/About/update integrations.

Those Android integrations were not duplicated unsafely into unrelated platforms merely to claim identical parity. Future shared persistence work should be implemented behind explicit cross-platform abstractions and tests.

## Toolchain changes

The current cross-platform toolchain is:

```text
Kotlin:                 2.4.10
Compose Multiplatform:  1.11.1
Android Gradle Plugin:  8.13.2
Gradle in CI:           8.13
JDK:                    17
Android compile SDK:    36
Android target SDK:     36
Android min SDK:        26
Android Build Tools:    35.0.0
```

Kotlin was upgraded from 2.2.20 to 2.4.10 during this continuation to keep the Compose Multiplatform 1.11.1 native/web configuration on a current compatible Kotlin toolchain.

Versions remain centralized in:

```text
gradle/libs.versions.toml
```

## Version alignment

Application package metadata is aligned to **2.0.12**:

- Android `versionName = "2.0.12"`;
- Android `versionCode = 2012`;
- Compose Desktop `packageVersion = "2.0.12"`;
- iOS `CFBundleShortVersionString = 2.0.12`;
- Xcode marketing version = 2.0.12.

## New `composeApp` module

Added:

```text
composeApp/
```

The module owns the shared application presentation for iOS/iPadOS, Windows, macOS, Linux, JavaScript web, and WebAssembly web.

Configured targets:

- `jvm("desktop")`;
- `iosArm64()`;
- `iosSimulatorArm64()`;
- JavaScript browser executable;
- WebAssembly JavaScript browser executable.

The module consumes:

```text
project(":shared")
```

so it does not reimplement BMI, waist-to-height, unit conversion, or validation logic.

### Shared Compose application

Added:

```text
composeApp/src/commonMain/kotlin/io/github/sanskarin/healthmetric/App.kt
```

The shared UI contains:

- adult-use gate;
- BMI / waist-to-height navigation;
- metric / imperial selection;
- validated measurement fields;
- domain-backed calculation actions;
- error presentation;
- neutral result cards;
- explicit educational/non-diagnostic wording;
- no appearance score or pressure-oriented body target.

The adult reference tools are explicitly intended for adults age 18 or older.

### Desktop entry point

Added:

```text
composeApp/src/desktopMain/kotlin/io/github/sanskarin/healthmetric/Main.kt
```

It creates the HealthMetric Compose Desktop application window and uses the common `App()` composable.

Desktop package formats configured in `composeApp/build.gradle.kts`:

- `TargetFormat.Msi`;
- `TargetFormat.Dmg`;
- `TargetFormat.Deb`.

### Browser entry point

Added:

```text
composeApp/src/webMain/kotlin/io/github/sanskarin/healthmetric/Main.kt
```

It uses `ComposeViewport { App() }` for the browser application.

Added browser resources:

```text
composeApp/src/webMain/resources/index.html
composeApp/src/webMain/resources/styles.css
```

The HTML includes viewport metadata and the CSS sizes the page/canvas for a full browser viewport rather than relying on an accidental default size.

### Apple bridge

Added:

```text
composeApp/src/iosMain/kotlin/io/github/sanskarin/healthmetric/HealthMetricViewControllerFactory.kt
```

The Apple target exports a static framework named:

```text
HealthMetricUI
```

The bridge returns a `ComposeUIViewController` containing the common HealthMetric application.

## Native iOS/iPadOS host

Added:

```text
iosApp/
```

Key files:

```text
iosApp/HealthMetricApp/HealthMetricApp.swift
iosApp/HealthMetricApp/ContentView.swift
iosApp/HealthMetricApp/Info.plist
iosApp/HealthMetric.xcodeproj/project.pbxproj
iosApp/HealthMetric.xcodeproj/xcshareddata/xcschemes/HealthMetric.xcscheme
```

The SwiftUI lifecycle remains native. `ContentView` embeds the Kotlin Compose controller through `UIViewControllerRepresentable`.

The Xcode project includes a build phase for:

```bash
gradle :composeApp:embedAndSignAppleFrameworkForXcode
```

The repository contains no Apple private signing material. Production device/App Store archives must use a protected Apple/Xcode signing workflow.

## Shared domain target expansion

`shared/build.gradle.kts` now exposes:

- Android;
- JVM desktop;
- iOS ARM64 device;
- iOS ARM64 simulator;
- JavaScript browser;
- WebAssembly browser.

The domain remains presentation-independent and continues to own:

- BMI arithmetic;
- adult reference profile/bands;
- waist-to-height arithmetic;
- metric/imperial conversions;
- finite/range validation;
- evidence/reference metadata;
- cross-platform tests.

## Cross-platform CI

Added:

```text
.github/workflows/cross-platform.yml
```

### Web job

Runs on Ubuntu and verifies:

- shared/Compose formatting;
- shared JVM tests;
- JavaScript production webpack;
- WebAssembly production webpack;
- compatibility browser distribution;
- browser artifact upload.

Important tasks:

```text
:composeApp:jsBrowserProductionWebpack
:composeApp:wasmJsBrowserProductionWebpack
:composeApp:composeCompatibilityBrowserDistribution
```

### Desktop matrix

Runs independently on:

```text
ubuntu-latest
windows-latest
macos-latest
```

Each runner executes:

```text
:composeApp:compileKotlinDesktop
:composeApp:packageDistributionForCurrentOS
```

and uploads the native package directory.

This prevents a false assumption that one operating system can authoritatively build all native installer formats.

### iOS job

Runs on macOS and verifies:

```text
:composeApp:linkDebugFrameworkIosSimulatorArm64
```

plus a signing-disabled Xcode simulator application build:

```text
xcodebuild -project iosApp/HealthMetric.xcodeproj -scheme HealthMetric -sdk iphonesimulator -configuration Debug CODE_SIGNING_ALLOWED=NO build
```

The pre-existing Apple shared-core workflow remains as an independent lower-level domain target gate.

## Multi-platform tagged release automation

`.github/workflows/release.yml` was expanded from an Android-only release job into a multi-platform release workflow.

A successful `v*` tag build prepares seven GitHub Release assets:

1. `HealthMetric-<tag>-android-unsigned.apk`;
2. `HealthMetric-<tag>-android-unsigned.aab`;
3. `HealthMetric-<tag>-windows.msi`;
4. `HealthMetric-<tag>-macos.dmg`;
5. `HealthMetric-<tag>-linux.deb`;
6. `HealthMetric-<tag>-web.zip`;
7. `HealthMetric-<tag>-ios-framework.zip`.

The iOS framework ZIP is intentionally a developer artifact, not a signed App Store IPA.

The final release job uses:

```text
actions/download-artifact@v8
```

to collect the per-platform artifacts and refuses publication if fewer than seven files are present.

## Local verification scripts

Updated:

```text
scripts/verify.sh
scripts/verify.ps1
```

They now cover:

- shared ktlint;
- Compose app ktlint;
- Android ktlint;
- shared JVM tests;
- desktop compilation;
- JavaScript production build;
- WebAssembly production build;
- browser compatibility distribution;
- Android JVM tests;
- Android release lint;
- Android debug APK;
- Android release APK;
- Android release AAB.

`verify.sh` additionally compiles the iOS simulator framework when it runs on macOS.

`GRADLE_BIN` can still override the Gradle executable.

## Repository integrity checks

`scripts/check_repository.py` now prevents cross-platform support from silently disappearing while documentation continues to claim it.

It checks:

- required cross-platform files;
- web resource files;
- Xcode host/project/scheme;
- `composeApp` inclusion;
- current Kotlin 2.4.10 / Compose Multiplatform 1.11.1 toolchain metadata;
- shared JS/Wasm/iOS targets;
- compose desktop/iOS/JS/Wasm targets;
- MSI/DMG/DEB packaging declarations;
- Android/iOS/desktop 2.0.12 version alignment;
- Windows/macOS/Linux CI runners;
- JS/Wasm/desktop/iOS CI tasks;
- all seven multi-platform release asset names;
- browser compatibility release build;
- iOS release framework build;
- `actions/download-artifact@v8` release aggregation;
- existing Android privacy/security invariants;
- required project/funding/contact/license metadata.

## Documentation work

Added:

```text
docs/cross-platform.md
```

It documents:

- platform matrix;
- feature-parity boundary;
- prerequisites;
- Android build commands;
- Windows run/MSI commands;
- macOS run/DMG commands;
- Linux run/DEB commands;
- Wasm development/production commands;
- JavaScript development/production commands;
- compatibility browser distribution;
- SwiftUI/Xcode architecture;
- iOS framework/Xcode build commands;
- shared target compilation;
- cross-platform CI;
- complete local verification recipes;
- troubleshooting;
- release checklist.

Updated to match the new platform architecture:

- `README.md`;
- `CHANGELOG.md`;
- `docs/setup.md`;
- `docs/architecture.md`;
- `docs/testing.md`;
- `docs/release.md`;
- `scripts/check_repository.py`;
- `scripts/verify.sh`;
- `scripts/verify.ps1`;
- this `what_changed.md`.

## Changelog

`CHANGELOG.md` now contains:

```text
## [2.0.12] - 2026-08-20
```

covering the shared Compose application, new native/web targets, platform CI, packaging, toolchain, documentation, and feature-parity note.

## Android behavior preserved

The cross-platform work intentionally preserves the mature Android privacy/data architecture.

Existing Android security/privacy invariants remain important:

- no required Internet permission for the offline calculator core;
- Android backup disabled;
- cleartext traffic disabled;
- opt-in history default;
- bounded retention;
- bounded backup payloads;
- explicit backup/restore actions;
- adult-use/onboarding/history-consent state excluded from portable restore authority;
- no production signing keys committed.

## Safety/product-language boundary

The new shared calculator UI preserves the adult-only product boundary and deliberately avoids body-image pressure.

Results are presented as educational screening information. They are not diagnoses, appearance ratings, personal body targets, or instructions to pursue a particular physique.

This is a required product invariant for future platform work.

## Verification status at this handoff commit

PR #15 is the cross-platform integration pull request.

This handoff update intentionally becomes a final documentation/invariant commit before the branch is frozen for CI inspection. GitHub may cancel older in-progress workflow runs when a newer branch commit supersedes them because the workflows use concurrency cancellation. Superseded/cancelled runs should not be interpreted as application test failures.

The authoritative verification status is the workflow group attached to the final `feature/full-cross-platform` head after this file is committed.

Required final checks include:

- CI;
- Cross-platform;
- Android instrumentation;
- Apple shared core;
- CodeQL;
- Dependency Review;
- Secret Scan.

Any actual failed final-head job must be inspected and fixed before merging. A queued or superseded job is not equivalent to a passing build.

## External distribution tasks intentionally not stored in source

The following remain protected/manual distribution concerns rather than missing source-code features:

1. Android production signing credentials.
2. Apple production certificates/provisioning/App Store signing.
3. macOS publisher signing/notarization where required for distribution.
4. Windows publisher signing where desired for distribution trust.
5. Web-host/CDN deployment credentials.
6. Physical-device and manual accessibility evidence.
7. Release screenshots captured from real clients/devices.

Private signing credentials must not be committed merely to make repository CI emit store-signed binaries.

## Exact continuation point

1. Treat this commit as the cross-platform branch freeze unless final-head CI finds a real defect.
2. Inspect the workflow group for the final PR #15 head.
3. If a job fails, inspect its job steps/logs and fix the root cause; then let the new final-head checks run.
4. When all required checks pass, merge PR #15 into `main`.
5. Confirm the merge is present on `main`.
6. Do **not** create or move a production `v2.0.12` tag until the exact intended release commit has passed the required release-candidate checks and the release action is explicitly desired.
