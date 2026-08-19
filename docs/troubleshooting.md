# Troubleshooting

## Python is not found

Repository/documentation/release tooling requires Python 3 and currently uses only the Python standard library.

```bash
python3 --version
```

On Windows the executable may be named `python`. `scripts/verify.ps1` supports overriding it with `PYTHON_BIN`.

Do not skip the Python checks just because the Kotlin/Gradle build succeeds; repository, documentation and release-integrity failures are independent quality gates.

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

Run:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
```

The repository audit checks required project/workflow paths, Android manifest privacy invariants, shared/Android/desktop safety and release contracts, and **exhaustive tracked-file documentation coverage**.

If it reports:

```text
docs/repository-file-reference.md must document tracked file: <path>
```

then `<path>` is returned by `git ls-files` but is missing from the exhaustive file reference. Add the exact backticked path to [`repository-file-reference.md`](repository-file-reference.md) with a real explanation of its responsibility. If the file should not be tracked at all, remove it appropriately instead of documenting accidental/generated state.

Use [`documentation-map.md`](documentation-map.md) to determine whether the behavior also requires updates to architecture, privacy, backup, testing, release or another canonical document.

The Markdown audit validates repository-local relative targets. Fix broken/moved targets rather than disabling the check.

## Repository audit cannot run `git ls-files`

`scripts/check_repository.py` must run inside a real Git checkout because exhaustive documentation coverage is defined against Git's tracked set.

Confirm:

```bash
git rev-parse --show-toplevel
git ls-files
```

Running a copied subset of source files without `.git` metadata is not equivalent to repository verification.

## Release-tooling Python tests fail

Run:

```bash
python3 -m unittest discover -s scripts/tests -p "test_*.py" -v
```

The suite covers stable release-tag validation, Android semantic `versionCode` mapping, Android/desktop/native-package version agreement, deterministic artifact staging, exact final asset verification, and checksums. Treat failures as release-infrastructure defects even when application tests pass.

For the prepared `2.0.12` candidate, also run:

```bash
python3 scripts/check_release_version.py v2.0.12
```

The expected metadata is Android `versionName = 2.0.12`, Android `versionCode = 20012`, desktop project `version = 2.0.12`, and desktop native `packageVersion = 2.0.12`.

## Shared tests fail

```bash
gradle :shared:desktopTest --stacktrace
```

Calculation failures should be reproduced with deterministic example inputs and regression coverage. Platform UI code should not patch around a shared formula/validation defect.

## Desktop tests fail

```bash
gradle :desktopApp:test --stacktrace
```

Use `DesktopNumbersTest` for text syntax parsing and `DesktopCalculationsTest` for shared-core/presentation integration, including split imperial-height component checks. Calculation thresholds/formulas should be fixed/tested in `shared`, not duplicated in desktop code.

## Desktop imperial height says remaining inches are invalid

This can be intentional. Desktop imperial forms use **whole feet + remaining inches**, and the remaining-inch component must be in `[0, 12)`.

Examples:

- `5 ft 8 in` — valid component shape;
- `5 ft 11.9 in` — valid component shape;
- `5 ft 12 in` — rejected;
- `5 ft 20 in` — rejected.

HealthMetric does not silently normalize `5 ft 20 in` to another feet/inches representation. Once the component shape is valid, total adult height-range validation remains in the shared domain.

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

Native DMG/MSI/DEB packaging is platform-specific. Use the matching operating system:

```bash
# Linux
gradle :desktopApp:packageDeb --stacktrace

# Windows
gradle :desktopApp:packageMsi --stacktrace

