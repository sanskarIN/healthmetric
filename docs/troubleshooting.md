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

The repository audit verifies required project/documentation/workflow paths, offline Android manifest invariants, required README/contact/funding metadata, key privacy documentation, APK/AAB release-pipeline configuration, the eight required screenshot-evidence captures, and the absence of known forbidden temporary probe files. The Markdown audit verifies relative internal links.

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

## About has no bottom navigation

This is intentional. About is a secondary destination and hides bottom navigation. Use its top-bar **Back** action or Android system back to return to the screen that opened About.

If either action does not return to the originating screen, reproduce with `AboutNavigationUiTest` before changing navigation code.

## History is not saving

Open Settings and check **Save local history**. History is disabled by default. When disabled, calculations remain available but new history entries are intentionally not stored.

Importing a backup does not enable history saving. This is intentional: history opt-in remains a device-local privacy choice.

## History order changes unexpectedly

Persisted and imported history is canonical newest-first by `timestampEpochMillis`. Undoing deletion of an older entry should restore it to chronological position rather than moving it to the top.

If an imported JSON array is out of order, HealthMetric validates/deduplicates accepted records, sorts them newest-first, then applies the selected retention cap. Array position is not authoritative chronology.

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

## Release App Bundle is missing

Run:

```bash
gradle :androidApp:bundleRelease --stacktrace
```

The expected output is under:

`androidApp/build/outputs/bundle/release/`

The main CI and tagged release workflows also require the `.aab` artifact. If documentation says AAB packaging is available while this task/artifact is missing, treat that as release-pipeline drift and fix the implementation rather than weakening repository invariants.

## Android emulator CI fails

Reproduce with:

```bash
gradle :androidApp:connectedDebugAndroidTest --stacktrace
```

Use an API 35 Pixel 7-style emulator where practical to match the dedicated workflow. Check instrumentation reports uploaded by the workflow before changing production code.

The emulator workflow also expects the release screenshot test to create eight PNG files under the app-scoped `release-screenshots` directory. Missing PNGs cause the `android-release-screenshots` artifact upload to fail.

## Screenshot evidence artifact is missing

First confirm `ReleaseScreenshotCaptureTest` passed. Then verify the workflow can pull:

`/sdcard/Android/data/io.github.sanskarin.healthmetric/files/release-screenshots/`

Expected files are listed in [`assets/screenshots/README.md`](assets/screenshots/README.md). Do not work around a missing artifact by uploading fabricated/mock screenshots.

## CI differs from local results

Match CI's baseline:

- Ubuntu runner for Android/JVM verification;
- JDK 17;
- Gradle 8.13;
- Android SDK 36;
- Build Tools 35.0.0;
- API 35 Pixel 7 emulator for connected tests/screenshot evidence;
- macOS runner for Apple shared-target compilation.

Then rerun the exact commands from `.github/workflows/`.

## Security/privacy bug

Do not post sensitive details publicly. Follow [`../SECURITY.md`](../SECURITY.md).
