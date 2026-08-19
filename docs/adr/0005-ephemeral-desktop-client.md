# ADR 0005: Keep the desktop client ephemeral and shared-core driven

- Status: Accepted
- Date: 2026-08-19

## Context

HealthMetric now has a Compose Multiplatform JVM desktop client in addition to the Android application and Kotlin Multiplatform shared calculation core.

Android already provides explicit, opt-in local history and bounded backup/restore features. Re-implementing that persistence stack immediately on desktop would add a second storage format, migration surface, consent model, and security boundary before there is a demonstrated desktop requirement for persistent measurement history.

The desktop client still needs to provide clear user value rather than merely proving that the shared JVM target compiles.

## Decision

The desktop client will:

- provide a real graphical UI for the existing adult BMI and waist-to-height tools;
- require an explicit adult-use choice before adult reference calculators are shown;
- keep the under-18 path separate from adult reference results;
- call the existing `shared` calculators and validators rather than duplicating thresholds or formulas;
- keep calculator inputs, calculator results, adult-use choice, theme choice, and navigation state in memory only;
- discard that state when the desktop process closes;
- open external project/evidence/funding URLs only after explicit user actions;
- provide desktop-specific input parsing and presentation tests;
- verify desktop compilation/tests/packaging on Linux, Windows, and macOS in CI.

The desktop client will not initially:

- persist measurement history;
- import Android backup files;
- export a desktop backup format;
- silently synchronize data with another device;
- introduce an account or backend requirement;
- duplicate adult reference thresholds in desktop UI code.

## Consequences

### Positive

- Desktop becomes a genuine user-facing platform instead of only a shared-core compilation target.
- The shared calculation core remains the single source of truth for formulas, validation, evidence metadata, and adult reference wording.
- Desktop privacy behavior is easy to understand: measurement state is transient.
- No new database, encryption, migration, or backup compatibility surface is introduced.
- Desktop CI can validate three major desktop operating-system families independently.

### Trade-offs

- Desktop users do not receive Android's history/backup features yet.
- Theme and adult-use choices reset at each launch.
- Native installer publication still requires platform-specific manual release verification.

## Future change criteria

Desktop persistence should be considered only if a concrete product requirement justifies it. Any future persistence proposal must define:

- the exact fields stored;
- opt-in/default behavior;
- retention/deletion behavior;
- portability rules;
- migration/versioning strategy;
- threat model and storage protection;
- test coverage;
- privacy-documentation changes.

If desktop backup compatibility with Android is added, it must reuse or deliberately version the documented portable schema rather than creating an undocumented parallel format.

## Related documents

- [`../architecture.md`](../architecture.md)
- [`../desktop.md`](../desktop.md)
- [`../backup-format.md`](../backup-format.md)
- [`../../PRIVACY.md`](../../PRIVACY.md)
- [`0002-local-privacy-first-persistence.md`](0002-local-privacy-first-persistence.md)
- [`0004-bounded-user-controlled-local-data.md`](0004-bounded-user-controlled-local-data.md)
