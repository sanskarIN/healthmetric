# Troubleshooting

## Gradle is not found

Install/use Gradle 8.13 or open the project in Android Studio and configure the IDE-managed Gradle environment.

Verify:

```bash
gradle --version
```

## Wrong Java version

HealthMetric uses JDK 17 for Android builds.

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
gradle :androidApp:lintDebug
```

Open the HTML report under `androidApp/build/reports/`. Fix the root cause rather than globally suppressing warnings.

## Shared tests fail

Run only the shared suite for a faster feedback loop:

```bash
gradle :shared:desktopTest --stacktrace
```

Calculation failures should be reproduced with deterministic example inputs and covered by a regression test.

## App opens the adult-only screen

The current adult BMI and waist-to-height reference calculators are intentionally unavailable when the onboarding response indicates the user is under 18. Deleting all app data resets onboarding.

## History is not saving

Open Settings and check **Save local history**. When disabled, calculations remain available but new history entries are intentionally not stored.

## Restore says backup is invalid

HealthMetric currently supports backup schema version `1`. The restore parser rejects unsupported top-level schemas and safely ignores malformed history payloads rather than applying uncertain data.

Do not manually edit a backup to bypass schema checks. Use a backup exported by a compatible HealthMetric version.

## Export opens the Android share sheet instead of saving a file

This is expected in the initial implementation. Export currently shares JSON text through an explicit Android chooser. Direct Storage Access Framework file export is tracked on the roadmap.

## CI differs from local results

Match CI's baseline:

- Ubuntu runner;
- JDK 17;
- Gradle 8.13;
- Android SDK 36;
- Build Tools 35.0.0.

Then rerun the exact commands from `.github/workflows/ci.yml`.

## Security/privacy bug

Do not post sensitive details publicly. Follow [`../SECURITY.md`](../SECURITY.md).
