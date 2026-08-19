# HealthMetric — Work Handoff

Last updated: 2026-08-19

## Current milestone

- Phase 0 — repository foundation: **implemented**.
- Phase 1 — clean Android end-to-end MVP: **implemented**.
- Phase 2 — product-completeness baseline: **substantially implemented**.
- Phase 3 — optional multiplatform expansion/advanced quality: **not required for the initial Android release and remains roadmap work**.
- Phase 4 — automated verification depth: **partially implemented**; domain/property/privacy-default/UI-onboarding tests exist, additional device-level integration coverage remains.
- Phase 5 — release readiness: **automation implemented, external verification pending**.
- Phase 6 — final release audit: **source/repository audit performed; clean Android checkout/device/CI confirmation still required before tagging `v0.1.0`**.

Do **not** mark `v0.1.0` released until the pending Android/CI checks below are observed passing.

## Repository state at start

- Repository: `https://github.com/sanskarIN/healthmetric`
- Visibility: public.
- Default branch: `main`.
- Repository contained only `LICENSE` before this implementation.
- Existing repository history was preserved.
- Initial existing commit was not rewritten.

## Implemented product

HealthMetric is now an Android-first, open-source adult BMI / health measurement calculator with a Kotlin Multiplatform shared domain core.

### Adult-use safety and product language

- Adult-only first-run gate for the current BMI and waist-to-height reference calculators.
- Users selecting the under-18 path do not receive the adult reference calculators.
- BMI copy explicitly describes the output as population screening information rather than diagnosis or appearance scoring.
- Waist-to-height output is shown neutrally and intentionally does not assign appearance grades, pressure-oriented goals, or unsupported clinical tiers.
- Versioned adult BMI reference profile includes evidence/source metadata.
- Evidence policy is documented in `docs/evidence.md` and ADR 0003.

### Shared calculation module

Implemented under `shared/`:

- `Units.kt`
  - metric/imperial input models;
  - pounds/kilograms conversion;
  - inches/centimetres conversion;
  - feet/inches height conversion.
- `Bmi.kt`
  - adult BMI calculation;
  - versioned reference profile;
  - ordered reference bands;
  - source metadata;
  - neutral adult-only educational notice.
- `Validation.kt`
  - finite-number validation;
  - supported adult measurement ranges;
  - explicit validation error types.
- `WaistToHeight.kt`
  - metric and imperial adult waist-to-height ratio arithmetic;
  - neutral educational notice.

The shared module has Android and JVM/Desktop targets and does not depend on Android UI/persistence APIs.

### Android application

Implemented under `androidApp/`:

- `MainActivity.kt`
  - Android SplashScreen installation;
  - edge-to-edge Compose host;
  - lifecycle ViewModel wiring.
- `ui/HealthMetricApp.kt`
  - adult gate;
  - BMI / ratio / history / settings navigation;
  - About navigation;
  - bounded adaptive content width for wider Android windows;
  - import document picker;
  - explicit export chooser;
  - explicit external-link intents;
  - localized user-visible status messages;
  - privacy-safe failure logging.
- `ui/HealthMetricViewModel.kt`
  - immutable combined UI state;
  - focused persistence mutations;
  - calculation-history recording;
  - delete/export/restore operations;
  - no localized UI copy stored in the state layer.
- `ui/components/MeasurementNumberField.kt`
  - reusable validated numeric input;
  - decimal/whole-number filtering;
  - bounded input length.
- `ui/screens/OnboardingScreen.kt`
  - adult-only first-run disclosure;
  - privacy/offline disclosure;
  - explicit adult/under-18 choices.
- `ui/screens/CalculatorScreen.kt`
  - metric/imperial adult BMI input;
  - validation feedback;
  - neutral result/reference presentation.
- `ui/screens/WaistToHeightScreen.kt`
  - metric/imperial ratio input;
  - validation feedback;
  - neutral result presentation.
