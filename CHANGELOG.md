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
- Recoverable Android adult-use selection so an accidental under-18 choice can return to age selection without clearing unrelated local data.
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
- Overflow-safe Android history-chart normalization for extreme finite imported values.
- Confirmation for destructive Android history deletion, complete local-data deletion, and backup restore.
- Storage Access Framework JSON backup-to-file flow in addition to explicit share export.
- Defensive JSON restore with 1 MiB backup size cap, schema/top-level structure validation, malformed-record recovery, duplicate-ID handling, bounded history, chronological normalization, and fail-closed all-invalid backup handling.
- Device-local consent/safety boundary that keeps history opt-in, adult-use confirmation, and onboarding state out of portable Android backup restore.
- Locale-aware decimal parsing and numeric formatting for Android calculator inputs/results/history.
- Light, dark, system, and Android dynamic-color theming.
- Branded Android splash/launch treatment.
- Adaptive, round, and Android 13+ themed launcher icons.
- Reusable typography, shape, spacing, elevation, and motion design tokens.
- Reusable validated numeric measurement field component.
- Stable Compose semantics tags for critical Android UI automation, navigation journeys, and adult-gate correction controls.
- Defensive saved-enum restoration for Android navigation/history filter state after future enum changes.
- Externalized Android UI strings for localization-ready presentation.
- Privacy-safe structured operational logger with fixed event names.
- Explicit in-app and system-back navigation from the Android About screen.
- Collision-resistant UUID identifiers for newly recorded Android history entries.
- GitHub Actions CI, CodeQL, dependency review, secret scanning, Android emulator instrumentation, Apple shared-core compilation, desktop verification, tagged release automation, and Dependabot.
- CI assembly and artifact upload for the debug APK, unsigned release APK, unsigned release Android App Bundle, and desktop runnable JAR.
- Tagged release workflow packaging for Android unsigned APK/App Bundle plus desktop artifacts where produced by supported runners.
- Stable-tag/project-version validator for Android and desktop release versions.
- Deterministic cross-platform release-asset staging with exact-single-output and non-empty checks.
- Exact release-asset set verification plus generated `SHA256SUMS.txt` for published binaries.
- Python regression tests for release tag validation, asset staging, and final asset/checksum verification.
- Real-app Android instrumentation capture of the eight required release-evidence screenshots, uploaded by CI as `android-release-screenshots`.
- Repository invariant checks for desktop module presence, required screenshot evidence, AAB packaging tasks/artifacts, hardened release tooling, chart safety, adult-gate correction, backup structure, and accidental temporary probe files.
- Exhaustive `docs/repository-file-reference.md` documenting every tracked repository file by exact path, responsibility, and maintenance boundary.
- `docs/documentation-map.md` defining canonical documentation ownership, audience entry points, ADR policy, and a change-to-document update matrix.
- Automated tracked-file documentation coverage: repository verification compares `git ls-files` with the exhaustive file reference and fails when a tracked file is undocumented.
- Domain unit, boundary, conversion, validation, deterministic property, onboarding UI, adult-gate, privacy-default, retention-policy, locale-number, bounded backup IO, and desktop calculation/parser tests.
- Instrumentation tests for BMI/ratio success and error journeys, About return navigation, privacy settings, history controls, retention, adult-gate controls, DataStore export/restore, malformed backups, consent/safety boundaries, chronology, and deletion/restore behavior.
- Repository community, security, support, privacy, desktop, architecture, setup, testing, release, design-system, evidence, governance, troubleshooting, and contribution documentation.

### Changed

