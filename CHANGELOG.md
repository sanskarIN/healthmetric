# Changelog

All notable HealthMetric changes are documented here. The project follows Semantic Versioning once tagged releases begin.

## [Unreleased]

### Added

- Kotlin Multiplatform shared calculation module with Android, JVM, iOS device, and iOS simulator targets.
- Adult metric and imperial BMI calculation.
- Versioned adult BMI reference profile and evidence metadata with explicit source review date.
- Adult waist-to-height ratio calculator.
- Metric/imperial conversion helpers.
- Strict finite-value and plausible-range input validation.
- Adult-only onboarding safety gate.
- Jetpack Compose Android UI with BMI, ratio, history, settings, About, and update/release access.
- Optional privacy-first local history using DataStore, disabled by default until explicitly enabled.
- User-selectable history retention limits of 50, 100, 250, or 500 results.
- Per-entry history deletion with immediate snackbar undo.
- Accessible neutral measurement history chart with screen-reader summaries.
- Confirmation for destructive history deletion, complete local-data deletion, and backup restore.
- Storage Access Framework JSON backup-to-file flow in addition to explicit share export.
- Defensive JSON restore with 1 MiB backup size cap, schema validation, malformed-record recovery, duplicate-ID handling, and bounded history.
- Device-local consent/safety boundary that keeps history opt-in, adult-use confirmation, and onboarding state out of portable backup restore.
- Locale-aware decimal parsing and numeric formatting for calculator inputs/results/history.
- Light, dark, system, and Android dynamic-color theming.
- Branded Android splash/launch treatment.
- Adaptive, round, and Android 13+ themed launcher icons.
- Reusable typography, shape, spacing, elevation, and motion design tokens.
- Reusable validated numeric measurement field component.
- Stable Compose semantics tags for critical UI automation journeys.
- Externalized Android UI strings for localization-ready presentation.
- Privacy-safe structured operational logger with fixed event names.
- GitHub Actions CI, CodeQL, dependency review, secret scanning, Android emulator instrumentation, Apple shared-core compilation, tagged release automation, and Dependabot.
- CI assembly and artifact upload for the debug APK, unsigned release APK, and unsigned release Android App Bundle.
- Tagged release workflow packaging for both the unsigned release APK and unsigned App Bundle.
- Domain unit, boundary, conversion, validation, deterministic property, onboarding UI, adult-gate, privacy-default, retention-policy, locale-number, and bounded backup IO tests.
- Instrumentation tests for BMI/ratio success and error journeys, privacy settings, history controls, retention, DataStore export/restore, malformed backups, consent/safety boundaries, and deletion/restore behavior.
- Repository community, security, support, privacy, design-system, evidence, and contribution documentation.

### Changed

- GitHub Actions workflow dependencies were updated to current supported major versions for checkout, Java setup, Gradle setup, CodeQL, dependency review, and artifact upload where applicable.
- Release CI now runs Android unit tests and release lint before creating unsigned APK/App Bundle artifacts.
- Local Unix and Windows verification scripts now include `:androidApp:bundleRelease`.
- Lowering the local history retention limit immediately trims older entries beyond the newly selected limit.
- Portable backups now contain only portable settings/history; current history opt-in and adult-use/onboarding state remain device-local.
- File export generates backup content after the user selects the destination document, avoiding reliance on transient pre-launch payload state.

### Security

- Android backup disabled.
- Cleartext traffic disabled.
- No Internet permission or ad/analytics trackers included.
- Restore parser validates the supported schema and caps history size.
- Backup file reads and writes are capped at 1 MiB.
- Malformed history records are ignored individually instead of invalidating valid neighboring records.
- Duplicate/blank history identifiers, negative timestamps, non-finite values, and unknown calculator types are rejected during restore.
- Local history requires explicit opt-in on fresh/default state.
- Import cannot enable adult-only reference calculators or silently enable future history saving.
- Secret scanning checks repository history in CI.

### Known verification limitation

- The initial coding execution environment did not include Gradle or an Android SDK, so authoritative Android build/lint/instrumentation verification is performed by GitHub Actions.
- Release signing remains intentionally external to source control and must be configured through a protected distribution process.
- Real device screenshots and manual accessibility evidence remain release-candidate tasks.

## [0.1.0] - Planned

First development release candidate after CI, emulator/device testing, screenshot capture, accessibility evidence, and clean-checkout verification pass.