- `ui/screens/HistoryScreen.kt`
  - calculator-type filtering;
  - accessible chart;
  - screen-reader chart summary;
  - no health meaning encoded only by color;
  - empty states;
  - destructive erase confirmation.
- `ui/screens/SettingsScreen.kt`
  - privacy/data controls;
  - local-history opt-in toggle;
  - export/restore/delete controls;
  - light/dark/system appearance selection;
  - accessibility section;
  - installed version and public GitHub releases link;
  - About navigation.
- `ui/screens/AboutScreen.kt`
  - version;
  - MIT license;
  - project identity;
  - GitHub;
  - Buy Me a Coffee;
  - business/support contacts;
  - privacy summary;
  - **Made by the Sanskar**.
- `ui/theme/DesignTokens.kt`
  - spacing scale;
  - elevation scale;
  - motion timings;
  - shape scale;
  - typography scale.
- `ui/theme/Theme.kt`
  - light/dark/system theme;
  - Android dynamic color where supported;
  - shared typography/shapes.

### Privacy-first persistence

Implemented in `data/HealthMetricDataStore.kt` and related models:

- Preferences DataStore local persistence.
- **History is disabled by default**.
- New history is stored only after explicit user opt-in.
- Raw weight/height/waist inputs are not stored in history.
- Stored history is capped at 500 entries.
- History stores only local ID, timestamp, calculator type, calculated value, and neutral summary.
- User can disable future history.
- User can erase history.
- User can delete all local HealthMetric data/settings.
- JSON export schema version `1`.
- JSON restore rejects unsupported schema versions.
- Restored history is bounded.
- Missing history preference restores to the privacy-first disabled state.
- Android application backup is disabled.
- Cleartext traffic is disabled.
- No Android Internet permission is requested.
- No ad/analytics tracker SDK is included.

### Structured logging

`data/SafeLogger.kt`:

- uses fixed event enum values;
- accepts only sanitized exception class names for warnings;
- does not accept raw measurement values, backup contents, email addresses, credentials, tokens, or arbitrary user-controlled strings.

### Branding and localization readiness

- Editable repository logo: `docs/assets/logo.svg`.
- Android vector icon: `androidApp/src/main/res/drawable/ic_healthmetric.xml`.
- Android SplashScreen launch theme and branded icon.
- Android UI copy centralized in `androidApp/src/main/res/values/strings.xml`.
- Dynamic/common domain reference explanations remain shared-domain data; Android presentation copy is resource-backed.

## Build and repository configuration

Created/updated:

- `settings.gradle.kts`
- `build.gradle.kts`
- `gradle.properties`
- `gradle/libs.versions.toml`
- `shared/build.gradle.kts`
- `androidApp/build.gradle.kts`
- `androidApp/proguard-rules.pro`
- `.gitignore`
- `.editorconfig`
- `.gitattributes`
- `.env.example`

Pinned/configured project baseline includes Kotlin, Android Gradle Plugin, Compose, Material 3, Lifecycle, DataStore, coroutines, Android SplashScreen, testing libraries, and ktlint.

The repository currently documents/uses `gradle` commands and does not contain a generated Gradle wrapper binary. A developer or CI runner therefore needs Gradle 8.13 available as documented. GitHub Actions installs that Gradle version explicitly.

## GitHub automation and repository quality

Created:

- `.github/workflows/ci.yml`
  - ktlint checks;
  - shared JVM tests;
  - Android unit tests;
  - Android lint;
  - debug assembly;
  - lint/APK artifacts.
- `.github/workflows/codeql.yml`
  - Java/Kotlin CodeQL analysis with manual build.
- `.github/workflows/dependency-review.yml`
  - PR dependency review;
  - high-severity findings fail review.
- `.github/workflows/secret-scan.yml`
  - full-history Gitleaks scan.
- `.github/workflows/release.yml`
  - tag-triggered release verification;
  - unsigned release APK;
  - GitHub Release creation.
- `.github/dependabot.yml`
  - Gradle and GitHub Actions dependency updates.
