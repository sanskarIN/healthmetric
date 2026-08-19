# HealthMetric — Final 2.0.12 Work Handoff

Last updated: 2026-08-19

## Authoritative repository state

Repository:

`https://github.com/sanskarIN/healthmetric`

Primary branch:

`main`

Active integration branch:

`phase3/release-readiness-and-desktop-core`

Active integration pull request:

- PR #14 — `release: prepare HealthMetric 2.0.12`
- `https://github.com/sanskarIN/healthmetric/pull/14`

PR #14 is the authoritative integration path for the current milestone. It combines the desktop/release-readiness work with the meaningful Android release-hardening/evidence work previously developed separately in PR #13.

PR #13 must not be merged independently after PR #14. After PR #14 is merged, compare PR #13 against merged `main`; close it as superseded only if no meaningful unique change remains.

This document supersedes earlier active handoff instructions that targeted the original `0.1.0` development release.

## Current release target

Prepared public version:

`2.0.12`

Prepared stable tag:

`v2.0.12`

Android release metadata:

- `versionName = "2.0.12"`
- `versionCode = 20012`

Desktop release metadata:

- project `version = "2.0.12"`
- native `packageVersion = "2.0.12"`
- Linux DEB, Windows MSI, and macOS DMG use the same `2.0.12` package metadata.

Android version-code mapping:

`MAJOR * 10000 + MINOR * 100 + PATCH`

For `2.0.12`:

`2 * 10000 + 0 * 100 + 12 = 20012`

The mapping reserves two digits each for `MINOR` and `PATCH`. Release tooling rejects semantic versions whose minor/patch values cannot be represented by that mapping.

The previous desktop `0.1.0` development configuration used a macOS-only `1.0.0` native package workaround. That override has been removed because `2.0.12` already has a positive major component and can be used directly by all configured native package targets.

Do not create `v2.0.12` solely because these fields are configured. Exact-head automation, artifact review, physical/host testing, accessibility review, and protected signing/trust gates remain required.

## Product implementation status

### Shared Kotlin Multiplatform core

Completed:

- adult metric BMI calculation;
- adult imperial BMI calculation;
- adult waist-to-height calculation;
- metric/imperial conversions;
- strict finite-number validation;
- plausible adult measurement bounds;
- versioned adult BMI reference profile;
- evidence publisher/title/URL/note metadata;
- evidence review-date metadata;
- neutral educational notices;
- deterministic unit and boundary tests;
- seeded deterministic property-style tests;
- Android target;
- JVM/Desktop target;
- iOS device target;
- iOS simulator target.

The shared module remains the authoritative calculation/validation layer. Android and desktop presentation code must not duplicate or silently change shared adult-reference thresholds.

### Imperial validation corrections

Completed:

- imperial weight range errors report pounds rather than kilogram limits;
- imperial waist range errors report inches rather than centimeter limits;
- imperial BMI no longer validates in imperial units and then revalidates converted values against slightly different metric boundaries;
- documented imperial BMI boundary values remain valid;
- imperial waist-to-height keeps the imperial validation contract rather than reusing metric validation after conversion;
- regression coverage locks unit-specific messages and documented imperial boundaries.

## Android client status

### Adult-use safety boundary

Completed:

- first-run adult-use notice;
- explicit `I am 18 or older` action;
- explicit `I am under 18` action;
- under-18 path does not expose adult BMI/waist reference results;
- adult-use confirmation remains device-local;
- portable backup cannot set adult-use confirmation or onboarding completion;
- neutral educational wording avoids diagnosis claims, appearance rankings, and personal body targets.

An accidental under-18 selection is recoverable through **Return to age selection** without clearing unrelated local history consent, retention, theme, or saved history. The adult calculators remain blocked until the adult option is explicitly selected again.

### Privacy-first local history

History is optional and disabled by default.

When enabled, persisted history contains only:

- local identifier;
- timestamp;
- calculator type;
- calculated value;
- short neutral summary.

