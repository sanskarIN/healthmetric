# Changelog

All notable HealthMetric changes are documented here. The project follows Semantic Versioning once tagged releases begin.

## [Unreleased]

### Added

- Kotlin Multiplatform shared calculation module with Android, JVM/Desktop, JavaScript, Wasm, iOS device, Intel simulator, and Apple-silicon simulator targets.
- Stable `HealthMetricEngine` façade with explicit adult-use eligibility enforcement and metric/imperial routing for platform clients.
- Adult metric and imperial BMI calculation.
- Versioned adult BMI reference profile and evidence metadata with explicit source review date.
- Adult metric and imperial waist-to-height ratio calculation.
- Metric/imperial conversion helpers.
- Strict finite-value and plausible-range input validation.
- Adult-only onboarding safety gate.
- Jetpack Compose Android UI with BMI, ratio, history, settings, About, and update/release access.
- Reusable Compose Multiplatform `sharedUI` client with an adult-use gate plus metric/imperial calculator flows for desktop, web, and iOS hosts.
- Windows, macOS, and Linux Compose Desktop host with MSI, DMG, and DEB packaging configuration.
- JavaScript and Wasm browser clients using the reusable shared UI.
- SwiftUI iOS/iPadOS host generated reproducibly with XcodeGen and backed by the `HealthMetricUI` Kotlin framework.
- Optional privacy-first local Android history using DataStore, disabled by default until explicitly enabled.
- User-selectable history retention limits of 50, 100, 250, or 500 results.
- Per-entry history deletion with immediate snackbar undo.
- Accessible neutral measurement history chart with screen-reader summaries.
- Confirmation for destructive history deletion, complete local-data deletion, and backup restore.
- Storage Access Framework JSON backup-to-file flow in addition to explicit share export.
- Defensive JSON restore with 1 MiB backup size cap, schema validation, malformed-record recovery, duplicate-ID handling, and bounded history.
- Device-local consent/safety boundary that keeps history opt-in, adult-use confirmation, and onboarding state out of portable backup restore.
- Locale-aware decimal parsing and numeric formatting for Android calculator inputs/results/history.
- Shared cross-platform measurement-input normalization helpers with common unit tests.
- Light, dark, system, and Android dynamic-color theming.
- Branded Android splash/launch treatment.
- Adaptive, round, and Android 13+ themed launcher icons.
- Reusable typography, shape, spacing, elevation, and motion design tokens.
- Reusable validated numeric measurement field component.
- Stable Compose semantics tags for critical Android UI automation journeys.
- Externalized Android UI strings for localization-ready presentation.
- Privacy-safe structured operational logger with fixed event names.
- GitHub Actions CI, CodeQL, dependency review, secret scanning, Android emulator instrumentation, Apple shared-core/UI compilation, cross-platform desktop/web builds, native desktop packaging, tagged release automation, and Dependabot.
- CI assembly and artifact upload for the debug APK, unsigned release APK, and unsigned release Android App Bundle.
- Tagged release workflow packaging for both the unsigned release APK and unsigned App Bundle.
- Domain unit, boundary, conversion, validation, deterministic property, onboarding UI, adult-gate, privacy-default, retention-policy, locale-number, bounded backup IO, engine façade, and shared UI helper tests.
- Instrumentation tests for BMI/ratio success and error journeys, privacy settings, history controls, retention, DataStore export/restore, malformed backups, consent/safety boundaries, and deletion/restore behavior.
- Repository community, security, support, privacy, design-system, evidence, architecture, cross-platform setup/testing, and contribution documentation.
- Version-specific `v0.1.0` release-candidate notes with automated/manual gate and distribution-status boundaries.
- Release-version metadata validation for Semantic Versioning, Android `versionName`/`versionCode`, changelog presence, and tag-to-artifact consistency.

### Changed

- Kotlin Android JVM target configuration now uses the typed `compilerOptions` DSL required by Kotlin 2.4.10.
- Android SDK command-line tools are provisioned explicitly with `android-actions/setup-android@v4` in every workflow that configures Android targets.
- GitHub Actions workflow dependencies were updated to current supported major versions for checkout, Java setup, Gradle setup, Android setup, CodeQL, dependency review, and artifact upload where applicable.
- Release CI now runs Android unit tests and release lint before creating unsigned APK/App Bundle artifacts.
- Main CI now builds and uploads the unsigned release App Bundle in addition to APK artifacts.
- Tagged releases now attach both unsigned APK and App Bundle outputs.
- Tagged releases now reject mismatched `v*` tags and run repository-invariant and Markdown-link audits before packaging.
- Main CI validates release metadata on every change so invalid version/changelog state is caught before tagging.
- Local Unix and Windows verification scripts now run shared UI helper tests and `:androidApp:bundleRelease`.
- Cross-platform CI now runs shared UI helper tests before platform compilation and packaging work.
- Desktop, web, and iOS shared UI now exposes both metric and imperial calculation paths instead of metric-only forms.
- Lowering the local Android history retention limit immediately trims older entries beyond the newly selected limit.
- Portable Android backups now contain only portable settings/history; current history opt-in and adult-use/onboarding state remain device-local.
- File export generates backup content after the user selects the destination document, avoiding reliance on transient pre-launch payload state.
- Repository invariant checks now guard the Kotlin compiler DSL, Android SDK setup action, cross-platform unit routes, shared UI tests, Android App Bundle release path, and release-version gate.

### Security

- Android backup disabled.
- Cleartext traffic disabled.
- No Internet permission or ad/analytics trackers included in the Android app.
- Restore parser validates the supported schema and caps history size.
- Backup file reads and writes are capped at 1 MiB.
- Malformed history records are ignored individually instead of invalidating valid neighboring records.
- Duplicate/blank history identifiers, negative timestamps, non-finite values, and unknown calculator types are rejected during restore.
- Local Android history requires explicit opt-in on fresh/default state.
- Import cannot enable adult-only reference calculators or silently enable future history saving.
- Secret scanning checks repository history in CI.

### Known verification limitation

- The coding execution environment does not provide a usable Android/Gradle toolchain or outbound repository clone access, so authoritative Android, desktop, web, Apple, lint, packaging, and instrumentation verification is performed by GitHub Actions.
- Release signing remains intentionally external to source control and must be configured through a protected distribution process.
- Real device screenshots and manual accessibility evidence remain release-candidate tasks.

## [0.1.0] - Planned

First development release candidate after CI, emulator/device testing, screenshot capture, accessibility evidence, and clean-checkout verification pass.
