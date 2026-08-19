# Repository File Reference

This is the exhaustive tracked-file reference for HealthMetric. Every file tracked by Git is listed by its exact repository-relative path with its responsibility, important boundaries, and the documentation or tests that should move with it.

The repository invariant audit compares this document with `git ls-files`. Adding a tracked file without documenting it, or deleting/renaming a file without reconciling this inventory, is a verification failure.

## Root files

- `.editorconfig` — Repository-wide editor formatting defaults: UTF-8, LF line endings, final newlines, whitespace rules, and indentation overrides for structured text. Keep it aligned with ktlint and the formatting expected by CI.
- `.env.example` — Safe template for optional local environment configuration. It must contain examples/placeholders only and must never become a storage location for credentials or signing material.
- `.gitattributes` — Git text/line-ending behavior and repository attribute policy. Changes can affect cross-platform reproducibility and should be reviewed with Windows/macOS/Linux development in mind.
- `.gitignore` — Excludes local IDE state, build outputs, secrets, machine-specific files, and generated artifacts from version control. Release binaries are created by automation rather than committed.
- `CHANGELOG.md` — Unreleased/release history for user-visible, developer-visible, security/privacy, and release-process changes. Update it with meaningful behavior changes rather than raw commit logs.
- `CODE_OF_CONDUCT.md` — Community participation and conduct expectations for project spaces.
- `CONTRIBUTING.md` — Contributor workflow, quality gates, scope boundaries, security/privacy expectations, and submission guidance.
- `LICENSE` — MIT license governing the repository's source and documentation unless a file states otherwise.
- `PRIVACY.md` — Canonical privacy statement. It distinguishes Android's optional local persistence/portable backup from the desktop client's intentionally ephemeral HealthMetric state.
- `README.md` — Primary project landing page: scope, platform status, privacy posture, features, build/test entry points, documentation links, project metadata, and limitations.
- `ROADMAP.md` — Milestone status. Completed boxes must correspond to implemented behavior; physical-device, accessibility, signing, notarization, and human-review gates remain open until actually completed.
- `SECURITY.md` — Security policy, reporting route, threat/scope assumptions, and secret-handling expectations.
- `SUPPORT.md` — User/contributor support routing and expectations for bugs, feature requests, security reports, and general help.
- `build.gradle.kts` — Root Gradle build configuration and shared plugin declarations used by modules.
- `gradle.properties` — Gradle/Kotlin/Android build properties that apply across the repository. Treat changes as build-system changes and verify all supported targets.
- `settings.gradle.kts` — Gradle project composition, plugin/dependency repositories, and module inclusion for `shared`, `androidApp`, and `desktopApp`.
- `what_changed.md` — Long-form continuation/handoff ledger. It records implementation decisions, fixes, commit strategy, verification state, and remaining release blockers without replacing the changelog.

## GitHub repository configuration

- `.github/FUNDING.yml` — GitHub Sponsors/Funding configuration for the project's supported funding link.
- `.github/dependabot.yml` — Dependabot update configuration for Gradle and GitHub Actions dependencies.
- `.github/PULL_REQUEST_TEMPLATE.md` — Pull-request checklist covering scope, tests, privacy/safety, documentation, release impact, and evidence requirements.
- `.github/RELEASE_TEMPLATE.md` — Human release checklist/notes scaffold. It complements, rather than replaces, automated release gates.
- `.github/ISSUE_TEMPLATE/bug_report.yml` — Structured bug-report form for reproducible defects, environment details, and expected/actual behavior.
- `.github/ISSUE_TEMPLATE/config.yml` — Issue-template chooser configuration and routing to support/security resources where appropriate.
- `.github/ISSUE_TEMPLATE/feature_request.yml` — Structured feature-request form that asks for problem, value, privacy/safety, and platform context.

### GitHub Actions