Raw weight, height, and waist inputs are not persisted in calculation history.

Supported retention:

- 50;
- 100;
- 250;
- 500.

Default retention:

`100`

Hard maximum:

`500`

Lowering retention immediately trims older records beyond the selected maximum.

History is canonical newest-first:

1. sanitize accepted entries;
2. deduplicate IDs;
3. sort by `timestampEpochMillis` descending;
4. apply retention.

The same rule applies to new calculations, backup restore, and delete/Undo restoration. This fixes the earlier defect where undoing an older deleted record could move it to the top and prevents arbitrary JSON array order from deciding which records survive retention.

New locally recorded entries use `UUID.randomUUID().toString()`.

Imported schema-v1 IDs remain backward compatible but are trimmed, required to be non-blank, capped to 96 characters, and deduplicated.

Per-entry deletion plus snackbar Undo is implemented without enabling future history saving. Confirmed erase-all remains separate.

### Backup/export/restore

Android Settings provides:

- `Save JSON backup to a file`;
- `Share JSON backup`;
- `Restore from JSON backup`.

File save uses Android Storage Access Framework. Share uses an explicit chooser. Restore reads a selected file and requires confirmation before DataStore mutation.

Current portable schema version:

`1`

Portable top-level fields:

- `schemaVersion`;
- `historyRetentionLimit`;
- `themeMode`;
- `history`.

Deliberately non-portable:

- history-saving consent;
- adult-use confirmation;
- onboarding completion.

Maximum backup size:

`1,048,576 bytes` (1 MiB)

Implemented restore protections:

- bounded stream read/write;
- strict well-formed UTF-8 decoding;
- malformed/unmappable UTF-8 rejection before JSON parsing;
- independent DataStore-boundary UTF-8 byte-size check;
- supported schema validation;
- required top-level `history` JSON array before mutation;
- explicit `history: []` accepted as intentional empty history;
- non-empty arrays rejected when zero valid records survive sanitation;
- valid neighboring records can survive malformed records;
- retention/theme normalization;
- ID/timestamp/type/value/summary sanitation;
- duplicate-ID removal;
- newest-first normalization before retention;
- device-local consent/adult-gate preservation.

The final UTF-8 audit changed `BackupIo.readUtf8` to a decoder configured with `CodingErrorAction.REPORT` for malformed and unmappable input. Regression coverage includes deliberately malformed bytes that must fail rather than being replacement-decoded.

Authoritative contract:

`docs/backup-format.md`

### Android navigation/state/chart hardening

Completed:

- explicit About top-bar Back action;
- system Back handling from About;
- previous-destination restoration;
- stable navigation/UI automation tags;
- About-origin return instrumentation tests;
- `savedEnumValueOrDefault` fallback for stale saved navigation/history-filter enum names;
- JVM regression tests for known/stale/empty saved enum values;
- finite-safe `ChartScale.normalize` for extreme imported finite values;
- chart tests including `-Double.MAX_VALUE`, zero, and `Double.MAX_VALUE`.

### Android release evidence

`ReleaseScreenshotCaptureTest` creates the required fictional/example-data PNG set:

1. `01-onboarding.png`
2. `02-bmi-metric.png`
3. `03-bmi-result.png`
4. `04-waist-ratio.png`
5. `05-history.png`
6. `06-settings.png`
7. `07-about.png`
8. `08-dark-theme.png`

The Android instrumentation workflow runs connected tests on the configured emulator, pulls the app-generated screenshot directory, publishes `android-release-screenshots`, and publishes instrumentation reports.

Automated screenshot creation does not replace human visual/privacy approval.

## Desktop client status

Completed Compose Multiplatform desktop journeys:

- adult-use gate;
- under-18 unavailable path;
- metric/imperial BMI;
- metric/imperial waist-to-height;
- dot/comma decimal input;
- strict split imperial height;
- shared-domain calculations/validation;
- neutral educational results;
- light/dark session theme;
- About/evidence/project/support/funding information;
- explicit external-link actions.

