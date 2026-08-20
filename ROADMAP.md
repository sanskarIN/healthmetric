# HealthMetric Roadmap

The roadmap prioritizes correctness, privacy, accessibility, maintainability, and honest platform support over feature count.

## Release milestone — 2.0.12 cross-platform foundation

- [x] Android application client.
- [x] iPhone/iPad application client with native SwiftUI/Xcode host.
- [x] Windows Compose Desktop client and MSI packaging.
- [x] macOS Compose Desktop client and DMG packaging.
- [x] Linux Compose Desktop client and DEB packaging.
- [x] WebAssembly browser client.
- [x] JavaScript browser fallback/compatibility client.
- [x] Shared Kotlin Multiplatform domain used by all calculator clients.
- [x] Cross-platform CI matrix for Web, Windows, macOS, Linux, and iOS simulator.
- [x] Multi-platform tagged release workflow for Android APK/AAB, MSI, DMG, DEB, Web ZIP, and iOS framework ZIP.
- [x] Cross-platform build/run/package documentation.
- [x] Repository invariant checks for target/package/release wiring.

Android remains the most feature-complete client; persistent history, DataStore, JSON document backup/restore, Android dynamic color, and Android-specific document/share integrations are not yet claimed as feature-parity features on iOS/desktop/web.

## Phase 0 — Repository foundation

- [x] Public MIT-licensed repository baseline.
- [x] Kotlin/Android build configuration and centralized version catalog.
- [x] Formatting/lint integration.
- [x] CI, CodeQL, dependency review, secret scanning, Dependabot, and release automation.
- [x] Issue/PR templates, release template, support, security, privacy, and contribution docs.
- [x] Architecture decision records and continuation handoff process.
- [x] Architecture, evidence, design-system, setup, testing, performance, accessibility, release, troubleshooting, and cross-platform documentation.

## Phase 1 — Clean Android end-to-end MVP

- [x] Shared metric/imperial converters.
- [x] Adult BMI calculator.
- [x] Adult waist-to-height ratio calculator.
- [x] Input validation and neutral educational explanations.
- [x] Adult-only onboarding gate.
- [x] Android Compose calculator screens.
- [x] Local optional history disabled by default until explicit opt-in.
- [x] Delete-history and delete-all-data controls.

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
- [x] Defensive backup limits, record validation, malformed-record recovery, and duplicate-ID handling.
- [x] Device-local privacy/adult-gate state excluded from portable backup restore.
- [x] About/support/funding UI.
- [x] Privacy, data, appearance, accessibility, update, and About settings sections.
- [x] Localization-ready Android string resources.
- [x] Reusable validated measurement input component.
- [x] Locale-aware decimal input parsing and result/history formatting.
- [ ] Capture real Android screenshots on a device/emulator for release evidence.

## Phase 3 — Multiplatform application foundation

- [x] Android target in shared domain.
- [x] JVM/Desktop target in shared domain.
- [x] iOS ARM64 device target in shared domain.
- [x] iOS ARM64 simulator target in shared domain.
- [x] JavaScript browser target in shared domain.
- [x] WebAssembly browser target in shared domain.
- [x] `composeApp` shared calculator UI.
- [x] Desktop application entry point.
- [x] iOS Compose controller bridge.
- [x] Native SwiftUI iOS/iPadOS host.
- [x] Shared Xcode scheme/project.
- [x] Browser Compose viewport entry point.
- [x] Browser HTML/CSS shell.
- [x] MSI/DMG/DEB native desktop package configuration.
- [x] Cross-platform host-specific CI.
- [x] Toolchain aligned to Kotlin 2.4.10 / Compose Multiplatform 1.11.1 for this release line.

## Phase 4 — Verification depth

