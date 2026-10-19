## Summary

Describe the focused change and why it is needed.

## Verification

- [ ] Relevant unit/integration/UI tests added or updated.
- [ ] `python3 scripts/check_repository.py` passes.
- [ ] `python3 scripts/check_markdown_links.py` passes.
- [ ] `ktlintCheck` passes.
- [ ] Android lint passes for affected code.
- [ ] Relevant debug/release APK and App Bundle build tasks succeed.
- [ ] Connected Android tests were run when persistence/UI behavior changed.
- [ ] Release screenshot evidence was reviewed when release-critical UI changed.
- [ ] Apple shared targets were considered when shared-domain code changed.
- [ ] Documentation is updated where behavior changed.

## Privacy, safety, and accessibility

- [ ] No secrets, credentials, private health information, or production-only endpoints are committed.
- [ ] Adult health references remain educational, source-traceable, and non-diagnostic.
- [ ] No appearance-shaming, body-ranking, or pressure-oriented language was introduced.
- [ ] Adult-only reference access cannot be enabled by imported data or another user's state.
- [ ] History remains opt-in and imported data cannot silently enable future history saving.
- [ ] New stored/imported data is necessary, bounded, validated, and documented.
- [ ] History add/import/delete-undo behavior preserves canonical newest-first ordering before retention is applied.
- [ ] New local history identifiers remain collision-resistant and imported IDs remain bounded/deduplicated.
- [ ] Backup-format changes preserve size/schema/record/order validation and update `docs/backup-format.md`.
- [ ] Raw measurements, backup contents, and arbitrary user text are not added to logs.
- [ ] Offline/privacy-first behavior and least-privilege Android permissions are preserved.
- [ ] Destructive/replacement actions use appropriate confirmation or undo.
- [ ] Secondary destinations remain escapable with explicit/system back navigation.
- [ ] Accessibility implications were reviewed, including labels, large text, non-color meaning, and focus behavior.
- [ ] Numeric/input changes were reviewed for locale behavior where applicable.

## Release packaging / evidence

- [ ] Changes do not break debug APK, unsigned release APK, or unsigned release AAB generation.
- [ ] If release screenshot requirements changed, the capture test, emulator workflow, screenshot guide, and repository invariants were updated together.
- [ ] Signing material remains outside source control.

## Screenshots / recordings

Add UI evidence when applicable. Use fictional/example data only. Prefer the CI-generated `android-release-screenshots` artifact for release-critical Android UI review.
