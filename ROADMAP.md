# HealthMetric Roadmap

The roadmap prioritizes correctness, privacy, accessibility, and maintainability over feature count.

## Phase 0 — Repository foundation

- [x] Public MIT-licensed repository baseline.
- [x] Kotlin/Android build configuration and version catalog.
- [x] Formatting/lint integration.
- [x] CI, CodeQL, dependency review, secret scanning, Dependabot, and release workflow.
- [x] Issue/PR templates, release template, support, security, privacy, contribution docs.
- [x] Architecture decision records and continuation handoff process.
- [x] Architecture, evidence, design-system, setup, testing, performance, accessibility, release, and troubleshooting documentation.
- [x] Repository invariant checks for required files, privacy manifest rules, release packaging, screenshot evidence configuration, and forbidden temporary probe files.

## Phase 1 — Clean end-to-end MVP

- [x] Shared metric/imperial converters.
- [x] Adult BMI calculator.
- [x] Adult waist-to-height ratio calculator.
- [x] Input validation and neutral educational explanations.
- [x] Adult-only onboarding gate.
- [x] Android Compose calculator screens.
- [x] Local optional history, disabled by default until explicit opt-in.
- [x] Delete-history and delete-all-data controls.

## Phase 2 — Product completeness

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
- [x] Canonical newest-first ordering for add/import/delete-undo history flows.
- [x] Collision-resistant UUID identifiers for new history records.
- [x] User-selectable bounded history retention (50/100/250/500 entries).
- [x] Defensive backup limits, record validation, malformed-record recovery, duplicate-ID handling, and chronological normalization.
- [x] Device-local privacy/adult-gate state excluded from portable backup restore.
- [x] About/support/funding UI.
- [x] Explicit in-app and system-back navigation from About to the originating screen.
- [x] Privacy, data, appearance, accessibility, update, and About settings sections.
- [x] Localization-ready Android string resources.
- [x] Reusable validated measurement input component.
- [x] Locale-aware decimal input parsing and result/history formatting.
- [x] Implement deterministic real-app emulator capture for the eight required release-evidence screenshots.
- [ ] Confirm the screenshot capture job succeeds for the exact release-candidate commit and visually approve the generated PNG set.

## Phase 3 — Advanced quality

- [x] Add explicit iOS device/simulator targets to the shared module.
- [x] Add macOS CI compilation for the shared iOS targets.
- [x] Add source review date metadata and evidence review workflow documentation.
- [ ] Add a simple desktop Compose client only if it provides clear user value beyond the tested JVM shared core.
- [ ] Add reduced-motion setting only if future animations become substantial.
- [ ] Evaluate encrypted-at-rest history only if the documented threat model justifies the complexity.

## Phase 4 — Verification depth

- [x] Domain unit and boundary tests.
- [x] Deterministic property-style tests.
- [x] Privacy-default and retention-policy unit tests.
- [x] Backup IO size/UTF-8 unit tests.
- [x] Locale-aware numeric parsing/formatting unit tests.
- [x] Initial Compose onboarding UI test.
- [x] Under-18 adult-reference gate instrumentation coverage.
- [x] Instrumentation coverage for BMI success/error journeys.
- [x] Instrumentation coverage for waist-ratio success/error journeys.
- [x] Instrumentation coverage for history deletion and destructive confirmation.
- [x] Instrumentation coverage for privacy/retention/backup settings actions.
- [x] Instrumentation coverage for About return navigation.
- [x] DataStore opt-in, retention, export/restore, malformed-record, consent-boundary, chronology, deletion, and undo-persistence tests.
- [x] Dedicated GitHub Actions Android emulator workflow for connected tests.
- [x] Configure Android instrumentation to publish release screenshot evidence as a CI artifact.
- [ ] Add manual TalkBack/accessibility checklist evidence for the release candidate.
- [ ] Add baseline-profile/macrobenchmark module only if profiling shows a meaningful need.

## Phase 5 — Release readiness

- [x] Tagged unsigned release workflow.
- [x] Release documentation and release-notes template.
- [x] CI configured to assemble and upload a debug APK, unsigned release APK, and unsigned release App Bundle.
- [x] Tagged release workflow configured to build, upload, and attach both unsigned APK and App Bundle artifacts.
- [x] Unix and Windows verification scripts include debug APK, unsigned APK, and unsigned AAB builds.
- [ ] Run the complete PR #13 hardening check suite successfully and record the exact head/results in `what_changed.md`.
- [ ] Verify release build on physical Android hardware.
- [ ] Visually approve the generated release screenshots and complete manual accessibility evidence.
- [ ] Configure protected Android signing outside source control.
- [ ] Publish `v0.1.0` only after all blocker checks pass.

## Phase 6 — Final audit

- [x] Add automated required-path, privacy-manifest, AAB-packaging, screenshot-evidence, and Markdown-link checks.
- [ ] Verify every README verification command through the exact release-candidate automation/environment.
- [ ] Confirm CI, Android instrumentation, Apple shared-core compilation, CodeQL, dependency review, and secret scan are green on the exact release commit.
- [ ] Review dependency advisories and repository security settings before tagging.
- [x] Check internal documentation links automatically in CI.
- [x] Re-review adult-only safety language and evidence references in source/docs.
- [x] Confirm required documentation files exist and repository structure matches the architecture docs.
- [ ] Confirm `CHANGELOG.md`, `ROADMAP.md`, and `what_changed.md` match the exact release immediately before tagging.

## Non-goals

HealthMetric will not add:

- appearance scoring, body rankings, or shame-oriented copy;
- crash diets, restrictive eating plans, or pressure-oriented body goals;
- medical diagnosis claims;
- advertising trackers;
- forced accounts for offline calculations;
- unnecessary cloud storage of measurement history.