- Release target is now **HealthMetric 2.0.12** across Android `versionName`, desktop project version, and desktop native `packageVersion`.
- Android `versionCode` is now `20012`, following the repository's `MAJOR * 10000 + MINOR * 100 + PATCH` mapping for two-digit minor/patch components.
- Release validation now checks Android `versionName`, Android `versionCode`, desktop project version, desktop native `packageVersion`, and the stable release tag as one consistency boundary.
- GitHub Actions workflow dependencies were updated to current supported major versions for checkout, Java/Python setup, Gradle setup, CodeQL, dependency review, and artifact upload where applicable.
- Release CI now runs shared, Android, and desktop verification before producing distribution artifacts.
- Tagged release automation now performs a read-only preflight before builds, requires the tag to match all configured release versions, and requires the tag to target the current `main` commit.
- Release build jobs now use tested Python staging logic instead of duplicated shell-specific artifact discovery.
- The final release publication step rejects missing, extra, or empty binary assets before creating the GitHub Release.
- Release workflow repository permissions are read-only by default; `contents: write` is scoped only to the final publication job.
- Local Unix and Windows verification scripts include repository-tooling regression tests, desktop tests/packaging, and `:androidApp:bundleRelease`.
- Android history storage is normalized newest-first across new calculations, imports, and delete/undo restoration before retention limits are applied.
- Lowering the Android local history retention limit immediately trims older entries beyond the newly selected limit.
- Portable Android backups contain only portable settings/history; current history opt-in and adult-use/onboarding state remain device-local.
- Schema-v1 Android restore now requires the documented top-level `history` value to be a JSON array before any DataStore mutation; an explicit empty array is valid, while a non-empty array that yields zero valid entries is rejected before mutation.
- Android file export generates backup content after the user selects the destination document, avoiding reliance on transient pre-launch payload state.
- Release screenshot automation resets local Android app state before capture so results are independent of instrumentation test execution order.
- Desktop measurement parsing accepts ordinary dot/comma decimal syntax while rejecting scientific notation, signed input, malformed separators, and non-finite literals.
- Desktop split imperial height now treats the second field as a true remaining-inch component and rejects values outside `[0, 12)` instead of silently normalizing them into extra feet.
- Documentation now distinguishes persistent Android behavior from the intentionally ephemeral desktop client and documents release-integrity/checksum gates.
- README, setup, development, architecture, desktop, testing, release, troubleshooting, privacy, security, repository governance, contribution guidance, and the PR template now cross-reference one canonical documentation ownership system.
- Repository documentation now treats every tracked source, test, resource, build file, workflow, script, configuration file, ADR, and documentation asset as an explicitly owned file rather than documenting only major modules.

### Fixed

- Removed an accidentally committed temporary `docs/.noop-probe` repository-write test file.
- Fixed delete/undo of an older Android history entry incorrectly moving that entry to the top of the newest-first timeline.
- Fixed imported Android history depending on JSON array order instead of canonical timestamp order.
- Fixed the Android About screen having no in-app return path after bottom navigation was hidden.
- Fixed an accidental under-18 Android selection requiring complete app-data clearing before age selection could be revisited.
- Fixed stale saved Android navigation/history-filter enum values being able to crash composition after enum changes.
- Fixed extreme finite imported Android history values being able to overflow chart range arithmetic.
- Fixed malformed Android backups with a missing or non-array top-level `history` value being accepted as empty history and potentially replacing valid local history/preferences after confirmation.
- Fixed non-empty Android backups containing zero valid history entries being accepted as intentional empty history and potentially replacing valid portable local data.
- Fixed malformed UTF-8 Android backup bytes being silently replacement-decoded instead of rejected at the document boundary.
- Fixed imperial weight validation reporting kilogram bounds instead of pound bounds.
- Fixed imperial waist validation reporting metric bounds instead of inch bounds.
- Fixed imperial BMI revalidating converted values against metric boundaries after valid imperial validation, which could reject documented pound-boundary inputs.
- Fixed imperial waist-to-height calculation revalidating converted values through the metric path instead of preserving the imperial validation contract.
- Fixed desktop imperial forms accepting invalid remaining-inch components such as `12` or `20` and converting them into a different total height.
- Removed the avoidable timestamp-plus-small-random-suffix collision path for newly recorded Android history IDs.
- Corrected release documentation drift where AAB packaging had been documented before CI/release/verification scripts actually implemented `bundleRelease`.
- Corrected setup documentation that omitted Python 3 even though repository/release verification depends on Python tooling.
- Corrected troubleshooting documentation that understated native desktop CI/release packaging and omitted current backup/documentation-integrity diagnostics.
- Reconciled the desktop/release-readiness branch with the separate Android release-hardening branch so neither set of changes is lost.

