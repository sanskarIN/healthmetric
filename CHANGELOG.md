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
- Compose Multiplatform desktop client backed by the same shared calculation core.
- Desktop adult-use gate and metric/imperial BMI plus waist-to-height calculator journeys.
- Ephemeral desktop data model: measurement inputs/results, adult-use choice, theme, and navigation state are not persisted.
- Desktop parser/integration tests plus a dedicated cross-platform desktop workflow.
- Desktop runnable JAR packaging and native-distribution configuration for supported host platforms.
- Optional privacy-first Android local history using DataStore, disabled by default until explicitly enabled.
- User-selectable Android history retention limits of 50, 100, 250, or 500 results.
- Per-entry Android history deletion with immediate snackbar undo.
- Accessible neutral Android measurement history chart with screen-reader summaries.
- Confirmation for destructive Android history deletion, complete local-data deletion, and backup restore.
- Storage Access Framework JSON backup-to-file flow in addition to explicit share export.
- Defensive JSON restore with 1 MiB backup size cap, schema validation, malformed-record recovery, duplicate-ID handling, bounded history, and chronological normalization.
- Device-local consent/safety boundary that keeps history opt-in, adult-use confirmation, and onboarding state out of portable Android backup restore.
- Locale-aware decimal parsing and numeric formatting for Android calculator inputs/results/history.
- Light, dark, system, and Android dynamic-color theming.
- Branded Android splash/launch treatment.
- Adaptive, round, and Android 13+ themed launcher icons.
- Reusable typography, shape, spacing, elevation, and motion design tokens.
- Reusable validated numeric measurement field component.
- Stable Compose semantics tags for critical Android UI automation and navigation journeys.
- Externalized Android UI strings for localization-ready presentation.
- Privacy-safe structured operational logger with fixed event names.
- Explicit in-app and system-back navigation from the Android About screen.
- Collision-resistant UUID identifiers for newly recorded Android history entries.
- GitHub Actions CI, CodeQL, dependency review, secret scanning, Android emulator instrumentation, Apple shared-core compilation, desktop verification, tagged release automation, and Dependabot.
- CI assembly and artifact upload for the debug APK, unsigned release APK, unsigned release Android App Bundle, and desktop runnable JAR.
- Tagged release workflow packaging for Android unsigned APK/App Bundle plus desktop artifacts where produced by supported runners.
- Real-app Android instrumentation capture of the eight required release-evidence screenshots, uploaded by CI as `android-release-screenshots`.
- Repository invariant checks for desktop module presence, required screenshot evidence, AAB packaging tasks/artifacts, and accidental temporary probe files.
- Domain unit, boundary, conversion, validation, deterministic property, onboarding UI, adult-gate, privacy-default, retention-policy, locale-number, bounded backup IO, and desktop calculation/parser tests.
- Instrumentation tests for BMI/ratio success and error journeys, About return navigation, privacy settings, history controls, retention, DataStore export/restore, malformed backups, consent/safety boundaries, chronology, and deletion/restore behavior.
- Repository community, security, support, privacy, desktop, design-system, evidence, and contribution documentation.

### Changed

- GitHub Actions workflow dependencies were updated to current supported major versions for checkout, Java setup, Gradle setup, CodeQL, dependency review, and artifact upload where applicable.
- Release CI now runs shared, Android, and desktop verification before producing distribution artifacts.
- Local Unix and Windows verification scripts include desktop tests/packaging and `:androidApp:bundleRelease`.
- Android history storage is normalized newest-first across new calculations, imports, and delete/undo restoration before retention limits are applied.
- Lowering the Android local history retention limit immediately trims older entries beyond the newly selected limit.
- Portable Android backups contain only portable settings/history; current history opt-in and adult-use/onboarding state remain device-local.
- Android file export generates backup content after the user selects the destination document, avoiding reliance on transient pre-launch payload state.
- Release screenshot automation resets local Android app state before capture so results are independent of instrumentation test execution order.
- Documentation now distinguishes persistent Android behavior from the intentionally ephemeral desktop client.

### Fixed

- Removed an accidentally committed temporary `docs/.noop-probe` repository-write test file.
- Fixed delete/undo of an older Android history entry incorrectly moving that entry to the top of the newest-first timeline.
- Fixed imported Android history depending on JSON array order instead of canonical timestamp order.
- Fixed the Android About screen having no in-app return path after bottom navigation was hidden.
- Removed the avoidable timestamp-plus-small-random-suffix collision path for newly recorded Android history IDs.
- Corrected release documentation drift where AAB packaging had been documented before CI/release/verification scripts actually implemented `bundleRelease`.
- Reconciled the desktop/release-readiness branch with the separate Android release-hardening branch so neither set of changes is lost.

### Security and privacy

- Android backup disabled.
- Android cleartext traffic disabled.
- Android manifest requests no Internet permission; core calculation behavior remains offline-capable.
- No advertising or analytics trackers are included.
- Android restore validates supported schema and caps history size.
- Android backup file reads and writes are capped at 1 MiB.
- Malformed Android history records are ignored individually instead of invalidating valid neighboring records.
- Duplicate/blank Android history identifiers, negative timestamps, non-finite values, and unknown calculator types are rejected during restore.
- Android local history requires explicit opt-in on fresh/default state.
- Android import cannot enable adult-only reference calculators or silently enable future history saving.
- Desktop measurement/session state is deliberately not persisted.
- Secret scanning checks repository history in CI.
- Repository invariants reject the known accidental write-probe path and verify expected release packaging/evidence/desktop configuration.

### Remaining release verification

- The exact final pull-request and release-candidate commits must complete all configured automation successfully.
- Physical Android hardware review remains required before the first public Android release.
- Manual TalkBack, maximum-font/display, keyboard/DPAD where applicable, and final visual review remain release-candidate tasks.
- CI-generated Android screenshots must receive final human visual/privacy review before permanent store/README publication.
- Android release signing remains intentionally external to source control and must be configured through a protected distribution process.
- Desktop native packages should receive host-platform smoke testing before being promoted as release assets.

## [0.1.0] - Planned

First development release candidate after the exact release commit passes automation, required platform/device review, final screenshot/accessibility review, protected Android signing setup, and the release checklist.
