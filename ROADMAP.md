# HealthMetric Roadmap

The roadmap prioritizes correctness, privacy, accessibility, documentation integrity, and maintainability over feature count.

## Phase 0 — Repository foundation

- [x] Public MIT-licensed repository baseline.
- [x] Kotlin/Android build configuration and version catalog.
- [x] Formatting/lint integration.
- [x] CI, CodeQL, dependency review, secret scanning, Dependabot, and release workflow.
- [x] Issue/PR templates, release template, support, security, privacy, contribution docs.
- [x] Architecture decision records and continuation handoff process.
- [x] Architecture, desktop, evidence, design-system, setup, testing, performance, accessibility, release, and troubleshooting documentation.
- [x] Canonical documentation ownership map and change-to-document update matrix.
- [x] Exhaustive tracked-file reference covering every source, test, resource, workflow, script, build/configuration file, document, ADR, and documentation asset.
- [x] Repository invariant that compares `git ls-files` against the exhaustive file reference.

## Phase 1 — Clean end-to-end MVP

- [x] Shared metric/imperial converters.
- [x] Adult BMI calculator.
- [x] Adult waist-to-height ratio calculator.
- [x] Input validation and neutral educational explanations.
- [x] Adult-only onboarding gate.
- [x] Android Compose calculator screens.
- [x] Android optional local history, disabled by default until explicit opt-in.
- [x] Android delete-history and delete-all-data controls.

## Phase 2 — Android product completeness

- [x] Light/dark/system theme and Android dynamic color.
- [x] Shared typography, shape, spacing, elevation, and motion design tokens.
- [x] Branded launch splash treatment.
- [x] Adaptive, round, and Android 13+ themed launcher icons.
- [x] Adaptive bounded content width for wider Android windows.
- [x] Accessible local-history chart.
- [x] Overflow-safe history-chart scaling for extreme finite imported values.
- [x] Confirmation before destructive history deletion.
- [x] JSON share export and restore.
- [x] Storage Access Framework JSON export-to-file.
- [x] Restore confirmation before replacing portable local data.
- [x] Per-history-entry deletion with immediate undo.
- [x] User-selectable bounded history retention (50/100/250/500 entries).
- [x] Defensive backup limits, required top-level history-array validation, record validation, malformed-record recovery, duplicate-ID handling, and canonical timestamp ordering.
- [x] Distinguish intentional empty backup history from non-empty all-invalid history and reject the latter before DataStore mutation.
- [x] Device-local privacy/adult-gate state excluded from portable backup restore.
- [x] Recoverable adult-use choice so an accidental under-18 selection can return to age selection without clearing unrelated local data.
- [x] Collision-resistant UUID identifiers for new local history records.
- [x] About/support/funding UI.
- [x] Explicit in-app and system-back return navigation from About.
- [x] Privacy, data, appearance, accessibility, update, and About settings sections.
- [x] Localization-ready Android string resources.
- [x] Reusable validated measurement input component.
- [x] Locale-aware decimal input parsing and result/history formatting.
- [x] Safe fallback for stale saved navigation/history-filter enum names after app updates.
- [x] Deterministic real-app Android release screenshot capture test implemented.
- [ ] Human visual/privacy approval of the final CI-generated screenshot artifact for publication.

## Phase 3 — Multiplatform quality

- [x] Add explicit iOS device/simulator targets to the shared module.
- [x] Add macOS CI compilation for the shared iOS targets.
- [x] Add source review date metadata and evidence review workflow documentation.
- [x] Add a focused Compose Multiplatform desktop client using the tested shared calculation core.
- [x] Keep desktop measurement/session state ephemeral with no HealthMetric persistence layer.
- [x] Add desktop metric/imperial BMI and waist-to-height journeys.
- [x] Add strict desktop decimal/whole-feet parsing and split remaining-inch validation.
- [x] Add desktop parser/calculation tests, including invalid remaining-inch components.
- [x] Keep imperial validation/error contracts in imperial units without metric double-validation at documented boundaries.
- [x] Add dedicated Linux/Windows/macOS desktop CI packaging/verification.
- [x] Document desktop architecture, privacy boundaries, input contract, setup, testing, packaging, and release expectations.
- [ ] Add reduced-motion setting only if future animations become substantial.
- [ ] Evaluate encrypted-at-rest Android history only if the documented threat model justifies the complexity.

## Phase 4 — Verification depth

