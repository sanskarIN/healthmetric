<p align="center">
  <img src="docs/assets/logo.svg" alt="HealthMetric" width="720" />
</p>

# HealthMetric

[![CI](https://github.com/sanskarIN/healthmetric/actions/workflows/ci.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/ci.yml)
[![Android instrumentation](https://github.com/sanskarIN/healthmetric/actions/workflows/android-instrumentation.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/android-instrumentation.yml)
[![Apple shared core](https://github.com/sanskarIN/healthmetric/actions/workflows/apple-shared.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/apple-shared.yml)
[![CodeQL](https://github.com/sanskarIN/healthmetric/actions/workflows/codeql.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/codeql.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-sanskarIN-FFDD00?logo=buy-me-a-coffee&logoColor=000000)](https://buymeacoffee.com/sanskarIN)

**HealthMetric** is an open-source, privacy-first **adult BMI and health measurement calculator** built with Kotlin, Jetpack Compose, and a Kotlin Multiplatform shared calculation core.

> [!IMPORTANT]
> HealthMetric's BMI and waist-to-height tools are intended for adults age 18 or older. Results are educational screening information only. They are not medical diagnoses, appearance scores, or personal body targets.

**Made by the Sanskar**

## Why HealthMetric?

Health calculators are often tiny demos. HealthMetric is designed as a portfolio-quality product with a shared domain layer, strict validation, offline opt-in history, accessible neutral charts, bounded local retention, explicit file/share backup flows, locale-aware numeric handling, automated tests, emulator and Apple-target CI, security analysis, localization-ready resources, and complete project documentation.

## Features

- Adult BMI calculation in metric and imperial units.
- Configurable, versioned adult BMI reference metadata with source review information.
- Adult waist-to-height ratio calculation presented without appearance rankings or pressure-oriented goals.
- Strict finite-number and plausible-range validation.
- Accurate metric/imperial conversion helpers in the shared Kotlin Multiplatform module.
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
- Locale-aware decimal input accepting the user's decimal separator plus dot/comma fallback, with locale-aware result/history formatting.
- Full local-data deletion that returns the app to first-run privacy defaults.
- Light, dark, and system theme modes with Android dynamic color where supported.
- Branded splash screen, adaptive launcher icons, round icons, and Android 13+ themed icon support.
- Shared typography, shape, spacing, elevation, and motion design tokens.
- Android UI copy externalized to resources for localization readiness.
- About screen with license, version, GitHub, support contacts, funding link, and project credit.
- Settings update section linking to public GitHub releases.
- Kotlin formatting checks, release lint, unit tests, emulator instrumentation tests, Apple shared-core compilation, CodeQL, dependency review, secret scanning, and Dependabot automation.

## Supported platforms

| Platform | Status | Notes |
|---|---|---|
| Android | Primary | Jetpack Compose app, min SDK 26, target/compile SDK 36 |
| JVM/Desktop core | Ready | Shared calculation module exposes a JVM target and JVM test suite |
| iOS shared core | Configured | Kotlin Multiplatform `iosArm64` and `iosSimulatorArm64` targets; macOS CI compiles both |

The repository does not currently ship a desktop or iOS user interface. The shared calculation core is intentionally reusable for those future clients.

## Tech stack

- Kotlin 2.2.20
- Kotlin Multiplatform shared domain module
- Android Gradle Plugin 8.13.2
- Gradle 8.13 in CI
- Jetpack Compose UI 1.9.2
- Material 3 1.4.0
- AndroidX Core SplashScreen 1.2.0
- AndroidX DataStore Preferences 1.2.1
- AndroidX Lifecycle 2.9.4
- kotlinx.coroutines 1.10.2
- ktlint Gradle plugin 14.2.0
- JUnit, Compose UI testing, Android emulator instrumentation, and macOS Apple-target compilation

Versions are centrally pinned in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Screenshots / demo

Real device screenshots remain a release-candidate task because the initial coding environment does not provide an Android emulator UI for capture. Capture guidance and the required screenshot set are tracked in [`docs/assets/screenshots/README.md`](docs/assets/screenshots/README.md).

Planned captures:

1. Adult-use onboarding notice.
2. Metric BMI calculator and neutral educational result.
3. Waist-to-height calculator.
4. Local history with accessible chart and entry controls.
5. Privacy/data settings with retention and backup options.
6. Restore confirmation.
7. About and support screen.
8. Representative dark-theme screen.

## Quick start

### Requirements

- JDK 17 or newer supported by the configured Gradle/Android toolchain.
- Gradle 8.13 if you are not using an IDE-managed Gradle installation.
- Android Studio with Android SDK Platform 36 and Build Tools 35.0.0.

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

For Android Studio setup, see [`docs/setup.md`](docs/setup.md).

## Development setup

1. Clone the repository.
2. Open the repository root in Android Studio.
3. Use JDK 17 for Gradle.
4. Install Android SDK Platform 36 and Build Tools 35.0.0.
5. Allow Gradle sync to download dependencies.
6. Run the `androidApp` configuration on an Android 8.0+ device/emulator.

Full environment and troubleshooting notes:

- [`docs/setup.md`](docs/setup.md)
- [`docs/development.md`](docs/development.md)
- [`docs/troubleshooting.md`](docs/troubleshooting.md)

## Testing and verification

Run the local quality suite:

```bash
gradle :shared:ktlintCheck :androidApp:ktlintCheck
gradle :shared:desktopTest
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintRelease
gradle :androidApp:assembleDebug
gradle :androidApp:assembleRelease
```

Android instrumentation tests require a device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS, the configured Apple targets can be compiled with:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

Pull requests run dedicated workflows for the standard quality suite, Android emulator instrumentation, Apple shared-core compilation, CodeQL, dependency review, and full-history secret scanning.

See [`docs/testing.md`](docs/testing.md) for the test matrix and regression policy.

## Local data and backup behavior

HealthMetric stores no measurement history until the user explicitly enables it. When history is enabled, only the calculated value, calculator type, timestamp, local identifier, and a short neutral summary are retained; raw weight, height, and waist inputs are not stored.

The history retention setting caps stored results at 50, 100, 250, or 500 entries. Lowering the limit immediately trims older stored entries. Individual entries can be deleted and restored with the snackbar **Undo** action.

Backup options are explicit user actions:

- **Save JSON backup to a file** uses Android's Storage Access Framework document picker.
- **Share JSON backup** opens Android's chooser.
- **Restore from JSON backup** reads a selected local document and asks for confirmation before replacing portable history/settings.

Backup reads/writes are capped at 1 MiB. Restore accepts schema version 1, caps restored history, rejects invalid top-level schemas, ignores malformed history records, and keeps only the first valid record for duplicate IDs.

Portable backups intentionally exclude three device-local decisions: whether future history saving is enabled, whether adult use was confirmed, and whether onboarding was completed. Restore cannot silently turn history saving on or enable adult-only reference screens.

See [`docs/backup-format.md`](docs/backup-format.md) for the exact schema and compatibility rules.

## Build and release

Debug:

```bash
gradle :androidApp:assembleDebug
```

Unsigned release candidate:

```bash
gradle :shared:desktopTest :shared:ktlintCheck :androidApp:ktlintCheck :androidApp:testDebugUnitTest :androidApp:lintRelease :androidApp:assembleRelease
```

Pushing a `v*` tag runs the release workflow, verifies the project, creates an unsigned release APK, and creates a GitHub Release. Store/distribution signing must be performed through a protected signing process; signing keys must never be committed.

See [`docs/release.md`](docs/release.md).

## Architecture

```text
HealthMetric/
├── androidApp/                 # Android UI, DataStore persistence, platform integrations
│   └── src/main/java/...       # Compose app, screens, components, ViewModel, local data layer
├── shared/                     # Kotlin Multiplatform domain module
│   ├── src/commonMain/...      # Calculators, reference model, validation, conversions
│   └── src/commonTest/...      # Cross-platform domain tests
├── docs/                       # Architecture, backup format, evidence, design, testing, release, ADRs
├── gradle/libs.versions.toml   # Central dependency/version catalog
└── .github/                    # CI, emulator/Apple tests, security, release automation, templates
```

The shared module owns deterministic calculation rules and input validation. The Android app owns presentation, local persistence, document intents, platform links, locale-aware display parsing/formatting, and UI state.

Key architecture/design references:

- [`docs/architecture.md`](docs/architecture.md)
- [`docs/backup-format.md`](docs/backup-format.md)
- [`docs/design-system.md`](docs/design-system.md)
- [`docs/evidence.md`](docs/evidence.md)
- [`docs/adr/`](docs/adr/)

## Privacy and security

HealthMetric is designed to work without an account and without network access for its core functionality.

- No advertising trackers are included.
- Android application backup is disabled.
- Local history starts disabled and must be explicitly enabled.
- Retention is bounded and user-selectable.
- History can be disabled, selectively deleted, fully erased, or restored through undo.
- Export/import are explicit and user-initiated.
- Restore requires confirmation before portable data is replaced.
- Backup IO is limited to 1 MiB and restore validates schema and individual records.
- Portable backups cannot modify history opt-in or adult-use/onboarding safety state.
- Cleartext network traffic is disabled in the Android manifest.
- No Android Internet permission is requested.
- CI includes CodeQL, dependency review, full-history secret scanning, emulator tests, and Apple target compilation.
- Secrets and signing material are ignored by repository rules and must never be committed.

Read [`PRIVACY.md`](PRIVACY.md) and [`SECURITY.md`](SECURITY.md).

## Accessibility

HealthMetric targets WCAG-oriented mobile accessibility practices:

- semantic headings and labels;
- scalable system typography;
- Material touch targets;
- light/dark/system themes;
- status information not conveyed by color alone;
- chart content descriptions that summarize values;
- accessible labels for per-entry deletion;
- locale-aware numeric display;
- neutral wording without appearance ranking;
- centered bounded content width for wider Android windows.

See [`docs/accessibility.md`](docs/accessibility.md).

## Contributing

Contributions are welcome. Start with [`CONTRIBUTING.md`](CONTRIBUTING.md), follow the Code of Conduct, and keep changes focused and testable.

Use fictional/example measurement values in bug reports and screenshots. Do not submit private health information.

## Roadmap

See [`ROADMAP.md`](ROADMAP.md) for remaining release verification, screenshot/accessibility evidence, and optional future client/performance work.

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
