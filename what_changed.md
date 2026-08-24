# HealthMetric — Work Handoff

Last updated: 2026-08-24

## Current milestone

Active development branch:

`phase3/cross-platform-excellence`

Open pull request:

- PR #16 — `feat: establish cross-platform HealthMetric clients`
- https://github.com/sanskarIN/healthmetric/pull/16

Base branch: `main`

PR base commit:

`66a68b1f062863b2524cfcec43159bae771df13b`

Code/documentation head immediately before this handoff commit:

`afc3c28f5398d4f8ef7d95bf3078985122b29448` (`docs(release): document cross-platform version gate`)

GitHub reports PR #16 as open, non-draft, and mergeable. Immediately before this handoff update it contained 90 commits, 49 changed files, 3,462 additions, and 984 deletions. This handoff update adds another meaningful documentation commit.

The previous Phase-2 handoff that referenced PR #12 is obsolete. The privacy/data-control work from that phase is already part of the base history; the active continuation is PR #16.

## Current project stage

- Phase 0 repository foundation: implemented.
- Phase 1 Android end-to-end MVP: implemented.
- Phase 2 Android product completeness: implemented except release-candidate screenshots/manual device evidence.
- Phase 3 cross-platform foundation: implemented for shared domain, reusable shared UI, Windows/macOS/Linux desktop hosts, JavaScript/Wasm web hosts, and iOS/iPadOS SwiftUI host.
- Phase 4 automated verification depth: substantially implemented across shared domain, shared UI helpers, Android JVM/instrumentation, browser compilation/bundling, Apple framework/host build, and native desktop packaging workflows.
- Phase 5 release readiness: unsigned Android APK and App Bundle pipelines are implemented; release-version consistency gates and `v0.1.0` candidate notes are implemented; manual platform validation and protected production signing remain release tasks.
- Phase 6 final audit: repository/document/version invariants are automated, but final release status cannot be marked complete until the final GitHub Actions group finishes successfully and manual release evidence is completed.

## Continuation objective

This continuation took the existing cross-platform branch, audited its real GitHub Actions failures, fixed the underlying toolchain/configuration defects, filled missing metric/imperial parity in the reusable UI, added regression coverage, completed the Android App Bundle release path, strengthened executable repository invariants, aligned public/contributor/release documentation with the implementation, fixed the remaining Kotlin/iOS bridge style failures exposed by CI, and added cross-platform release-version safeguards for the first release candidate.

The work deliberately used many small, coherent commits instead of one large commit.

## Cross-platform product implementation

### Shared domain

`shared` now targets:

- Android;
- JVM/Desktop;
- JavaScript;
- Wasm;
- iOS x64 simulator;
- iOS arm64 device;
- iOS simulator arm64.

`HealthMetricEngine` is the stable primitive-input façade for platform clients and keeps the explicit adult-use boundary in the domain layer.

It exposes:

- `calculateAdultMetricBmi`;
- `calculateAdultImperialBmi`;
- `calculateAdultMetricWaistToHeight`;
- `calculateAdultImperialWaistToHeight`.

The minimum supported age remains 18. Under-18 requests are rejected before calculation routing.

### Reusable shared UI

`sharedUI` is a Compose Multiplatform presentation module used by desktop, web, and iOS hosts.

The current UI includes:

- explicit adult-use age gate;
- metric/imperial unit selector;
- adult metric BMI flow;
- adult imperial BMI flow;
- adult metric waist-to-height flow;
- adult imperial waist-to-height flow;
- neutral educational result language;
- no appearance scoring, body ranking, or personal body targets.

Desktop/web/iOS form values and results remain transient UI state. No persistence, analytics, advertising, account, or HealthMetric cloud synchronization was added to these beta clients.

### Shared input normalization

Added `MeasurementInput` under `sharedUI` to keep reusable form normalization deterministic and testable.

It covers:

- bounded whole-number input;
- practical dot/comma decimal normalization;
- removal of unrelated characters/repeated separators;
- imperial feet-plus-inches composition.

The composables use these helpers rather than embedding untested parsing helpers directly in screen code.

### Desktop

`desktopApp` provides the thin Compose Desktop host over `sharedUI`.

Configured native package formats:

- Windows MSI;
- macOS DMG;
- Linux DEB.

A dedicated matrix workflow builds host-native packages on each operating system.

### Web

`webApp` provides:

- Wasm browser executable;
- JavaScript compatibility browser executable.

