# Setup Guide

HealthMetric 2.0.12 is a Kotlin Multiplatform project with an Android application, a shared Compose Multiplatform client for desktop/iOS/web, and a native SwiftUI iOS host.

For the full platform-by-platform command reference, also see [`cross-platform.md`](cross-platform.md).

## Toolchain

The repository currently pins:

- Kotlin and Compose compiler plugin: 2.4.10;
- Compose Multiplatform: 1.11.1;
- Android Gradle Plugin: 8.13.2;
- Gradle used by CI: 8.13;
- Java/JDK: 17;
- Android compile/target SDK: 36;
- Android minimum SDK: 26;
- Android Build Tools: 35.0.0.

## Common prerequisites

Install:

- Git;
- JDK 17;
- Gradle 8.13, or an IDE environment configured to invoke the project with a compatible Gradle version.

The repository does not require API keys, backend credentials, remote database credentials, or a runtime account for calculator functionality.

## Clone

```bash
git clone https://github.com/sanskarIN/healthmetric.git
cd healthmetric
```

Optional owner commit identity:

```bash
git config user.email "sanskarin@outlook.in"
```

Confirm the toolchain:

```bash
java -version
gradle --version
```

Verify the shared health domain first:

```bash
gradle :shared:desktopTest
```

## Android development

### Requirements

- Android Studio;
- Android SDK Platform 36;
- Android Build Tools 35.0.0;
- JDK 17 selected as the Gradle JDK.

A command-line SDK installation equivalent to CI is:

```bash
sdkmanager "platforms;android-36" "build-tools;35.0.0"
```

Accept the Android SDK licenses through Android Studio or the Android SDK command-line tools as required by your environment.

### Android Studio

1. Open the repository root.
2. Set the Gradle JDK to JDK 17.
3. Allow Gradle sync to resolve dependencies.
4. Select the `androidApp` run configuration.
5. Select an Android 8.0/API 26 or newer device/emulator.
6. Run the application.

### Android command line

Debug APK:

```bash
gradle :androidApp:assembleDebug
```

Unsigned release APK:

```bash
gradle :androidApp:assembleRelease
```

Unsigned release App Bundle:

```bash
gradle :androidApp:bundleRelease
```

Instrumentation with a connected device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

## Windows desktop

Requirements:

- Windows;
- JDK 17;
- Gradle 8.13.

Run:

```powershell
gradle :composeApp:run
```

Compile:

```powershell
gradle :composeApp:compileKotlinDesktop
```

Build the configured MSI distribution:

```powershell
gradle :composeApp:packageDistributionForCurrentOS
```

Native desktop packages are written below `composeApp/build/compose/binaries/`.

## macOS desktop

Requirements:

- macOS;
- JDK 17;
- Gradle 8.13.

Run:

```bash
gradle :composeApp:run
```

Build the configured DMG distribution:

```bash
gradle :composeApp:packageDistributionForCurrentOS
```

## Linux desktop

Requirements:

- Linux desktop environment;
- JDK 17;
- Gradle 8.13.

Run:

```bash
gradle :composeApp:run
```

Build the configured DEB distribution:

```bash
gradle :composeApp:packageDistributionForCurrentOS
```

Native desktop packaging is host-specific. Build Windows packages on Windows, macOS packages on macOS, and Linux packages on Linux.

## Web development

The same `composeApp` provides both JavaScript and WebAssembly browser targets.

### WebAssembly development server

```bash
gradle :composeApp:wasmJsBrowserDevelopmentRun
```

### JavaScript development server

```bash
gradle :composeApp:jsBrowserDevelopmentRun
```

### Production bundles

```bash
gradle :composeApp:wasmJsBrowserProductionWebpack
gradle :composeApp:jsBrowserProductionWebpack
gradle :composeApp:composeCompatibilityBrowserDistribution
```

The web source set contains an explicit `index.html` and viewport stylesheet under `composeApp/src/webMain/resources/`.

## iPhone and iPad development

Apple application builds require macOS and Xcode in addition to JDK 17 and Gradle.

Open:

```text
iosApp/HealthMetric.xcodeproj
```

Or:

```bash
open iosApp/HealthMetric.xcodeproj
```

Select the shared `HealthMetric` scheme and an iPhone/iPad simulator.

The iOS host is a SwiftUI application that embeds the static `HealthMetricUI` framework built from `composeApp`.

Compile the simulator framework directly:

```bash
gradle :composeApp:linkDebugFrameworkIosSimulatorArm64
```

The Xcode build phase invokes the Kotlin direct-integration task:

```bash
gradle :composeApp:embedAndSignAppleFrameworkForXcode
```

Command-line simulator verification:

```bash
xcodebuild -project iosApp/HealthMetric.xcodeproj -scheme HealthMetric -sdk iphonesimulator -configuration Debug CODE_SIGNING_ALLOWED=NO build
```

Real-device/App Store archives require the distributor's Apple development team and protected signing configuration. Never commit certificates, private keys, passwords, or other signing secrets.

## Shared-platform compilation

The `shared` health-domain module targets Android, JVM desktop, iOS ARM64 device, iOS ARM64 simulator, JavaScript, and WebAssembly.

Representative commands:

```bash
gradle :shared:desktopTest
gradle :shared:compileKotlinDesktop
gradle :shared:compileKotlinJs
gradle :shared:compileKotlinWasmJs
```

On macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64
gradle :shared:compileKotlinIosArm64
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

Both scripts use `gradle` by default. Override it with the `GRADLE_BIN` environment variable when necessary.

The verification scripts cover shared tests, formatting, desktop compilation, JS/Wasm production builds, browser compatibility output, Android tests/lint/build artifacts, and the iOS simulator framework when the Unix script is run on macOS.

## First run and safety behavior

Every calculator client first presents an adult-use notice. Adult BMI/waist reference calculators are intended for adults age 18 or older and are educational screening tools, not diagnoses, appearance scores, or body targets.

The mature Android client additionally keeps persistent local history disabled on a fresh installation until the user explicitly enables it in Settings.

## Environment configuration

HealthMetric currently has no required runtime secret configuration. `.env.example` documents that fact and establishes a safe pattern for any future configuration.

Do not add secrets to `.env.example` and do not commit local `.env` files.

## Backup/restore development note

Android backups contain portable history/settings only. Current history opt-in, adult-use confirmation, and onboarding state remain local to the installation and are not imported. See [`backup-format.md`](backup-format.md).

## Next steps

- Cross-platform commands: [`cross-platform.md`](cross-platform.md)
- Development workflow: [`development.md`](development.md)
- Architecture: [`architecture.md`](architecture.md)
- Backup schema: [`backup-format.md`](backup-format.md)
- Test matrix: [`testing.md`](testing.md)
- Release process: [`release.md`](release.md)
- Common failures: [`troubleshooting.md`](troubleshooting.md)
