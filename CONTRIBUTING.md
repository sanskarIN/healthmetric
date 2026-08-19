# Contributing to HealthMetric

Thank you for helping improve HealthMetric. Contributions should preserve the project's privacy-first, adult-only, educational, non-diagnostic approach.

## Development principles

- Keep platform-neutral calculation/reference rules in `shared`.
- Keep Android presentation, locale formatting, persistence, and platform integrations in `androidApp`.
- Keep desktop window/forms/parsing/transient presentation behavior in `desktopApp`.
- Keep shared-domain changes compatible with Android, JVM/Desktop, iOS device, and iOS simulator targets.
- Desktop must use the shared calculators/validators instead of duplicating thresholds or formulas.
- Desktop measurement/results/adult/theme/navigation state remains in memory only unless ADR 0005 is deliberately superseded.
- Do not add appearance-shaming language, rankings, pressure-oriented goals, diagnostic claims, forced sign-in, advertising trackers, or unnecessary network dependencies.
- Never commit real personal health data. Use clearly fictional/example measurements in tests, screenshots, issues, and release evidence.
- Treat Android backup files as untrusted input and preserve documented size/schema/top-level-structure/record bounds.
- Keep Android history opt-in and adult-use/onboarding state device-local; portable backups must not silently change consent or adult eligibility.
- Prefer small, atomic changes with tests and documentation.

## Local setup

See [`docs/setup.md`](docs/setup.md). The baseline toolchain is JDK 17 and Gradle 8.13. Android development also requires Android SDK Platform 36 / Build Tools 35.0.0. Apple shared-target compilation requires macOS/Xcode.

Desktop architecture and commands are documented in [`docs/desktop.md`](docs/desktop.md).

For documentation ownership and file-level responsibilities, read:

- [`docs/documentation-map.md`](docs/documentation-map.md);
- [`docs/repository-file-reference.md`](docs/repository-file-reference.md).

## Quality checks

Run the complete non-device suite before opening a pull request:

```bash
bash scripts/verify.sh
```

or on Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

The verification suite includes repository/docs audits, repository/release-tooling Python regression tests, shared/Android/desktop formatting, shared and desktop tests, desktop runnable-JAR packaging, Android JVM tests/lint, debug APK, unsigned release APK, and unsigned release AAB assembly.

For targeted checks:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
python3 -m unittest discover -s scripts/tests -p "test_*.py"
gradle :shared:desktopTest
gradle :desktopApp:ktlintCheck :desktopApp:test :desktopApp:packageUberJarForCurrentOS
gradle :androidApp:testDebugUnitTest :androidApp:lintRelease
```

For Android UI/persistence changes:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

For shared-domain changes on macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

## Documentation maintenance is part of the change

HealthMetric treats documentation coverage as a repository invariant, not an optional cleanup step.

`docs/repository-file-reference.md` contains the exact path and purpose of every tracked file. `scripts/check_repository.py` reads `git ls-files` and fails when any tracked file is absent from that reference.

When a change adds, deletes, or renames a tracked file:

1. update [`docs/repository-file-reference.md`](docs/repository-file-reference.md) in the same pull request;
2. describe the file's responsibility and important safety/privacy/release boundaries rather than only listing its name;
3. consult [`docs/documentation-map.md`](docs/documentation-map.md) to identify every canonical topic document affected by the behavior;
4. update tests/invariants where the file establishes a durable contract;
5. run repository and Markdown-link audits before requesting review.

Examples:

- new Android persistence file → file reference + `PRIVACY.md`/architecture/backup docs as applicable;
- new CI workflow → file reference + testing/governance/release docs as applicable;
- new shared calculation source → file reference + architecture/evidence/testing docs and domain tests;
- new desktop persistence mechanism → file reference + a reviewed ADR that supersedes or updates the current ephemeral-client decision, plus privacy/desktop docs.

Do not satisfy the invariant with an unexplained filename dump. The reference exists to make ownership and maintenance discoverable.

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
- Explain user-visible behavior by affected platform.
- Add regression tests for bug fixes.
- Update docs when setup, architecture, privacy, backup schema, desktop behavior, workflow, release steps, or tracked-file structure changes.
- Include UI evidence for visual changes using fictional/example data only.
- Resolve applicable CI, Desktop, Android instrumentation, Apple shared-core, documentation-integrity, CodeQL, dependency-review, and secret-scan failures before merge.
- Complete the repository PR checklist rather than deleting privacy/safety items as “not applicable” without explanation.

## Calculation and evidence changes

Changes to adult reference ranges or explanatory health text require:

1. a documented authoritative source;
2. an updated source review date;
3. a clear versioned reference profile when thresholds/interpretation change;
4. tests for every boundary;
5. neutral wording that does not turn a population screening measure into a diagnosis or appearance goal;
6. updates to [`docs/evidence.md`](docs/evidence.md), `CHANGELOG.md`, and release notes.

Do not implement different reference thresholds in Android and desktop UI code.

## Android backup and persistence changes

Read [`docs/backup-format.md`](docs/backup-format.md) and ADR 0004 before changing portable data.

A change must retain or deliberately replace, with review:

- 1 MiB bounded IO;
- supported schema validation;
- required top-level `history` array validation before persistence mutation;
- an explicit empty `history: []` as the intentional empty-history representation;
- rejection of non-empty history arrays when no valid entry survives sanitation;
- bounded history retention;
- newest-first history normalization;
- per-record validation and valid-neighbor salvage;
- duplicate-ID protection;
- device-local history consent/adult-gate/onboarding state;
- explicit restore confirmation;
- regression coverage for migrations/compatibility.

## Desktop persistence changes

Read [`docs/desktop.md`](docs/desktop.md) and ADR 0005 first.

The current desktop client deliberately persists no HealthMetric measurement or preference state. Any future storage/import/sync proposal must define purpose, fields, default/consent model, retention/deletion, migration, threat model, portability, tests, and privacy/security documentation before implementation.

## Numeric/input changes

Shared arithmetic remains locale-neutral.

- Android `LocalizedNumbers` owns locale-aware display/input behavior.
- Desktop `DesktopNumbers` owns desktop text parsing.
- Desktop split imperial height uses whole feet plus a remaining-inches component in `[0, 12)`; do not silently normalize values such as 12 or 20 remaining inches into extra feet.
- Keep finite/range validation in `shared`.
- Test dot/comma behavior where presentation parsing changes.

## Desktop UI changes

Desktop changes should preserve:

- explicit adult-use choice before adult calculators;
- separate under-18 unavailable path;
- visible text labels for primary controls;
- keyboard/focus usability;
- text-based result/error meaning;
- explicit external-link actions;
- no background network/persistence behavior.

Run the current-OS desktop package locally when practical and rely on the three-OS Desktop workflow before merge.

## Security and privacy

Read [`SECURITY.md`](SECURITY.md) and [`PRIVACY.md`](PRIVACY.md). Report vulnerabilities privately instead of creating public exploit details.

## Conduct

Participation is governed by [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md).
