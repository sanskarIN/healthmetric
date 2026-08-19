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

Health calculators are often tiny demos. HealthMetric is designed as a production-oriented portfolio project with a shared domain layer, strict validation, explicit adult-use boundaries, privacy-first storage behavior, accessible neutral presentation, multi-platform verification, reproducible artifacts, security analysis, and detailed project documentation.

The Android client provides optional bounded local history and explicit backup/restore controls. The desktop client intentionally keeps measurement/session state in memory only and discards it when the app closes.

## Features

### Shared calculation core

- Adult BMI calculation in metric and imperial units.
- Versioned adult BMI reference metadata with source-review information.
- Adult waist-to-height ratio calculation with neutral educational wording.
- Strict finite-number and plausible-range validation.
- Metric/imperial conversion helpers.
- Kotlin Multiplatform targets for Android, JVM/Desktop, iOS device, and iOS simulator.
- Shared unit, boundary, conversion, evidence, and deterministic property-style tests.

### Android client

- Adult-only onboarding gate before adult reference calculators become available.
- Offline-first core behavior with no Android Internet permission, advertising SDK, or ad tracker.
- Optional calculation history that is **disabled by default** and requires explicit opt-in.
- User-selectable history retention limits of 50, 100, 250, or 500 results.
- Canonical newest-first history ordering before retention is applied.
- Collision-resistant UUID identifiers for newly recorded local history entries.
- Per-entry history deletion with immediate Undo that preserves chronology.
- Accessible measurement-history chart with text/screen-reader summaries and no color-only health meaning.
- Confirmation before erase-all, delete-all-data, and restore operations.
- JSON backup through Android's document picker or explicit share chooser.
- Defensive JSON restore with a 1 MiB limit, schema validation, malformed-record recovery, duplicate-ID protection, and bounded newest-first history.
- Portable backups cannot change current-device history opt-in, adult-use confirmation, or onboarding safety state.
- Locale-aware decimal input plus locale-aware result/history formatting.
- Full local-data deletion that restores first-run privacy defaults.
- Light, dark, and system themes with Android dynamic color where supported.
- Branded splash screen plus adaptive, round, and Android 13+ themed launcher icons.
- Externalized Android strings for localization readiness.
- About screen with explicit in-app/system back navigation, MIT license, version, GitHub, support, funding, and project credit.
- Settings update section linking to public GitHub releases.

### Desktop client

- Compose Multiplatform JVM UI for Linux, Windows, and macOS.
- Explicit adult-use gate and separate under-18 unavailable path.
- Metric and imperial adult BMI forms.
- Metric and imperial waist-to-height forms.
- Dot/comma decimal input support.
- Shared-domain validation/reference rules rather than duplicated health logic.
- Light/dark theme switch for the current session.
- Evidence, project, support, and funding information.
- Explicit external-link buttons only.
- No persistent measurement inputs/results, adult-use choice, theme, or navigation state.
- Runnable JAR packaging on each desktop host.
- Native DEB, MSI, and DMG packaging on matching Linux, Windows, and macOS hosts.

### Quality and automation

- Kotlin formatting checks across shared, Android, and desktop code.
- Shared JVM tests, Android JVM tests, desktop JVM tests, and Android instrumentation tests.
- Android release lint.
- Android API 35 emulator verification.
- Real-app Android release screenshot capture with eight required PNGs uploaded as `android-release-screenshots`.
- Apple shared-core compilation on macOS.
- Desktop verification on Linux, Windows, and macOS, including runnable JAR plus native installer creation.
- Debug APK, unsigned release APK, unsigned release AAB, desktop JAR, DEB, MSI, and DMG artifact pipelines.
- CodeQL, dependency review, full-history secret scanning, and Dependabot.
- Repository invariant and internal Markdown-link audits.

## Supported platforms

| Platform | Status | Notes |
|---|---|---|
| Android | Primary | Jetpack Compose app, min SDK 26, target/compile SDK 36 |
| Linux desktop | Implemented | Compose Multiplatform JVM client; CI builds runnable JAR + DEB |
| Windows desktop | Implemented | Compose Multiplatform JVM client; CI builds runnable JAR + MSI |
| macOS desktop | Implemented | Compose Multiplatform JVM client; CI builds runnable JAR + DMG |
| iOS shared core | Configured | Kotlin Multiplatform `iosArm64` and `iosSimulatorArm64` targets; macOS CI compiles both |