Desktop intentionally has no HealthMetric persistence layer. It does not persist measurement inputs, results, adult choice, theme, or navigation state. Closing the process discards that state.

Desktop does not currently import/export Android backup files.

Desktop measurement parsing accepts ordinary decimal syntax and rejects scientific notation, explicit signs, NaN/infinity, malformed separators, and non-whole feet.

Imperial split height means whole feet plus **remaining inches**. Remaining inches must be in `[0, 12)`. Inputs such as 12 or 20 remaining inches are rejected rather than silently normalized into additional feet.

Packaging configured and verified by workflow:

- current-OS runnable JAR;
- Linux DEB;
- Windows MSI;
- macOS DMG.

Current desktop project/native package metadata is uniformly `2.0.12`.

Native build success does not imply production signing/notarization or human host-platform acceptance.

## Apple shared core status

Configured:

- `iosArm64`;
- `iosSimulatorArm64`.

The Apple shared-core workflow compiles both on macOS and verifies shared JVM tests.

No iOS user interface is claimed.

## Release integrity tooling

### Version validator

`scripts/check_release_version.py` now validates:

- stable `vMAJOR.MINOR.PATCH` form;
- Android `versionName`;
- Android `versionCode`;
- semantic → Android version-code mapping;
- desktop project `version`;
- desktop native `packageVersion`;
- agreement of all configured release metadata with the proposed tag.

For the current candidate, regression tests explicitly lock:

- Android `2.0.12`;
- Android `20012`;
- desktop project `2.0.12`;
- desktop package `2.0.12`;
- valid stable tag `v2.0.12`.

Tests also reject minor or patch components greater than 99 under the current version-code mapping.

### Release workflow

The tagged workflow:

- checks out full history during preflight;
- runs repository/docs audits;
- runs Python release-tooling tests;
- validates release version metadata/tag;
- requires the tag commit to equal current `main`;
- defaults to `contents: read`;
- grants `contents: write` only to final publication;
- builds Android unsigned APK/AAB;
- builds desktop JAR + native package on Linux/Windows/macOS;
- uses deterministic cross-platform staging;
- verifies the exact final binary set;
- generates `SHA256SUMS.txt`;
- publishes with `gh release create --verify-tag`.

For `v2.0.12`, expected binary names are:

- `healthmetric-v2.0.12-android-unsigned.apk`;
- `healthmetric-v2.0.12-android-unsigned.aab`;
- `healthmetric-v2.0.12-desktop-linux.jar`;
- `healthmetric-v2.0.12-desktop-linux.deb`;
- `healthmetric-v2.0.12-desktop-windows.jar`;
- `healthmetric-v2.0.12-desktop-windows.msi`;
- `healthmetric-v2.0.12-desktop-macos.jar`;
- `healthmetric-v2.0.12-desktop-macos.dmg`.

Missing, extra, duplicate, ambiguous, or empty expected artifacts fail closed.

## Repository invariants and documentation integrity

`scripts/check_repository.py` verifies durable repository requirements including:

- required repository/community/documentation paths;
- Android no-Internet/backup-disabled/cleartext-disabled manifest posture;
- Android `versionName = 2.0.12`;
- Android `versionCode = 20012`;
- desktop project `version = 2.0.12`;
- desktop native `packageVersion = 2.0.12`;
- removal of the obsolete desktop macOS `1.0.0` package override;
- strict UTF-8 `CodingErrorAction.REPORT` decoder behavior in `BackupIo`;
- top-level Android backup-history structural guard;
- adult-use correction/saved-state/chart safety invariants;
- desktop module/shared dependency/native package configuration;
- Android AAB build/staging verification;
- Android screenshot evidence configuration/names;
- desktop JAR/DEB/MSI/DMG workflow coverage;
- release tag/version/main/staging/checksum/permission requirements;
- required version-validator patterns for Android versionCode and desktop packageVersion;
- explicit `2.0.12` release-version regression coverage;
- no accidental `docs/.noop-probe`;
- exhaustive documentation for every `git ls-files` tracked path.

