# Changelog

All notable HealthMetric changes are documented here. The project follows Semantic Versioning once tagged releases begin.

## [Unreleased]

### Added

- Kotlin Multiplatform shared calculation module with Android and JVM targets.
- Adult metric and imperial BMI calculation.
- Versioned adult BMI reference profile and evidence metadata.
- Adult waist-to-height ratio calculator.
- Metric/imperial conversion helpers.
- Strict finite-value and plausible-range input validation.
- Adult-only onboarding safety gate.
- Jetpack Compose Android UI with BMI, ratio, history, settings, and About experiences.
- Optional privacy-first local history using DataStore.
- Accessible neutral measurement history chart with screen-reader summaries.
- Light, dark, system, and Android dynamic-color theming.
- Local JSON export, restore, history deletion, and complete local-data deletion.
- GitHub Actions CI, CodeQL, dependency review, tagged release automation, and Dependabot.
- Domain unit, boundary, conversion, validation, deterministic property, and onboarding UI tests.
- Repository community, security, support, privacy, and contribution documentation.

### Security

- Android backup disabled.
- Cleartext traffic disabled.
- No Internet permission or ad/analytics trackers included.
- Restore parser validates the supported schema and caps history size.

### Known verification limitation

- The coding execution environment used to create the initial implementation did not include Gradle or an Android SDK, so the Android build/lint suite is delegated to GitHub Actions. The shared calculation source was separately compiled with the available Kotlin compiler during development.

## [0.1.0] - Planned

First development release candidate after CI, device/emulator testing, screenshot capture, and clean-checkout verification pass.