### Security and privacy

- Android application backup disabled.
- Android cleartext traffic disabled.
- Android manifest requests no Internet permission; core calculation behavior remains offline-capable.
- No advertising or analytics trackers are included.
- Android restore validates supported schema, requires a structurally valid top-level history array before mutation, distinguishes explicit empty history from corrupted all-invalid history, and caps final history size.
- Android backup file reads and writes are capped at 1 MiB, and malformed UTF-8 input is rejected instead of replacement-decoded.
- Malformed Android history records inside a valid history array are ignored individually when valid neighboring entries survive.
- Duplicate/blank Android history identifiers, negative timestamps, non-finite values, and unknown calculator types are rejected during restore.
- Android local history requires explicit opt-in on fresh/default state.
- Android import cannot enable adult-only reference calculators or silently enable future history saving.
- Adult-use-choice correction clears only adult/onboarding state and preserves unrelated local history/preferences.
- Extreme finite imported history values cannot overflow the chart normalization path.
- Stale Android saved enum names cannot directly crash navigation/filter state restoration.
- Desktop measurement/session state is deliberately not persisted.
- Desktop input rejects ambiguous/signed/scientific/non-finite measurement syntax and invalid split-height remaining-inch components.
- Secret scanning checks repository history in CI.
- Release tags fail closed unless they use stable SemVer form, match configured Android/desktop versions and Android version-code mapping, and target current `main`.
- Release asset publication fails closed on missing, unexpected, or empty files and publishes SHA-256 checksums.
- Release workflow write privilege is isolated to the final publication job.
- Repository invariants reject the known accidental write-probe path, verify expected release packaging/evidence/desktop/security configuration, and require documentation for every tracked file.

### Documentation and governance

- Added a canonical documentation map that identifies which file owns each detailed product, privacy, architecture, evidence, testing, release, governance, and continuation contract.
- Added an exhaustive file-by-file repository reference covering root metadata, GitHub automation/templates, Android production/resources/tests, shared code/tests, desktop code/tests, Gradle catalog, scripts/tests, all project documents, ADRs, logo and screenshot policy.
- Contributor instructions and the pull-request checklist now require file-reference reconciliation for every tracked-file add/delete/rename.
- Repository governance explicitly treats file-level documentation completeness as a CI-enforced rule.
- Release documentation treats exact tracked-file documentation and canonical-document agreement as pre-tag gates.
- Troubleshooting explains how to diagnose undocumented tracked files and why copied source directories without Git metadata cannot satisfy the repository audit.

### Remaining release verification

- The exact final pull-request and release-candidate commits must complete all configured automation successfully.
- Physical Android hardware review remains required before the public `2.0.12` Android release.
- Manual TalkBack, maximum-font/display, keyboard/DPAD where applicable, and final visual review remain release-candidate tasks.
- CI-generated Android screenshots must receive final human visual/privacy review before permanent store/README publication.
- Android release signing remains intentionally external to source control and must be configured through a protected distribution process.
- Desktop native packages should receive host-platform smoke/accessibility/install-uninstall testing before being promoted as release assets.
- Desktop production signing/notarization, if used, remains external to source control.

## [2.0.12] - Planned

Release candidate for HealthMetric `2.0.12`. Publish `v2.0.12` only after the exact release commit passes automation, required platform/device review, final screenshot/accessibility review, protected signing/trust setup, and the release checklist.
