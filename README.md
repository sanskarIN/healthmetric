<p align="center">
  <img src="docs/assets/logo.svg" alt="HealthMetric" width="720" />
</p>

# HealthMetric

[![CI](https://github.com/sanskarIN/healthmetric/actions/workflows/ci.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/ci.yml)
[![CodeQL](https://github.com/sanskarIN/healthmetric/actions/workflows/codeql.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/codeql.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-sanskarIN-FFDD00?logo=buy-me-a-coffee&logoColor=000000)](https://buymeacoffee.com/sanskarIN)

**HealthMetric** is an open-source, privacy-first **adult BMI and health measurement calculator** built with Kotlin, Jetpack Compose, and a Kotlin Multiplatform shared calculation core.

> [!IMPORTANT]
> HealthMetric's BMI and waist-to-height tools are intended for adults age 18 or older. Results are educational screening information only. They are not medical diagnoses, appearance scores, or personal body targets.

**Made by the Sanskar**

## Why HealthMetric?

Health calculators are often tiny demos. HealthMetric is designed as a portfolio-quality product with a shared domain layer, input validation, offline local history, accessible neutral charts, export/restore/delete controls, automated tests, CI, security analysis, and complete project documentation.

## Features

- Adult BMI calculation in metric and imperial units.
- Configurable adult BMI reference metadata and source information.
- Adult waist-to-height ratio calculation presented without appearance rankings or pressure-oriented goals.
- Strict finite-number and plausible-range validation.
- Accurate metric/imperial conversion helpers in the shared Kotlin Multiplatform module.
- Adult-only onboarding gate before reference calculators become available.
- Privacy-first offline operation; no advertising SDKs or ad trackers.
- Optional local calculation history that can be disabled at any time.
- Accessible measurement history chart with a screen-reader summary and no color-based health meaning.
- JSON data export, restore, history deletion, and full local-data deletion.
- Light, dark, and system theme modes with Android dynamic color where supported.
- About screen with license, version, GitHub, support contacts, funding link, and project credit.
- Kotlin formatting checks, Android lint, tests, CodeQL, dependency review, and Dependabot automation.

## Supported platforms

| Platform | Status | Notes |
|---|---|---|
| Android | Primary | Jetpack Compose app, min SDK 26, target/compile SDK 36 |
| JVM/Desktop core | Ready | Shared calculation module exposes a JVM target |
| iOS-ready core | Planned | Architecture keeps domain logic platform-neutral; iOS target is a roadmap item |

## Tech stack

- Kotlin 2.2.20
- Kotlin Multiplatform shared domain module
- Android Gradle Plugin 8.13.2
- Gradle 8.13 in CI
- Jetpack Compose UI 1.9.2
- Material 3 1.4.0
- AndroidX DataStore Preferences 1.2.1
- AndroidX Lifecycle 2.9.4
- kotlinx.coroutines 1.10.2
- ktlint Gradle plugin 14.2.0
- JUnit and Compose UI testing

Versions are centrally pinned in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Screenshots / demo

Real device screenshots are a release-candidate task because this repository is generated in an environment without an Android emulator. Capture guidance and the required screenshot set are tracked in [`docs/assets/screenshots/README.md`](docs/assets/screenshots/README.md).

Planned captures:

1. Adult-use onboarding notice.
2. Metric BMI calculator and neutral educational result.
3. Waist-to-height calculator.
4. Local history with accessible chart.
5. Privacy/data settings.
6. About and support screen.

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
gradle :androidApp:lintDebug
gradle :androidApp:assembleDebug
```

Android instrumentation tests require a device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

See [`docs/testing.md`](docs/testing.md) for the test matrix and regression policy.

## Build and release

Debug:

```bash
gradle :androidApp:assembleDebug
```

Unsigned release candidate:

```bash
gradle :shared:desktopTest :shared:ktlintCheck :androidApp:ktlintCheck :androidApp:lintRelease :androidApp:assembleRelease
```

Pushing a `v*` tag runs the release workflow, verifies the project, creates an unsigned release APK, and creates a GitHub Release. Store/distribution signing must be performed through a protected signing process; signing keys must never be committed.

See [`docs/release.md`](docs/release.md).

## Architecture

```text
HealthMetric/
├── androidApp/                 # Android UI, DataStore persistence, platform integrations
│   └── src/main/java/...       # Compose app, screens, ViewModel, local data layer
├── shared/                     # Kotlin Multiplatform domain module
│   ├── src/commonMain/...      # Calculators, reference model, validation, conversions
│   └── src/commonTest/...      # Cross-platform domain tests
├── docs/                       # Architecture, setup, testing, release, ADRs
├── gradle/libs.versions.toml   # Central dependency/version catalog
└── .github/                    # CI, security, release automation, templates
```

The shared module owns deterministic calculation rules and input validation. The Android app owns presentation, local persistence, platform intents, and UI state. See [`docs/architecture.md`](docs/architecture.md) and [`docs/adr/`](docs/adr/) for architectural decisions.

## Privacy and security

HealthMetric is designed to work without an account and without network access for its core functionality.

- No advertising trackers are included.
- Android application backup is disabled by default.
- Local history can be disabled or erased.
- Export is explicit and user-initiated.
- Import validates the supported backup schema and caps restored history.
- Cleartext network traffic is disabled in the Android manifest.
- CI includes CodeQL and dependency review.
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
- neutral wording without appearance ranking.

See [`docs/accessibility.md`](docs/accessibility.md).

## Contributing

Contributions are welcome. Start with [`CONTRIBUTING.md`](CONTRIBUTING.md), follow the Code of Conduct, and keep changes focused and testable.

Use fictional/example measurement values in bug reports and screenshots. Do not submit private health information.

## Roadmap

See [`ROADMAP.md`](ROADMAP.md) for the phased plan, including richer evidence metadata, export files through the Storage Access Framework, expanded accessibility testing, desktop UI, and an iOS-ready shared target.

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
