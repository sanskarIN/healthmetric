# Setup Guide

## Prerequisites

### Shared/JVM/Desktop development

- Git
- JDK 17
- Gradle 8.13 or an IDE-managed Gradle environment using the compatible build
- IntelliJ IDEA or Android Studio is recommended

### Android development

In addition to the shared/JVM requirements:

- Android Studio
- Android SDK Platform 36
- Android SDK Build Tools 35.0.0
- Android platform tools/device or emulator when running connected tests

### Apple shared-core development

To compile the configured iOS targets locally, use macOS with Xcode and Apple SDKs installed in addition to JDK 17 and Gradle 8.13.

The repository does not require API keys, accounts, backend credentials, or a local database server.

## Clone

```bash
git clone https://github.com/sanskarIN/healthmetric.git
cd healthmetric
```

Optional owner commit identity:

```bash
git config user.email "sanskarin@outlook.in"
```

## IDE setup

1. Open the repository root in Android Studio or IntelliJ IDEA.
2. Set the Gradle JDK to JDK 17.
3. Allow Gradle to resolve plugins/dependencies.
4. For Android development, install Android SDK Platform 36 and Build Tools 35.0.0.
5. Sync the Gradle project.

The repository contains three primary Gradle modules:

- `shared` — Kotlin Multiplatform domain core;
- `androidApp` — Android Jetpack Compose application;
- `desktopApp` — Compose Multiplatform JVM desktop application.

## Android first run

1. Select/create the `androidApp` run configuration.
2. Run on Android 8.0 (API 26) or newer.
3. The first screen presents the adult-use notice.
4. Adult reference calculators remain unavailable unless the adult option is selected.
5. Local history is disabled until explicitly enabled in Settings.

## Desktop first run

Run:

```bash
gradle :desktopApp:run
```

The desktop client:

- opens an adult-use selection screen;
- exposes adult calculators only after the adult option is selected;
- shows an unavailable path after the under-18 option;
- stores no measurement history or preferences to disk;
- clears its UI state when the process closes.

See [`desktop.md`](desktop.md) for desktop-specific architecture, privacy, packaging, and accessibility requirements.

## Command-line verification

Confirm Java:

```bash
java -version
```

Confirm Gradle:

```bash
gradle --version
```

Shared tests:

```bash
gradle :shared:desktopTest
```

Desktop tests and runnable JAR:

```bash
gradle :desktopApp:ktlintCheck :desktopApp:test
gradle :desktopApp:packageUberJarForCurrentOS
```

Build Android debug:

```bash
gradle :androidApp:assembleDebug
```

Run the complete non-device verification suite on Unix-like systems:

```bash
bash scripts/verify.sh
```

On Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

Both scripts use `gradle` by default. Override with `GRADLE_BIN` when necessary. The PowerShell script also supports `PYTHON_BIN` when the Python executable is named differently.

## Android SDK command-line packages

A CI-equivalent SDK installation is:

```bash
sdkmanager "platforms;android-36" "build-tools;35.0.0"
```

Accept local Android SDK licenses through Android Studio or the Android SDK tools as required by the environment.

Because Android modules participate in the root Gradle build, installing the Android SDK is recommended even for contributors primarily working on desktop when running the complete repository verification script.

## Android instrumentation

With an emulator or physical device connected:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

GitHub Actions provisions an API 35 emulator for pull-request/main instrumentation coverage.

## Desktop multi-platform verification

The repository's `Desktop` workflow runs the desktop formatting/test/JAR packaging sequence independently on:

- Ubuntu/Linux;
- Windows;
- macOS.

Local contributors should at minimum verify the current operating system with:

```bash
gradle :desktopApp:ktlintCheck :desktopApp:test :desktopApp:packageUberJarForCurrentOS
```

Before publishing a desktop artifact, manually launch and review it on that operating system even if CI is green.

## Apple shared-core compilation

On macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64
gradle :shared:compileKotlinIosArm64
```

The `Apple shared core` GitHub Actions workflow runs both compilation tasks on macOS and reruns shared JVM tests. HealthMetric does not currently ship an iOS UI application.

## Environment configuration

HealthMetric currently has no runtime secret configuration. `.env.example` exists to document that fact and establish a safe pattern for future configuration.

Do not add secrets to `.env.example` or commit local `.env` files.

## Android backup/restore development note

Android backups contain portable history/settings only. Current history opt-in, adult-use confirmation, and onboarding state remain local to the Android installation and are not imported.

See [`backup-format.md`](backup-format.md).

The desktop client currently has no backup/import/export feature because it does not persist HealthMetric measurement state.

## Offline behavior

Core calculator functionality is offline and requires no backend setup.

Android has no Internet permission for its application core. Desktop evidence/repository/funding links and Android external links are invoked only after explicit user actions.

## Next steps

- Development workflow: [`development.md`](development.md)
- Desktop guide: [`desktop.md`](desktop.md)
- Backup schema: [`backup-format.md`](backup-format.md)
- Test matrix: [`testing.md`](testing.md)
- Common failures: [`troubleshooting.md`](troubleshooting.md)
