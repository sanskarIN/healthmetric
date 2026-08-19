# Setup Guide

## Prerequisites

### Repository tooling

- Git
- Python 3 for repository, Markdown-link, and release-tooling checks

Python uses only the standard library for the repository scripts currently committed, so no Python virtual environment or `pip install` step is required.

### Shared/JVM/Desktop development

- JDK 17
- Gradle 8.13 or an IDE-managed Gradle environment using the compatible build
- IntelliJ IDEA or Android Studio is recommended

### Android development

In addition to the repository/shared/JVM requirements:

- Android Studio
- Android SDK Platform 36
- Android SDK Build Tools 35.0.0
- Android platform tools/device or emulator when running connected tests

### Apple shared-core development

To compile the configured iOS targets locally, use macOS with Xcode and Apple SDKs installed in addition to JDK 17, Gradle 8.13, Git and Python 3.

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

Do not rewrite another contributor's identity metadata.

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

Use [`repository-file-reference.md`](repository-file-reference.md) for the exact responsibility of every tracked file and [`documentation-map.md`](documentation-map.md) for canonical documentation ownership.

## Verify the toolchain

Confirm Python:

```bash
python3 --version
```

On Windows, the executable may be `python` instead of `python3`.

Confirm Java:

```bash
java -version
```

Confirm Gradle:

```bash
gradle --version
```

The expected Java runtime for Gradle is JDK 17. The repository verification scripts use `gradle` by default rather than assuming a committed Gradle wrapper.

## Android first run

1. Select/create the `androidApp` run configuration.
2. Run on Android 8.0 (API 26) or newer.
3. The first screen presents the adult-use notice.
4. Adult reference calculators remain unavailable unless the adult option is selected.
5. Local history is disabled until explicitly enabled in Settings.

If the under-18 option is selected accidentally, the blocked adult-reference screen provides a return-to-age-selection action; it does not require clearing unrelated local history/preferences.

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

Desktop imperial height inputs use whole feet plus **remaining inches**; the remaining-inch component must be in `[0, 12)`.

See [`desktop.md`](desktop.md) for desktop-specific architecture, privacy, packaging, and accessibility requirements.

## Repository/documentation checks

Run the portable repository checks independently with:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
python3 -m unittest discover -s scripts/tests -p "test_*.py"
```

`check_repository.py` includes exhaustive file-documentation verification: every path returned by `git ls-files` must be documented in `docs/repository-file-reference.md`.

If a newly tracked file causes this check to fail, document the file's responsibility rather than weakening or bypassing the invariant.

## Command-line verification

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

The complete scripts run repository/docs checks, release-tooling Python tests, Kotlin formatting/tests, desktop JAR packaging, Android unit/lint checks, debug/release APK assembly, and release AAB assembly.

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

GitHub Actions provisions an API 35 emulator for pull-request/main instrumentation coverage. That workflow also generates the eight-file real-app Android release screenshot artifact using fictional/example values.

## Desktop multi-platform verification

The repository's `Desktop` workflow runs the desktop formatting/test/JAR/native packaging sequence independently on:

- Ubuntu/Linux → runnable JAR + DEB;
- Windows → runnable JAR + MSI;
- macOS → runnable JAR + DMG.

Local contributors should at minimum verify the current operating system with:

```bash
gradle :desktopApp:ktlintCheck :desktopApp:test :desktopApp:packageUberJarForCurrentOS
```

Native package commands are host-specific:

```bash
# Linux
gradle :desktopApp:packageDeb

# Windows
gradle :desktopApp:packageMsi

# macOS
gradle :desktopApp:packageDmg
```

Before publishing a desktop artifact, manually launch/review the runnable JAR and matching native package on that operating system even if CI is green.

## Apple shared-core compilation

On macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64
gradle :shared:compileKotlinIosArm64
```

The `Apple shared core` GitHub Actions workflow runs both compilation tasks on macOS and reruns shared JVM tests. HealthMetric does not currently ship an iOS UI application.

## Environment configuration

HealthMetric currently has no runtime secret configuration. `.env.example` exists to document that fact and establish a safe pattern for future configuration.

Do not add secrets to `.env.example` or commit local `.env` files. Signing keys, passwords, certificates and notarization credentials must remain outside source control.

## Android backup/restore development note

Android backups contain portable history/settings only. Current history opt-in, adult-use confirmation, and onboarding state remain local to the Android installation and are not imported.

Restore schema v1 requires `history` to be a JSON array. `history: []` is an intentional empty-history backup, while a non-empty array from which no valid record survives is rejected before DataStore mutation.

See [`backup-format.md`](backup-format.md).

The desktop client currently has no backup/import/export feature because it does not persist HealthMetric measurement state.

## Offline behavior

Core calculator functionality is offline and requires no backend setup.

Android has no Internet permission for its application core. Desktop evidence/repository/funding links and Android external links are invoked only after explicit user actions.

## Next steps

- Documentation map: [`documentation-map.md`](documentation-map.md)
- Complete file reference: [`repository-file-reference.md`](repository-file-reference.md)
- Development workflow: [`development.md`](development.md)
- Desktop guide: [`desktop.md`](desktop.md)
- Backup schema: [`backup-format.md`](backup-format.md)
- Test matrix: [`testing.md`](testing.md)
- Release process: [`release.md`](release.md)
- Common failures: [`troubleshooting.md`](troubleshooting.md)
