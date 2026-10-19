# ADR 0004 — Bounded, user-controlled local data

- Status: Accepted
- Date: 2026-08-19

## Context

HealthMetric can operate without persisted measurement history. When an adult user explicitly enables history, local storage and exported backups contain health-related calculated values. The product needs useful continuity without silently accumulating an unbounded local record or accepting arbitrarily large imported files.

Portable backup data must also not transfer device-local consent or adult-use gate decisions. Importing another person's backup must never enable future history saving or enable adult reference calculators.

History ordering is part of the data contract. Delete/undo and backup import must not make an older record appear newest merely because it was reinserted later or appeared earlier in a JSON array. Retention must preserve the chronologically newest valid records, not the first records encountered in serialized input.

## Decision

HealthMetric will apply bounded, explicit data controls:

1. History remains disabled by default.
2. Users can choose a retention maximum of 50, 100, 250, or 500 results; the default maximum is 100.
3. History is normalized newest-first by `timestampEpochMillis` before a retention cap is applied.
4. Lowering the retention limit immediately removes older entries beyond the new limit.
5. New locally recorded history entries use UUID identifiers; imported/programmatic identifiers remain bounded, validated, and deduplicated.
6. Individual history entries can be deleted and immediately restored with an explicit undo action; undo restores chronological position rather than forcing the entry to the top.
7. Full-history and full-local-data deletion remain separately available destructive actions.
8. JSON backup reads and writes are limited to 1 MiB.
9. Restore accepts only the supported top-level schema and validates history records independently.
10. Invalid individual records are skipped; duplicate record identifiers are deduplicated before chronological sorting and retention.
11. Restoring an individual entry for an undo operation does not enable future history saving.
12. Raw weight, height, and waist inputs are not added to persisted history.
13. Portable backups do not contain the current history opt-in state, adult-use confirmation, or onboarding-completion state.
14. Restore preserves those three device-local consent/safety values even when a legacy schema-v1 backup contains fields with those names.
15. Restore requires an explicit confirmation after file selection before portable local history/settings are replaced.

## Rationale

A bounded model reduces accidental retention and makes storage, rendering, and backup behavior predictable. Explicit retention levels are understandable without introducing complicated date-based policies. Independent record validation makes a partially damaged backup recoverable while preventing malformed records from reaching Compose list keys or application state.

Canonical timestamp-descending ordering makes history behavior independent of insertion/serialization order. Applying retention after sorting preserves the newest valid records even when a backup's JSON array is out of order. The 1 MiB payload bound keeps full validation/deduplication/sorting bounded without prematurely stopping after the first 500 valid input records.

UUIDs remove the avoidable collision risk of a timestamp plus a small random suffix for newly recorded entries. Imported IDs remain schema-compatible rather than requiring historical backups to be rewritten as UUIDs.

A hard byte cap protects the JSON parser and document flows from unexpectedly large inputs. Rechecking the limit inside the persistence boundary prevents callers from bypassing the stream helper.

Consent to save future history and confirmation that adult-only reference tools are appropriate are decisions about the current installation/user context, not transferable data. Keeping them out of backups prevents restore from silently changing those decisions.

## Consequences

Positive:

- predictable upper bounds for local history and backups;
- deterministic newest-first history across add/import/delete-undo flows;
- retention preserves chronologically newest valid data rather than serialization order;
- collision-resistant IDs for newly recorded local history;
- stronger privacy defaults and clearer user control;
- safer restore behavior for malformed files;
- backup import cannot change the adult-use safety gate;
- backup import cannot silently enable future history collection;
- deterministic tests for retention, chronology, consent, and backup boundaries;
- no backend or account required.

Trade-offs:

- older history is irreversibly removed when a lower retention limit is applied;
- only schema version 1 is currently supported;
- valid bounded backups may require sorting the complete accepted record set before applying the retention cap;
- the 1 MiB cap must be revisited deliberately if future schema growth requires it;
- a restored installation may require the user to explicitly enable history again;
- exported copies outside HealthMetric are controlled by the destination selected by the user.

## Verification

Regression coverage belongs in:

- `AppPreferencesTest` for retention policy defaults;
- `BackupIoTest` for payload limits and UTF-8 IO;
- `HealthMetricDataStoreTest` for opt-in, retention, chronological normalization, portable-field boundaries, adult-gate preservation, export/restore, malformed records, duplicate IDs, and delete/restore behavior;
- Compose instrumentation tests for settings, restore confirmation, history controls, and release-critical navigation/evidence journeys.