CI builds and uploads production distributions for both.

### iOS / iPadOS

`iosApp` provides a SwiftUI host backed by the reusable Kotlin `HealthMetricUI` framework.

The Xcode project is reproducible from `iosApp/project.yml` with XcodeGen rather than committing generated project state.

Apple CI:

- runs shared JVM tests on macOS;
- compiles shared iOS targets;
- links shared UI frameworks for simulator/device;
- regenerates the Xcode project;
- builds the unsigned iOS simulator host.

## Regression coverage added in this continuation

### Shared engine

`HealthMetricEngineTest` now explicitly covers imperial routing in addition to the existing metric/adult-boundary checks.

Added assertions include:

- imperial BMI routes through shared conversion/calculation correctly;
- imperial waist-to-height routes through shared conversion/calculation correctly;
- adult eligibility remains enforced.

### Shared UI helpers

Added `MeasurementInputTest` under `sharedUI/src/commonTest`.

Coverage includes:

- dot decimal normalization;
- comma decimal normalization;
- unrelated-character/repeated-separator handling;
- bounded whole-number normalization;
- imperial total-height composition.

`sharedUI` now declares `kotlin("test")` for common tests.

Cross-platform CI and both local verification scripts execute `:sharedUI:desktopTest`.

## CI failures found and fixed

The previous PR heads exposed configuration/toolchain/style failures before the full product verification matrix could complete. The concrete failures inspected so far were fixed rather than bypassed.

### Root cause 1 — Android SDK command-line tools were not provisioned

CI, Android instrumentation, cross-platform, Apple, and CodeQL jobs failed before meaningful Gradle verification because `sdkmanager` was unavailable on the runner path.

Affected workflows were migrated to explicit Android toolchain provisioning with:

`android-actions/setup-android@v4`

The configured packages are:

- `platform-tools`;
- `platforms;android-36`;
- `build-tools;35.0.0`.

The same setup is now used consistently in:

- `.github/workflows/ci.yml`;
- `.github/workflows/android-instrumentation.yml`;
- `.github/workflows/apple-shared.yml`;
- `.github/workflows/cross-platform.yml`;
- `.github/workflows/desktop-packages.yml`;
- `.github/workflows/codeql.yml`;
- `.github/workflows/release.yml`.

The desktop matrix no longer maintains separate bash/PowerShell `sdkmanager` branches.

### Root cause 2 — Kotlin 2.4.10 rejected the legacy Android JVM target DSL

Desktop package jobs exposed the hard configuration error caused by:

`kotlinOptions { jvmTarget = "17" }`

`androidApp/build.gradle.kts` now uses the typed compiler DSL:

`JvmTarget.fromTarget("17")`

inside `kotlin.compilerOptions`.

Java source/target compatibility remains Java 17.

### Root cause 3 — Kotlin formatting/style gate failures

A later CI head reached the Kotlin style gate and reported deterministic formatting issues in the shared domain sources plus the iOS controller factory naming rule.

The continuation formatted:

- `Bmi.kt`;
- `HealthMetricEngine.kt`;
- `Units.kt`;
- `Validation.kt`;
- `WaistToHeight.kt`.

The iOS Kotlin bridge factory was renamed from `MainViewController()` to idiomatic `mainViewController()`, and `iosApp/HealthMetric/ContentView.swift` was updated to call the new exported Kotlin symbol. No lint suppression was added.

An older Apple log also referenced `KeyboardOptions` symbols in a shared UI snapshot, but the inspected source for the relevant branch/commit no longer contained those symbols. No speculative source change was made for a stale/non-reproducible log; fresh exact-head Apple CI remains the authoritative check.

## Android release artifact completion

The documentation/roadmap claimed Android App Bundle verification, but the actual CI/release workflows were only building APKs. This mismatch was fixed in the implementation rather than hiding it in documentation.

### Main CI

`ci.yml` now builds:

- debug APK;
- unsigned release APK;
- unsigned release App Bundle.

It uploads all applicable build/lint artifacts, including:

`androidApp/build/outputs/bundle/release/*.aab`

### Tagged release workflow

`release.yml` now:

1. audits repository invariants and internal Markdown links;
2. validates release/tag metadata;
3. verifies shared tests, Android unit tests, ktlint, and release lint;
4. assembles the unsigned release APK;
5. builds the unsigned release App Bundle;
6. uploads APK and AAB workflow artifacts;
7. attaches both unsigned artifacts to the generated GitHub Release.