- `.github/workflows/android-instrumentation.yml` — Provisions the Android emulator, runs connected instrumentation/UI tests, pulls the real-app release screenshots, and publishes screenshot/test artifacts.
- `.github/workflows/apple-shared.yml` — macOS verification for the shared Kotlin Multiplatform core, including iOS simulator/device compilation. It does not represent an iOS UI application.
- `.github/workflows/ci.yml` — Main portable quality gate: repository/docs audits, Python tooling tests, Kotlin formatting, shared/desktop/Android tests, desktop JAR packaging, Android lint, APK, and AAB builds.
- `.github/workflows/codeql.yml` — CodeQL static analysis workflow for supported repository languages/configuration.
- `.github/workflows/dependency-review.yml` — Pull-request dependency review gate, intended to block unacceptable high-severity dependency changes.
- `.github/workflows/desktop.yml` — Linux/Windows/macOS matrix for desktop formatting/tests, runnable JAR creation, and DEB/MSI/DMG native packaging on matching hosts.
- `.github/workflows/release.yml` — Tagged release pipeline. It performs read-only preflight, stable-tag/version/current-main validation, Android/desktop builds, deterministic asset staging, exact-set verification/checksums, and grants write permission only to final publication.
- `.github/workflows/secret-scan.yml` — Repository-history secret scan. No credential, signing key, password, token, or private certificate belongs in Git history.

## Android application module

- `androidApp/build.gradle.kts` — Android application plugin/configuration, SDK/application versions, Compose/DataStore/test dependencies, packaging, lint, and build types.
- `androidApp/proguard-rules.pro` — Release shrinker/obfuscation rules. Keep minimal and review additions for correctness and unnecessary reflection exposure.
- `androidApp/src/main/AndroidManifest.xml` — Android application declaration and launcher activity. Privacy/security invariants include no Internet permission, Android backup disabled, and cleartext traffic disabled.

### Android entry point and data layer

- `androidApp/src/main/java/io/github/sanskarin/healthmetric/MainActivity.kt` — Android activity entry point that creates the app-level ViewModel/Compose content and applies edge-to-edge/window behavior.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/data/AppModels.kt` — Android-local models/enums for preferences, history entries, calculator kind, theme mode, and retention policy.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/data/BackupIo.kt` — Bounded UTF-8 backup stream reader/writer with the 1 MiB limit. It protects document I/O before JSON restore logic runs.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/data/HealthMetricDataStore.kt` — Android DataStore persistence and portable JSON export/restore. It owns opt-in history storage, retention, chronology, sanitation, consent/adult-gate separation, required backup structure validation, and atomic portable restore mutation.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/data/SafeLogger.kt` — Privacy-safe operational logging facade using fixed event names instead of measurement values or backup contents.

### Android application shell/state orchestration

- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/HealthMetricApp.kt` — Top-level Compose app shell, onboarding/adult gate, destination state, bottom navigation, About return behavior, backup document/share/restore launchers, dialogs/snackbars, and screen wiring.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/HealthMetricViewModel.kt` — UI-facing state and persistence operations: preferences/history flows, onboarding/adult-choice actions, retention/theme changes, history mutation, export/restore, and local-data deletion.

### Android reusable UI and formatting

- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/components/MeasurementNumberField.kt` — Reusable validated numeric Compose field used by measurement screens, including keyboard/input/error semantics.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/format/ChartScale.kt` — Finite-safe chart normalization utility designed to avoid overflow when imported history contains extreme but finite values.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/format/LocalizedNumbers.kt` — Locale-aware Android decimal parsing/formatting policy used for measurement entry and display while keeping domain arithmetic locale-independent.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/state/SavedEnum.kt` — Defensive saved-state enum restoration; unknown/stale enum names fall back safely rather than crashing after future enum changes.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/testing/HealthMetricTestTags.kt` — Stable Compose semantics/test-tag constants for critical automation paths. Tags supplement accessible semantics rather than replacing them.

### Android screens

- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/screens/AboutScreen.kt` — About/evidence/project/support/funding content plus explicit in-app return action.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/screens/CalculatorScreen.kt` — Adult BMI UI for metric/imperial measurements, validation/result presentation, and optional local-save action.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/screens/HistoryScreen.kt` — Opt-in saved-history list, filtering, finite-safe chart, per-entry deletion, undo-related UI, accessibility summaries, and erase controls.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/screens/OnboardingScreen.kt` — Adult-use notice, explicit adult/under-18 choices, and the blocked adult-reference screen with a safe return-to-age-selection correction path.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/screens/SettingsScreen.kt` — Privacy/history opt-in, retention, theme, backup save/share/restore actions, destructive local-data controls, update and About access.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/screens/WaistToHeightScreen.kt` — Adult waist-to-height metric/imperial input/result journey with neutral educational presentation and optional local history save.

### Android theme/design files

- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/theme/DesignTokens.kt` — Reusable spacing, shape, typography/elevation/motion-related design tokens that centralize Android visual decisions.
- `androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/theme/Theme.kt` — Material theme composition, light/dark/system behavior and supported Android dynamic-color integration.