- `.github/FUNDING.yml`
  - Buy Me a Coffee custom funding URL.
- `.github/PULL_REQUEST_TEMPLATE.md`
- `.github/RELEASE_TEMPLATE.md`
- `.github/ISSUE_TEMPLATE/bug_report.yml`
- `.github/ISSUE_TEMPLATE/feature_request.yml`
- `.github/ISSUE_TEMPLATE/config.yml`
- `docs/github-governance.md`
  - branch protection/ruleset guidance;
  - labels;
  - milestones;
  - Discussions guidance;
  - merge/release guidance.

## Tests added

### Shared/common tests

- `BmiCalculatorTest.kt`
  - metric result;
  - imperial/metric equivalence;
  - adult reference boundary behavior.
- `UnitConverterTest.kt`
  - pounds round trip;
  - feet/inches conversion precision.
- `ValidationTest.kt`
  - non-finite input rejection;
  - unsupported height rejection;
  - imperial inches boundary rejection.
- `WaistToHeightTest.kt`
  - metric ratio;
  - metric/imperial equivalence.
- `CalculatorPropertyTest.kt`
  - deterministic seeded 1,000-input BMI property loop;
  - deterministic seeded 1,000-input ratio property loop.

### Android tests

- `androidApp/src/test/.../AppPreferencesTest.kt`
  - locks privacy-first default: history disabled, system theme, onboarding/adult confirmation false.
- `androidApp/src/androidTest/.../OnboardingUiTest.kt`
  - confirms adult-use onboarding title/actions are visible on a fresh state.

## Documentation completed

Required/core files now present:

- `README.md`
- `LICENSE`
- `CONTRIBUTING.md`
- `CODE_OF_CONDUCT.md`
- `SECURITY.md`
- `SUPPORT.md`
- `PRIVACY.md`
- `CHANGELOG.md`
- `ROADMAP.md`
- `what_changed.md`
- `.gitignore`
- `.editorconfig`
- `.gitattributes`
- `.env.example`
- `docs/architecture.md`
- `docs/setup.md`
- `docs/development.md`
- `docs/testing.md`
- `docs/release.md`
- `docs/troubleshooting.md`
- `docs/accessibility.md`
- `docs/performance.md`
- `docs/design-system.md`
- `docs/evidence.md`
- `docs/github-governance.md`
- `docs/adr/0001-shared-domain-kmp.md`
- `docs/adr/0002-local-privacy-first-persistence.md`
- `docs/adr/0003-versioned-adult-reference-profiles.md`
- `docs/assets/logo.svg`
- `docs/assets/screenshots/README.md`

README includes project identity, value proposition, screenshot plan, features, platforms, tech stack, setup, testing, build/release, architecture, privacy/security, accessibility, contributions, MIT license, contacts, BMC badge, and **Made by the Sanskar**.

## Verification performed in this session

### Available local toolchain inspection

Observed locally:

- Java: OpenJDK 21.0.11 available.
- Kotlin compiler: 1.9.0 available.
- Gradle: **not installed** in the execution environment.
- Android SDK: **not available** in the execution environment.

### Shared-domain compiler smoke check

The shared `commonMain` domain sources were reproduced into a temporary local directory and compiled with the available Kotlin compiler using a command equivalent to:

```bash
kotlinc <shared-domain-sources> -d /tmp/hmcore/out.jar
```

Result: **success** (no compiler error output).

This is only a syntax/compiler smoke check for the platform-neutral domain source. It does not replace the configured Gradle/KMP test task.

### Repository audit searches

GitHub repository searches performed after implementation:

- `TODO FIXME XXX` → no matches returned.
- `android.permission.INTERNET` → no matches returned.
- common private-key/password/API-key/secret/token search terms → no suspicious matches returned.
- obsolete `kotlin.serialization` plugin reference search → no matches returned after the root-plugin fix.
- obsolete invalid Compose `KeyboardOptions` import search → no matches returned after the UI import fixes.

