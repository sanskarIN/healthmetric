<p align="center">
  <img src="docs/assets/logo.svg" alt="HealthMetric" width="720" />
</p>

# HealthMetric

[![CI](https://github.com/sanskarIN/healthmetric/actions/workflows/ci.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/ci.yml)
[![Cross-platform](https://github.com/sanskarIN/healthmetric/actions/workflows/cross-platform.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/cross-platform.yml)
[![Android instrumentation](https://github.com/sanskarIN/healthmetric/actions/workflows/android-instrumentation.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/android-instrumentation.yml)
[![CodeQL](https://github.com/sanskarIN/healthmetric/actions/workflows/codeql.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/codeql.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-sanskarIN-FFDD00?logo=buy-me-a-coffee&logoColor=000000)](https://buymeacoffee.com/sanskarIN)

**HealthMetric** is an open-source, privacy-first adult BMI and health measurement calculator built with Kotlin Multiplatform, Jetpack Compose, Compose Multiplatform, and a SwiftUI iOS/iPadOS host.

> [!IMPORTANT]
> HealthMetric's BMI and waist-to-height tools are intended for adults age 18 or older. Results are educational screening information only. They are not medical diagnoses, appearance scores, or personal body targets.

**Made by the Sanskar**

## Cross-platform support

HealthMetric has first-class application targets for the major mobile, desktop, and browser platforms while keeping one validated health-calculation domain.

| Platform | Status | Application technology | Primary output |
|---|---|---|---|
| Android | ✅ Full Android client | Jetpack Compose | APK / AAB |
| iPhone / iPad | ✅ Calculator client | Compose Multiplatform hosted by SwiftUI/UIKit | Xcode app; signed IPA via protected Apple signing |
| Windows | ✅ Calculator client | Compose Desktop | MSI |
| macOS | ✅ Calculator client | Compose Desktop | DMG |
| Linux | ✅ Calculator client | Compose Desktop | DEB |
| Web — WebAssembly | ✅ Supported | Compose Multiplatform Wasm | Browser distribution |
| Web — JavaScript fallback | ✅ Supported | Compose Multiplatform JS | Browser distribution |
| ChromeOS | ✅ Through Android where Android apps are supported | Android app | APK / store install |

The Android client remains the most feature-complete platform-specific client. It additionally contains opt-in persistent history, configurable retention, JSON backup/restore, Android dynamic color, and Android document/share integrations. The iOS, desktop, and web clients provide the shared adult-use gate plus metric/imperial BMI and waist-to-height calculator experience backed by the same domain validation. This distinction is documented deliberately so platform support is not confused with identical feature parity.

See **[`docs/cross-platform.md`](docs/cross-platform.md)** for the complete platform build, run, packaging, Xcode, browser, CI, release, and troubleshooting guide.

## Core features

- Adult metric and imperial BMI calculation.
- Versioned adult BMI reference metadata with reviewed evidence information.
- Adult waist-to-height ratio calculation presented without appearance rankings or pressure-oriented goals.
- Strict finite-number and plausible-range input validation.
- Shared metric/imperial conversion helpers.
- Adult-use confirmation before adult reference calculators become available.
- Local calculator execution without requiring a remote health API.
- One Kotlin Multiplatform health domain reused across Android, iOS/iPadOS, desktop, JavaScript, and WebAssembly.
- Shared Compose calculator UI for iOS, Windows, macOS, Linux, JavaScript web, and WebAssembly web.
- Native SwiftUI/Xcode host for iPhone and iPad.
- Native desktop packaging for MSI, DMG, and DEB.
- Browser production builds for Wasm and JavaScript compatibility.
- Cross-platform GitHub Actions build/packaging verification.

### Android-specific product features

- Optional local calculation history, **disabled by default** until explicitly enabled.
- History-retention limits of 50, 100, 250, or 500 results.
- Per-entry deletion with immediate undo.
- Accessible neutral measurement-history chart.
- Confirmation before destructive erase-all, delete-all-data, and restore actions.
- JSON backup through Android's document picker and explicit share flow.
- Defensive restore with schema validation, 1 MiB limits, bounded history, malformed-record recovery, and duplicate-ID protection.
- Portable backups cannot alter current-device history opt-in, adult-use confirmation, or onboarding safety state.
- Locale-aware decimal input and result/history formatting.
- Light, dark, system, and Android dynamic-color themes.
- Branded splash screen, adaptive icons, round icons, and Android 13+ themed icon support.

## Architecture

```text
HealthMetric/
├── androidApp/                 # Mature Android UI + Android persistence/integrations
├── composeApp/                 # Shared iOS/Desktop/Web Compose application
│   ├── src/commonMain/         # Cross-platform calculator UI + adult-use gate
│   ├── src/desktopMain/        # Windows/macOS/Linux entry point
│   ├── src/iosMain/            # UIKit controller bridge
│   └── src/webMain/            # JS/Wasm browser entry point + web resources
├── iosApp/                     # SwiftUI host + Xcode project/scheme
├── shared/                     # Kotlin Multiplatform domain + validation
│   ├── src/commonMain/         # Calculators, units, references, validation
│   └── src/commonTest/         # Cross-platform domain tests
├── docs/                       # Architecture, setup, testing, release, platform docs
├── gradle/libs.versions.toml   # Central dependency/version catalog
└── .github/workflows/          # Android, cross-platform, security, release CI
```

The `shared` module owns deterministic calculation and validation rules; platform clients do not reimplement BMI or waist-to-height formulas. `androidApp` owns Android-specific product/data behavior. `composeApp` owns the reusable calculator presentation for Apple, desktop, and browser targets. `iosApp` is the native Apple host.

## Technology stack

- Kotlin **2.4.10**.
- Kotlin Multiplatform.
- Compose Multiplatform **1.11.1**.
- Jetpack Compose UI 1.9.2 for the existing Android application.
- Material 3.
- SwiftUI/UIKit host integration for iOS/iPadOS.
- Android Gradle Plugin 8.13.2.
- Gradle 8.13 in CI.
- JDK 17.
- AndroidX DataStore Preferences for Android local settings/history.
- Kotlin Test/JUnit, Android instrumentation, Xcode simulator builds, desktop packaging checks, JS/Wasm browser compilation, CodeQL, dependency review, and secret scanning.

Versions are centrally pinned in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Quick start

### Common requirements

- Git.
- JDK 17.
- Gradle 8.13, unless an IDE-provided compatible Gradle launcher is used.

```bash
git clone https://github.com/sanskarIN/healthmetric.git
cd healthmetric
gradle :shared:desktopTest
```

### Android

Requirements: Android Studio, Android SDK Platform 36, Build Tools 35.0.0.

Debug APK:

```bash
gradle :androidApp:assembleDebug
```

Unsigned release packages:

```bash
gradle :androidApp:assembleRelease :androidApp:bundleRelease
```

Outputs:

```text
androidApp/build/outputs/apk/
androidApp/build/outputs/bundle/
```

### Windows, macOS, and Linux

Run:

```bash
gradle :composeApp:run
```

Build the native package for the current host OS:

```bash
gradle :composeApp:packageDistributionForCurrentOS
```

Configured formats are MSI on Windows, DMG on macOS, and DEB on Linux. Outputs are below `composeApp/build/compose/binaries/`.

### Web — WebAssembly

Development:

```bash
gradle :composeApp:wasmJsBrowserDevelopmentRun
```

Production:

```bash
gradle :composeApp:wasmJsBrowserProductionWebpack
```

### Web — JavaScript fallback

Development:

```bash
gradle :composeApp:jsBrowserDevelopmentRun
```

Production:

```bash
gradle :composeApp:jsBrowserProductionWebpack
```

Compatibility distribution:

```bash
gradle :composeApp:composeCompatibilityBrowserDistribution
```

### iOS / iPadOS

Apple application builds require macOS and Xcode. Open:

```text
iosApp/HealthMetric.xcodeproj
```

Select the shared `HealthMetric` scheme and an iPhone/iPad simulator. The Xcode build phase invokes the Kotlin direct-integration task:

```bash
gradle :composeApp:embedAndSignAppleFrameworkForXcode
```

Direct simulator framework build:

```bash
gradle :composeApp:linkDebugFrameworkIosSimulatorArm64
```

Production device/App Store signing stays in a protected Xcode/Apple distribution process; private signing material is never committed to the repository.

## Testing and verification

Common local checks:

```bash
gradle :shared:ktlintCheck :composeApp:ktlintCheck :androidApp:ktlintCheck
gradle :shared:desktopTest
gradle :composeApp:compileKotlinDesktop
gradle :composeApp:jsBrowserProductionWebpack
gradle :composeApp:wasmJsBrowserProductionWebpack
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintRelease
```

Android instrumentation with a connected device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS, verify the iOS framework and Xcode host:

```bash
gradle :composeApp:linkDebugFrameworkIosSimulatorArm64
xcodebuild -project iosApp/HealthMetric.xcodeproj -scheme HealthMetric -sdk iphonesimulator -configuration Debug CODE_SIGNING_ALLOWED=NO build
```

Complete local verification helpers:

```bash
bash scripts/verify.sh
```

Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

Pull requests run standard Android CI, Android emulator instrumentation, a Windows/macOS/Linux/Web/iOS cross-platform matrix, Apple shared-core compilation, CodeQL, dependency review, and repository-history secret scanning.

See [`docs/testing.md`](docs/testing.md) and [`docs/cross-platform.md`](docs/cross-platform.md).

## Privacy and local data

The shared calculators do not require an account, advertising SDK, remote service, or health-data backend. Android persistent measurement history is disabled by default. When Android history is explicitly enabled, HealthMetric stores calculated result metadata rather than raw weight/height/waist input fields.

Android backup and restore are explicit user actions. Backup reads/writes are capped at 1 MiB. Portable backups exclude history opt-in, adult-use confirmation, and onboarding completion, so importing a file cannot silently enable local collection or bypass the adult-use gate.

See [`PRIVACY.md`](PRIVACY.md) and [`docs/backup-format.md`](docs/backup-format.md).

## Build and release

Tags matching `v*` run the multi-platform release workflow. A successful tagged build prepares these GitHub Release assets:

1. Android unsigned APK.
2. Android unsigned AAB.
3. Windows MSI.
4. macOS DMG.
5. Linux DEB.
6. Web compatibility-distribution ZIP.
7. iOS ARM64 developer framework ZIP.

The iOS framework ZIP is a developer artifact, not a signed App Store IPA. Apple production archives/signing, Android store signing, desktop publisher signing/notarization where required, and deployment credentials remain outside source control in protected distribution environments.

See [`docs/release.md`](docs/release.md) and [`docs/cross-platform.md`](docs/cross-platform.md).

## Documentation

- [`docs/cross-platform.md`](docs/cross-platform.md) — complete platform build/run/package guide.
- [`docs/setup.md`](docs/setup.md) — environment setup.
- [`docs/development.md`](docs/development.md) — development workflow.
- [`docs/architecture.md`](docs/architecture.md) — architecture and boundaries.
- [`docs/testing.md`](docs/testing.md) — test matrix and regression policy.
- [`docs/release.md`](docs/release.md) — multi-platform release process.
- [`docs/troubleshooting.md`](docs/troubleshooting.md) — common problems.
- [`docs/accessibility.md`](docs/accessibility.md) — accessibility expectations.
- [`docs/evidence.md`](docs/evidence.md) — health-reference evidence workflow.
- [`SECURITY.md`](SECURITY.md) — security policy.
- [`CONTRIBUTING.md`](CONTRIBUTING.md) — contribution guide.

## Project links and contact

- GitHub: https://github.com/sanskarIN
- Repository: https://github.com/sanskarIN/healthmetric
- Buy Me a Coffee: https://buymeacoffee.com/sanskarIN
- LinkedIn: https://www.linkedin.com/in/sanskarIN
- YouTube: https://youtube.com/@Sanskar-in
- X: https://www.x.com/Sanskar_in
- Business: sanskarin@outlook.in
- Business: sanskarin.business@gmail.com
- Support: supportramsandesh@gmail.com

## License

HealthMetric is open source under the **MIT License**. See [`LICENSE`](LICENSE).
