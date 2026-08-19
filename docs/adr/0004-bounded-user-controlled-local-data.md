# ADR 0004 — Bounded, user-controlled local data

- Status: Accepted
- Date: 2026-08-19

## Context

HealthMetric can operate without persisted measurement history. When an adult user explicitly enables history, local storage and exported backups contain health-related calculated values. The product needs useful continuity without silently accumulating an unbounded local record or accepting arbitrarily large imported files.

## Decision

HealthMetric will apply bounded, explicit data controls:

1. History remains disabled by default.
2. Users can choose a retention maximum of 50, 100, 250, or 500 results; the default maximum is 100.
3. Lowering the retention limit immediately removes older entries beyond the new limit.
4. Individual history entries can be deleted and immediately restored with an explicit undo action.
5. Full-history and full-local-data deletion remain separately available destructive actions.
6. JSON backup reads and writes are limited to 1 MiB.
7. Restore accepts only the supported top-level schema and validates history records independently.
8. Invalid individual records are skipped; duplicate record identifiers are deduplicated.
9. Restoring an individual entry for an undo operation does not enable future history saving.
10. Raw weight, height, and waist inputs are not added to persisted history.

## Rationale

A bounded model reduces accidental retention and makes storage, rendering, and backup behavior predictable. Explicit retention levels are understandable without introducing complicated date-based policies. Independent record validation makes a partially damaged backup recoverable while preventing malformed records from reaching Compose list keys or application state.

A hard byte cap protects the JSON parser and document flows from unexpectedly large inputs. Rechecking the limit inside the persistence boundary prevents callers from bypassing the stream helper.

## Consequences

Positive:

- predictable upper bounds for local history and backups;
- stronger privacy defaults and clearer user control;
- safer restore behavior for malformed files;
- deterministic tests for retention and backup boundaries;
- no backend or account required.

Trade-offs:

- older history is irreversibly removed when a lower retention limit is applied;
- only schema version 1 is currently supported;
- the 1 MiB cap must be revisited deliberately if future schema growth requires it;
- exported copies outside HealthMetric are controlled by the destination selected by the user.

## Verification

Regression coverage belongs in:

- `AppPreferencesTest` for retention policy defaults;
- `BackupIoTest` for payload limits and UTF-8 IO;
- `HealthMetricDataStoreTest` for opt-in, retention, export/restore, malformed records, duplicate IDs, and delete/restore behavior;
- Compose instrumentation tests for settings and history controls.
