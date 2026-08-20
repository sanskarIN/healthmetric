# Troubleshooting

For platform-specific setup/build commands, start with [`cross-platform.md`](cross-platform.md) and [`setup.md`](setup.md).

## Gradle is not found

HealthMetric uses Gradle 8.13 in CI.

Verify:

```bash
gradle --version
```

If your executable has another path/name, set `GRADLE_BIN` before running `scripts/verify.sh` or `scripts/verify.ps1`.

## Wrong Java version

HealthMetric uses JDK 17 for Gradle/Android/desktop/web builds.

```bash
java -version
```

In Android Studio, set **Gradle JDK** to JDK 17.

## Kotlin / Compose compatibility failure

The current release-line toolchain is:

```text
Kotlin 2.4.10
Compose Multiplatform 1.11.1
AGP 8.13.2
Gradle 8.13
JDK 17
```

Confirm `gradle/libs.versions.toml` was not partially downgraded. Run:

```bash
gradle --refresh-dependencies :shared:desktopTest
```

Do not mix an arbitrary Kotlin/Compose compiler version with the pinned catalog simply to suppress a build error.

## Android SDK 36 missing

Install:

```bash
sdkmanager "platforms;android-36" "build-tools;35.0.0"
```

Then resync Gradle.

## Dependency resolution failure

Confirm the machine can reach Google Maven, Maven Central, and the Gradle Plugin Portal. Do not add random mirror repositories or disable TLS verification as a workaround.

```bash
gradle --refresh-dependencies :shared:desktopTest
```

## ktlint failure

Check all Kotlin modules:

```bash
gradle :shared:ktlintCheck :composeApp:ktlintCheck :androidApp:ktlintCheck
```

Format when appropriate:

```bash
gradle :shared:ktlintFormat :composeApp:ktlintFormat :androidApp:ktlintFormat
```

Review the diff before committing.

## Repository/documentation audit failure

Run:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
```

The repository audit checks required files, Android offline/privacy manifest invariants, toolchain/version alignment, platform target declarations, cross-platform CI coverage, desktop package formats, iOS host files, and the seven expected multi-platform release asset names.

Fix the inconsistent source rather than bypassing the audit.

## Shared tests fail

```bash
gradle :shared:desktopTest --stacktrace
```

Calculator failures should be reproduced with deterministic example inputs and protected by a regression test in `shared/src/commonTest`.

## Desktop application does not compile

```bash
gradle :composeApp:compileKotlinDesktop --stacktrace
```

Check:

- JDK 17 is active;
- Kotlin/Compose versions match the version catalog;
- common Compose code does not import Android/iOS/browser-only APIs;
- the desktop source set only contains desktop entry-point/platform behavior.

## Desktop native package fails

Run on the destination operating system:

```bash
gradle :composeApp:packageDistributionForCurrentOS --stacktrace
```

Native packaging is host-specific:

- Windows builds MSI;
- macOS builds DMG;
- Linux builds DEB.

Do not treat failure to create a Windows MSI on Linux as a supported cross-package path. Compare with the matching job in `.github/workflows/cross-platform.yml`.

## WebAssembly build fails

Run:

```bash
gradle :composeApp:wasmJsBrowserProductionWebpack --stacktrace
```

Then also verify JavaScript and the compatibility distribution:

```bash
gradle :composeApp:jsBrowserProductionWebpack
gradle :composeApp:composeCompatibilityBrowserDistribution
```

Check that common UI code does not depend on JVM/Android/Apple-only APIs and that `composeApp/src/webMain/resources/index.html` plus `styles.css` are still present.

## Browser opens a blank or incorrectly sized canvas

Confirm the application was built from the generated browser distribution rather than by opening source files directly.

Check:

```text
composeApp/src/webMain/resources/index.html
composeApp/src/webMain/resources/styles.css
```

The CSS must keep the document/canvas viewport sized correctly. Use the local development task and the URL printed by Gradle:

```bash
gradle :composeApp:wasmJsBrowserDevelopmentRun
```

For compatibility testing, also try the JavaScript development/production target.

## WebAssembly is unavailable in a browser

Use the JavaScript/compatibility output rather than assuming all browsers/environments support the same Wasm path.

Build:

```bash
gradle :composeApp:composeCompatibilityBrowserDistribution
```

Do not remove the JS target merely because the preferred browser supports Wasm.

## iOS shared framework fails

Apple framework builds require macOS with Xcode/Apple SDKs.

Shared domain targets:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64 --stacktrace
```

Application framework:

```bash
gradle :composeApp:linkDebugFrameworkIosSimulatorArm64 --stacktrace
```