`docs/repository-file-reference.md` documents every tracked source, test, resource, workflow, script, configuration/build file, ADR, document, logo, and screenshot-policy asset by exact path/responsibility.

`docs/documentation-map.md` identifies canonical documentation ownership and the change-type → document-update matrix.

Internal relative Markdown links are machine-checked by `scripts/check_markdown_links.py`.

## 2.0.12 documentation reconciliation

The active release target and current hardening behavior are aligned across:

- `README.md`;
- `CHANGELOG.md`;
- `ROADMAP.md`;
- `PRIVACY.md`;
- `SECURITY.md`;
- `docs/backup-format.md`;
- `docs/desktop.md`;
- `docs/setup.md`;
- `docs/testing.md`;
- `docs/release.md`;
- `docs/troubleshooting.md`;
- `.github/RELEASE_TEMPLATE.md`;
- PR #14 title/body;
- this `what_changed.md`.

The setup/troubleshooting docs now use `python3 scripts/check_release_version.py v2.0.12` rather than the superseded `v0.1.0` example.

Privacy/security documentation explicitly records malformed UTF-8 rejection before JSON restore parsing.

## Verification commands

Current release-version gate:

```bash
python3 scripts/check_release_version.py v2.0.12
```

Repository/docs/tooling checks:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
python3 -m unittest discover -s scripts/tests -p "test_*.py"
```

Complete Unix-like non-device suite:

```bash
bash scripts/verify.sh
```

Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

Major Gradle checks:

```bash
gradle :shared:ktlintCheck :androidApp:ktlintCheck :desktopApp:ktlintCheck
gradle :shared:desktopTest
gradle :desktopApp:test
gradle :desktopApp:packageUberJarForCurrentOS
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintRelease
gradle :androidApp:assembleDebug
gradle :androidApp:assembleRelease
gradle :androidApp:bundleRelease
```

Android connected suite:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

Apple targets on macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

Native desktop packaging:

```bash
# Linux
gradle :desktopApp:packageDeb

# Windows
gradle :desktopApp:packageMsi

