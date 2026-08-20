<p align="center">
  <img src="docs/assets/logo.svg" alt="HealthMetric" width="720" />
</p>

# HealthMetric

[![CI](https://github.com/sanskarIN/healthmetric/actions/workflows/ci.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/ci.yml)
[![Cross-platform](https://github.com/sanskarIN/healthmetric/actions/workflows/cross-platform.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/cross-platform.yml)
[![Android instrumentation](https://github.com/sanskarIN/healthmetric/actions/workflows/android-instrumentation.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/android-instrumentation.yml)
[![Apple](https://github.com/sanskarIN/healthmetric/actions/workflows/apple-shared.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/apple-shared.yml)
[![CodeQL](https://github.com/sanskarIN/healthmetric/actions/workflows/codeql.yml/badge.svg)](https://github.com/sanskarIN/healthmetric/actions/workflows/codeql.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-sanskarIN-FFDD00?logo=buy-me-a-coffee&logoColor=000000)](https://buymeacoffee.com/sanskarIN)

**HealthMetric** is an open-source, privacy-first adult health-measurement toolkit built with Kotlin Multiplatform, Jetpack Compose, Compose Multiplatform, and SwiftUI.

> [!IMPORTANT]
> HealthMetric's BMI and waist-to-height reference tools are intended for adults age 18 or older. Results are educational screening information only. They are not medical diagnoses, appearance scores, or personal body targets.

**Made by the Sanskar**

## What makes HealthMetric different?

HealthMetric treats a small calculator as a real software product rather than a demo. The repository separates deterministic health-domain rules from platform UI, keeps Android history opt-in and local, applies bounded backup handling, verifies adult-use safety rules, supports locale-aware input, and now exposes the same adult-safe calculation engine across Android, desktop, web, and Apple targets.

The cross-platform clients deliberately stay simple: calculations execute locally in the running client, no analytics or advertising SDK is added, and no account or backend is required for the calculator experience.

## Platform support

| Platform | Status | Implementation |
|---|---|---|
| Android | Primary / feature-complete | Native Jetpack Compose app with opt-in DataStore history, backup/restore, settings, accessibility semantics, and instrumentation tests |
| Windows | Beta client | Compose Desktop JVM app; native MSI packaging configured |
| macOS | Beta client | Compose Desktop JVM app; native DMG packaging configured |
| Linux | Beta client | Compose Desktop JVM app; native DEB packaging configured |
| Web — Wasm | Beta client | Compose Multiplatform browser executable using `ComposeViewport` |
| Web — JavaScript | Compatibility client | JavaScript browser executable built from the same shared UI |
| iOS / iPadOS | Beta host | SwiftUI host generated with XcodeGen and backed by the reusable `HealthMetricUI` Kotlin framework |
| Shared Kotlin core | Stable foundation | Android, JVM/Desktop, JS, Wasm, iOS device, Intel simulator, and Apple-silicon simulator targets |

Android remains the richest client because local history, Android document-provider backup/restore, and platform settings are Android-specific today. Desktop, web, and iOS share the adult gate and calculator experience through `sharedUI`; persistence parity is intentionally tracked as future work rather than pretending platform-specific storage already exists.

## Core features

- Adult BMI calculation in metric and imperial units in the domain engine.
- Adult waist-to-height ratio calculation with neutral educational output.
- Stable `HealthMetricEngine` façade so platform clients use identical age eligibility and calculation routing.
- Versioned adult BMI reference metadata with explicit evidence review date.
- Strict finite-number and plausible-range validation.
- Shared metric/imperial conversion helpers.
- Adult-use eligibility gate before adult reference calculators are exposed.
- No appearance scoring, body ranking, or pressure-oriented body targets.
- No required account, backend, advertising SDK, or analytics tracker.

### Android product features

- Optional local calculation history, **disabled by default** until explicit opt-in.
- User-selectable history retention limits: 50, 100, 250, or 500 results.
- Per-entry deletion with immediate undo.
- Accessible history chart whose meaning is not communicated by color alone.
- JSON backup through Android's document picker and explicit share chooser.
- Defensive restore: 1 MiB IO bound, schema validation, bounded records, malformed-record recovery, duplicate-ID protection, and restore confirmation.
- Portable backups cannot alter current history opt-in, adult-use confirmation, or onboarding safety state.
- Locale-aware decimal input plus locale-aware result/history formatting.
- Full local-data deletion returning the app to first-run privacy defaults.
- Light, dark, system, and supported Android dynamic-color presentation.
- Branded splash, adaptive/round launcher icons, and Android 13+ themed icon support.
- About/support/funding and update links.

## Repository architecture

```text
HealthMetric/
├── shared/          # Pure Kotlin Multiplatform health domain + tests
├── sharedUI/        # Reusable Compose Multiplatform adult gate/calculator UI
├── androidApp/      # Full Android product client and local persistence
├── desktopApp/      # Windows/macOS/Linux Compose Desktop launcher + packaging
├── webApp/          # JavaScript and Wasm browser launchers
├── iosApp/          # SwiftUI host + reproducible XcodeGen project specification
├── docs/            # Architecture, setup, testing, evidence, privacy, release docs
├── scripts/         # Cross-platform local verification helpers
└── .github/         # Build, emulator, Apple, security, and release automation
```

### Ownership boundaries

- `shared` owns calculations, conversions, validation, adult eligibility, reference metadata, and primitive result summaries.
- `sharedUI` owns only reusable presentation for desktop/web/iOS. It does not own storage or platform integrations.
- `androidApp` owns Android persistence, locale presentation helpers, document/share intents, settings, and Android-specific UI flows.
- `desktopApp`, `webApp`, and `iosApp` are intentionally thin hosts over `sharedUI`.

See [`docs/architecture.md`](docs/architecture.md) and the [`docs/adr/`](docs/adr/) directory.

## Tech stack

- Kotlin 2.4.10
- Kotlin Multiplatform
- Compose Multiplatform 1.11.0
- Android Gradle Plugin 8.13.2
- Gradle 8.13 in CI and documented local setup
- Jetpack Compose UI 1.9.2 for the Android client
- Material 3 1.4.0
- AndroidX DataStore Preferences 1.2.1
- AndroidX Lifecycle 2.9.4
- kotlinx.coroutines 1.10.2
- SwiftUI + XcodeGen for the iOS host
- ktlint 14.2.0
- JUnit, Compose UI tests, Android instrumentation, cross-platform compilation, CodeQL, dependency review, and secret scanning

Versions are centrally pinned in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Requirements

### Common / Android / desktop / web

- Git
- JDK 17
- Gradle 8.13 available as `gradle`
- Android SDK Platform 36 and Build Tools 35.0.0 because the multi-project build configures Android targets

Android development additionally uses Android Studio. Desktop and web development can use IntelliJ IDEA or Android Studio with Kotlin support.

### iOS

- macOS
- Xcode with iOS SDKs
- JDK 17
- Gradle 8.13
- XcodeGen

The repository currently uses system Gradle in development/CI. If a verified Gradle wrapper is added later, the iOS build phase automatically prefers `./gradlew`.

## Quick start

```bash
git clone https://github.com/sanskarIN/healthmetric.git
cd healthmetric
gradle :shared:desktopTest
```

Run the complete non-device cross-platform verification suite:

```bash
bash scripts/verify.sh
```

Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

Both scripts accept `GRADLE_BIN` when Gradle is installed under a non-default executable name.

## Run Android

Build a debug APK:

```bash
gradle :androidApp:assembleDebug
```

Run the `androidApp` configuration from Android Studio, or install the generated APK from:

```text
androidApp/build/outputs/apk/debug/
```

Connected tests:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

## Run desktop

```bash
gradle :desktopApp:run
```

Build the native distribution supported by the current host OS:

```bash
gradle :desktopApp:packageDistributionForCurrentOS
```

The Gradle configuration declares DMG, MSI, and DEB formats. Native installer packaging should be performed on the corresponding operating system.

## Run web

Wasm development server:

```bash
gradle :webApp:wasmJsBrowserDevelopmentRun
```

JavaScript compatibility development server:

```bash
gradle :webApp:jsBrowserDevelopmentRun
```

Production bundles:

```bash
gradle :webApp:wasmJsBrowserProductionWebpack
gradle :webApp:jsBrowserProductionWebpack
```

The browser clients calculate locally and do not add an application backend or telemetry SDK.

## Run iOS / iPadOS

Generate the Xcode project:

```bash
brew install xcodegen
cd iosApp
xcodegen generate
open HealthMetricIOS.xcodeproj
```

Select the `HealthMetric` scheme and an iPhone/iPad simulator or configured device. The Xcode pre-build phase invokes `:sharedUI:embedAndSignAppleFrameworkForXcode`; it prefers `./gradlew` when present and otherwise uses the documented `gradle` executable.

CI also regenerates the project and performs an unsigned simulator build.

## Testing and CI

Local cross-platform verification includes:

```bash
gradle :shared:desktopTest
gradle :shared:compileKotlinJs :shared:compileKotlinWasmJs
gradle :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinJs :sharedUI:compileKotlinWasmJs
gradle :desktopApp:compileKotlin
gradle :webApp:jsBrowserProductionWebpack :webApp:wasmJsBrowserProductionWebpack
gradle :androidApp:testDebugUnitTest :androidApp:lintRelease :androidApp:assembleDebug :androidApp:assembleRelease
```

On macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
gradle :sharedUI:linkDebugFrameworkIosSimulatorArm64 :sharedUI:linkDebugFrameworkIosArm64
```

Pull requests use separate workflows for:

- standard Android/JVM quality checks;
- desktop/JavaScript/Wasm compilation and web production artifacts;
- Android emulator instrumentation;
- iOS core/UI framework compilation plus generated SwiftUI-host build;
- CodeQL;
- dependency review;
- full-history secret scanning.

See [`docs/testing.md`](docs/testing.md).

## Privacy model

HealthMetric is designed so core calculations require no account or application backend.

### Android

- Android manifest requests no `INTERNET` permission.
- Application backup is disabled.
- Cleartext traffic is disabled.
- Measurement history starts disabled and requires explicit opt-in.
- History retention is bounded and user-controlled.
- Raw weight, height, and waist values are not stored in history.
- Export/import actions are explicit and bounded.

### Desktop, web, and iOS

The current cross-platform calculator UI keeps form values and results in transient UI state only. It does not add history persistence, advertising, analytics, or cloud synchronization. Web assets of course must be delivered to the browser by whatever hosting environment the distributor chooses, but the calculator itself does not require a HealthMetric backend.

Read [`PRIVACY.md`](PRIVACY.md), [`SECURITY.md`](SECURITY.md), and [`docs/backup-format.md`](docs/backup-format.md).

## Accessibility

HealthMetric uses neutral, non-appearance-oriented copy and aims for semantic labels, scalable text, keyboard/touch compatibility, non-color-only meaning, and bounded responsive layouts. Android has the deepest current accessibility test coverage; manual platform accessibility verification remains part of release-candidate work.

See [`docs/accessibility.md`](docs/accessibility.md).

## Screenshots and release evidence

Real release screenshots and final manual accessibility evidence remain release-candidate tasks. Use fictional/example measurements only in screenshots and bug reports. Capture requirements are documented in [`docs/assets/screenshots/README.md`](docs/assets/screenshots/README.md).

## Release

Android tagged releases remain the only automated public binary release path today. Desktop/web/iOS modules are buildable beta clients and should not be represented as store-published binaries until their platform-specific signing, manual accessibility, screenshots, and release validation are complete.

See [`docs/release.md`](docs/release.md) and [`ROADMAP.md`](ROADMAP.md).

## Contributing

Contributions are welcome. Start with [`CONTRIBUTING.md`](CONTRIBUTING.md), follow the Code of Conduct, and keep changes focused and testable.

Do not submit private health information. Use fictional/example values in issues, screenshots, and tests.

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
