# HealthMetric cross-platform guide

This document is the source of truth for building, running, packaging, and verifying HealthMetric outside the Android-only workflow.

HealthMetric uses two application layers over one Kotlin Multiplatform health domain:

- `androidApp` is the mature Android application and includes Android-specific persistence, backup, theming, and platform integration.
- `composeApp` is the shared Compose Multiplatform calculator client used by iOS/iPadOS, Windows, macOS, Linux, JavaScript web, and WebAssembly web.
- `iosApp` is the small native SwiftUI/Xcode host that embeds the `composeApp` Apple framework.
- `shared` contains deterministic calculator, conversion, validation, and reference metadata code used by every client.

The platform clients must not duplicate BMI or waist-to-height formulas. Any change to those rules belongs in `shared` and requires shared tests.

## Platform matrix

| Target | User interface | Build target | Distribution |
|---|---|---|---|
| Android | Jetpack Compose | `androidApp` | APK / AAB |
| iPhone / iPad | Compose Multiplatform in SwiftUI/UIKit host | `composeApp` + `iosApp` | Xcode archive / IPA after Apple signing |
| Windows | Compose Desktop | `composeApp` desktop JVM | MSI |
| macOS | Compose Desktop | `composeApp` desktop JVM | DMG |
| Linux | Compose Desktop | `composeApp` desktop JVM | DEB |
| WebAssembly web | Compose Multiplatform | `wasmJs` | static browser distribution |
| JavaScript web | Compose Multiplatform | `js` | static browser distribution |
| ChromeOS | Android app where Android applications are supported | `androidApp` | Play Store/APK |

## Current feature parity

All first-class clients provide:

- adult-use confirmation before the adult reference calculators;
- metric BMI calculation;
- imperial BMI calculation;
- metric waist-to-height calculation;
- imperial waist-to-height calculation;
- the same shared range validation and finite-number checks;
- the same adult BMI reference metadata;
- neutral, non-diagnostic result language;
- local calculation execution without requiring a remote API.

The Android application additionally provides platform-specific features that have not yet been generalized into the shared client:

- opt-in persistent calculation history;
- configurable history retention;
- Android DataStore persistence;
- JSON file/share backup and restore;
- Android dynamic color;
- Android document-picker/share integrations;
- Android-specific history chart and settings/about navigation.

This distinction is intentional: cross-platform support means every target has a buildable, runnable calculator application backed by the same safe domain, while Android-specific product integrations stay isolated until equivalent platform abstractions are implemented. Do not claim identical feature parity where it does not yet exist.

## Repository layout

```text
HealthMetric/
├── androidApp/
│   └── src/main/...
├── composeApp/
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/kotlin/.../App.kt
│       ├── desktopMain/kotlin/.../Main.kt
│       ├── iosMain/kotlin/.../HealthMetricViewControllerFactory.kt
│       └── webMain/kotlin/.../Main.kt
├── iosApp/
│   ├── HealthMetric.xcodeproj/
│   └── HealthMetricApp/
├── shared/
│   ├── build.gradle.kts
│   ├── src/commonMain/...
│   └── src/commonTest/...
└── .github/workflows/cross-platform.yml
```

## Common prerequisites

All Gradle-based targets need:

1. Git.
2. JDK 17.
3. Gradle 8.13, or an IDE-compatible Gradle launcher matching the project.
4. Network access the first time Gradle resolves dependencies.

Clone the project:

```bash
git clone https://github.com/sanskarIN/healthmetric.git
cd healthmetric
```

Verify the shared health domain before platform work:

```bash
gradle :shared:desktopTest
```

Run formatting checks:

```bash
gradle :shared:ktlintCheck :composeApp:ktlintCheck :androidApp:ktlintCheck
```

## Android

### Requirements

- Android Studio.
- Android SDK Platform 36.
- Android Build Tools 35.0.0.
- JDK 17 configured for Gradle.

### Run from Android Studio

1. Open the repository root.
2. Allow Gradle sync to finish.
3. Select the `androidApp` run configuration.
4. Select an Android 8.0/API 26 or newer device/emulator.
5. Run the app.

### Debug APK

```bash
gradle :androidApp:assembleDebug
```

Output:

```text
androidApp/build/outputs/apk/debug/
```

### Unsigned release APK

```bash
gradle :androidApp:assembleRelease
```

Output:

```text
androidApp/build/outputs/apk/release/
```

### Unsigned Android App Bundle

```bash
gradle :androidApp:bundleRelease
```

Output:

```text
androidApp/build/outputs/bundle/release/
```

Never commit a production keystore or signing password. Store distribution signing belongs in a protected release environment.

## Windows desktop

### Requirements

- Windows 10 or newer suitable for the configured Compose Desktop runtime.
- JDK 17.
- Gradle 8.13.

### Run

From PowerShell, Command Prompt, Windows Terminal, or an IDE terminal:

```powershell
gradle :composeApp:run
```

### Compile only

```powershell
gradle :composeApp:compileKotlinDesktop
```

### Build MSI installer