Confirm JDK 17 and Gradle are available in the macOS environment.

## Xcode cannot import `HealthMetricUI`

Open:

```text
iosApp/HealthMetric.xcodeproj
```

Verify:

1. the `HealthMetric` scheme is selected;
2. `gradle :composeApp:linkDebugFrameworkIosSimulatorArm64` succeeds;
3. the Xcode build phase can execute `gradle`;
4. the configured framework search path still points beneath `composeApp/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)`;
5. the framework name remains `HealthMetricUI` in `composeApp/build.gradle.kts`;
6. `ContentView.swift` imports `HealthMetricUI`.

The Xcode direct-integration build phase runs:

```bash
gradle :composeApp:embedAndSignAppleFrameworkForXcode
```

## Xcode simulator build fails because of signing

CI validates a simulator build with signing disabled:

```bash
xcodebuild -project iosApp/HealthMetric.xcodeproj -scheme HealthMetric -sdk iphonesimulator -configuration Debug CODE_SIGNING_ALLOWED=NO build
```

Real-device/App Store builds require the distributor's protected Apple signing configuration. Do not commit private signing material to fix a local signing error.

## Android lint failure

```bash
gradle :androidApp:lintRelease
```

Open the HTML report under `androidApp/build/reports/` and fix the root cause rather than globally suppressing warnings.

## Android emulator CI fails

Reproduce with:

```bash
gradle :androidApp:connectedDebugAndroidTest --stacktrace
```

Use an API 35 emulator where practical to match the dedicated workflow, and inspect its uploaded instrumentation report.

## App opens the adult-only screen

This is intentional when adult-use confirmation has not been provided. The current BMI and waist-to-height reference tools are intended for adults age 18 or older.

On Android, portable backups cannot change adult-use confirmation. Do not edit backup files to try to bypass the gate.

## Android history is not saving

Open Settings and check **Save local history**. History is disabled by default. Importing a backup does not enable future history saving because opt-in remains a device-local privacy choice.

The iOS/desktop/web calculator clients currently do not claim Android DataStore history parity. Session-only calculator state on those clients is expected.

## Older Android history disappeared after changing retention

Lowering **History retention** immediately trims older entries beyond the selected maximum. Supported limits are 50, 100, 250, and 500.

This trimming is intentionally irreversible inside the current local store. Create an explicit backup first if an external copy is needed.

## Decimal input is rejected

The mature Android client accepts locale-aware decimal input. The shared Compose calculator accepts practical dot/comma decimal input and still passes numeric values through the shared range validation.

Do not enter unit suffixes, multiple decimal separators, `NaN`, or infinity text.

## Android restore says backup is invalid

Current portable backup schema version is `1`. Common causes:

- unsupported/missing `schemaVersion`;
- file larger than 1 MiB;
- invalid top-level JSON;
- document is not a HealthMetric backup.

Malformed individual history records are skipped when the top-level document is otherwise valid. See [`backup-format.md`](backup-format.md).

## Android restore does not change history opt-in/adult gate

This is intentional. Portable backups do not control:

- future history-saving consent;
- adult-use confirmation;
- onboarding completion.

Legacy fields with similar names are ignored for those device-local choices.

## Save backup opens a document picker

Expected Android behavior. **Save JSON backup to a file** uses the Storage Access Framework. **Share JSON backup** is a separate action using the Android share chooser.

## Restore asks for confirmation after file selection

Expected Android behavior. The app reads the bounded selected document, then asks before replacing portable settings/history.

## CI differs from local results

Match the runner responsible for the failing target:

- Ubuntu — repository/Android/JVM/Web/Linux checks;
- Windows — Windows desktop package;
- macOS — macOS desktop and iOS/Xcode checks;
- API 35 Android emulator — connected Android tests.

Baseline toolchain:

- JDK 17;
- Gradle 8.13;
- Kotlin 2.4.10;
- Compose Multiplatform 1.11.1;
- Android SDK 36 / Build Tools 35.0.0 where Android is configured.

Then rerun the exact task from `.github/workflows/`.

## Tagged release is missing a platform asset

The release workflow expects seven assets:

1. Android unsigned APK;
2. Android unsigned AAB;
3. Windows MSI;
4. macOS DMG;
5. Linux DEB;
6. Web ZIP;
7. iOS developer framework ZIP.

The final publish job deliberately refuses to create a complete release if fewer than seven files were aggregated. Inspect the corresponding platform build job rather than weakening the count check.

## Security/privacy bug

Do not post sensitive vulnerability details publicly. Follow [`../SECURITY.md`](../SECURITY.md).
