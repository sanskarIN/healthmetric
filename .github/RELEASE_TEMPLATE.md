# HealthMetric {{VERSION}}

## Highlights

Describe the user-visible improvements in this release, grouped by Android, desktop, shared-core, repository/release tooling, and documentation changes where relevant.

Current prepared release target: **2.0.12** (`v2.0.12`). Replace this statement when preparing a later release.

## Privacy and data behavior

- Note any changes to Android local storage, history, export/restore, permissions, or external links.
- Confirm whether the Android backup schema changed.
- Confirm malformed UTF-8 backup documents are still rejected before JSON restore parsing.
- Confirm the schema-v1 distinction between explicit `history: []` and a non-empty all-invalid history array remains accurate when applicable.
- Confirm whether the desktop ephemeral-data decision in ADR 0005 changed.
- Never include real user measurements or other private information in release notes, screenshots, or test evidence.

## Adult reference and educational content

- Note any changes to BMI/waist reference metadata or explanatory language.
- Confirm adult-only, educational, non-diagnostic behavior remains clear on Android and desktop.
- Record the reviewed evidence date when the shared reference profile changes.

## Accessibility

Describe accessibility improvements and manual verification performed on each platform being published. Automated UI tests do not replace TalkBack, screen-reader, large-scaling, keyboard/focus, or physical/host review where required.

## Documentation integrity

- [ ] `docs/documentation-map.md` still identifies the canonical owner for each affected contract.
- [ ] `docs/repository-file-reference.md` documents every exact path returned by `git ls-files`.
- [ ] Every file added/deleted/renamed in the candidate was reconciled in the exhaustive file reference.
- [ ] README, privacy, architecture, backup, desktop, testing, release, changelog, roadmap, and handoff documents agree with the exact candidate where applicable.
- [ ] `CHANGELOG.md`, `ROADMAP.md`, `docs/release.md`, and `what_changed.md` identify the same release target.
- [ ] Local Markdown-link verification passed.
- [ ] Manual/external release gates are not marked complete merely because automated builds succeeded.

## Version consistency

For the prepared `2.0.12` candidate:

- [ ] Android `versionName` is `2.0.12`.
- [ ] Android `versionCode` is `20012`.
- [ ] Desktop project `version` is `2.0.12`.
- [ ] Desktop native `packageVersion` is `2.0.12`.
- [ ] Proposed tag is `v2.0.12`.
- [ ] `python3 scripts/check_release_version.py v2.0.12` passed on the exact candidate.

The repository maps Android `versionCode` as `MAJOR * 10000 + MINOR * 100 + PATCH` and reserves two digits each for `MINOR` and `PATCH`.

## Verification

- [ ] Repository invariant, Python repository/release-tooling tests, and internal Markdown-link checks passed for the exact release commit.
- [ ] Shared, Android, and desktop formatting/tests passed.
- [ ] Android release lint passed.
- [ ] Android debug APK, unsigned release APK, and unsigned release AAB builds succeeded.
- [ ] Connected Android UI/persistence tests passed on the release-candidate emulator/device.
- [ ] Android backup regression coverage includes malformed UTF-8 rejection, required top-level history structure, and fail-closed non-empty all-invalid history behavior.
- [ ] `android-release-screenshots` contains all eight required PNGs and received human visual/privacy review.
- [ ] Apple shared-core device/simulator compilation passed on macOS.
- [ ] Desktop runnable JARs built on Linux, Windows, and macOS.
- [ ] Linux DEB, Windows MSI, and macOS DMG builds succeeded on matching hosts.
- [ ] Desktop split imperial height rejects remaining-inch values outside `[0, 12)` on release-candidate builds.
- [ ] Published desktop native installers were smoke-tested on their target host operating systems.
- [ ] CodeQL, Dependency Review where applicable, and Secret Scan were reviewed and passed for the exact release candidate.
- [ ] Stable release tag matches Android `versionName`, Android `versionCode` mapping, desktop project version, and desktop native `packageVersion`.
- [ ] Release tag targets the current `main` commit.
- [ ] Final downloaded release asset set contains exactly the expected eight non-empty binaries.
- [ ] Published `SHA256SUMS.txt` matches the eight binary release assets.
- [ ] Android primary flows were manually verified on physical hardware before public release.
- [ ] Android TalkBack/large-font/display and applicable keyboard/DPAD checks were completed.
- [ ] Desktop keyboard/focus/display-scaling/screen-reader checks were completed on published platforms.
- [ ] Android production signing is configured only through a protected environment.
- [ ] Desktop signing/notarization status is accurately documented; credentials are not committed.

## Release assets

Expected automated asset set when all configured platforms are published:

- Android unsigned APK;
- Android unsigned AAB;
- Linux runnable JAR and DEB;
- Windows runnable JAR and MSI;
- macOS runnable JAR and DMG;
- `SHA256SUMS.txt` generated from the eight binary assets after exact-set verification.

For `v2.0.12`, the deterministic binary names are:

- `healthmetric-v2.0.12-android-unsigned.apk`;
- `healthmetric-v2.0.12-android-unsigned.aab`;
- `healthmetric-v2.0.12-desktop-linux.jar`;
- `healthmetric-v2.0.12-desktop-linux.deb`;
- `healthmetric-v2.0.12-desktop-windows.jar`;
- `healthmetric-v2.0.12-desktop-windows.msi`;
- `healthmetric-v2.0.12-desktop-macos.jar`;
- `healthmetric-v2.0.12-desktop-macos.dmg`.

The release workflow fails closed on missing, extra, or empty binary assets. Do not describe unsigned/unnotarized artifacts as store-signed or platform-trusted builds.

## Known limitations

List only current, concrete limitations. Do not mark manual/device/signing work complete unless it was actually performed for this exact release candidate.

For the `2.0.12` release candidate, explicitly call out any still-open physical Android, accessibility, screenshot approval, target-host smoke-test, signing, or notarization requirement.

## Support

- Support: supportramsandesh@gmail.com
- Business: sanskarin@outlook.in
- Business: sanskarin.business@gmail.com
- GitHub: https://github.com/sanskarIN
- Buy Me a Coffee: https://buymeacoffee.com/sanskarIN

[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-sanskarIN-FFDD00?logo=buy-me-a-coffee&logoColor=000000)](https://buymeacoffee.com/sanskarIN)

**Made by the Sanskar**
