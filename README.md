<p align="center">
  <img src="docs/assets/logo.svg" alt="HealthMetric" width="720" />
</p>

# HealthMetric

[![CI](https://github.com/sanskarIN/healthmetric/actions/workflows/ci.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/ci.yml)
[![Android instrumentation](https://github.com/sanskarIN/healthmetric/actions/workflows/android-instrumentation.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/android-instrumentation.yml)
[![Desktop](https://github.com/sanskarIN/healthmetric/actions/workflows/desktop.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/desktop.yml)
[![Apple shared core](https://github.com/sanskarIN/healthmetric/actions/workflows/apple-shared.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/apple-shared.yml)
[![CodeQL](https://github.com/sanskarIN/healthmetric/actions/workflows/codeql.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/codeql.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-sanskarIN-FFDD00?logo=buy-me-a-coffee&logoColor=000000)](https://buymeacoffee.com/sanskarIN)

**HealthMetric** is an open-source, privacy-first **adult BMI and health measurement calculator** with Android and desktop clients backed by one Kotlin Multiplatform calculation core.

> [!IMPORTANT]
> HealthMetric's BMI and waist-to-height tools are intended for adults age 18 or older. Results are educational screening information only. They are not medical diagnoses, appearance scores, or personal body targets.

**Made by the Sanskar**

## Why HealthMetric?

Health calculators are often tiny demos. HealthMetric is designed as a portfolio-quality product with a shared domain layer, strict validation, explicit adult-use boundaries, privacy-first storage behavior, accessible neutral presentation, automated multi-platform verification, release artifacts, security analysis, and complete project documentation.

The Android client adds opt-in bounded local history and explicit backup/restore controls. The desktop client intentionally keeps measurements and results in memory only and discards them when the app closes.

## Features

### Shared calculation core

- Adult BMI calculation in metric and imperial units.
- Configurable, versioned adult BMI reference metadata with source review information.
- Adult waist-to-height ratio calculation presented without appearance rankings or pressure-oriented goals.
- Strict finite-number and plausible-range validation.
- Accurate metric/imperial conversion helpers.
- Kotlin Multiplatform targets for Android, JVM/Desktop, iOS device, and iOS simulator.
- Shared unit, boundary, conversion, evidence, and deterministic property-style tests.

### Android client

- Adult-only onboarding gate before reference calculators become available.
- Privacy-first offline operation; no advertising SDKs or ad trackers.
- Optional local calculation history that is **disabled by default** and requires explicit opt-in.
- User-selectable local history retention limits of 50, 100, 250, or 500 results.
- Per-entry history deletion with an immediate undo action.
- Accessible measurement history chart with a screen-reader summary and no color-based health meaning.
- Confirmation before destructive erase-all, delete-all-data, and restore operations.
- JSON backup saved directly through Android's document picker or explicitly shared to another app.
- JSON restore with schema validation, bounded file size, bounded history, malformed-record recovery, and duplicate-ID protection.
- Portable backups cannot change the current device's history opt-in, adult-use confirmation, or onboarding safety state.
- Locale-aware decimal input accepting the user's decimal separator plus dot/comma fallback.
- Locale-aware result/history formatting.
- Full local-data deletion that returns the app to first-run privacy defaults.
- Light, dark, and system theme modes with Android dynamic color where supported.
- Branded splash screen, adaptive launcher icons, round icons, and Android 13+ themed icon support.
- Shared typography, shape, spacing, elevation, and motion design tokens.
- Android UI copy externalized to resources for localization readiness.
- About screen with license, version, GitHub, support contacts, funding link, and project credit.
- Settings update section linking to public GitHub releases.

### Desktop client

- Compose Multiplatform desktop UI for Windows, macOS, and Linux JVM environments.
- Explicit adult-use gate before calculator access.
- Separate under-18 unavailable path with no adult reference results.
- Metric and imperial adult BMI forms.
- Metric and imperial waist-to-height forms.
- Dot/comma decimal input support.
- Shared-domain validation and reference wording rather than duplicated calculation rules.
- Light/dark theme switch for the current session.
- Evidence, project, support, and funding information.
- Explicit external-link buttons only.
- No persistent measurement history, result storage, adult-use state, or theme state.
- Cross-platform desktop formatting, test, and runnable-JAR packaging workflow.

### Quality and automation

- Kotlin formatting checks across shared, Android, and desktop code.
- Shared JVM tests, Android JVM tests, and desktop JVM tests.
- Android release lint.
- Android emulator instrumentation tests.
- Apple shared-core compilation on macOS.
- Desktop verification on Linux, Windows, and macOS.
- Debug APK, unsigned release APK, unsigned release AAB, and desktop runnable-JAR artifacts.
- CodeQL, dependency review, full-history secret scanning, and Dependabot automation.
- Repository invariant and internal Markdown-link audits.

## Supported platforms

| Platform | Status | Notes |
|---|---|---|
| Android | Primary | Jetpack Compose app, min SDK 26, target/compile SDK 36 |
| Windows desktop | Implemented | Compose Multiplatform JVM client; runnable JAR verified in desktop CI |
| macOS desktop | Implemented | Compose Multiplatform JVM client; runnable JAR verified in desktop CI |
| Linux desktop | Implemented | Compose Multiplatform JVM client; runnable JAR verified in desktop CI |
| iOS shared core | Configured | Kotlin Multiplatform `iosArm64` and `iosSimulatorArm64` targets; macOS CI compiles both |

The repository does not currently ship an iOS user interface. The iOS targets validate the shared calculation core for a future Apple client.

## Tech stack

- Kotlin 2.2.20
- Kotlin Multiplatform shared domain module
- Android Gradle Plugin 8.13.2
- Gradle 8.13 in CI
- Jetpack Compose UI 1.9.2
- Android Material 3 1.4.0
- Compose Multiplatform 1.9.1 for desktop
- AndroidX Core SplashScreen 1.2.0
- AndroidX DataStore Preferences 1.2.1
- AndroidX Lifecycle 2.9.4
- kotlinx.coroutines 1.10.2
- ktlint Gradle plugin 14.2.0
- JUnit, Kotlin test, Compose UI testing, Android emulator instrumentation, desktop CI, and macOS Apple-target compilation

Versions are centrally pinned in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Screenshots / demo

Real publication screenshots remain a release-candidate task. Capture guidance and the required Android screenshot set are tracked in [`docs/assets/screenshots/README.md`](docs/assets/screenshots/README.md).

Planned Android captures:

1. Adult-use onboarding notice.
2. Metric BMI calculator and neutral educational result.
3. Waist-to-height calculator.
4. Local history with accessible chart and entry controls.
5. Privacy/data settings with retention and backup options.
6. Restore confirmation.
7. About and support screen.
8. Representative dark-theme screen.

Desktop release screenshots should additionally show the adult-use gate, one calculator view, and the About & evidence view using fictional/example values only.

## Quick start

### Requirements

- JDK 17 or newer supported by the configured Gradle/Kotlin toolchain.
- Gradle 8.13 if you are not using an IDE-managed Gradle installation.
- Android Studio with Android SDK Platform 36 and Build Tools 35.0.0 for Android work.

### Clone and verify the shared core

```bash
git clone https://github.com/sanskarIN/healthmetric.git
cd healthmetric
gradle :shared:desktopTest
```

### Build the Android debug APK

```bash
gradle :androidApp:assembleDebug
```

The APK is written under `androidApp/build/outputs/apk/debug/`.

### Run the desktop client

```bash
gradle :desktopApp:run
```

### Build a desktop runnable JAR

```bash
gradle :desktopApp:packageUberJarForCurrentOS
```

Desktop Compose JARs are written under `desktopApp/build/compose/jars/`.

For platform setup details, see [`docs/setup.md`](docs/setup.md) and [`docs/desktop.md`](docs/desktop.md).

## Development setup

1. Clone the repository.
2. Open the repository root in Android Studio or IntelliJ IDEA.
3. Use JDK 17 for Gradle.
4. Install Android SDK Platform 36 and Build Tools 35.0.0 when developing or verifying Android modules.
5. Allow Gradle sync to download dependencies.
6. Run the `androidApp` configuration on an Android 8.0+ device/emulator for Android work.
7. Run `gradle :desktopApp:run` for the desktop client.

Full environment and troubleshooting notes:

- [`docs/setup.md`](docs/setup.md)
- [`docs/development.md`](docs/development.md)
- [`docs/desktop.md`](docs/desktop.md)
- [`docs/troubleshooting.md`](docs/troubleshooting.md)

## Testing and verification

Run the repository verification script:

```bash
bash scripts/verify.sh
```

Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

Equivalent major tasks include:

```bash
gradle :shared:ktlintCheck :androidApp:ktlintCheck :desktopApp:ktlintCheck
gradle :shared:desktopTest
gradle :desktopApp:test
gradle :desktopApp:packageUberJarForCurrentOS
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintRelease
gradle :androidApp:assembleDebug
gradle :androidApp:assembleRelease
gradle :androidApp:bundleRelease
```

Android instrumentation tests require a device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS, the configured Apple targets can be compiled with:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

Pull requests run dedicated workflows for the standard quality suite, Android emulator instrumentation, cross-platform desktop verification, Apple shared-core compilation, CodeQL, dependency review, and full-history secret scanning.

See [`docs/testing.md`](docs/testing.md) for the test matrix and regression policy.

## Local data and backup behavior

### Android

HealthMetric stores no measurement history until the user explicitly enables it. When history is enabled, only the calculated value, calculator type, timestamp, local identifier, and a short neutral summary are retained; raw weight, height, and waist inputs are not stored.

The history retention setting caps stored results at 50, 100, 250, or 500 entries. Lowering the limit immediately trims older stored entries. Individual entries can be deleted and restored with the snackbar **Undo** action.

Backup options are explicit user actions:

- **Save JSON backup to a file** uses Android's Storage Access Framework document picker.
- **Share JSON backup** opens Android's chooser.
- **Restore from JSON backup** reads a selected local document and asks for confirmation before replacing portable history/settings.

Backup reads/writes are capped at 1 MiB. Restore accepts schema version 1, caps restored history, rejects invalid top-level schemas, ignores malformed history records, and keeps only the first valid record for duplicate IDs.

Portable backups intentionally exclude three device-local decisions: whether future history saving is enabled, whether adult use was confirmed, and whether onboarding was completed. Restore cannot silently turn history saving on or enable adult-only reference screens.

See [`docs/backup-format.md`](docs/backup-format.md) for the exact schema and compatibility rules.

### Desktop

The desktop client does not persist calculator inputs, calculator results, adult-use selection, theme selection, or navigation state. Closing the desktop application discards that in-memory UI state.

See [`docs/desktop.md`](docs/desktop.md).

## Build and release

### Android debug

```bash
gradle :androidApp:assembleDebug
```

### Android unsigned release candidates

```bash
gradle :androidApp:assembleRelease :androidApp:bundleRelease
```

Outputs include an unsigned release APK and unsigned release Android App Bundle. Production/store signing must happen through a protected signing process; signing keys and signing passwords must never be committed.

### Desktop runnable JAR

```bash
gradle :desktopApp:packageUberJarForCurrentOS
```

The desktop module also declares current-platform native distribution configuration for Windows MSI, macOS DMG, and Debian-compatible Linux DEB packaging. Native packages must be built on the corresponding operating system and manually verified before publication.

Pushing a `v*` tag runs the Android release workflow. Tagged release behavior and remaining manual release gates are documented in [`docs/release.md`](docs/release.md).

## Architecture

```text
HealthMetric/
├── androidApp/                 # Android UI, DataStore persistence, document/platform integrations
│   └── src/main/java/...       # Compose app, screens, components, ViewModel, local data layer
├── desktopApp/                 # Compose Multiplatform JVM desktop UI; in-memory state only
│   └── src/main/kotlin/...     # Adult gate, calculator forms, presentation adapter, input parsing
├── shared/                     # Kotlin Multiplatform domain module
│   ├── src/commonMain/...      # Calculators, reference model, validation, conversions
│   └── src/commonTest/...      # Cross-platform domain tests
├── docs/                       # Architecture, desktop, backup, evidence, design, testing, release, ADRs
├── gradle/libs.versions.toml   # Central dependency/version catalog
└── .github/                    # CI, Android/desktop/Apple tests, security, release automation, templates
```

The shared module owns deterministic calculation rules and input validation. Android owns persistent local-data behavior and Android integrations. Desktop owns its Compose window, transient screen state, external-link actions, and presentation adapter while delegating calculation rules back to `shared`.

Key architecture/design references:

- [`docs/architecture.md`](docs/architecture.md)
- [`docs/desktop.md`](docs/desktop.md)
- [`docs/backup-format.md`](docs/backup-format.md)
- [`docs/design-system.md`](docs/design-system.md)
- [`docs/evidence.md`](docs/evidence.md)
- [`docs/adr/`](docs/adr/)

## Privacy and security

HealthMetric is designed to perform its calculation core without an account or backend.

- No advertising trackers are included.
- Android application backup is disabled.
- Android local history starts disabled and must be explicitly enabled.
- Android history retention is bounded and user-selectable.
- Android history can be disabled, selectively deleted, fully erased, or restored through undo.
- Android export/import is explicit and user-initiated.
- Android restore requires confirmation before portable data is replaced.
- Android backup IO is limited to 1 MiB and restore validates schema and individual records.
- Portable Android backups cannot modify history opt-in or adult-use/onboarding safety state.
- Cleartext network traffic is disabled in the Android manifest.
- No Android Internet permission is requested.
- Desktop calculator data and adult-use/theme state are not persisted.
- Desktop external URLs open only after an explicit user action.
- CI includes CodeQL, dependency review, full-history secret scanning, Android emulator tests, desktop multi-OS verification, and Apple target compilation.
- Secrets and signing material are ignored by repository rules and must never be committed.

Read [`PRIVACY.md`](PRIVACY.md), [`SECURITY.md`](SECURITY.md), and [`docs/desktop.md`](docs/desktop.md).

## Accessibility

HealthMetric targets accessible, text-first interaction patterns:

- semantic headings and labels;
- scalable system typography;
- Material touch/click targets;
- light/dark presentation;
- status information not conveyed by color alone;
- Android chart content descriptions that summarize values;
- accessible labels for per-entry deletion;
- locale-aware Android numeric display;
- neutral wording without appearance ranking;
- centered bounded Android content width for wider windows;
- desktop visible text labels for all major controls;
- standard desktop focusable Material controls and keyboard traversal targets.

Manual TalkBack and desktop screen-reader/platform accessibility evidence remains part of release-candidate verification.

See [`docs/accessibility.md`](docs/accessibility.md) and [`docs/desktop.md`](docs/desktop.md).

## Contributing

Contributions are welcome. Start with [`CONTRIBUTING.md`](CONTRIBUTING.md), follow the Code of Conduct, and keep changes focused and testable.

Use fictional/example measurement values in bug reports and screenshots. Do not submit private health information.

## Roadmap

See [`ROADMAP.md`](ROADMAP.md) for remaining release verification, screenshot/accessibility evidence, signing, and optional future work.

## License

HealthMetric is released under the [MIT License](LICENSE).

## Contact and support

- GitHub: https://github.com/sanskarIN
- Business: sanskarin@outlook.in
- Business: sanskarin.business@gmail.com
- Support: supportramsandesh@gmail.com
- Buy Me a Coffee: https://buymeacoffee.com/sanskarIN

[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-sanskarIN-FFDD00?logo=buy-me-a-coffee&logoColor=000000)](https://buymeacoffee.com/sanskarIN)

---

**Made by the Sanskar**
