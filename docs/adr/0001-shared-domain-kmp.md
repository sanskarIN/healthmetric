# ADR 0001: Keep calculator rules in a Kotlin Multiplatform shared domain module

- Status: Accepted
- Date: 2026-08-19

## Context

HealthMetric is Android-first but should keep core calculation behavior portable to desktop and future iOS clients. Calculation/reference rules should not be duplicated across UIs.

## Decision

Place unit conversion, validation, BMI calculation, waist-to-height calculation, and evidence/reference models in the `shared` Kotlin Multiplatform module.

The Android app depends on `shared`; `shared` must not depend on Android UI or persistence APIs.

## Consequences

### Positive

- One source of truth for calculations.
- JVM tests run without Android UI infrastructure.
- Future platform clients can reuse the same validated logic.
- Reference-boundary changes can be versioned/tested centrally.

### Trade-offs

- Multiplatform Gradle configuration is more complex than a single Android module.
- Platform-specific APIs require adapters outside `commonMain`.

## Rejected alternatives

### Put all code in the Android app

Simpler initially, but creates future duplication and couples deterministic health calculation rules to one platform.

### Create a remote calculation service

Rejected because the calculations are simple, deterministic, and should work offline without transmitting measurements.