- [x] Domain unit and boundary tests.
- [x] Deterministic property-style tests.
- [x] Privacy-default and retention-policy unit tests.
- [x] Backup IO size/UTF-8 unit tests.
- [x] Locale-aware numeric parsing/formatting unit tests.
- [x] Desktop parsing/calculation tests.
- [x] Imperial BMI and waist-to-height boundary/error regression tests.
- [x] Desktop split-height remaining-inch regression tests.
- [x] Initial Compose onboarding UI test.
- [x] Under-18 adult-reference gate instrumentation coverage.
- [x] Adult-gate correction-control instrumentation coverage.
- [x] Instrumentation coverage for BMI success/error journeys.
- [x] Instrumentation coverage for waist-ratio success/error journeys.
- [x] Instrumentation coverage for history deletion and destructive confirmation.
- [x] Instrumentation coverage for privacy/retention/backup settings actions.
- [x] Instrumentation coverage for About origin/back navigation.
- [x] DataStore opt-in, retention, export/restore, malformed-record, malformed top-level structure, all-invalid-history, consent-boundary, chronology, deletion, undo-persistence, and adult-choice-reset tests.
- [x] Unit coverage for finite-safe Android chart normalization and stale saved-enum fallback.
- [x] Python regression coverage for release tag/version validation, deterministic asset staging, exact asset verification, and checksums.
- [x] Dedicated GitHub Actions Android emulator workflow for connected tests.
- [x] Android instrumentation workflow configured to publish eight real-app release screenshots.
- [x] Repository invariant audit covers Android release evidence, desktop module expectations, release-integrity tooling, chart safety, adult-gate correction, backup structure, and tracked-file documentation completeness.
- [x] Internal Markdown-link audit for repository-local documentation targets.
- [ ] Add manual TalkBack/accessibility evidence for the release candidate.
- [ ] Add baseline-profile/macrobenchmark module only if profiling shows a meaningful need.

## Phase 5 — Release readiness

- [x] Tagged unsigned release workflow.
- [x] Release documentation and release-notes template.
- [x] CI configured to assemble/upload debug APK, unsigned release APK, and unsigned release App Bundle.
- [x] Tagged release workflow configured for unsigned Android APK/App Bundle artifacts.
- [x] Desktop runnable JAR/native-distribution build configuration.
- [x] Dedicated desktop workflow across Linux, Windows, and macOS.
- [x] Local Unix/Windows verification scripts include Python repository/release checks, Android AAB, and desktop verification.
- [x] Stable release tags are machine-checked against Android and desktop public versions.
- [x] Tagged release preflight requires the tag to target the current `main` commit.
- [x] Release workflow defaults to read-only repository permissions and grants write only to final publication.
- [x] Cross-platform release staging fails closed on zero, duplicate, or empty expected build outputs.
- [x] Final release publication rejects missing, extra, or empty binary assets.
- [x] Final release publication generates `SHA256SUMS.txt` for the eight expected binaries and verifies the Git tag before release creation.
- [x] Documentation integrity and exact tracked-file coverage are pre-tag repository gates.
- [ ] Confirm the exact final PR #14 head is green across CI, Android instrumentation, Apple shared core, Desktop, CodeQL, Dependency Review, and Secret Scan.
- [ ] Confirm the exact final Android instrumentation run publishes all eight required screenshot PNGs.
- [ ] Confirm exact-head Desktop artifacts contain JAR + DEB, JAR + MSI, and JAR + DMG on matching runners.
- [ ] Verify Android release candidate on physical Android hardware.
- [ ] Complete manual Android accessibility review and screenshot approval.
- [ ] Smoke-test release desktop packages on their target host operating systems before promotion.
- [ ] Complete desktop keyboard/focus/display-scaling/screen-reader review on published hosts.
- [ ] Configure protected Android signing outside source control.
- [ ] Decide/configure desktop signing/notarization outside source control if production-trusted installers are published.
- [ ] Publish `v0.1.0` only after all blocker checks pass.

## Phase 6 — Final audit

- [x] Repository-required paths, Android manifest privacy invariants, desktop module configuration, AAB tasks, screenshot evidence, release staging, checksum verification, and publication permission boundaries are machine-checked.
- [x] Every tracked repository file is documented by exact path and machine-checked against `git ls-files`.
- [x] Internal Markdown links are machine-checked without network access.
- [x] Re-review adult-only safety language and evidence references in source/docs.
- [x] Remove and forbid accidental `docs/.noop-probe` repository state.
- [x] Reconcile the Android release-hardening work with the desktop/release-readiness branch.
- [x] Reconcile release documentation/template with the hardened tag/version/main/checksum workflow.
- [x] Deeply reconcile README, setup, development, architecture, desktop, testing, release, troubleshooting, privacy, security, governance, contribution, changelog, roadmap, and file-level documentation with current code behavior.
- [x] Document Python 3 as a verification prerequisite and document the exact repository/release tooling commands.
- [ ] Confirm every final README/setup/release command against the exact release candidate through exact-head CI/host workflows.
- [ ] Confirm all configured workflows are green on the exact release commit.
- [ ] Review dependency advisories and repository security settings immediately before release.
- [ ] Confirm `CHANGELOG.md`, `ROADMAP.md`, and `what_changed.md` match the exact release immediately before tagging.

## Non-goals

HealthMetric will not add:

- appearance scoring, body rankings, or shame-oriented copy;
- crash diets, restrictive eating plans, or pressure-oriented body goals;
- medical diagnosis claims;
- advertising trackers;
- forced accounts for offline calculations;
- unnecessary cloud storage of measurement history;
- desktop persistence merely to mirror Android when the ephemeral desktop model is simpler and more private.
