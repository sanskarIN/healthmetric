# Contributing to HealthMetric

Thank you for helping improve HealthMetric. Contributions should preserve the project's privacy-first, adult-only, educational, non-diagnostic approach.

## Development principles

- Keep calculation rules in the shared domain module when they are platform-neutral.
- Keep Android presentation and persistence concerns in `androidApp`.
- Do not add appearance-shaming language, rankings, pressure-oriented goals, forced sign-in, advertising trackers, or unnecessary network dependencies.
- Never commit real personal health data. Use clearly fictional/example measurements in tests, screenshots, and issues.
- Prefer small, atomic changes with tests and documentation.

## Local setup

See [`docs/setup.md`](docs/setup.md). The baseline toolchain is JDK 17, Gradle 8.13, Android SDK Platform 36, and Build Tools 35.0.0.

## Quality checks

Run the smallest relevant checks while working. Before opening a pull request, run:

```bash
gradle :shared:ktlintCheck :androidApp:ktlintCheck
gradle :shared:desktopTest
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintDebug
gradle :androidApp:assembleDebug
```

For UI changes, run instrumentation tests on a device/emulator where available:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

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
- Explain user-visible behavior changes.
- Add regression tests for bug fixes.
- Update docs when setup, architecture, privacy, behavior, or release steps change.
- Include UI evidence for visual changes using fictional/example data only.
- Resolve lint, test, build, and security failures before merge.

## Calculation and evidence changes

Changes to adult reference ranges or explanatory health text require:

1. A documented source in code or docs.
2. A clear versioned reference profile when thresholds change.
3. Tests for every boundary.
4. Neutral wording that does not turn a population screening measure into a diagnosis or appearance goal.

## Security and privacy

Read [`SECURITY.md`](SECURITY.md) and [`PRIVACY.md`](PRIVACY.md). Report vulnerabilities privately instead of creating public exploit details.

## Conduct

Participation is governed by [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md).