```powershell
gradle :composeApp:packageDistributionForCurrentOS
```

The configured Windows distribution format is MSI. Generated desktop packages are placed below:

```text
composeApp/build/compose/binaries/
```

The packaging task is host-aware: build the MSI on Windows rather than trying to cross-package it on Linux or macOS.

## macOS desktop

### Requirements

- macOS.
- JDK 17.
- Gradle 8.13.

### Run

```bash
gradle :composeApp:run
```

### Compile only

```bash
gradle :composeApp:compileKotlinDesktop
```

### Build DMG

```bash
gradle :composeApp:packageDistributionForCurrentOS
```

Output root:

```text
composeApp/build/compose/binaries/
```

Production notarization/signing is separate from source code and requires the distributor's Apple credentials.

## Linux desktop

### Requirements

- A supported Linux desktop environment.
- JDK 17.
- Gradle 8.13.

### Run

```bash
gradle :composeApp:run
```

### Compile only

```bash
gradle :composeApp:compileKotlinDesktop
```

### Build DEB

```bash
gradle :composeApp:packageDistributionForCurrentOS
```

Output root:

```text
composeApp/build/compose/binaries/
```

Build native desktop packages on the operating system for which the package is intended.

## WebAssembly browser application

The Wasm client is the preferred modern browser path. It uses the same `App()` composable and shared Kotlin health domain.

### Development server

```bash
gradle :composeApp:wasmJsBrowserDevelopmentRun
```

The development task starts a local browser development server. Use the URL printed by Gradle rather than assuming a fixed port.

### Production bundle

```bash
gradle :composeApp:wasmJsBrowserProductionWebpack
```

Production browser artifacts are created beneath the `composeApp/build/` distribution directories.

### Compatibility considerations

WebAssembly execution depends on browser capabilities. HealthMetric therefore also builds a JavaScript target and a compatibility distribution. Keep both targets compiling in CI.

## JavaScript browser application

### Development server

```bash
gradle :composeApp:jsBrowserDevelopmentRun
```

### Production bundle

```bash
gradle :composeApp:jsBrowserProductionWebpack
```

### Combined compatibility distribution

```bash
gradle :composeApp:composeCompatibilityBrowserDistribution
```

This task is used in cross-platform CI so the repository verifies that the browser compatibility output can be assembled from the configured JS and Wasm targets.

### Static hosting

Production browser output can be hosted by a static site host. The application does not require a server-side calculation API because the shared calculator executes in the browser client.

When deploying, configure the static host to serve the generated files exactly as produced. Do not manually rewrite generated module file names.

## iPhone and iPad

Apple application builds require macOS and Xcode.

### Architecture

`composeApp` creates a static framework named `HealthMetricUI`. `iosApp` is a SwiftUI application that embeds the shared Compose view controller through `UIViewControllerRepresentable`.

This keeps the native application lifecycle in Xcode while sharing the calculator presentation and health logic.

### Open the project

Open:

```text
iosApp/HealthMetric.xcodeproj
```

Or from Terminal:

```bash
open iosApp/HealthMetric.xcodeproj
```

Use the shared `HealthMetric` scheme.

### Simulator build from Gradle

```bash
gradle :composeApp:linkDebugFrameworkIosSimulatorArm64
```

### Device framework build

On a compatible macOS/Xcode environment:

```bash
gradle :composeApp:linkReleaseFrameworkIosArm64
```

### Xcode build

Select an iPhone/iPad simulator and press Run in Xcode. The project has a build phase that invokes:

```bash
gradle :composeApp:embedAndSignAppleFrameworkForXcode
```

The task receives Xcode environment variables and places the framework where the Xcode target expects it.

### Command-line simulator verification

```bash
xcodebuild \
  -project iosApp/HealthMetric.xcodeproj \
  -scheme HealthMetric \
  -sdk iphonesimulator \
  -configuration Debug \
  CODE_SIGNING_ALLOWED=NO \
  build
```

CI uses the same kind of signing-disabled simulator build.

### App Store/device distribution

For a real device/archive:

1. Open the Xcode project.
2. Select your Apple development team.
3. Confirm the bundle identifier is appropriate for your account/distribution plan.
4. Create an Archive in Xcode.
5. Sign/distribute using Apple's normal protected signing workflow.

Do not commit certificates, provisioning profiles containing private material, signing keys, or passwords.

## Shared Kotlin targets

`shared/build.gradle.kts` exposes:

- Android;
- JVM desktop;
- iOS ARM64 device;
- iOS ARM64 simulator;
- JavaScript browser;
- WebAssembly JavaScript browser.

Representative verification commands:

```bash
gradle :shared:desktopTest
gradle :shared:compileKotlinDesktop
gradle :shared:compileKotlinJs
gradle :shared:compileKotlinWasmJs
```

