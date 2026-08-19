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
- [x] Adaptive bounded content width for wider Android windows.
- [x] Accessible local-history chart.
- [x] Confirmation before destructive history deletion.
- [x] JSON export and restore.
- [x] About/support/funding UI.
- [x] Privacy, data, appearance, accessibility, update, and About settings sections.
- [x] Localization-ready Android string resources.
- [x] Reusable validated measurement input component.
- [ ] Capture real screenshots on an Android device/emulator.
- [ ] Add Storage Access Framework export-to-file in addition to share-text export.
- [ ] Add per-history-entry deletion and optional undo.
- [ ] Add user-selectable history retention limit if real usage demonstrates a need.

## Phase 3 — Advanced quality

- [ ] Add iOS target to the shared module and validate on macOS CI.
- [ ] Add a simple desktop Compose client if it provides clear user value.
- [ ] Add source review dates and review workflow to versioned evidence metadata.
- [ ] Add locale-aware numeric parsing/formatting beyond the initial English UI.
- [ ] Add reduced-motion setting if future animations become substantial.
- [ ] Evaluate encrypted-at-rest history only if the documented threat model justifies the complexity.

## Phase 4 — Verification depth

- [x] Domain unit and boundary tests.
- [x] Deterministic property-style tests.
- [x] Privacy-default unit test.
- [x] Initial Compose onboarding UI test.
- [ ] Add instrumentation coverage for calculator success/error journeys.
- [ ] Add DataStore backup/restore instrumentation tests.
- [ ] Add accessibility scanner/manual TalkBack checklist evidence.
- [ ] Add baseline-profile/macrobenchmark module only if profiling shows a meaningful need.

## Phase 5 — Release readiness

- [x] Tagged unsigned release workflow.
- [x] Release documentation and release-notes template.
- [ ] Run clean-checkout CI successfully and record the run in `what_changed.md`.
- [ ] Verify release build on physical Android hardware and emulator.
- [ ] Capture required screenshots and accessibility evidence.
- [ ] Configure protected Android signing outside source control.
- [ ] Publish `v0.1.0` only after all blocker checks pass.

## Phase 6 — Final audit

- [ ] Verify every README command from a clean clone with Gradle/Android SDK available.
- [ ] Confirm CI, CodeQL, dependency review, and secret scan are green on the release commit.
- [ ] Review dependency advisories and repository security settings.
- [ ] Check documentation links.
- [x] Re-review adult-only safety language and evidence references in source/docs.
- [x] Confirm required documentation files exist and repository structure matches the architecture docs.
- [ ] Confirm `CHANGELOG.md`, `ROADMAP.md`, and `what_changed.md` match the release immediately before tagging.

## Non-goals

HealthMetric will not add:

- appearance scoring, body rankings, or shame-oriented copy;
- crash diets, restrictive eating plans, or pressure-oriented body goals;
- medical diagnosis claims;
- advertising trackers;
- forced accounts for offline calculations;
- unnecessary cloud storage of measurement history.
