# HealthMetric Roadmap

The roadmap prioritizes correctness, privacy, accessibility, and maintainability over feature count.

## Phase 0 — Repository foundation

- [x] Public MIT-licensed repository baseline.
- [x] Kotlin/Android build configuration and version catalog.
- [x] Formatting/lint integration.
- [x] CI, CodeQL, dependency review, Dependabot, and release workflow.
- [x] Issue/PR templates, support, security, privacy, contribution docs.
- [x] Architecture decision records and continuation handoff process.

## Phase 1 — Clean end-to-end MVP

- [x] Shared metric/imperial converters.
- [x] Adult BMI calculator.
- [x] Adult waist-to-height ratio calculator.
- [x] Input validation and neutral educational explanations.
- [x] Adult-only onboarding gate.
- [x] Android Compose calculator screens.
- [x] Local optional history.
- [x] Delete-history and delete-all-data controls.

## Phase 2 — Product completeness

- [x] Light/dark/system theme.
- [x] Accessible local-history chart.
- [x] JSON export and restore.
- [x] About/support/funding UI.
- [x] Privacy and accessibility settings sections.
- [ ] Capture real screenshots on an Android device/emulator.
- [ ] Add Storage Access Framework export-to-file in addition to share-text export.
- [ ] Add per-history-entry deletion and optional undo.
- [ ] Add user-selectable history retention limit.

## Phase 3 — Advanced quality

- [ ] Add iOS target to the shared module and validate on macOS CI.
- [ ] Add a simple desktop Compose client if it provides clear user value.
- [ ] Add richer versioned evidence metadata with source review dates.
- [ ] Add locale-aware numeric parsing/formatting beyond the initial English UI.
- [ ] Add reduced-motion setting if future animations become substantial.
- [ ] Add encrypted-at-rest history only if the UX/threat model justifies the complexity.

## Phase 4 — Verification depth

- [x] Domain unit and boundary tests.
- [x] Deterministic property-style tests.
- [x] Initial Compose onboarding UI test.
- [ ] Add instrumentation coverage for calculator success/error journeys.
- [ ] Add DataStore backup/restore instrumentation tests.
- [ ] Add accessibility scanner/manual TalkBack checklist evidence.
- [ ] Add baseline-profile/macrobenchmark module only if profiling shows a meaningful need.

## Phase 5 — Release readiness

- [x] Tagged unsigned release workflow.
- [x] Release documentation.
- [ ] Run clean-checkout CI successfully.
- [ ] Verify release build on physical Android hardware and emulator.
- [ ] Capture required screenshots and accessibility evidence.
- [ ] Configure protected Android signing outside source control.
- [ ] Publish `v0.1.0` only after all blocker checks pass.

## Phase 6 — Final audit

- [ ] Verify every README command from a clean clone.
- [ ] Confirm CI and CodeQL are green on the release commit.
- [ ] Review dependency advisories and repository secret scanning settings.
- [ ] Check documentation links.
- [ ] Re-review adult-only safety language and evidence references.
- [ ] Confirm `CHANGELOG.md`, `ROADMAP.md`, and `what_changed.md` match the release.

## Non-goals

HealthMetric will not add:

- appearance scoring, body rankings, or shame-oriented copy;
- crash diets, restrictive eating plans, or pressure-oriented body goals;
- medical diagnosis claims;
- advertising trackers;
- forced accounts for offline calculations;
- unnecessary cloud storage of measurement history.
