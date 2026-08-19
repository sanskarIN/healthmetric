# Troubleshooting

## Gradle is not found

Install/use Gradle 8.13 or configure an IDE-managed Gradle environment.

```bash
gradle --version
```

Set `GRADLE_BIN` before `scripts/verify.sh` or `scripts/verify.ps1` if the executable has a different path/name.

## Wrong Java version

HealthMetric uses JDK 17 for Android, desktop, shared JVM, and CI builds.

```bash
java -version
```

Set the IDE Gradle JDK to JDK 17.

## Android SDK 36 missing

Install:

```bash
sdkmanager "platforms;android-36" "build-tools;35.0.0"
```

Then resync Gradle.

Even desktop-focused contributors may need the Android SDK when running the complete root verification suite because Android modules participate in the same Gradle project.

## Dependency resolution failure

Confirm access to Google Maven, Maven Central, and the Gradle Plugin Portal. Do not add untrusted mirrors or disable TLS verification.

```bash
gradle --refresh-dependencies :shared:desktopTest :desktopApp:test :androidApp:assembleDebug
```

## ktlint failure

Inspect the reported file/line, then run:

```bash
gradle :shared:ktlintFormat :androidApp:ktlintFormat :desktopApp:ktlintFormat
```

Review the diff before committing.

## Android lint failure

```bash
gradle :androidApp:lintRelease
```

Open the HTML report under `androidApp/build/reports/` and fix the root cause rather than globally suppressing warnings.

## Repository/documentation audit failure

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
```

The repository audit verifies required Android/desktop/docs/workflow paths, Android manifest privacy invariants, README metadata, desktop adult/privacy structure, and key privacy documentation. The Markdown audit verifies internal relative targets.

Fix the missing/inconsistent source rather than bypassing the audit.

## Shared tests fail

```bash
gradle :shared:desktopTest --stacktrace
```

Calculation failures should be reproduced with deterministic example inputs and regression coverage.

## Desktop tests fail

```bash
gradle :desktopApp:test --stacktrace
```

Use `DesktopNumbersTest` for text parsing and `DesktopCalculationsTest` for shared-core/presentation integration. Calculation thresholds/formulas should be fixed/tested in `shared`, not duplicated in desktop code.

## Desktop app does not start

Verify JDK 17, then run:

```bash
gradle :desktopApp:run --stacktrace
```

If the failure is dependency/plugin resolution, verify network/proxy access. If the window starts but rendering fails, compare the current OS/JDK/runtime with the `Desktop` GitHub Actions matrix.

## Desktop runnable JAR is missing

Run:

```bash
gradle :desktopApp:packageUberJarForCurrentOS --stacktrace
```

Expected Compose Desktop JAR output is under:

```text
desktopApp/build/compose/jars/
```

Do not hard-code a different generated path into release automation without verifying the Compose task output.

## Desktop native package task fails

Native DMG/MSI/DEB packaging is platform-specific. Build the target format on its matching operating system and inspect the Compose Desktop Gradle task failure.

The standard cross-platform CI/release flow validates runnable JARs. Native installer publication requires additional manual platform checks and, if introduced, protected signing/notarization outside source control.

## Desktop state resets after restart

This is intentional. The desktop client does not persist measurements, results, adult-use selection, theme, or navigation state.

See [`desktop.md`](desktop.md) and ADR 0005.

## Desktop under-18 selection hides calculators

This is intentional. HealthMetric does not apply its adult BMI or waist-to-height reference tools to people under 18. Return to the age-selection screen if the wrong option was selected; the desktop choice is not persisted.

## Apple shared target fails locally

The iOS targets require macOS with Xcode/Apple SDKs.

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64 --stacktrace
```

If Android project configuration reports a missing compile SDK, install Android SDK Platform 36/Build Tools 35.0.0. Compare with `.github/workflows/apple-shared.yml`.

## Android app opens the adult-only unavailable screen

The adult BMI/waist reference calculators are intentionally unavailable when onboarding indicates the user is under 18. Portable backups cannot change the adult-use gate.

Do not edit a backup to try to change age-gate state; those fields are non-portable/ignored on restore.

## Android history is not saving

Open Settings and check **Save local history**. History is disabled by default. Importing a backup does not enable history saving.

## Older Android history disappeared after changing retention

Lowering **History retention** immediately trims older entries beyond the selected maximum. Supported limits are 50, 100, 250, and 500.

## Decimal input is rejected

Android decimal measurement fields accept digits plus one `.` or `,` separator and format results using the active locale. Desktop fields likewise accept dot/comma decimal text but do not interpret grouping separators, unit suffixes, multiple separators, `NaN`, or infinity as valid measurements.

## Restore says backup is invalid

Android currently supports backup schema version `1`. Common causes:

- unsupported/missing `schemaVersion`;
- file larger than 1 MiB;
- invalid top-level JSON;
- document is not a HealthMetric backup.

Malformed individual history records are skipped when the top-level document is otherwise valid. See [`backup-format.md`](backup-format.md).

Desktop does not currently import HealthMetric backups.

## Restore does not change history opt-in or adult-use screen

This is intentional. Android portable backups do not control future history-saving consent, adult-use confirmation, or onboarding completion.

## Save backup opens a document picker

Expected. **Save JSON backup to a file** uses Android Storage Access Framework; **Share JSON backup** is separate and opens Android's share chooser.

## Restore asks for confirmation after file selection

Expected. HealthMetric reads the bounded selected document first, then requires explicit confirmation before portable history/settings are replaced.

## Android emulator CI fails

```bash
gradle :androidApp:connectedDebugAndroidTest --stacktrace
```

Use an API 35 emulator where practical and inspect uploaded instrumentation reports before changing production code.

## Desktop CI fails only on one operating system

Reproduce on that OS with:

```bash
gradle :desktopApp:ktlintCheck :desktopApp:test :desktopApp:packageUberJarForCurrentOS --stacktrace
```

Treat platform-specific failures as real until explained; do not remove the OS from the matrix just to make checks green.

## CI differs from local results

Match the relevant workflow baseline:

- JDK 17 and Gradle 8.13 across build workflows;
- Android SDK 36/Build Tools 35.0.0 for Android/main CI;
- API 35 emulator for connected Android tests;
- Linux/Windows/macOS runners for Desktop workflow;
- macOS runner for Apple shared-target compilation.

Then rerun the exact commands from `.github/workflows/`.

## Security/privacy bug

Do not post sensitive details publicly. Follow [`../SECURITY.md`](../SECURITY.md).