The repository does not currently ship an iOS user interface. The Apple targets validate the shared calculation core for a future client.

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
- JUnit, Kotlin test, Compose UI testing, Android emulator instrumentation, desktop multi-OS CI, and Apple-target compilation

Versions are centrally pinned in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Android release screenshot evidence

The repository contains `ReleaseScreenshotCaptureTest`, which drives the real Android app and captures the required release-evidence set with fictional/example data. `.github/workflows/android-instrumentation.yml` pulls those app-generated files from the API 35 Pixel 7 emulator and uploads them as `android-release-screenshots`.

Required PNGs:

1. `01-onboarding.png`
2. `02-bmi-metric.png`
3. `03-bmi-result.png`
4. `04-waist-ratio.png`
5. `05-history.png`
6. `06-settings.png`
7. `07-about.png`
8. `08-dark-theme.png`

Automated generation does not replace human visual/privacy approval before permanent README/store publication. See [`docs/assets/screenshots/README.md`](docs/assets/screenshots/README.md).

## Quick start

### Requirements

- JDK 17.
- Gradle 8.13 when not using an IDE-managed Gradle installation.
- Android Studio plus Android SDK Platform 36 / Build Tools 35.0.0 for Android development.

### Clone and verify the shared core

```bash
git clone https://github.com/sanskarIN/healthmetric.git
cd healthmetric
gradle :shared:desktopTest
```

### Build Android debug

```bash
gradle :androidApp:assembleDebug
```

### Run desktop

```bash
gradle :desktopApp:run
```

### Build a desktop runnable JAR

```bash
gradle :desktopApp:packageUberJarForCurrentOS
```

For setup details, see [`docs/setup.md`](docs/setup.md) and [`docs/desktop.md`](docs/desktop.md).

## Development setup

1. Clone the repository.
2. Open the root in Android Studio or IntelliJ IDEA.
3. Use JDK 17 for Gradle.
4. Install Android SDK Platform 36 and Build Tools 35.0.0 for Android work.
5. Allow Gradle to resolve dependencies.
6. Run the `androidApp` configuration on Android 8.0+ for Android work.
7. Run `gradle :desktopApp:run` for the desktop client.

More detail:

- [`docs/setup.md`](docs/setup.md)
- [`docs/development.md`](docs/development.md)
- [`docs/desktop.md`](docs/desktop.md)
- [`docs/troubleshooting.md`](docs/troubleshooting.md)

## Testing and verification

Unix-like systems:

```bash
bash scripts/verify.sh
```

Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

Equivalent major tasks:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
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

Android connected tests:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

Apple shared targets on macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

Native desktop packaging is host-specific:

```bash
# Linux
gradle :desktopApp:packageDeb

# Windows
gradle :desktopApp:packageMsi

# macOS
gradle :desktopApp:packageDmg
```

Pull requests run CI, Android instrumentation, Desktop, Apple shared-core, CodeQL, Dependency Review, and Secret Scan workflows. See [`docs/testing.md`](docs/testing.md).

## Local data and backup behavior

### Android

HealthMetric stores no measurement history until the user explicitly enables it. When enabled, history stores a local UUID, timestamp, calculator type, calculated value, and short neutral summary; raw weight, height, and waist inputs are not retained in history.

History is normalized newest-first before a retention cap of 50, 100, 250, or 500 is applied. Lowering the limit immediately removes older entries. Undo restores an entry without enabling future history saving and without moving an older entry to the newest position.

Backup options are explicit user actions:

- **Save JSON backup to a file** uses Android's Storage Access Framework document picker.
- **Share JSON backup** opens Android's chooser.
- **Restore from JSON backup** reads a selected local document and asks for confirmation before mutation.

Backup reads/writes are capped at 1 MiB. Restore accepts schema version 1, validates/sanitizes records, deduplicates IDs, sorts accepted records by timestamp descending, and then applies retention.

Portable backups intentionally exclude history opt-in, adult-use confirmation, and onboarding completion. See [`docs/backup-format.md`](docs/backup-format.md).

### Desktop