- [x] Domain unit and boundary tests.
- [x] Deterministic property-style tests.
- [x] Privacy-default and retention-policy unit tests.
- [x] Backup IO size/UTF-8 unit tests.
- [x] Locale-aware numeric parsing/formatting unit tests.
- [x] Initial Android Compose onboarding UI test.
- [x] Under-18 adult-reference gate instrumentation coverage.
- [x] Android instrumentation for BMI success/error journeys.
- [x] Android instrumentation for waist-ratio success/error journeys.
- [x] Android instrumentation for history deletion/destructive confirmation.
- [x] Android instrumentation for privacy/retention/backup settings actions.
- [x] DataStore opt-in, retention, export/restore, malformed-record, consent-boundary, deletion, and undo-persistence tests.
- [x] Dedicated Android emulator workflow.
- [x] Apple shared-core compilation workflow.
- [x] Desktop Windows/macOS/Linux compile/package matrix.
- [x] JS production browser build gate.
- [x] Wasm production browser build gate.
- [x] Combined browser compatibility-distribution gate.
- [x] iOS framework build gate.
- [x] Xcode iOS simulator application build gate.
- [ ] Add automated common Compose UI behavior tests where stable multiplatform test APIs provide useful coverage beyond compile/package gates.
- [ ] Record manual accessibility evidence on representative Android, iOS, desktop, and browser clients.
- [ ] Add baseline-profile/macrobenchmark work only if profiling shows a meaningful Android need.

## Phase 5 — Cross-platform feature parity

These are optional future product improvements, not blockers for the 2.0.12 calculator-client platform foundation.

- [ ] Design a reviewed shared persistence contract before adding history outside Android.
- [ ] Implement privacy-first iOS local history only after the persistence contract and deletion/retention rules are tested.
- [ ] Implement privacy-first desktop local history only after the same contract is tested.
- [ ] Implement privacy-first browser storage only if its security/privacy tradeoffs are documented and opt-in behavior remains explicit.
- [ ] Generalize backup/export/import only where each platform has a safe explicit document/user-action model.
- [ ] Add shared theme preference persistence without weakening platform accessibility/system-theme behavior.
- [ ] Add shared About/support/release UI where it benefits non-Android clients.
- [ ] Generalize localization resources/formatting across the shared Compose client.
- [ ] Consider native platform integrations only when they improve user value rather than increasing permissions/dependencies.

## Phase 6 — Release readiness

- [x] Android unsigned APK/AAB build automation.
- [x] Windows MSI build automation.
- [x] macOS DMG build automation.
- [x] Linux DEB build automation.
- [x] Web compatibility ZIP build automation.
- [x] iOS developer framework ZIP automation.
- [x] GitHub Release aggregation of the seven cross-platform artifacts on `v*` tags.
- [x] Release documentation and release-note guidance.
- [x] Application metadata aligned to version 2.0.12 across Android/iOS/desktop.
- [ ] Confirm all PR #15 final-head required workflows are green before merge.
- [ ] Run clean-checkout release-candidate verification on the intended tagged commit.
- [ ] Verify representative clients manually on physical/interactive devices where CI cannot supply UX evidence.
- [ ] Capture release screenshots and accessibility evidence.
- [ ] Configure protected Android production signing outside source control.
- [ ] Configure protected Apple production signing/archive outside source control.
- [ ] Configure optional Windows/macOS publisher signing/notarization outside source control where distribution policy requires it.
- [ ] Deploy Web assets only through a protected hosting pipeline.
- [ ] Create `v2.0.12` only when an actual release is explicitly intended and the exact commit has passed release-candidate checks.

## Phase 7 — Final audit

- [x] Repository invariant script checks required cross-platform files/targets/version/package/release wiring.
- [x] Documentation link audit exists.
- [x] Re-review adult-only safety language and evidence references in source/docs.
- [x] Cross-platform README/setup/architecture/testing/release/development/troubleshooting/contribution documentation aligned to current implementation.
- [x] `CHANGELOG.md` contains the 2.0.12 cross-platform milestone.
- [x] `what_changed.md` records the cross-platform continuation.
- [ ] Confirm CI, Cross-platform, Android instrumentation, Apple shared core, CodeQL, dependency review, and secret scan are green on the final PR head.
- [ ] Review dependency advisories and repository security settings immediately before release.
- [ ] Confirm release assets on a release-candidate tag/dry-run process before a public production tag if practical.
- [ ] Confirm `CHANGELOG.md`, `ROADMAP.md`, and `what_changed.md` match the exact release commit immediately before tagging.

## Non-goals

HealthMetric will not add:

- appearance scoring, body rankings, or shame-oriented copy;
- crash diets, restrictive eating plans, or pressure-oriented body goals;
- medical diagnosis claims;
- advertising trackers;
- forced accounts for offline calculations;
- unnecessary cloud storage of measurement history;
- committed production signing credentials merely to make CI emit store-signed binaries.