### Android resources

- `androidApp/src/main/res/drawable/ic_healthmetric.xml` — Vector brand mark used by Android launch/UI surfaces.
- `androidApp/src/main/res/drawable/ic_healthmetric_foreground.xml` — Adaptive-icon foreground vector layer.
- `androidApp/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` — API 26+ adaptive launcher icon definition.
- `androidApp/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml` — API 26+ round adaptive launcher icon definition.
- `androidApp/src/main/res/mipmap-anydpi-v33/ic_launcher.xml` — Android 13+ launcher icon definition including themed/monochrome behavior where configured.
- `androidApp/src/main/res/mipmap-anydpi-v33/ic_launcher_round.xml` — Android 13+ round launcher icon counterpart.
- `androidApp/src/main/res/values/launcher_colors.xml` — Launcher/adaptive-icon background color resources.
- `androidApp/src/main/res/values/strings.xml` — Canonical Android user-visible string resources for localization-ready UI, accessibility labels, errors, dialogs, actions, and product copy.
- `androidApp/src/main/res/values/themes.xml` — Android XML launch/application theme resources, including splash-to-Compose handoff configuration.

## Android JVM unit tests

- `androidApp/src/test/java/io/github/sanskarin/healthmetric/data/AppPreferencesTest.kt` — Locks privacy-first defaults and supported history-retention normalization policy.
- `androidApp/src/test/java/io/github/sanskarin/healthmetric/data/BackupIoTest.kt` — Tests bounded backup stream read/write behavior, UTF-8 round trips, and oversize rejection.
- `androidApp/src/test/java/io/github/sanskarin/healthmetric/ui/format/ChartScaleTest.kt` — Regression tests for chart normalization including flat ranges and extreme finite values.
- `androidApp/src/test/java/io/github/sanskarin/healthmetric/ui/format/LocalizedNumbersTest.kt` — Locale parsing/formatting tests across representative dot/comma decimal conventions and malformed input.
- `androidApp/src/test/java/io/github/sanskarin/healthmetric/ui/state/SavedEnumTest.kt` — Tests known/stale/empty saved enum restoration and safe fallback behavior.

## Android instrumentation/UI tests

- `androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/AboutNavigationUiTest.kt` — Full-app About navigation regression coverage for explicit and system back behavior from originating destinations.
- `androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/AdultGateUiTest.kt` — Under-18 blocked-state behavior, age-choice actions, optional correction control, and stable gate semantics.
- `androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/CalculatorUiTest.kt` — BMI UI success/error journeys using Compose semantics.
- `androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/HealthMetricDataStoreTest.kt` — Android persistence integration coverage: opt-in, retention, chronology, backup round trip/structure, malformed-record salvage, fail-closed corrupt restore, consent/adult-gate preservation, delete/undo, IDs and sanitation.
- `androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/HistoryUiTest.kt` — History list controls, per-entry deletion and confirmed erase-all UI behavior.
- `androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/OnboardingUiTest.kt` — Fresh onboarding adult-use notice and age-choice surface coverage.
- `androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/ReleaseScreenshotCaptureTest.kt` — Drives the real app through the canonical release-evidence journey and writes the required eight fictional/example-data PNG screenshots.
- `androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/SettingsUiTest.kt` — Settings semantics/actions for explicit history opt-in, retention and backup save/share controls.
- `androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/WaistToHeightUiTest.kt` — Waist-to-height ratio success/error UI journeys.

## Shared Kotlin Multiplatform module

- `shared/build.gradle.kts` — Kotlin Multiplatform configuration for Android, JVM/Desktop, iOS device and iOS simulator targets plus shared test dependencies.
- `shared/src/commonMain/kotlin/io/github/sanskarin/healthmetric/domain/Bmi.kt` — Shared adult BMI calculation result/reference model, versioned reference profile/evidence metadata, metric and imperial entry points, educational notices, and classification behavior.
- `shared/src/commonMain/kotlin/io/github/sanskarin/healthmetric/domain/Units.kt` — Unit-system/input models and deterministic pound/kilogram, inch/centimeter and imperial-height conversion helpers.
- `shared/src/commonMain/kotlin/io/github/sanskarin/healthmetric/domain/Validation.kt` — Central plausible-range and finite-value validation for metric/imperial adult measurements, including unit-specific error contracts.
- `shared/src/commonMain/kotlin/io/github/sanskarin/healthmetric/domain/WaistToHeight.kt` — Shared adult waist-to-height calculation and educational result model with separate metric/imperial validation paths.