The desktop client has no HealthMetric persistence layer. Calculator inputs/results, adult-use choice, theme, and navigation state live only in process memory and are discarded when the desktop application closes.

See [`docs/desktop.md`](docs/desktop.md) and [`docs/adr/0005-ephemeral-desktop-client.md`](docs/adr/0005-ephemeral-desktop-client.md).

## Build and release

### Android

Debug APK:

```bash
gradle :androidApp:assembleDebug
```

Unsigned release APK + AAB:

```bash
gradle :androidApp:assembleRelease :androidApp:bundleRelease
```

Production Android signing must happen through a protected process; keys/passwords must never be committed.

### Desktop

Runnable JAR:

```bash
gradle :desktopApp:packageUberJarForCurrentOS
```

Native installers:

```bash
# Linux
gradle :desktopApp:packageDeb

# Windows
gradle :desktopApp:packageMsi

# macOS
gradle :desktopApp:packageDmg
```

The dedicated Desktop workflow verifies JAR plus native packaging on all three operating-system families. Native package build success does not imply production code signing/notarization or human host-platform acceptance testing.

### Tagged releases

Pushing a `v*` tag runs the multiplatform release workflow. It rebuilds/verifies Android artifacts and desktop artifacts on matching hosts, then requires the complete automated asset set before creating one GitHub Release:

- Android unsigned APK;
- Android unsigned AAB;
- Linux JAR + DEB;
- Windows JAR + MSI;
- macOS JAR + DMG.

See [`docs/release.md`](docs/release.md) for signing, notarization, screenshot review, accessibility, physical-device, and host-smoke-test gates that automation cannot truthfully replace.

## Architecture

```text
HealthMetric/
├── androidApp/                 # Android UI, DataStore persistence, document/platform integrations
├── desktopApp/                 # Compose JVM desktop UI; ephemeral session state
├── shared/                     # Kotlin Multiplatform calculation/validation domain
├── docs/                       # Architecture, desktop, backup, evidence, testing, release, ADRs
├── scripts/                    # Repository/link audits and local verification
├── gradle/libs.versions.toml   # Central dependency/version catalog
└── .github/                    # CI, Android/desktop/Apple/security/release automation
```

The shared module owns deterministic health calculation and validation rules. Android owns persistent local-data behavior and Android platform integration. Desktop owns only transient UI/session concerns and delegates health rules to `shared`.

Key references:

- [`docs/architecture.md`](docs/architecture.md)
- [`docs/desktop.md`](docs/desktop.md)
- [`docs/backup-format.md`](docs/backup-format.md)
- [`docs/design-system.md`](docs/design-system.md)
- [`docs/evidence.md`](docs/evidence.md)
- [`docs/adr/`](docs/adr/)

## Privacy and security

- No advertising trackers are included.
- Core calculation behavior requires no account/backend.
- Android application backup is disabled.
- Android history is opt-in and bounded.
- Android import/export is explicit and user-initiated.
- Android backup IO is capped at 1 MiB.
- Android portable backups cannot modify device-local consent/adult-gate state.
- Android manifest requests no Internet permission and disallows cleartext traffic.
- Desktop measurement/session state is not persisted.
- Desktop external URLs open only after explicit user action.
- CodeQL, Dependency Review, Secret Scan, Dependabot, repository invariants, and link audits are configured.
- Signing/notarization material must remain outside source control.

Read [`PRIVACY.md`](PRIVACY.md), [`SECURITY.md`](SECURITY.md), and [`docs/desktop.md`](docs/desktop.md).

## Accessibility

HealthMetric uses text-first, non-color-only interaction patterns with semantic labels, scalable text, standard Material controls, Android chart descriptions, explicit navigation semantics, and desktop keyboard/focus-capable controls.

Automated checks do not replace manual TalkBack, screen-reader, maximum-font/display-scaling, keyboard/DPAD, physical-device, or host-platform release review.

See [`docs/accessibility.md`](docs/accessibility.md).

## Contributing

Contributions are welcome. Start with [`CONTRIBUTING.md`](CONTRIBUTING.md), follow the Code of Conduct, and keep changes focused and testable.

Use fictional/example measurement values in bug reports, tests, and screenshots. Do not submit private health information.

## Roadmap

See [`ROADMAP.md`](ROADMAP.md) for exact remaining release verification and manual gates.

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
