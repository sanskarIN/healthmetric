# Setup Guide

## Prerequisites

### Android/JVM development

- Git
- JDK 17
- Gradle 8.13 or an Android Studio environment configured to use that Gradle version
- Android Studio
- Android SDK Platform 36
- Android SDK Build Tools 35.0.0

### Apple shared-core development

To compile the configured iOS targets locally, use macOS with Xcode and the Apple SDKs installed in addition to JDK 17 and Gradle 8.13.

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

## Android Studio

1. Open the repository root.
2. Set the Gradle JDK to JDK 17.
3. Install Android SDK Platform 36 if Android Studio prompts for it.
4. Install Build Tools 35.0.0.
5. Sync Gradle.
6. Select the `androidApp` run configuration.
7. Run on an Android 8.0 (API 26) or newer device/emulator.

## Command line

Confirm Java:

```bash
java -version
```

Confirm Gradle:

```bash
gradle --version
```

Run the shared test suite:

```bash
gradle :shared:desktopTest
```

Build Android debug:

```bash
gradle :androidApp:assembleDebug
```

Run the complete local non-device verification suite on Unix-like systems:

```bash
bash scripts/verify.sh
```

On Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

Both scripts use `gradle` by default. Override the executable with the `GRADLE_BIN` environment variable when necessary.

## Android SDK command-line packages

A CI-equivalent SDK installation is:

```bash
sdkmanager "platforms;android-36" "build-tools;35.0.0"
```

Accept local Android SDK licenses through Android Studio or the Android SDK tools as required by your environment.

## Android instrumentation

With an emulator or physical device connected:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

GitHub Actions also provisions an API 35 emulator for pull-request/main instrumentation coverage.

## Apple shared-core compilation

On macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64
gradle :shared:compileKotlinIosArm64
```

The `Apple shared core` GitHub Actions workflow runs both compilation tasks on macOS and reruns the shared JVM tests. HealthMetric does not currently ship an iOS UI application.

## Environment configuration

HealthMetric currently has no runtime secret configuration. `.env.example` exists to document that fact and establish a safe pattern for future configuration.

Do not add secrets to `.env.example` or commit local `.env` files.

## First run

The app first shows an adult-use notice. Adult BMI/waist reference calculators are unavailable unless the user confirms they are 18 or older. This gate does not collect a date of birth or identity document.

Local history is disabled on a fresh install until the user explicitly enables it in Settings.

## Backup/restore development note

Backups contain portable history/settings only. Current history opt-in, adult-use confirmation, and onboarding state remain local to the installation and are not imported. See [`backup-format.md`](backup-format.md).

## Offline behavior

Core calculator functionality is offline. No backend setup is needed. External GitHub, release, email, and funding links open only after explicit user interaction.

## Next steps

- Development workflow: [`development.md`](development.md)
- Backup schema: [`backup-format.md`](backup-format.md)
- Test matrix: [`testing.md`](testing.md)
- Common failures: [`troubleshooting.md`](troubleshooting.md)
