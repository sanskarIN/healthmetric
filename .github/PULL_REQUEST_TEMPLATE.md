## Summary

Describe the focused change, why it is needed, and affected platform(s): shared core, Android, desktop, Apple-target build configuration, documentation, or release infrastructure.

## Verification

- [ ] Relevant unit/integration/UI tests added or updated.
- [ ] Repository invariant and Markdown-link checks pass when relevant.
- [ ] Repository/release Python tooling tests pass when relevant.
- [ ] Shared/Android/desktop `ktlintCheck` passes for affected modules.
- [ ] Android lint passes for affected Android code.
- [ ] Relevant debug/release/package task succeeds.
- [ ] Connected Android tests were run when Android persistence/UI behavior changed.
- [ ] Desktop tests/current-OS packaging were run when desktop behavior changed.
- [ ] Apple shared targets were considered when shared-domain/build configuration changed.
- [ ] Documentation is updated where behavior/setup/privacy/release steps changed.

## Documentation completeness

- [ ] I reviewed [`docs/documentation-map.md`](../docs/documentation-map.md) for the canonical documents affected by this change.
- [ ] Every added/deleted/renamed tracked file is reconciled in [`docs/repository-file-reference.md`](../docs/repository-file-reference.md).
- [ ] New file-reference entries explain responsibility/maintenance boundaries rather than only repeating the filename.
- [ ] `python3 scripts/check_repository.py` confirms every `git ls-files` path is represented in the exhaustive file reference.
- [ ] Architecture/evidence/privacy/backup/testing/release documentation was updated when its owned contract changed.
- [ ] ADRs were added or superseded when a durable architecture/privacy/persistence/evidence boundary changed.

## Privacy, safety, and accessibility

- [ ] No secrets, credentials, private health information, production-only endpoints, or signing material are committed.
- [ ] Adult health references remain educational, source-traceable, and non-diagnostic.
- [ ] No appearance-shaming, body-ranking, or pressure-oriented language was introduced.
- [ ] Under-18 paths remain isolated from adult reference calculator results.
- [ ] Android adult-only reference access cannot be enabled by imported data or another user's state.
- [ ] Android history remains opt-in and imported data cannot silently enable future history saving.
- [ ] New Android stored/imported data is necessary, bounded, validated, and documented.
- [ ] Android backup-format changes preserve payload size, top-level structure, record validation, intentional-empty/all-invalid distinction, and update `docs/backup-format.md`.
- [ ] Desktop calculator/adult/theme/navigation state remains non-persistent unless ADR 0005 is deliberately superseded.
- [ ] Raw measurements, backup contents, and arbitrary user text are not added to logs.
- [ ] Offline/privacy-first behavior and least-privilege permissions/behavior are preserved.
- [ ] External links or destructive/replacement actions require appropriate explicit user actions/confirmation/undo.
- [ ] Accessibility implications were reviewed, including labels, large text/display scaling, non-color meaning, keyboard/focus behavior, and screen-reader semantics where applicable.
- [ ] Numeric/input changes were reviewed for platform decimal parsing/locale behavior where applicable.
- [ ] Split imperial height inputs keep remaining inches in `[0, 12)` rather than silently normalizing an invalid component when applicable.

## Platform release impact

- [ ] Android APK/AAB behavior considered if Android/build configuration changed.
- [ ] Linux/Windows/macOS Desktop workflow behavior considered if desktop/build configuration changed.
- [ ] Release workflow asset naming/publication considered if packaging changed.
- [ ] Manual release checks are documented instead of being claimed complete when they require physical devices, assistive technology, signing, notarization, or screenshots.

## Screenshots / recordings

Add UI evidence when applicable. Use fictional/example data only.