### Shared tests

- `shared/src/commonTest/kotlin/io/github/sanskarin/healthmetric/domain/BmiCalculatorTest.kt` — Deterministic BMI, reference-band, evidence, imperial-equivalence and documented boundary regression tests.
- `shared/src/commonTest/kotlin/io/github/sanskarin/healthmetric/domain/CalculatorPropertyTest.kt` — Seeded property-style coverage across large sets of valid BMI/ratio inputs; seeds remain deterministic for reproducibility.
- `shared/src/commonTest/kotlin/io/github/sanskarin/healthmetric/domain/UnitConverterTest.kt` — Precision/round-trip expectations for shared unit conversion helpers.
- `shared/src/commonTest/kotlin/io/github/sanskarin/healthmetric/domain/ValidationTest.kt` — Metric/imperial validation boundaries, non-finite rejection, and unit-specific validation-message coverage.
- `shared/src/commonTest/kotlin/io/github/sanskarin/healthmetric/domain/WaistToHeightTest.kt` — Metric/imperial waist-to-height correctness and documented boundary regression tests.

## Desktop application module

- `desktopApp/build.gradle.kts` — Compose Multiplatform desktop configuration, dependency on the shared core, public version, runnable JAR and host-native DMG/MSI/DEB packaging metadata.
- `desktopApp/src/main/kotlin/io/github/sanskarin/healthmetric/desktop/DesktopCalculations.kt` — Desktop presentation adapter around shared BMI/ratio domain APIs, field-specific validation feedback, split imperial height handling, and neutral result text.
- `desktopApp/src/main/kotlin/io/github/sanskarin/healthmetric/desktop/DesktopNumbers.kt` — Strict desktop measurement-number parser: ordinary dot/comma decimals and whole-number feet only; rejects scientific notation, signs, non-finite and malformed syntax.
- `desktopApp/src/main/kotlin/io/github/sanskarin/healthmetric/desktop/Main.kt` — Compose Desktop application UI: process-local adult gate, calculators, section navigation, theme, evidence/About links, and explicit statement that measurement/session state is not persisted.
- `desktopApp/src/test/kotlin/io/github/sanskarin/healthmetric/desktop/DesktopCalculationsTest.kt` — Shared-core desktop integration tests, metric/imperial result/error behavior, unit-specific feedback, and split imperial remaining-inches validation.
- `desktopApp/src/test/kotlin/io/github/sanskarin/healthmetric/desktop/DesktopNumbersTest.kt` — Desktop parser regression coverage for accepted decimal syntax and rejected malformed/scientific/signed/non-finite input.

## Gradle dependency catalog

- `gradle/libs.versions.toml` — Central versions, libraries, and plugin aliases for Android/Kotlin/Compose/DataStore/testing/formatting dependencies. Dependabot and maintainers update this rather than scattering versions across modules where possible.

## Repository tooling and release scripts

- `scripts/check_markdown_links.py` — Offline validator for repository-local Markdown link targets. It intentionally does not require external network access.
- `scripts/check_release_version.py` — Stable SemVer tag validator that cross-checks release tag, Android `versionName`, desktop project version and related release preconditions.
- `scripts/check_repository.py` — Machine-readable repository invariant audit covering required files, privacy/security settings, platform/module expectations, evidence/release tooling, safety regressions, and exhaustive file-reference coverage.
- `scripts/stage_release_assets.py` — Cross-platform deterministic artifact staging. It requires exactly one expected non-empty output for Android or a desktop host and renames it to canonical versioned release names.
- `scripts/verify_release_assets.py` — Final release-directory verifier requiring the exact eight expected non-empty binaries, rejecting extras/missing files, and writing deterministic SHA-256 checksums.
- `scripts/verify.sh` — Unix-like full non-device local verification entry point: repository/docs/tooling tests, formatting, shared/desktop/Android tests, desktop JAR, lint, APK and AAB builds.
- `scripts/verify.ps1` — Windows PowerShell counterpart to `verify.sh`, kept behaviorally aligned with the same non-device verification gates.

### Repository-tooling tests

