# ADR 0004 — Bounded, user-controlled Android local data

- Status: Accepted
- Date: 2026-08-19

## Context

HealthMetric Android can operate without persisted measurement history. When an adult user explicitly enables history, local storage and exported backups contain health-related calculated values. The product needs useful continuity without silently accumulating an unbounded local record or accepting arbitrarily large imported files.

Portable backup data must also not transfer device-local consent or adult-use gate decisions. Importing another person's backup must never enable future history saving or enable adult reference calculators.

The desktop client uses a separate ephemeral data decision documented by ADR 0005 and does not use this Android persistence/backup model.

## Decision

HealthMetric Android will apply bounded, explicit data controls:

1. History remains disabled by default.
2. Users can choose a retention maximum of 50, 100, 250, or 500 results; the default maximum is 100.
3. History is canonical newest-first by `timestampEpochMillis` before a retention cap is applied.
4. Lowering the retention limit immediately removes older entries beyond the new limit.
5. New locally recorded entries use UUID identifiers to avoid an unnecessary timestamp/small-random-suffix collision path.
6. Imported schema-v1 identifiers remain backward-compatible: they need not be UUIDs, but are trimmed, required to be non-blank, capped to 96 characters, and deduplicated after sanitation.
7. Individual history entries can be deleted and immediately restored with an explicit undo action without changing their chronological position.
8. Full-history and full-local-data deletion remain separately available destructive actions.
9. JSON backup reads and writes are limited to 1 MiB.
10. Restore accepts only the supported top-level schema and validates history records independently.
11. Invalid individual records are skipped; duplicate normalized record identifiers retain the first valid input record.
12. The complete accepted bounded input set is sorted newest-first before the application-wide/selected retention cap is applied. Serialized array order is not authoritative chronology.
13. Restoring an individual entry for an undo operation does not enable future history saving.
14. Raw weight, height, and waist inputs are not added to persisted history.
15. Portable backups do not contain the current history opt-in state, adult-use confirmation, or onboarding-completion state.
16. Restore preserves those three device-local consent/safety values even when a legacy schema-v1 backup contains fields with those names.
17. Restore requires an explicit confirmation after file selection before portable local history/settings are replaced.

## Rationale

A bounded model reduces accidental retention and makes storage, rendering, and backup behavior predictable. Explicit retention levels are understandable without introducing complicated date-based policies. Independent record validation makes a partially damaged backup recoverable while preventing malformed records from reaching Compose list keys or application state.

Chronological normalization is part of the data contract rather than a presentation-only choice. Applying retention after sorting ensures a valid out-of-order backup cannot make older serialized records displace newer records merely because they appeared earlier in JSON. Undo uses the same invariant so restoring an older item does not incorrectly promote it to newest.

UUIDs make locally generated identifiers collision-resistant without coupling identity to time. Backward-compatible imported IDs preserve schema-v1 interoperability while sanitation and deduplication protect persistence/UI keys.

A hard byte cap protects the JSON parser and document flows from unexpectedly large inputs. Rechecking the limit inside the persistence boundary prevents callers from bypassing the stream helper.

Consent to save future history and confirmation that adult-only reference tools are appropriate are decisions about the current installation/user context, not transferable data. Keeping them out of backups prevents restore from silently changing those decisions.

## Consequences

Positive:

- predictable upper bounds for Android local history and backups;
- deterministic newest-first chronology across record, import, and undo paths;
- stronger privacy defaults and clearer user control;
- safer restore behavior for malformed files;
- lower avoidable local-ID collision risk;
- backup import cannot change the adult-use safety gate;
- backup import cannot silently enable future history collection;
- deterministic tests for retention, chronology, consent, and backup boundaries;
- no backend or account required.

Trade-offs:

- older history is irreversibly removed when a lower retention limit is applied;
- only schema version 1 is currently supported;
- the 1 MiB cap must be revisited deliberately if future schema growth requires it;
- a restored installation may require the user to explicitly enable history again;
- exported copies outside HealthMetric are controlled by the destination selected by the user;
- sorting the accepted restore set requires holding that bounded set in memory, with the 1 MiB input cap providing the primary complexity bound.

## Verification

Regression coverage belongs in:

- `AppPreferencesTest` for retention policy defaults;
- `BackupIoTest` for payload limits and UTF-8 IO;
- `HealthMetricDataStoreTest` for opt-in, retention, portable-field boundaries, adult-gate preservation, export/restore, malformed records, duplicate IDs, canonical chronology, and delete/restore behavior;
- Compose instrumentation tests for settings, restore confirmation, history controls, and delete/undo flows.

Repository/release documentation must continue to distinguish this Android persistence decision from the intentionally ephemeral desktop data model in ADR 0005.
