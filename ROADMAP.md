# HealthMetric Roadmap

The roadmap prioritizes correctness, privacy, accessibility, and maintainability over feature count.

## Phase 0 — Repository foundation

- [x] Public MIT-licensed repository baseline.
- [x] Kotlin/Android build configuration and version catalog.
- [x] Formatting/lint integration.
- [x] CI, CodeQL, dependency review, secret scanning, Dependabot, and release workflow.
- [x] Issue/PR templates, release template, support, security, privacy, contribution docs.
- [x] Architecture decision records and continuation handoff process.
- [x] Architecture, desktop, evidence, design-system, setup, testing, performance, accessibility, release, and troubleshooting documentation.

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
- [x] Confirmation before destructive history deletion.
- [x] JSON share export and restore.
- [x] Storage Access Framework JSON export-to-file.
- [x] Restore confirmation before replacing portable local data.
- [x] Per-history-entry deletion with immediate undo.
- [x] User-selectable bounded history retention (50/100/250/500 entries).
- [x] Defensive backup limits, record validation, malformed-record recovery, duplicate-ID handling, and canonical timestamp ordering.
- [x] Device-local privacy/adult-gate state excluded from portable backup restore.
- [x] Collision-resistant UUID identifiers for new local history records.
- [x] About/support/funding UI.
- [x] Explicit in-app and system-back return navigation from About.
- [x] Privacy, data, appearance, accessibility, update, and About settings sections.
- [x] Localization-ready Android string resources.
- [x] Reusable validated measurement input component.
- [x] Locale-aware decimal input parsing and result/history formatting.
- [x] Deterministic real-app Android release screenshot capture test implemented.
- [ ] Human visual/privacy approval of the final CI-generated screenshot artifact for publication.

## Phase 3 — Multiplatform quality

- [x] Add explicit iOS device/simulator targets to the shared module.
- [x] Add macOS CI compilation for the shared iOS targets.
- [x] Add source review date metadata and evidence review workflow documentation.
- [x] Add a focused Compose Multiplatform desktop client using the tested shared calculation core.
- [x] Keep desktop measurement/session state ephemeral with no HealthMetric persistence layer.
- [x] Add desktop metric/imperial BMI and waist-to-height journeys.
- [x] Add desktop parser/calculation tests.
- [x] Add dedicated Linux/Windows/macOS desktop CI packaging/verification.
- [x] Document desktop architecture, privacy boundaries, setup, testing, and release expectations.
- [ ] Add reduced-motion setting only if future animations become substantial.
- [ ] Evaluate encrypted-at-rest Android history only if the documented threat model justifies the complexity.

## Phase 4 — Verification depth

- [x] Domain unit and boundary tests.
- [x] Deterministic property-style tests.
- [x] Privacy-default and retention-policy unit tests.
- [x] Backup IO size/UTF-8 unit tests.
- [x] Locale-aware numeric parsing/formatting unit tests.
- [x] Desktop parsing/calculation tests.
- [x] Initial Compose onboarding UI test.
- [x] Under-18 adult-reference gate instrumentation coverage.
- [x] Instrumentation coverage for BMI success/error journeys.
- [x] Instrumentation coverage for waist-ratio success/error journeys.
- [x] Instrumentation coverage for history deletion and destructive confirmation.
- [x] Instrumentation coverage for privacy/retention/backup settings actions.
- [x] Instrumentation coverage for About origin/back navigation.
- [x] DataStore opt-in, retention, export/restore, malformed-record, consent-boundary, chronology, deletion, and undo-persistence tests.
- [x] Dedicated GitHub Actions Android emulator workflow for connected tests.
- [x] Android instrumentation workflow configured to publish eight real-app release screenshots.
- [x] Repository invariant audit covers Android release evidence and desktop module expectations.
- [ ] Add manual TalkBack/accessibility evidence for the release candidate.
- [ ] Add baseline-profile/macrobenchmark module only if profiling shows a meaningful need.

## Phase 5 — Release readiness

- [x] Tagged unsigned release workflow.
- [x] Release documentation and release-notes template.
- [x] CI configured to assemble/upload debug APK, unsigned release APK, and unsigned release App Bundle.
- [x] Tagged release workflow configured for unsigned Android APK/App Bundle artifacts.
- [x] Desktop runnable JAR/native-distribution build configuration.
- [x] Dedicated desktop workflow across Linux, Windows, and macOS.
- [x] Local Unix/Windows verification scripts include Android AAB and desktop verification.
- [ ] Confirm the exact final PR #14 head is green across CI, Android instrumentation, Apple shared core, Desktop, CodeQL, Dependency Review, and Secret Scan.
- [ ] Confirm the exact final Android instrumentation run publishes all eight required screenshot PNGs.
- [ ] Verify Android release candidate on physical Android hardware.
- [ ] Complete manual Android accessibility review and screenshot approval.
- [ ] Smoke-test release desktop packages on their target host operating systems before promotion.
- [ ] Configure protected Android signing outside source control.
- [ ] Publish `v0.1.0` only after all blocker checks pass.

## Phase 6 — Final audit

- [x] Repository-required paths, Android manifest privacy invariants, desktop module configuration, AAB tasks, and screenshot-evidence configuration are machine-checked.
- [x] Internal Markdown links are machine-checked without network access.
- [x] Re-review adult-only safety language and evidence references in source/docs.
- [x] Remove and forbid accidental `docs/.noop-probe` repository state.
- [x] Reconcile the Android release-hardening work with the desktop/release-readiness branch.
- [ ] Confirm every final README/setup/release command against the exact release candidate.
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
