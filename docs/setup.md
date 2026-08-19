# Setup Guide

## Prerequisites

- Git
- JDK 17
- Gradle 8.13 or an Android Studio environment configured to use that Gradle version
- Android Studio
- Android SDK Platform 36
- Android SDK Build Tools 35.0.0

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

## Android SDK command-line packages

A CI-equivalent SDK installation is:

```bash
sdkmanager "platforms;android-36" "build-tools;35.0.0"
```

Accept local Android SDK licenses through Android Studio or the Android SDK tools as required by your environment.

## Environment configuration

HealthMetric currently has no runtime secret configuration. `.env.example` exists to document that fact and establish a safe pattern for future configuration.

Do not add secrets to `.env.example` or commit local `.env` files.

## First run

The app first shows an adult-use notice. Adult BMI/waist reference calculators are unavailable unless the user confirms they are 18 or older. This gate does not collect a date of birth or identity document.

## Offline behavior

Core calculator functionality is offline. No backend setup is needed. External GitHub, email, and funding links open only after explicit user interaction.

## Next steps

- Development workflow: [`development.md`](development.md)
- Test matrix: [`testing.md`](testing.md)
- Common failures: [`troubleshooting.md`](troubleshooting.md)
