# Contributing to HealthMetric

Thank you for helping improve HealthMetric. Contributions must preserve the project's privacy-first, adult-only, educational, non-diagnostic approach and the integrity of every supported platform target.

## Development principles

- Keep deterministic calculation, validation, conversion, and reference rules in `shared` when they are platform-neutral.
- Keep Android-specific persistence, DataStore, document/share integrations, and Android presentation concerns in `androidApp`.
- Put reusable iOS/Desktop/Web calculator presentation in `composeApp/src/commonMain` when it is genuinely portable.
- Keep `desktopMain`, `iosMain`, and `webMain` focused on platform entry/integration concerns.
- Keep the SwiftUI/Xcode application lifecycle in `iosApp`.
- Keep shared-domain changes compatible with Android, JVM/Desktop, iOS device/simulator, JavaScript, and WebAssembly targets.
- Do not duplicate BMI or waist-to-height formulas in a platform client.
- Do not add appearance-shaming language, body rankings, pressure-oriented goals, forced sign-in, advertising trackers, or unnecessary network dependencies.
- Never commit real personal health data. Use clearly fictional/example measurements in tests, screenshots, and issues.
- Treat Android backup files as untrusted input and preserve documented size/schema/record bounds.
- Keep Android history opt-in and adult-use/onboarding state device-local; portable backups must not silently change consent or adult-only eligibility.
- Never commit production Android/Apple/desktop signing keys, passwords, certificates, provisioning secrets, or deployment credentials.
- Prefer small, atomic changes with tests and documentation.

## Supported development targets

The repository currently contains application clients for:

- Android;
- iPhone/iPad;
- Windows;
- macOS;
- Linux;
- WebAssembly browsers;
- JavaScript browser fallback/compatibility.

ChromeOS is supported through the Android application on ChromeOS environments that support Android apps.

See [`docs/cross-platform.md`](docs/cross-platform.md) for the exact feature-parity and build matrix.

## Local setup

See [`docs/setup.md`](docs/setup.md).

Current toolchain baseline:

```text
JDK 17
Gradle 8.13
Kotlin 2.4.10
Compose Multiplatform 1.11.1
Android SDK Platform 36
Android Build Tools 35.0.0
```

Apple application/framework work additionally requires macOS and Xcode.

## Quality checks

Run the complete local verification suite where the required platform tools are installed.

Unix-like systems:

```bash
bash scripts/verify.sh
```

Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

The scripts now include shared tests, formatting, desktop compilation, JS/Wasm production builds, compatibility browser output, Android tests/lint/packages, and iOS simulator framework compilation on macOS.

Repository/document integrity checks:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
```

The repository audit verifies cross-platform target declarations, toolchain/version alignment, required native/web entry files, CI host coverage, package formats, release assets, and existing Android privacy/security invariants.

## Target-specific checks

### Shared domain

```bash
gradle :shared:desktopTest
```

### Android

```bash
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintRelease
gradle :androidApp:assembleDebug
gradle :androidApp:bundleRelease
```

UI/persistence changes should also run:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

### Desktop

```bash
gradle :composeApp:compileKotlinDesktop
gradle :composeApp:packageDistributionForCurrentOS
```

Native package verification must run on the destination host: Windows for MSI, macOS for DMG, Linux for DEB.

### Web

```bash
gradle :composeApp:jsBrowserProductionWebpack
gradle :composeApp:wasmJsBrowserProductionWebpack
gradle :composeApp:composeCompatibilityBrowserDistribution
```

### iOS/iPadOS

On macOS:

```bash
gradle :composeApp:linkDebugFrameworkIosSimulatorArm64
xcodebuild -project iosApp/HealthMetric.xcodeproj -scheme HealthMetric -sdk iphonesimulator -configuration Debug CODE_SIGNING_ALLOWED=NO build
```

For shared-domain Apple changes also run:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

## Cross-platform CI expectations

A pull request affecting shared code may affect every client. Resolve failures in the relevant workflows before merge:

- main CI;
- Android instrumentation;
- Cross-platform (Web + Windows/macOS/Linux + iOS host);
- Apple shared core;
- CodeQL;
- Dependency Review;
- Secret Scan.

Do not remove or bypass a platform gate because the contributor's local operating system cannot execute that platform's native tooling.

## Commit style

Use Conventional Commits where practical:

- `feat: ...`
- `fix: ...`
- `test: ...`
- `docs: ...`
- `refactor: ...`
- `perf: ...`
- `build: ...`
- `ci: ...`
- `chore: ...`

For local Git configuration, the project owner requested:

```bash
git config user.email "sanskarin@outlook.in"
```

Do not rewrite other contributors' identity metadata.

## Pull requests

- Keep each PR focused.
- Explain user-visible and platform-specific behavior changes.
- State which platforms/modules are affected.
- Add regression tests/checks for bug fixes.
- Update docs when setup, architecture, privacy, backup schema, platform support, packaging, or release steps change.
- Include UI evidence for visual changes using fictional/example data only.
- Resolve formatting, tests, lint, native packaging, browser build, emulator, Apple-target, documentation-integrity, and security failures before merge.
- Do not claim feature parity that the implementation does not provide.
- Complete the repository PR checklist rather than deleting safety/privacy items without explanation.

## Calculation and evidence changes

Changes to adult reference ranges or explanatory health text require:

1. a documented authoritative source;
2. updated source review date;
3. a clear versioned reference profile when thresholds/interpretation change;
4. tests for every boundary;
5. neutral wording that does not turn population screening into diagnosis or an appearance goal;
6. updates to [`docs/evidence.md`](docs/evidence.md), `CHANGELOG.md`, and release notes.

The shared reference/calculator implementation must remain the single source used by all platform clients.

## Android backup and persistence changes

Read [`docs/backup-format.md`](docs/backup-format.md) and ADR 0004 before changing portable Android data.

A change must retain or deliberately replace, with review:

- 1 MiB bounded IO;
- supported schema validation;
- bounded history retention;
- per-record validation;
- duplicate-ID protection;
- device-local history consent/adult-gate/onboarding state;
- explicit restore confirmation;
- migration/compatibility regression coverage.

If persistent history is generalized to other platforms, do not copy Android DataStore code into unsupported targets. Introduce a reviewed shared contract plus platform implementations and privacy tests.

## Locale changes

Shared arithmetic remains locale-neutral. Android presentation owns its mature locale-aware formatting. The shared Compose client currently accepts practical dot/comma decimal input.

When changing numeric presentation, test the affected platforms and do not let locale logic alter domain arithmetic.

## New platform features

When generalizing a feature:

1. identify platform-independent behavior;
2. place deterministic rules in `shared`;
3. place reusable Compose presentation in `composeApp/commonMain` where appropriate;
4. isolate native integration in the relevant platform source set/host;
5. add CI for the platform behavior;
6. update feature-parity documentation only after the implementation exists;
7. preserve adult-only/non-diagnostic safety language.

## Release workflow changes

The tagged workflow currently aggregates seven release assets:

- Android unsigned APK;
- Android unsigned AAB;
- Windows MSI;
- macOS DMG;
- Linux DEB;
- Web ZIP;
- iOS developer framework ZIP.

Do not convert the iOS framework artifact into a repository-stored signed IPA by committing Apple signing secrets. Store signing remains a protected distribution concern.

## Security and privacy

Read [`SECURITY.md`](SECURITY.md) and [`PRIVACY.md`](PRIVACY.md). Report vulnerabilities privately instead of creating public exploit details.

## Conduct

Participation is governed by [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md).
