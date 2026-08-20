# Setup Guide

## Overview

HealthMetric is a multi-project Kotlin/Compose repository with four user-facing clients and two reusable modules:

- `androidApp` — full Android client;
- `desktopApp` — Windows/macOS/Linux Compose Desktop client;
- `webApp` — JavaScript and Wasm browser clients;
- `iosApp` — SwiftUI host generated with XcodeGen;
- `sharedUI` — reusable Compose Multiplatform calculator UI for desktop/web/iOS;
- `shared` — deterministic Kotlin Multiplatform health domain.

The repository does not require API keys, accounts, backend credentials, analytics credentials, or a local database server.

## Common prerequisites

Install:

- Git;
- JDK 17;
- Gradle 8.13 available as `gradle`;
- Android SDK Platform 36;
- Android SDK Build Tools 35.0.0.

The Android SDK is required even when you are working only on desktop/web because Gradle configures the Android targets in the same multi-project build.

Verify:

```bash
java -version
gradle --version
```

Expected major versions:

- Java/JDK: 17;
- Gradle: 8.13.

## Clone

```bash
git clone https://github.com/sanskarIN/healthmetric.git
cd healthmetric
```

Optional owner commit identity:

```bash
git config user.email "sanskarin@outlook.in"
```

## Android development

### Requirements

- Android Studio;
- Android SDK Platform 36;
- Android SDK Build Tools 35.0.0;
- Android 8.0 / API 26 or newer device/emulator for running the app.

### Android Studio

1. Open the repository root.
2. Set the Gradle JDK to JDK 17.
3. Install Android SDK Platform 36 when prompted.
4. Install Build Tools 35.0.0.
5. Sync Gradle.
6. Select the `androidApp` run configuration.
7. Run on an API 26+ device/emulator.

### Android command line

```bash
gradle :androidApp:assembleDebug
```

Debug APK output:

```text
androidApp/build/outputs/apk/debug/
```

Connected tests:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

A CI-equivalent SDK package install is:

```bash
sdkmanager "platforms;android-36" "build-tools;35.0.0"
```

Accept Android SDK licenses through Android Studio or the Android SDK command-line tools as required by your environment.

## Desktop development

The desktop client uses Compose Desktop/JVM and the reusable `sharedUI` module.

Run:

```bash
gradle :desktopApp:run
```

Compile only:

```bash
gradle :desktopApp:compileKotlin
```

Build the installer/distribution supported by the current OS:

```bash
gradle :desktopApp:packageDistributionForCurrentOS
```

Configured native formats:

- Windows: MSI;
- macOS: DMG;
- Linux: DEB.

Native installers should be produced on their corresponding host operating systems.

## Web development

The web client is available as both Wasm and JavaScript executables.

### Wasm development server

```bash
gradle :webApp:wasmJsBrowserDevelopmentRun
```

### JavaScript development server

```bash
gradle :webApp:jsBrowserDevelopmentRun
```

### Production bundles

```bash
gradle :webApp:wasmJsBrowserProductionWebpack
gradle :webApp:jsBrowserProductionWebpack
```

The web client uses the same `sharedUI` and `HealthMetricEngine` logic as the desktop/iOS clients. The calculator does not require a HealthMetric backend.

## iOS / iPadOS development

### Requirements

Use macOS with:

- Xcode and iOS SDKs;
- JDK 17;
- Gradle 8.13;
- XcodeGen.

Install XcodeGen with Homebrew:

```bash
brew install xcodegen
```

Generate the project:

```bash
cd iosApp
xcodegen generate
open HealthMetricIOS.xcodeproj
```

In Xcode:

1. Select the `HealthMetric` scheme.
2. Choose an iPhone/iPad simulator or configured device.
3. Configure your signing team only when a physical-device/archive build requires signing.
4. Build/run normally.

The generated Xcode project is intentionally not committed. `iosApp/project.yml` is the reviewable source of truth.

Before Swift compilation, the project invokes:

```bash
gradle :sharedUI:embedAndSignAppleFrameworkForXcode
```

The build script prefers `./gradlew` automatically if a verified wrapper is added to the repository in the future. Today the documented fallback is Gradle 8.13 from `PATH`.

### Apple target verification without Xcode UI

```bash
gradle :shared:compileKotlinIosSimulatorArm64
gradle :shared:compileKotlinIosArm64
gradle :sharedUI:linkDebugFrameworkIosSimulatorArm64
gradle :sharedUI:linkDebugFrameworkIosArm64
```

CI also runs `xcodegen generate` and an unsigned iOS simulator build.

## Shared domain verification

Run deterministic shared tests:

```bash
gradle :shared:desktopTest
```

Compile the pure domain for browser targets:

```bash
gradle :shared:compileKotlinJs :shared:compileKotlinWasmJs
```

## Complete local verification

Unix-like systems:

```bash
bash scripts/verify.sh
```

Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

These scripts run formatting, shared tests, shared browser compilation, shared UI compilation, desktop compilation, web production bundling, Android unit tests, Android release lint, and Android debug/release assembly.

Both scripts use `gradle` by default. Override the executable when necessary:

```bash
GRADLE_BIN=/path/to/gradle bash scripts/verify.sh
```

PowerShell:

```powershell
$env:GRADLE_BIN = "C:\path\to\gradle.bat"
.\scripts\verify.ps1
```

## First run and adult-use boundary

HealthMetric's adult BMI and waist-to-height reference calculators are intended for adults age 18 or older.

- Android uses its first-run adult-use/onboarding flow.
- Desktop, web, and iOS use the shared `sharedUI` age gate.
- The shared `HealthMetricEngine` repeats the age eligibility check so a client cannot bypass the domain boundary simply by skipping the UI gate.

This gate does not require a date of birth, identity document, or account.

## Local data behavior

### Android

History is disabled on a fresh install until explicitly enabled in Settings. Portable backup cannot change the current installation's history opt-in, adult-use confirmation, or onboarding state.

See [`backup-format.md`](backup-format.md).

### Desktop / web / iOS

The current beta clients do not persist history. Form measurements and calculation results remain transient UI state.

## Offline and network behavior

- Shared calculation logic requires no network.
- Android requests no Internet permission for its core application.
- Desktop/iOS clients add no application backend, telemetry, or cloud synchronization.
- The web application must naturally be delivered to a browser by a hosting environment, but calculations do not require a HealthMetric server API.
- External GitHub/support/funding links are Android user-initiated actions.

## Environment configuration

HealthMetric currently has no runtime secret configuration. `.env.example` exists to document that fact and establish a safe pattern for future configuration.

Do not add secrets to `.env.example` or commit local `.env`, signing, provisioning, or keystore files.

## Next steps

- Development workflow: [`development.md`](development.md)
- Architecture: [`architecture.md`](architecture.md)
- Backup schema: [`backup-format.md`](backup-format.md)
- Test matrix: [`testing.md`](testing.md)
- Release process: [`release.md`](release.md)
- Common failures: [`troubleshooting.md`](troubleshooting.md)