Production signing remains intentionally outside source control.

### Local verification parity

Both:

- `scripts/verify.sh`;
- `scripts/verify.ps1`

now execute:

- cross-platform ktlint;
- `:shared:desktopTest`;
- `:sharedUI:desktopTest`;
- shared JS/Wasm compilation;
- sharedUI desktop/JS/Wasm compilation;
- desktop application compilation;
- JS/Wasm production web bundling;
- Android JVM tests;
- Android release lint;
- Android debug APK assembly;
- Android unsigned release APK assembly;
- Android release App Bundle generation.

## Repository invariant hardening

`scripts/check_repository.py` was expanded so the fixes above cannot silently drift away.

The audit now guards, among other existing requirements:

- required shared/sharedUI source and test paths;
- absence of the obsolete `docs/.noop-probe` file;
- Android manifest offline/privacy invariants;
- typed Kotlin `compilerOptions` usage instead of legacy `kotlinOptions`;
- JVM target alignment to Java 17;
- all four metric/imperial `HealthMetricEngine` routes;
- reusable UI metric and imperial paths;
- use of `MeasurementInput` in shared UI;
- `android-actions/setup-android@v4` in every workflow that configures Android targets;
- `:sharedUI:desktopTest` in cross-platform CI;
- `:androidApp:bundleRelease` in CI, tagged release, and both local verification scripts;
- AAB artifact publication paths in CI and tagged release;
- presence of the release-version validation script;
- release-version validation in normal CI;
- exact tag/version validation in tagged release CI.

The existing internal Markdown link audit remains part of main CI and is also run before tagged release packaging.

## Cross-platform version/release safeguards added on 2026-08-24

Added `scripts/check_release_version.py` as an executable metadata gate.

Normal CI now validates that:

- Android `versionName` is valid Semantic Versioning;
- Android `versionCode` is positive;
- desktop `packageVersion` matches the Android user-facing version;
- iOS `MARKETING_VERSION` matches the Android user-facing version;
- iOS `CURRENT_PROJECT_VERSION` matches the Android build number;
- `CHANGELOG.md` contains the configured release section;
- `docs/releases/v<version>.md` exists.

Tagged release CI additionally validates that:

- the Git tag is a valid `v`-prefixed Semantic Versioning tag;
- the tag matches the configured cross-platform version;
- the changelog release heading has been finalized to a `YYYY-MM-DD` date rather than remaining `Planned`.

The current aligned metadata is:

- Android `versionName`: `0.1.0`;
- Android `versionCode`: `1`;
- desktop `packageVersion`: `0.1.0`;
- iOS `MARKETING_VERSION`: `0.1.0`;
- iOS `CURRENT_PROJECT_VERSION`: `1`.

`docs/releases/v0.1.0.md` now records the intended scope, automated gates, manual gates, distribution boundaries, and tagging rule for the first candidate. The release is intentionally not tagged while the changelog remains `## [0.1.0] - Planned` and required automated/manual evidence is incomplete.

## Android privacy/safety behavior preserved

The cross-platform work does not remove the Android privacy model completed earlier.

Android still preserves:

- no `INTERNET` permission for the application core;
- `android:allowBackup="false"`;
- `android:usesCleartextTraffic="false"`;
- local history disabled by default;
- explicit history opt-in;
- bounded selectable retention;
- per-entry delete/undo;
- erase/delete-all controls;
- bounded JSON backup/restore;
- restore confirmation;
- device-local history/adult/onboarding consent state excluded from portable backup restore;
- locale-aware Android numeric parsing/display.

The shared adult-reference language remains educational and non-diagnostic.

## Documentation aligned in this continuation

Updated to match the actual cross-platform and release implementation:

- `README.md`;
- `CHANGELOG.md`;
- `ROADMAP.md`;
- `docs/testing.md`;
- `docs/release.md`;
- `docs/releases/v0.1.0.md`;
- `docs/setup.md`;
- `docs/development.md`;
- PR #16 description;
- this `what_changed.md`.

Key corrections include:

- desktop/web/iOS are real beta hosts rather than future-only ideas;
- shared UI exposes metric and imperial calculator paths;
- shared UI helper tests are part of CI/local verification;
- JavaScript/Wasm and all Apple targets are documented;
- native desktop packages are documented;
- Android AAB generation/upload is documented and implemented;
- workflow Android SDK provisioning uses setup-android v4;
- protected signing/manual validation are clearly separated from repository build readiness;
- first-candidate version metadata is aligned and automatically checked across Android, desktop, and iOS;
- tag publication is blocked until the changelog section is finalized with a real release date.