### GitHub workflow visibility limitation

The available GitHub connector could create/read repository files and commits, but its workflow-run lookup did not provide a completed run for these direct `main` writes. Combined commit status also returned no usable check entries during this session.

Therefore this handoff **does not claim** that CI, CodeQL, dependency review, secret scan, Android lint, or Android builds have passed remotely yet.

## Verification still required before `v0.1.0`

Run/observe all of the following on a clean environment with JDK 17, Gradle 8.13, Android SDK Platform 36, and Build Tools 35.0.0:

```bash
gradle :shared:ktlintCheck :androidApp:ktlintCheck
gradle :shared:desktopTest
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintRelease
gradle :androidApp:assembleRelease
```

With a device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

Also confirm GitHub-hosted:

- CI green;
- CodeQL green;
- dependency review green/available for PRs;
- secret scan green;
- no new dependency/security advisory blocker.

Manual release-candidate checks:

1. Fresh-install adult onboarding.
2. Under-18 path does not expose adult calculators.
3. Metric BMI success/error boundaries.
4. Imperial BMI success/error boundaries.
5. Metric/imperial waist ratio.
6. History is disabled on fresh/default state.
7. Enable history and verify entries appear.
8. History filter/chart semantics.
9. Erase-history confirmation.
10. Export JSON.
11. Restore valid schema-v1 JSON.
12. Reject unsupported/corrupted backup.
13. Delete-all-data resets onboarding/defaults.
14. Light/dark/system theme.
15. Large font/display scale.
16. TalkBack traversal and chart description.
17. About/support/funding links.
18. Settings release link.
19. Branded splash launch behavior.
20. Physical device + emulator release-build smoke test.

## Known limitations / intentionally unfinished release tasks

These are not hidden TODOs in code; they are explicit release/roadmap items:

- Full Android Gradle build/lint/test verification could not run in the coding execution environment because Gradle and Android SDK were unavailable.
- Remote workflow pass state could not be reliably observed through the available connector.
- Real screenshots still require a built app on an Android emulator/device. Capture checklist exists in `docs/assets/screenshots/README.md`.
- Production Android signing must be configured outside Git using protected signing material.
- Direct Storage Access Framework file export is roadmap work; current export is explicit Android share of JSON.
- Additional calculator journey and DataStore restore instrumentation tests remain useful verification-depth work.
- iOS target/desktop UI are optional future multiplatform expansion; the shared domain is structured to support them but they are not claimed as shipped clients.

## Data / migration notes

Current local backup schema: `1`.

Schema-v1 export contains:

- `schemaVersion`
- `historyEnabled`
- `themeMode`
- `adultUseConfirmed`
- `onboardingComplete`
- `history`

History entries contain:

- `id`
- `timestampEpochMillis`
- `calculator`
- `value`
- `summary`

Privacy-default migration behavior:

- no stored `history_enabled` key means history is disabled;
- missing `historyEnabled` in a restored schema-v1 payload means disabled;
- unsupported top-level schema versions are rejected;
- malformed history payloads are discarded rather than crashing;
- restored history is limited to 500 entries.

If the format becomes incompatible after a release, increment `schemaVersion`, add migration tests, update `PRIVACY.md`, `CHANGELOG.md`, `docs/development.md`, release notes, and this file.

## Release notes draft — planned 0.1.0

### Highlights

- Android-first HealthMetric adult BMI and waist-to-height measurement application.
- Kotlin Multiplatform shared calculation/validation core.
- Metric and imperial inputs.
- Adult-only reference gate and neutral non-diagnostic language.
- Privacy-first history disabled by default.
- Local JSON export/restore/delete controls.
- Accessible neutral history chart.
- Light/dark/system/dynamic-color theming.
- Branded Android splash and app icon.
- Localization-ready Android resources.
- Project design system and evidence documentation.
- Complete open-source/community documentation.
- CI, CodeQL, dependency review, secret scanning, Dependabot, and release automation.