# macOS
gradle :desktopApp:packageDmg
```

## Focused final-pass commits

The repository uses meaningful granular commits rather than empty/churn commits.

Recent final hardening/version commits include:

- `fix: reject malformed UTF-8 backup input`;
- `test: cover malformed UTF-8 backup rejection`;
- `fix: use byte literals in malformed backup test`;
- `docs: document strict UTF-8 backup decoding`;
- `docs: record strict backup encoding rejection`;
- `docs: record final strict backup encoding audit`;
- `release(android): set version 2.0.12`;
- `release(desktop): set version 2.0.12`;
- `release: validate Android semantic version code mapping`;
- `test(release): lock version 2.0.12 code mapping`;
- `release: validate desktop native package version`;
- `test(release): verify all 2.0.12 platform versions`;
- `docs: target HealthMetric 2.0.12 in roadmap`;
- `docs: prepare changelog for version 2.0.12`;
- `docs: prepare release guide for 2.0.12`;
- `docs: align release template with 2.0.12`;
- `docs: align desktop packaging with 2.0.12`;
- `docs: test version 2.0.12 release consistency`;
- `docs: publish 2.0.12 release target in readme`;
- `docs: finalize HealthMetric 2.0.12 handoff`;
- `docs: add 2.0.12 setup verification`;
- `docs: troubleshoot 2.0.12 release validation`;
- `docs: align privacy with strict UTF-8 restore`;
- `docs: align security gates with 2.0.12`;
- `ci: enforce 2.0.12 release metadata invariants`;
- this final handoff commit.

Git history is authoritative for exact commit SHAs.

Requested project commit email:

`sanskarin@outlook.in`

## What remains before merging PR #14

Implementation/repository work is intended to be complete subject to **exact-head automation**.

The exact final PR #14 head must pass:

- CI;
- Android instrumentation;
- Desktop;
- Apple shared core;
- CodeQL;
- Dependency Review;
- Secret Scan.

Also verify exact-head artifacts:

- `android-release-screenshots` contains all eight required PNGs;
- Linux Desktop artifact contains JAR + DEB;
- Windows Desktop artifact contains JAR + MSI;
- macOS Desktop artifact contains JAR + DMG.

A queued/pending workflow is neither a pass nor a source failure. If a workflow actually fails, inspect its exact job/log and make only the smallest root-cause fix with regression/invariant coverage where appropriate.

Do not use successful results from a superseded SHA as evidence for the final head.

## Manual/external gates before public v2.0.12

These remain intentionally open because source-control work cannot truthfully complete them.

### Android device acceptance

Required:

- physical Android release-candidate smoke test;
- adult gate/correction path;
- BMI and ratio journeys;
- history disabled/enabled behavior;
- retention changes;
- delete/Undo and erase-all;
- file/share backup;
- valid restore;
- malformed UTF-8/unsupported/oversized/structurally invalid/all-invalid restore rejection;
- delete-all-data;
- theme/update/About behavior.

### Android accessibility/visual acceptance

Required:

- TalkBack;
- maximum font/display scaling;
- keyboard/DPAD where applicable;
- light/dark/dynamic-theme review;
- chart accessibility;
- destructive-dialog accessibility;
- human visual/privacy approval of exact CI screenshots.

### Desktop target-host acceptance

Required on each published platform:

- runnable JAR launch;
- native installer install/launch/uninstall;
- adult gate;
- metric/imperial BMI and ratio;
- split remaining-inch rejection;
- process-restart ephemerality;
- keyboard/focus behavior;
- display scaling;
- screen-reader naming where available;
- external links;
- platform signing/notarization warnings/behavior.

### Protected signing/trust

Still external to Git/source control:

- Android production signing / Play App Signing;
- Android signing passwords/keys;
- desktop signing certificates/private keys if signed installers are distributed;
- macOS notarization credentials if notarized DMGs are distributed.

No signing secret/private key/password/notarization credential belongs in the repository.

## Exact merge/release sequence

1. Fetch PR #14 after this commit and record its exact head SHA.
2. Inspect workflow runs only for that SHA.
3. Require CI, Android instrumentation, Desktop, Apple shared core, CodeQL, Dependency Review, and Secret Scan to succeed.
4. Inspect exact-head Android screenshot and desktop artifacts.
5. Fix only concrete exact-head failures.
6. Merge PR #14 into `main` with normal history-preserving merge behavior only after required exact-head automation is green.
7. Inspect post-merge `main` automation.
8. Compare PR #13 against merged `main`; close it as superseded only when no meaningful unique change remains.
9. Complete the physical Android, accessibility/visual, desktop host, and protected signing/trust gates.
10. On the exact release commit, run/confirm:

```bash
python3 scripts/check_release_version.py v2.0.12
```

11. Confirm `README.md`, `CHANGELOG.md`, `ROADMAP.md`, `PRIVACY.md`, `SECURITY.md`, `docs/release.md`, and this handoff match the exact release commit.
12. Only then create the annotated tag:

```bash
git tag -a v2.0.12 -m "HealthMetric v2.0.12"
git push origin v2.0.12
```

13. Let the tagged workflow independently run preflight, build/stage all platform artifacts, verify the exact eight binaries, write checksums, verify the tag, and create the GitHub Release.
14. Never rewrite a published tag to hide a defect; fix `main` and publish a new patch release.

## Final continuation rule

This commit is intended to be the final implementation/documentation head for the `2.0.12` preparation pass.

Do not add commits merely to increase commit count. Move the branch again only when:

- exact-head automation reports a concrete failure;
- a real correctness/security/privacy/accessibility/release-integrity defect is found; or
- a new explicit functional/version requirement is requested.

Every new commit supersedes older exact-head workflow evidence and requires verification on the new SHA.
