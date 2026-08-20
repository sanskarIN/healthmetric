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

**HealthMetric** is an open-source, privacy-first adult BMI and health measurement calculator built with Kotlin, Jetpack Compose, Compose Multiplatform, SwiftUI, and a shared Kotlin Multiplatform calculation core.

> [!IMPORTANT]
> HealthMetric's BMI and waist-to-height tools are intended for adults age 18 or older. Results are educational screening information only. They are not medical diagnoses, appearance scores, or personal body targets.

**Made by the Sanskar**

## Cross-platform support

HealthMetric now has first-class application targets for the major mobile, desktop, and browser platforms while keeping one validated health calculation domain.

| Platform | Support | Application technology | Primary output |
|---|---|---|---|
| Android | ✅ Full | Jetpack Compose | APK / AAB |
| iPhone / iPad | ✅ Full calculator client | Compose Multiplatform hosted by SwiftUI | Xcode app / IPA through normal Apple signing |
| Windows | ✅ Full calculator client | Compose Desktop | MSI |
| macOS | ✅ Full calculator client | Compose Desktop | DMG |
| Linux | ✅ Full calculator client | Compose Desktop | DEB |
| Web — WebAssembly | ✅ Supported | Compose Multiplatform Wasm | Browser distribution |
| Web — JavaScript fallback | ✅ Supported | Compose Multiplatform JS | Browser distribution |
| ChromeOS | ✅ Supported through Android where Android apps are available | Android app | APK / store install |

The Android client remains the most feature-complete platform-specific client: it additionally includes opt-in persistent history, retention controls, JSON backup/restore, Android dynamic color, and Android platform integrations. The new iOS, desktop, and web clients currently provide the shared adult-use gate plus metric/imperial BMI and waist-to-height calculator experience. Platform support and buildability are enforced by `.github/workflows/cross-platform.yml`; feature-parity work can continue independently without duplicating calculation rules.

See **[`docs/cross-platform.md`](docs/cross-platform.md)** for the complete build, run, packaging, Xcode, browser, CI, and troubleshooting guide.

## Why HealthMetric?

HealthMetric is designed as a maintainable product rather than a one-screen calculator demo. The repository provides a deterministic shared domain, strict validation, adult-only safety gates, privacy-first defaults, accessible neutral presentation, Android local-data controls, reproducible builds, automated tests, security checks, and cross-platform CI.

## Core features

- Adult metric and imperial BMI calculation.
- Versioned adult BMI reference metadata with reviewed evidence information.
- Adult waist-to-height ratio calculation presented without appearance rankings or pressure-oriented goals.
- Strict finite-number and plausible-range input validation.
- Accurate metric/imperial conversions in the shared Kotlin Multiplatform domain.
- Adult-use confirmation before adult reference calculators are available.
- Offline/local calculation behavior with no ad SDK or advertising tracker requirement.
- Shared Compose calculator UI for iOS, Windows, macOS, Linux, JavaScript web, and WebAssembly web.
- Native SwiftUI host project for iPhone/iPad.
- Native desktop packaging for MSI, DMG, and DEB.
- JavaScript and Wasm browser production builds.

### Android-specific product features