- `scripts/tests/test_check_release_version.py` — Stable/invalid tag and project-version agreement tests for the release-version validator.
- `scripts/tests/test_stage_release_assets.py` — Android/Linux/Windows/macOS staging tests including duplicate, missing, empty, invalid-tag and unsupported-platform failures.
- `scripts/tests/test_verify_release_assets.py` — Exact release-set, missing/extra/empty asset and checksum-manifest regression coverage.

## Project documentation

- `docs/accessibility.md` — Android and desktop accessibility implementation guidance plus manual release-candidate checks such as TalkBack, keyboard focus, scaling and non-color-only meaning.
- `docs/architecture.md` — System architecture, dependency direction, platform boundaries, persistence/data flows, shared domain ownership and architectural invariants.
- `docs/backup-format.md` — Authoritative Android portable backup schema v1: size, top-level structure, record validation, chronology/deduplication/retention, device-local consent separation and compatibility rules.
- `docs/design-system.md` — Android visual/design token and reusable-component guidance, adaptive layout expectations, semantic/accessibility rules and theme behavior.
- `docs/desktop.md` — Desktop-specific product scope, ephemeral privacy model, shared-core integration, build/package commands, testing and manual release checks.
- `docs/development.md` — Contributor engineering workflow, module boundaries, commands, test placement, quality gates and change practices.
- `docs/documentation-map.md` — Documentation ownership/index and maintenance matrix describing which canonical documents must move with each category of change.
- `docs/evidence.md` — Health-reference source provenance, reviewed-date policy and rules for evidence/profile changes.
- `docs/github-governance.md` — Repository governance, protection/review expectations, workflow responsibilities, dependency/security automation and release controls.
- `docs/performance.md` — Performance budgets/risks, measurement strategy and optimization rules for bounded local data and UI behavior.
- `docs/release.md` — Canonical release process: preflight, commands, workflow gates, Android/desktop artifacts, exact asset/checksum rules, manual acceptance, signing/trust boundaries and rollback.
- `docs/repository-file-reference.md` — This exhaustive tracked-file inventory; repository verification checks it against Git's tracked file set.
- `docs/setup.md` — Development environment prerequisites and setup for JDK, Gradle, Android tooling, desktop packaging and supported platform-specific tasks.
- `docs/testing.md` — Automated/manual verification matrix, regression policy, Android screenshot evidence, release-integrity checks and release-candidate test sequence.
- `docs/troubleshooting.md` — Build, Android emulator/device, desktop packaging, backup/restore, Gradle/toolchain and common developer failure diagnostics.

## Architecture Decision Records

- `docs/adr/0001-shared-domain-kmp.md` — Decision to keep calculation/domain rules in Kotlin Multiplatform shared code instead of duplicating them per UI platform.
- `docs/adr/0002-local-privacy-first-persistence.md` — Decision for Android local, opt-in history with privacy-first defaults rather than account/cloud storage.
- `docs/adr/0003-versioned-adult-reference-profiles.md` — Decision to version adult reference/evidence interpretation so published behavior changes are explicit and reviewable.
- `docs/adr/0004-bounded-user-controlled-local-data.md` — Decision for bounded Android local history and bounded/versioned user-controlled backup/restore with consent/safety state kept device-local.
- `docs/adr/0005-ephemeral-desktop-client.md` — Decision that the desktop HealthMetric client stores measurement/session state only in process memory and does not mirror Android persistence by default.

## Documentation assets

- `docs/assets/logo.svg` — Repository/documentation HealthMetric logo asset. Keep source-controlled artwork deterministic and free of embedded private metadata.
- `docs/assets/screenshots/README.md` — Screenshot evidence policy, required Android screenshot names, generation source, fictional/example-data requirement and human publication-review boundary. Actual generated release screenshots are CI artifacts until explicitly reviewed for permanent publication.

## File-reference maintenance contract

For every future tracked-file addition, deletion or rename:

1. update this document in the same pull request;
2. describe the file's responsibility rather than only repeating its filename;
3. update [`documentation-map.md`](documentation-map.md) when the new file changes canonical documentation ownership;
4. update topic-specific docs/tests/invariants where behavior changes;
5. run `python3 scripts/check_repository.py` and `python3 scripts/check_markdown_links.py`.

A file being small, configuration-only, test-only, generated-by-hand, or documentation-only is not a reason to omit it from this inventory.