Apple target compilation must run on macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64
gradle :shared:compileKotlinIosArm64
```

## Cross-platform CI

`.github/workflows/cross-platform.yml` has three job groups.

### Web job

Runs on Ubuntu and verifies:

- shared/Compose formatting;
- shared JVM tests;
- JS browser production webpack;
- Wasm browser production webpack;
- combined compatibility browser distribution;
- browser artifact upload.

### Desktop matrix

Runs on:

- `ubuntu-latest`;
- `windows-latest`;
- `macos-latest`.

Each runner:

1. compiles the desktop client;
2. runs `packageDistributionForCurrentOS`;
3. uploads the generated native desktop package.

This ensures platform packaging is verified on the host operating system instead of assuming one runner can cross-package all installers.

### iOS job

Runs on `macos-latest` and verifies:

1. the Kotlin iOS simulator framework;
2. the native Xcode application using a signing-disabled simulator build.

## Full local verification recipes

### Linux developer machine

```bash
gradle :shared:ktlintCheck :composeApp:ktlintCheck :androidApp:ktlintCheck
gradle :shared:desktopTest
gradle :composeApp:compileKotlinDesktop
gradle :composeApp:jsBrowserProductionWebpack
gradle :composeApp:wasmJsBrowserProductionWebpack
gradle :composeApp:composeCompatibilityBrowserDistribution
gradle :composeApp:packageDistributionForCurrentOS
```

If the Android SDK is installed, add:

```bash
gradle :androidApp:testDebugUnitTest :androidApp:lintRelease :androidApp:assembleDebug
```

### Windows developer machine

```powershell
gradle :shared:ktlintCheck :composeApp:ktlintCheck :androidApp:ktlintCheck
gradle :shared:desktopTest
gradle :composeApp:compileKotlinDesktop
gradle :composeApp:jsBrowserProductionWebpack
gradle :composeApp:wasmJsBrowserProductionWebpack
gradle :composeApp:packageDistributionForCurrentOS
```

### macOS developer machine

```bash
gradle :shared:ktlintCheck :composeApp:ktlintCheck :androidApp:ktlintCheck
gradle :shared:desktopTest
gradle :composeApp:compileKotlinDesktop
gradle :composeApp:jsBrowserProductionWebpack
gradle :composeApp:wasmJsBrowserProductionWebpack
gradle :composeApp:packageDistributionForCurrentOS
gradle :composeApp:linkDebugFrameworkIosSimulatorArm64
xcodebuild -project iosApp/HealthMetric.xcodeproj -scheme HealthMetric -sdk iphonesimulator -configuration Debug CODE_SIGNING_ALLOWED=NO build
```

## Troubleshooting

### `gradle` is not found

Install/configure Gradle 8.13 or use an IDE that provides a compatible Gradle launcher. This repository currently invokes `gradle` directly in scripts/CI rather than relying on a committed wrapper.

### JDK mismatch

Check:

```bash
java -version
gradle --version
```

Use JDK 17 for the configured project toolchain.

### Desktop packaging succeeds on one OS but not another

Native desktop packages are host-specific. Run `packageDistributionForCurrentOS` on the destination operating system. CI intentionally uses separate Windows/macOS/Linux runners.

### iOS framework cannot be imported

Confirm:

1. you opened `iosApp/HealthMetric.xcodeproj` on macOS;
2. Gradle is available in the Xcode build environment;
3. `gradle :composeApp:linkDebugFrameworkIosSimulatorArm64` succeeds;
4. the framework search path remains configured to `composeApp/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)`;
5. the Xcode build phase runs before Swift compilation/linking.

### Browser target fails in an older browser

Use the JavaScript/compatibility distribution instead of assuming every browser supports the Wasm path. Keep the Wasm and JS builds together during deployment validation.

### Android works but shared client lacks Android history/backup

This is a feature-parity distinction, not a calculator-domain inconsistency. Android persistence uses Android-specific APIs. Cross-platform persistent storage should be introduced through a shared persistence abstraction and platform implementations rather than copying Android DataStore code into unsupported targets.

## Adding a future platform feature

When adding a feature used by multiple clients:

1. put deterministic, platform-independent rules in `shared`;
2. add `commonTest` coverage;
3. put reusable Compose presentation in `composeApp/src/commonMain` when appropriate;
4. isolate true native integration in the relevant platform source set/host;
5. add a CI build/test for the affected target;
6. update this document and the platform matrix;
7. preserve the adult-only and neutral, non-diagnostic product language.

## Release checklist for cross-platform changes

Before claiming a platform supported in a release:

- [ ] shared tests pass;
- [ ] ktlint checks pass;
- [ ] Android CI remains green;
- [ ] Android instrumentation remains green for Android-affecting changes;
- [ ] JS production build passes;
- [ ] Wasm production build passes;
- [ ] compatibility browser distribution builds;
- [ ] Windows desktop package builds on Windows CI;
- [ ] macOS desktop package builds on macOS CI;
- [ ] Linux desktop package builds on Linux CI;
- [ ] iOS framework builds on macOS CI;
- [ ] Xcode iOS simulator application builds;
- [ ] security workflows remain green;
- [ ] production signing secrets remain outside source control;
- [ ] platform docs match the shipped implementation.