- Optional local calculation history, **disabled by default** until explicitly enabled.
- Retention limits of 50, 100, 250, or 500 results.
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
├── androidApp/                 # Mature Android app + Android persistence/integrations
├── composeApp/                 # Shared iOS/Desktop/Web Compose application
│   ├── src/commonMain/         # Cross-platform calculator UI and adult-use gate
│   ├── src/desktopMain/        # Windows/macOS/Linux entry point
│   ├── src/iosMain/            # UIKit controller bridge
│   └── src/webMain/            # JS/Wasm browser entry point
├── iosApp/                     # SwiftUI host + Xcode project/scheme
├── shared/                     # Kotlin Multiplatform domain and validation
│   ├── src/commonMain/         # Calculators, units, reference model, validation
│   └── src/commonTest/         # Cross-platform domain tests
├── docs/                       # Architecture, setup, testing, release and platform docs
├── gradle/libs.versions.toml   # Central dependency/version catalog
└── .github/workflows/          # Android, cross-platform, security and release CI
```

The `shared` module owns deterministic health calculation and validation rules. No platform reimplements BMI or waist-to-height math. `androidApp` owns the mature Android-specific product/data layer. `composeApp` provides a reusable UI for Apple, desktop, and browser targets, and `iosApp` is the native Apple host.

## Technology stack

- Kotlin 2.2.20.
- Kotlin Multiplatform.
- Compose Multiplatform 1.11.1.
- Jetpack Compose UI 1.9.2 for the existing Android app.
- Material 3.
- SwiftUI/UIKit host integration for iOS/iPadOS.
- Android Gradle Plugin 8.13.2.
- Gradle 8.13 in CI.
- AndroidX DataStore Preferences for Android local settings/history.
- JUnit/Kotlin Test, Android instrumentation, Xcode simulator builds, desktop packaging checks, JS/Wasm browser compilation, CodeQL, dependency review, and secret scanning.

Versions are centrally pinned in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Quick start

### Common requirements

- Git.
- JDK 17.
- Gradle 8.13, unless an IDE-provided compatible Gradle launcher is being used.

Clone once:

```bash
git clone https://github.com/sanskarIN/healthmetric.git
cd healthmetric
```

Verify the shared domain:

```bash
gradle :shared:desktopTest
```

### Android

Requirements: Android Studio, Android SDK Platform 36, Build Tools 35.0.0.

```bash
gradle :androidApp:assembleDebug
```

Debug APK:

```text
androidApp/build/outputs/apk/debug/
```

Unsigned release APK:

```bash
gradle :androidApp:assembleRelease
```

Unsigned Android App Bundle:

```bash
gradle :androidApp:bundleRelease
```

### Windows, macOS, and Linux desktop

Run the desktop application:

```bash
gradle :composeApp:run
```

Build the installer/package appropriate for the current operating system:

```bash
gradle :composeApp:packageDistributionForCurrentOS
```

Configured formats are MSI on Windows, DMG on macOS, and DEB on Linux. Outputs are placed under `composeApp/build/compose/binaries/`.

### Web — WebAssembly

Development server:

```bash
gradle :composeApp:wasmJsBrowserDevelopmentRun
```

Production build:

```bash
gradle :composeApp:wasmJsBrowserProductionWebpack
```

### Web — JavaScript fallback

Development server:

```bash
gradle :composeApp:jsBrowserDevelopmentRun
```

Production build:

```bash
gradle :composeApp:jsBrowserProductionWebpack
```

Build the compatibility distribution that can serve the Wasm path with a JS fallback:

```bash
gradle :composeApp:composeCompatibilityBrowserDistribution
```

### iOS / iPadOS

Apple builds require macOS and Xcode. Open:

```text
iosApp/HealthMetric.xcodeproj
```

Select the shared `HealthMetric` scheme and an iPhone/iPad simulator. The Xcode build phase invokes:

```bash
gradle :composeApp:embedAndSignAppleFrameworkForXcode
```

For a direct Kotlin simulator-framework build:

```bash
gradle :composeApp:linkDebugFrameworkIosSimulatorArm64
```

Production device/archive signing stays in Xcode and must use the developer's protected Apple signing identity; signing credentials are never committed to this repository.

## Testing and verification

Local common checks:

```bash
gradle :shared:ktlintCheck :composeApp:ktlintCheck :androidApp:ktlintCheck
gradle :shared:desktopTest
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintRelease
```

Cross-platform build checks:

```bash
gradle :composeApp:compileKotlinDesktop
gradle :composeApp:jsBrowserProductionWebpack
gradle :composeApp:wasmJsBrowserProductionWebpack
```

Android instrumentation requires an Android device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS, verify the iOS framework and Xcode host with:

```bash
gradle :composeApp:linkDebugFrameworkIosSimulatorArm64
xcodebuild -project iosApp/HealthMetric.xcodeproj -scheme HealthMetric -sdk iphonesimulator -configuration Debug CODE_SIGNING_ALLOWED=NO build
```

Pull requests run separate standard Android, Android-emulator, cross-platform, CodeQL, dependency-review, secret-scan, and Apple/shared checks.

See [`docs/testing.md`](docs/testing.md) and [`docs/cross-platform.md`](docs/cross-platform.md).

## Privacy and local data

The shared calculators do not need an account, advertising SDK, or remote service. Android persistent measurement history is disabled by default. When Android history is explicitly enabled, HealthMetric stores calculated result metadata rather than raw weight/height/waist input fields.

Android backup and restore are explicit user actions. Backup reads/writes are capped at 1 MiB. Portable backups exclude history opt-in, adult-use confirmation, and onboarding completion, so importing a file cannot silently enable local collection or bypass the adult-use gate.

See [`PRIVACY.md`](PRIVACY.md) and [`docs/backup-format.md`](docs/backup-format.md).

## Build and release

Android tagged releases continue to produce unsigned Android artifacts that must be signed through a protected release process. Desktop packages are generated per host operating system. iOS distribution requires normal Apple signing/archive steps. Web production distributions can be deployed as static browser assets.

See [`docs/release.md`](docs/release.md) and [`docs/cross-platform.md`](docs/cross-platform.md) for platform-specific distribution commands.

## Documentation

- [`docs/cross-platform.md`](docs/cross-platform.md) — complete platform build/run/package guide.
- [`docs/setup.md`](docs/setup.md) — development environment setup.
- [`docs/development.md`](docs/development.md) — development workflow.
- [`docs/architecture.md`](docs/architecture.md) — architecture and boundaries.
- [`docs/testing.md`](docs/testing.md) — test matrix and regression policy.
- [`docs/release.md`](docs/release.md) — release process.
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
