# Troubleshooting

## Gradle is not found

Install/use Gradle 8.13 or open the project in Android Studio and configure the IDE-managed Gradle environment.

Verify:

```bash
gradle --version
```

You can also set `GRADLE_BIN` before running `scripts/verify.sh` or `scripts/verify.ps1` if your Gradle executable has a different path/name.

## Wrong Java version

HealthMetric uses JDK 17 for Android builds and CI.

Verify:

```bash
java -version
```

In Android Studio, set **Gradle JDK** to a JDK 17 installation.

## Android SDK 36 missing

Install:

```bash
sdkmanager "platforms;android-36" "build-tools;35.0.0"
```

Then resync Gradle.

## Dependency resolution failure

Confirm the machine can reach Google Maven, Maven Central, and the Gradle Plugin Portal. Do not add random mirror repositories or disable TLS verification as a workaround.

Retry after checking network/proxy configuration:

```bash
gradle --refresh-dependencies :androidApp:assembleDebug
```

## ktlint failure

Inspect the reported file/line, then run:

```bash
gradle :shared:ktlintFormat :androidApp:ktlintFormat
```

Review the formatting diff before committing.

## Android lint failure

Run:

```bash
gradle :androidApp:lintRelease
```

Open the HTML report under `androidApp/build/reports/`. Fix the root cause rather than globally suppressing warnings.

## Repository/documentation audit failure

Run:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
```

The first command verifies required repository files, offline Android manifest invariants, README project/contact/funding metadata, and key privacy documentation. The second verifies relative Markdown link targets.

Fix the missing/inconsistent source rather than bypassing the audit.

## Shared tests fail

Run only the shared suite for a faster feedback loop:

```bash
gradle :shared:desktopTest --stacktrace
```

Calculation failures should be reproduced with deterministic example inputs and covered by a regression test.

## Apple shared target fails locally

The iOS targets require macOS with Xcode/Apple SDKs. Run:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64 --stacktrace
```

If Android project configuration also reports a missing compile SDK, install Android SDK Platform 36/Build Tools 35.0.0. Compare with `.github/workflows/apple-shared.yml`.

## App opens the adult-only screen

The current adult BMI and waist-to-height reference calculators are intentionally unavailable when the onboarding response indicates the user is under 18. Portable backups cannot change the adult-use gate.

Do not edit a backup to try to change age-gate state; those fields are not portable and are ignored on restore.

## History is not saving

Open Settings and check **Save local history**. History is disabled by default. When disabled, calculations remain available but new history entries are intentionally not stored.

Importing a backup does not enable history saving. This is intentional: history opt-in remains a device-local privacy choice.

## Older history disappeared after changing retention

Lowering **History retention** immediately trims older entries beyond the selected maximum. Supported limits are 50, 100, 250, and 500.

This trimming is intentionally irreversible. Create an explicit backup before lowering the limit if you need to preserve a copy outside the app.

## Decimal input is rejected

Decimal measurement fields accept digits plus one `.` or `,` separator. They do not accept grouping separators, unit suffixes, multiple separators, `NaN`, or infinity text.

If the on-screen keyboard supplies a locale-specific separator, HealthMetric accepts comma/dot decimal input and formats results using the active locale.

## Restore says backup is invalid

HealthMetric currently supports backup schema version `1`. Common causes include:

- unsupported/missing `schemaVersion`;
- file larger than 1 MiB;
- invalid top-level JSON;
- a document that is not a HealthMetric backup.

Malformed individual history records are skipped when the top-level document is otherwise valid. See [`backup-format.md`](backup-format.md).

Do not manually edit a backup to bypass schema or safety checks.

## Restore does not change history opt-in or the adult-use screen

This is intentional. Portable backups do not control:

- future history-saving consent;
- adult-use confirmation;
- onboarding completion.

Restore preserves those current-device values even if an older schema-v1 file contains similarly named legacy fields.

## Save backup opens a document picker

This is expected. **Save JSON backup to a file** uses Android's Storage Access Framework so the user chooses the destination. Backup JSON is generated after the destination is selected.

**Share JSON backup** is a separate action and opens Android's share chooser.

## Restore asks for confirmation after file selection

This is expected. HealthMetric first reads the bounded selected document, then requires explicit confirmation before portable history/settings are replaced.

## Android emulator CI fails

Reproduce with:

```bash
gradle :androidApp:connectedDebugAndroidTest --stacktrace
```

Use an API 35 emulator where practical to match the dedicated workflow. Check instrumentation reports uploaded by the workflow before changing production code.

## CI differs from local results

Match CI's baseline:

- Ubuntu runner for Android/JVM verification;
- JDK 17;
- Gradle 8.13;
- Android SDK 36;
- Build Tools 35.0.0;
- API 35 emulator for connected tests;
- macOS runner for Apple shared-target compilation.

Then rerun the exact commands from `.github/workflows/`.

## Security/privacy bug

Do not post sensitive details publicly. Follow [`../SECURITY.md`](../SECURITY.md).