## Commits made during the earlier 2026-08-23 continuation before its handoff

That continuation added 30 meaningful commits before the previous handoff commit:

1. `150699ba` — `fix(android): migrate JVM target to compilerOptions`
2. `d4b27cdc` — `ci: provision Android SDK before verification`
3. `21da5d1e` — `ci(android): provision SDK before instrumentation`
4. `1b45dfa9` — `ci(multiplatform): provision Android SDK for clients`
5. `c7d412c0` — `ci(apple): provision Android SDK for project configuration`
6. `9a0d9357` — `ci(codeql): provision Android SDK before analysis`
7. `2267682a` — `ci(desktop): modernize Android SDK provisioning`
8. `5ab2d132` — `ci(release): provision Android SDK before packaging`
9. `36415a9b` — `test(shared): cover imperial engine routing`
10. `c0dea777` — `feat(shared-ui): add imperial calculator parity`
11. `9ce20638` — `refactor(shared-ui): extract measurement input helpers`
12. `f5ebac75` — `test(shared-ui): enable common unit tests`
13. `db728076` — `test(shared-ui): cover measurement input normalization`
14. `6ce267b7` — `ci(multiplatform): run shared UI helper tests`
15. `8635a01e` — `refactor(shared-ui): use tested measurement helpers`
16. `884fea56` — `test(scripts): run shared UI tests on Unix`
17. `c6629713` — `test(scripts): run shared UI tests on PowerShell`
18. `e52e1287` — `docs(roadmap): record imperial UI and helper tests`
19. `0413d97c` — `docs(testing): document shared UI regression coverage`
20. `f1d564a5` — `ci: enforce cross-platform tooling invariants`
21. `43abb65e` — `ci(android): build and upload release app bundle`
22. `f48daeee` — `ci(release): publish unsigned apk and app bundle`
23. `021ccaca` — `test(scripts): verify release app bundle on Unix`
24. `157ca1c2` — `test(scripts): verify release app bundle on PowerShell`
25. `0e52029b` — `ci: lock release bundle verification invariants`
26. `87f4f73e` — `docs(changelog): record cross-platform and release fixes`
27. `9e4602b5` — `docs(readme): align cross-platform and release verification`
28. `9c8a8717` — `docs(release): align app bundle and parity checks`
29. `82d9cee8` — `docs(setup): align verification and release artifacts`
30. `e75257e7` — `docs(development): align contributor workflow with all platforms`

No empty commits were created solely to inflate the count.

## Commits made during the 2026-08-24 continuation before this handoff

This continuation added 18 focused commits before the current handoff commit:

1. `69482b62` — `style(shared): format BMI domain model`
2. `93fa059a` — `style(shared): format platform engine facade`
3. `d2ebe080` — `style(shared): align chained summary calls`
4. `4b680070` — `style(shared): format unit converter signature`
5. `0ae9909a` — `style(shared): format validation declarations`
6. `0d8fbc20` — `style(shared): format waist-to-height calculator`
7. `a302f9f3` — `style(ios): use idiomatic controller factory name`
8. `fe25d288` — `fix(ios): update Swift controller bridge`
9. `36f1ee8a` — `ci(release): add version metadata consistency check`
10. `f1617f45` — `ci: validate release metadata on every change`
11. `62882713` — `ci(release): reject mismatched version tags`
12. `e5e4760b` — `ci: lock release version gate invariants`
13. `acfa4647` — `docs(release): prepare v0.1.0 candidate notes`
14. `ddc13ac9` — `ci(release): audit repository before publishing`
15. `48e6c2a8` — `docs(changelog): record release version safeguards`
16. `f4ebd4fe` — `ci(release): require finalized changelog before tagging`
17. `bc415e0f` — `ci(release): enforce cross-platform version alignment`
18. `afc3c28f` — `docs(release): document cross-platform version gate`

No empty commits were created solely to inflate the count.

## Verification state at the 2026-08-23 handoff

The code/documentation head before the previous handoff was:

`e75257e7d20fb3a46983436a2e46ac86eb8672a6`

The workflow group observed for that head was queued/pending at the time of that handoff:

- Dependency Review — run `32627871583` — queued;
- CodeQL — run `32627871604` — queued;
- Secret Scan — run `32627871643` — queued;
- Cross-platform clients — run `32627871587` — queued;
- Desktop native packages — run `32627871601` — queued;
- Android instrumentation — run `32627871588` — queued;
- Apple shared core and UI — run `32627871577` — pending;
- CI — run `32627871584` — queued.

Subsequent runs reached the Kotlin formatting/style gate and exposed the additional source/bridge issues fixed in the 2026-08-24 commits listed above.

## Verification state immediately before this handoff commit

The stable code/documentation head immediately before this handoff update is:

`afc3c28f5398d4f8ef7d95bf3078985122b29448`

GitHub reports PR #16 as open, non-draft, and mergeable.

Fresh workflow runs for that exact head were inspected and all eight were queued rather than reporting a pass/fail conclusion:

- Android instrumentation — run `32732620373` — queued;
- Apple shared core and UI — run `32732620467` — queued;
- CI — run `32732620544` — queued;
- Cross-platform clients — run `32732620378` — queued;
- Dependency Review — run `32732620367` — queued;
- CodeQL — run `32732620451` — queued;
- Secret Scan — run `32732620459` — queued;
- Desktop native packages — run `32732620402` — queued.

Because this `what_changed.md` update creates a new PR head, GitHub will schedule a new exact-head check group for this documentation commit. The PR must not be described as fully green or release-ready until the checks for the actual merge/release head complete successfully.

## Execution-environment limitation

The coding container cannot perform an authoritative local clone/build of the repository because outbound GitHub hostname resolution is unavailable and it does not provide the complete Android/Apple toolchains needed for this project.

That is an execution-environment limitation, not evidence that the repository fails to build. GitHub-hosted workflows are the authoritative automated build/test/package runners for this continuation.

The repository itself does not currently include a Gradle wrapper; CI pins Gradle 8.13 through `gradle/actions/setup-gradle`, while local/Xcode setup documentation requires an installed compatible Gradle when `./gradlew` is unavailable. Adding a repository wrapper remains a future reproducibility improvement if binary wrapper material is intentionally adopted and reviewed.

## Known external/manual release blockers

These are intentionally left open rather than falsely marked complete:

1. Final GitHub Actions group must complete successfully on the eventual merge/release commit.
2. Android release must be run interactively on representative emulator/physical hardware.
3. Android TalkBack, large-font/display scaling, keyboard/DPAD where relevant, and screenshot evidence remain manual release-candidate checks.
4. Windows/macOS/Linux native installers must be installed and reviewed interactively on representative clean systems.
5. JavaScript/Wasm clients need representative browser keyboard/screen-reader/manual loading checks before public hosting.
6. iOS/iPadOS needs simulator plus physical-device validation, VoiceOver/Dynamic Type evidence, and protected signing before TestFlight/App Store distribution.
7. Android production signing material must remain outside source control and be configured in a protected distribution environment.
8. Apple signing/provisioning must remain outside source control.
9. Web hosting and production security headers/privacy behavior must be selected and documented before public deployment.
10. `CHANGELOG.md` must be changed from `[0.1.0] - Planned` to the actual `YYYY-MM-DD` release date only when the candidate is truly ready to tag.
11. `v0.1.0` must not be tagged until the automated and manual release checklist is satisfied.

These are release/environment tasks rather than missing core calculation implementation.

## Next exact tasks

1. Inspect the GitHub Actions group for the exact head created by this handoff commit.
2. If any job fails, inspect its job logs and fix the root cause with a focused regression/verification commit.
3. Repeat until CI, cross-platform clients, desktop packages, Android instrumentation, Apple host build, CodeQL, dependency review, and secret scan are green on one stable head.
4. Run the remaining manual device/browser/accessibility checks for each platform intended to be claimed in `v0.1.0`.
5. Configure protected Android/Apple signing only in the external distribution environments that require it.
6. Finalize the `[0.1.0]` changelog heading with the actual release date only after the release checklist is satisfied.
7. Merge PR #16 into `main` only after the automated branch gate is green.
8. Tag `v0.1.0` only from the reviewed release commit after the required manual gates for the claimed release scope are complete.

## Release principle

Do not weaken checks or documentation merely to obtain a green badge. Fix the underlying implementation/tooling defect, add regression/invariant coverage when practical, and keep unfinished manual evidence explicitly visible until it is actually completed.
