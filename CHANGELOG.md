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
- Jetpack Compose Android UI with BMI, ratio, history, settings, About, and update/release access.
- Optional privacy-first local history using DataStore, disabled by default until explicitly enabled.
- Accessible neutral measurement history chart with screen-reader summaries.
- Confirmation for destructive history deletion and complete local-data deletion.
- Light, dark, system, and Android dynamic-color theming.
- Branded Android splash/launch treatment.
- Reusable typography, shape, spacing, elevation, and motion design tokens.
- Reusable validated numeric measurement field component.
- Externalized Android UI strings for localization-ready presentation.
- Local JSON export, restore, history deletion, and complete local-data deletion.
- Privacy-safe structured operational logger with fixed event names.
- GitHub Actions CI, CodeQL, dependency review, secret scanning, tagged release automation, and Dependabot.
- Domain unit, boundary, conversion, validation, deterministic property, onboarding UI, and privacy-default tests.
- Repository community, security, support, privacy, design-system, evidence, and contribution documentation.

### Security

- Android backup disabled.
- Cleartext traffic disabled.
- No Internet permission or ad/analytics trackers included.
- Restore parser validates the supported schema and caps history size.
- Local history requires explicit opt-in on fresh/default state.
- Secret scanning checks repository history in CI.

### Known verification limitation

- The coding execution environment used to create the initial implementation did not include Gradle or an Android SDK, so the Android build/lint suite is delegated to GitHub Actions. The shared calculation source was separately compiled with the available Kotlin compiler during development.
- The available GitHub connector does not expose a reliable completed workflow-run listing for these direct default-branch writes, so this document does not claim that the remote Android CI run has passed until its status is independently visible/confirmed.

## [0.1.0] - Planned

First development release candidate after CI, device/emulator testing, screenshot capture, and clean-checkout verification pass.