### Release blocker statement

Do not publish/tag `v0.1.0` until the clean Gradle/Android CI and device/emulator checks above pass.

## Commit identity note

Requested commit email: `sanskarin@outlook.in`.

The GitHub connector used for repository writes does not expose per-write author/committer email fields. Commit search results also return `git_author_email: null` through this connector. Therefore this session cannot honestly claim that connector-generated commits carry the requested email.

For local contributor/owner Git configuration, documentation includes:

```bash
git config user.email "sanskarin@outlook.in"
```

## Recent meaningful commits

Recent atomic commits at the time of this handoff include:

- `049459d72ca118ec1a67b6651bd72e19b560d40a` — `refactor: apply design spacing tokens to ratio screen`
- `77c06927408aaedca93216c8f59f9981435ac171` — `refactor: apply design spacing tokens to BMI screen`
- `9248c938c23b3e14d788968cfa1d6fc00d00b33f` — `refactor: apply shared spacing tokens to onboarding`
- `47fb2d161acf2b2764a15264535f7e37848df001` — `docs: refresh roadmap after product and security polish`
- `0b71913a4dd2f1914f41a45ca5a49353eff68972` — `docs: align README with privacy design and security baseline`
- `3ac097a17dfc18922419c1534ca7d93ca7f02c30` — `docs: record privacy design localization and security improvements`
- `7261339f458b35fb62b9bc9abd1a57767d4a1b8a` — `test: lock privacy-first preference defaults`
- `d52f9e6fd82b4640b61b5408eb8429d7e4de665e` — `ci: add repository secret scanning workflow`
- `1af1f64d419db56ad3661684ba947abecba46571` — `docs: document adult reference evidence and update policy`
- `871dea7437ae78e09e9d5aa03dca3e64fedd82c4` — `docs: document HealthMetric visual and content design system`
- `07ee2abb786ed18d16aef91cd38114cc8cffeae6` — `feat: confirm destructive history deletion`
- `351644d5f0cb74213fb412738a60495742c33478` — `refactor: add reusable validated measurement input component`
- `4ed4eb6aaffe4de6529dcb36c6258b0825126e87` — `feat: add reusable design tokens for spacing type shapes and motion`
- `677f4ce59d613a1e1a7ad45787a2cea5dc3996a6` — `feat: externalize app shell navigation and status strings`
- `c433c65119ec0cb40181a678961cd99fd85c0bbc` — `privacy: require explicit opt-in before storing history`
- `ae9a3315e2afda60fc84a7cfaa3223ac0288a3e6` — `privacy: disable local history by default`
- `f4625a32c39797710611929d1eab3ba2baa61858` — `feat: add adaptive content width and release navigation`
- `bc3cb3f004470b5a6a57b34f2b7cfe3ef051e347` — `feat: install Android splash screen before activity creation`
- `d667dcee2faaf6115dacfc9f9fac2075bb79e62e` — `feat: use privacy-safe structured logging for data operations`

Use Git history for the complete granular commit sequence; many additional atomic commits cover build configuration, domain models, tests, workflows, UI screens, policies, ADRs, and documentation.

## Next exact tasks for a later chat/session

1. Read this file first.
2. Inspect latest `main` and any workflow/check results that are now visible.
3. If CI has failures, fix each failure with a regression-focused atomic commit and record the exact error/fix here.
4. Run/confirm the full clean-checkout quality suite.
5. Run Android instrumentation tests on emulator/device.
6. Add targeted DataStore export/restore instrumentation tests if any persistence issue is discovered during device verification.
7. Perform TalkBack + large-text manual accessibility review and record evidence.
8. Capture the screenshot set specified in `docs/assets/screenshots/README.md` using fictional/example data only.
9. Confirm protected release signing exists outside source control.
10. Update `CHANGELOG.md`, `ROADMAP.md`, and this file with final green verification hashes/results.
11. Only then create/tag `v0.1.0`.