# macOS
gradle :desktopApp:packageDmg --stacktrace
```

The dedicated `Desktop` workflow verifies JAR **and** matching native package creation on Linux, Windows, and macOS. The tagged release workflow also stages those packages.

For the `2.0.12` candidate, all desktop hosts use native `packageVersion = 2.0.12`; there is no separate macOS package-version workaround.

Build success still does not prove production signing/notarization or human host-platform acceptance. Certificates/private keys/notarization credentials stay outside source control.

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

Compiling these shared targets does not mean the repository contains an iOS UI application.

## Android app opens the adult-only unavailable screen

The adult BMI/waist reference calculators are intentionally unavailable when onboarding indicates the user is under 18. Portable backups cannot change the adult-use gate.

If the choice was accidental, use **Return to age selection**. That correction clears only the Android adult/onboarding choice and preserves unrelated local settings/history.

Do not edit a backup to try to change age-gate state; those fields are non-portable/ignored on restore.

## Android history is not saving

Open Settings and check **Save local history**. History is disabled by default. Importing a backup does not enable history saving.

## Older Android history disappeared after changing retention

Lowering **History retention** immediately trims older entries beyond the selected maximum. Supported limits are 50, 100, 250, and 500.

History is normalized newest-first by timestamp before retention is applied.

## Decimal input is rejected

Android decimal measurement fields accept digits plus one `.` or `,` separator and format results using the active locale.

Desktop measurement fields accept ordinary dot/comma decimal text but intentionally reject grouping separators, unit suffixes, multiple separators, explicit signs, scientific notation, `NaN`, and infinity. Desktop feet must be whole numbers, and remaining inches must stay below 12.

## Restore says backup is invalid

Android currently supports backup schema version `1`. Common causes include:

- malformed UTF-8 bytes in the selected document;
- unsupported/missing `schemaVersion`;
- file larger than 1 MiB;
- invalid top-level JSON;
- missing top-level `history`;
- `history` is not a JSON array;
- `history` is non-empty but **no valid history entry survives sanitation**;
- document is not a HealthMetric backup.

Malformed UTF-8 is rejected at the document-read boundary before JSON restore parsing. HealthMetric does not silently replacement-decode malformed byte sequences.

Important distinction:

- `"history": []` is a valid intentional empty-history backup;
- a non-empty array containing only malformed/invalid records is rejected before DataStore mutation;
- when at least one record is valid, malformed neighboring records can be skipped independently.

This fail-closed rule prevents a damaged non-empty backup from being interpreted as a deliberate empty-history restore and replacing valid local portable data.

See [`backup-format.md`](backup-format.md).

Desktop does not currently import HealthMetric backups.

## Failed restore changed nothing

For malformed UTF-8, unsupported schema, malformed/missing/non-array history structure, oversized data, or a non-empty all-invalid history array, this is intentional: decoding/structural validation happens before the DataStore edit transaction.

If a failed restore changes theme/history/retention, treat it as a regression and add the lowest practical unit/instrumentation coverage before fixing it.

## Restore does not change history opt-in or adult-use screen

This is intentional. Android portable backups do not control future history-saving consent, adult-use confirmation, or onboarding completion.

## Save backup opens a document picker

Expected. **Save JSON backup to a file** uses Android Storage Access Framework; **Share JSON backup** is separate and opens Android's share chooser.

## Restore asks for confirmation after file selection

Expected. HealthMetric reads the bounded, well-formed UTF-8 selected document first, then requires explicit confirmation before portable history/settings are replaced.

## Android history chart fails with extreme imported values

The UI should normalize any finite imported result safely through `ChartScale` before Canvas positioning. A crash/non-finite coordinate from extreme finite values is a regression; reproduce it in `ChartScaleTest` rather than clamping or rewriting backup data silently.

## Android navigation crashes after an app update

Saved navigation/history-filter enum names should pass through `savedEnumValueOrDefault`, so stale/removed names fall back safely. Add a `SavedEnumTest` regression for any newly discovered state-restoration crash.

## Android emulator CI fails

```bash
gradle :androidApp:connectedDebugAndroidTest --stacktrace
```

Use an API 35 emulator where practical and inspect uploaded instrumentation reports before changing production code.

The workflow also requires the eight release screenshot PNGs. A screenshot-artifact failure may be a capture/path/evidence failure even when earlier UI assertions passed.

## Desktop CI fails only on one operating system

Reproduce on that OS with:

```bash
gradle :desktopApp:ktlintCheck :desktopApp:test :desktopApp:packageUberJarForCurrentOS --stacktrace
```

Then run the matching native package task. Treat platform-specific failures as real until explained; do not remove the OS from the matrix just to make checks green.

## Tagged release preflight fails

Check these independently:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
python3 -m unittest discover -s scripts/tests -p "test_*.py"
python3 scripts/check_release_version.py v2.0.12
```

For the current candidate, a valid release requires:

- stable tag `v2.0.12`;
- Android `versionName = 2.0.12`;
- Android `versionCode = 20012` according to the repository semantic mapping;
- desktop project `version = 2.0.12`;
- desktop native `packageVersion = 2.0.12`;
- the tag commit to equal current `main`.

A future tag must use stable `vMAJOR.MINOR.PATCH` and satisfy the same cross-platform metadata rules for its own version.

The release checkout uses complete history for the current-main comparison. Do not bypass a preflight failure by weakening version, tag, current-main, or documentation checks.

## Tagged release says artifact count is wrong

`scripts/stage_release_assets.py` requires exactly one non-empty expected build output for each artifact type. Duplicate old build outputs in a workspace are intentionally treated as ambiguous rather than selecting one arbitrarily.

For `v2.0.12`, the final `scripts/verify_release_assets.py` step requires exactly eight versioned binary assets with no missing, unexpected, or empty files and writes `SHA256SUMS.txt` before publication.

## CI differs from local results

Match the relevant workflow baseline:

- Python 3 for repository/release tooling;
- JDK 17 and Gradle 8.13 across build workflows;
- Android SDK 36/Build Tools 35.0.0 for Android/main CI;
- API 35 emulator for connected Android tests;
- Linux/Windows/macOS runners for Desktop workflow;
- macOS runner for Apple shared-target compilation.

Then rerun the exact commands from `.github/workflows/`.

For pull-request release readiness, compare workflow results only for the exact current head commit; a green superseded head is not evidence for a newer commit.

## Security/privacy bug

Do not post sensitive details publicly. Follow [`../SECURITY.md`](../SECURITY.md).
