# HealthMetric Roadmap

The roadmap prioritizes correctness, privacy, accessibility, portability, and maintainability over feature count.

## Phase 0 — Repository foundation

- [x] Public MIT-licensed repository baseline.
- [x] Kotlin/Android build configuration and version catalog.
- [x] Formatting/lint integration.
- [x] CI, CodeQL, dependency review, secret scanning, Dependabot, and release workflow.
- [x] Issue/PR templates, release template, support, security, privacy, contribution docs.
- [x] Architecture decision records and continuation handoff process.
- [x] Architecture, evidence, design-system, setup, testing, performance, accessibility, release, and troubleshooting documentation.
- [x] Executable repository invariant audit.

## Phase 1 — Clean Android end-to-end MVP

- [x] Shared metric/imperial converters.
- [x] Adult BMI calculator.
- [x] Adult waist-to-height ratio calculator.
- [x] Input validation and neutral educational explanations.
- [x] Adult-only onboarding gate.
- [x] Android Compose calculator screens.
- [x] Local optional history, disabled by default until explicit opt-in.
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
- [ ] Capture real screenshots on Android hardware/emulator.

## Phase 3 — Cross-platform foundation

- [x] Align Kotlin/AGP/Compose Multiplatform toolchain.
- [x] Extend `shared` to Android, JVM/Desktop, JS, Wasm, iOS x64, iOS arm64, and iOS simulator arm64.
- [x] Add stable `HealthMetricEngine` façade with explicit 18+ eligibility enforcement.
- [x] Add shared façade regression tests.
- [x] Add reusable `sharedUI` Compose Multiplatform module.
- [x] Add Windows/macOS/Linux Compose Desktop application host.
- [x] Configure native MSI/DMG/DEB desktop packaging.
- [x] Add JavaScript browser client.
- [x] Add Wasm browser client.
- [x] Add SwiftUI iOS/iPadOS host.
- [x] Make the iOS Xcode project reproducible through XcodeGen.
- [x] Link shared iOS UI frameworks in macOS CI.
- [x] Build the generated SwiftUI host in macOS CI.
- [x] Add dedicated desktop/web cross-platform CI.
- [x] Add native desktop packaging CI for Windows/macOS/Linux.
- [x] Expand local verification scripts for desktop/web targets.
- [x] Update architecture/setup/testing documentation for every client.

## Phase 4 — Verification depth

- [x] Domain unit and boundary tests.
- [x] Deterministic property-style tests.
- [x] Privacy-default and retention-policy unit tests.
- [x] Backup IO size/UTF-8 unit tests.
- [x] Locale-aware numeric parsing/formatting unit tests.
- [x] Shared cross-platform engine eligibility/routing tests.
- [x] Initial Compose onboarding UI test.
- [x] Under-18 adult-reference gate instrumentation coverage.
- [x] Instrumentation coverage for BMI success/error journeys.
- [x] Instrumentation coverage for waist-ratio success/error journeys.
- [x] Instrumentation coverage for history deletion and destructive confirmation.
- [x] Instrumentation coverage for privacy/retention/backup settings actions.
- [x] DataStore opt-in, retention, export/restore, malformed-record, consent-boundary, deletion, and undo-persistence tests.
- [x] Dedicated GitHub Actions Android emulator workflow.
- [x] Compile sharedUI on JVM/JS/Wasm in CI.
- [x] Compile/link iOS shared UI framework in CI.
- [x] Build generated iOS host in CI.
- [x] Build native desktop packages on all three desktop operating systems in CI.
- [ ] Add manual Android TalkBack evidence.
- [ ] Add manual iOS VoiceOver evidence.
- [ ] Add desktop keyboard/screen-reader evidence.
- [ ] Add browser keyboard/screen-reader evidence.
- [ ] Add baseline-profile/macrobenchmark module only if profiling shows meaningful need.

## Phase 5 — Release readiness

- [x] Tagged unsigned Android release workflow.
- [x] Release documentation and release-notes template.
- [x] CI configured to assemble/upload Android debug APK, unsigned release APK, and unsigned release App Bundle.
- [x] Tagged Android release workflow configured to publish unsigned APK/App Bundle artifacts.
- [x] Web production artifact generation configured in CI.
- [x] Desktop native installer artifact generation configured in CI.
- [ ] Confirm a clean cross-platform PR run is green and record exact workflow runs in `what_changed.md`.
- [ ] Verify Android release build on physical hardware and emulator.
- [ ] Verify desktop packages interactively on Windows/macOS/Linux.
- [ ] Verify Wasm and JavaScript clients manually in representative browsers.
- [ ] Verify iOS host on simulator and physical device.
- [ ] Capture required platform screenshots and accessibility evidence.
- [ ] Configure protected Android signing outside source control.
- [ ] Configure protected Apple signing/provisioning outside source control before distribution.
- [ ] Decide production web hosting and security headers before public web deployment.
- [ ] Publish `v0.1.0` only after blocker checks pass.

## Phase 6 — Final audit

- [ ] Verify every README command from clean platform checkouts with documented toolchains.
- [ ] Confirm standard CI, cross-platform CI, native desktop packages, Android instrumentation, Apple host build, CodeQL, dependency review, and secret scan are green on the release commit.
- [ ] Review dependency advisories and repository security settings.
- [ ] Check documentation links.
- [x] Re-review adult-only safety language and evidence references in source/docs.
- [x] Confirm repository/document/module structure through automated invariants.
- [ ] Confirm `CHANGELOG.md`, `ROADMAP.md`, and `what_changed.md` match the release immediately before tagging.

## Phase 7 — Optional platform parity after the foundation is proven

These are intentionally not release blockers for the beta cross-platform clients.

- [ ] Evaluate local history persistence on desktop with the same privacy-first opt-in semantics as Android.
- [ ] Evaluate iOS local persistence only with explicit consent and bounded retention.
- [ ] Evaluate browser persistence only after a clear privacy model and erase/export controls are designed.
- [ ] Add platform-native share/export flows where they materially help users.
- [ ] Add richer locale-aware number formatting to `sharedUI` without moving locale concerns into the calculation domain.
- [ ] Add localization resources for cross-platform UI.
- [ ] Evaluate encrypted-at-rest history only if the documented threat model justifies it.
- [ ] Add reduced-motion setting only if future motion becomes substantial.

## Non-goals

HealthMetric will not add:

- appearance scoring, body rankings, or shame-oriented copy;
- crash diets, restrictive eating plans, or pressure-oriented body goals;
- medical diagnosis claims;
- advertising trackers;
- forced accounts for offline calculations;
- unnecessary cloud storage of measurement history;
- silent cross-platform persistence without an explicit privacy/retention design.
