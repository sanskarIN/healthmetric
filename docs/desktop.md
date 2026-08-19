# Desktop Client

HealthMetric includes a JVM desktop client built with Compose Multiplatform. It reuses the same Kotlin Multiplatform calculation and validation core as the Android app while keeping desktop-specific UI concerns in the `desktopApp` module.

## Scope

The desktop client provides:

- an explicit adult-use gate before adult reference calculators become available;
- metric and imperial adult BMI calculation;
- metric and imperial waist-to-height calculation;
- neutral educational result wording;
- validation messages sourced from the shared domain rules;
- light and dark theme switching for the current session;
- project, evidence, support, and funding information;
- explicit external-link actions;
- no persistent measurement history.

The desktop client does not currently implement Android's optional local-history, retention, backup, or restore features. This is intentional: desktop measurements, results, theme selection, navigation state, and the adult-use choice are held only in memory and are discarded when the application closes.

## Safety boundary

The desktop client mirrors the Android adult-use boundary:

1. On startup, the user must choose whether they are 18 or older.
2. Choosing the adult option opens the adult BMI and waist-to-height calculators.
3. Choosing the under-18 option shows an unavailable screen rather than adult reference ranges.
4. The choice is not written to disk by the desktop client.
5. Resetting the choice returns to the startup gate.

The UI avoids appearance scoring, body rankings, diagnostic claims, and personal body targets.

## Architecture

```text
desktopApp/
├── build.gradle.kts
└── src/
    ├── main/kotlin/io/github/sanskarin/healthmetric/desktop/
    │   ├── Main.kt
    │   ├── DesktopCalculations.kt
    │   └── DesktopNumbers.kt
    └── test/kotlin/io/github/sanskarin/healthmetric/desktop/
        ├── DesktopCalculationsTest.kt
        └── DesktopNumbersTest.kt
```

Responsibilities:

- `Main.kt`: Compose Desktop window, adult-use gate, navigation, calculator forms, results, theme state, evidence/about UI, and explicit external links.
- `DesktopCalculations.kt`: presentation-facing adapter around the shared `BmiCalculator` and `WaistToHeightCalculator`.
- `DesktopNumbers.kt`: tolerant dot/comma decimal parsing for desktop text inputs.
- `shared`: authoritative arithmetic, unit conversion, validation, evidence metadata, adult BMI reference profile, and educational notices.

The desktop layer must not duplicate or silently change shared calculation thresholds.

## Build requirements

- JDK 17.
- Gradle 8.13 when not using an IDE-managed Gradle installation.
- Network access for the first dependency resolution.

The repository's Android modules are still part of the same Gradle build, so an Android-capable development environment remains recommended when working on the whole repository.

## Run in development

```bash
gradle :desktopApp:run
```

## Run tests

```bash
gradle :desktopApp:ktlintCheck
gradle :desktopApp:test
```

## Build a runnable JAR

```bash
gradle :desktopApp:packageUberJarForCurrentOS
```

Generated Compose Desktop JARs are written under:

```text
desktopApp/build/compose/jars/
```

## Native distributions

The desktop module declares and CI-verifies host-specific native distribution formats:

- Debian-compatible Linux DEB;
- Windows MSI;
- macOS DMG.

Build them only on their matching host operating systems:

```bash
# Linux
gradle :desktopApp:packageDeb

# Windows
gradle :desktopApp:packageMsi

# macOS
gradle :desktopApp:packageDmg
```

Expected output directories:

```text
desktopApp/build/compose/binaries/main/deb/
desktopApp/build/compose/binaries/main/msi/
desktopApp/build/compose/binaries/main/dmg/
```

The public desktop application version remains `0.1.0` for the planned first release. Compose/JDK macOS DMG packaging requires package metadata whose major component is greater than zero, so `desktopApp/build.gradle.kts` uses a macOS-specific native package version of `1.0.0` while leaving the public project/release version unchanged. Treat that DMG package version as platform packaging metadata, not as a separate public HealthMetric release number.

The cross-platform `.github/workflows/desktop.yml` workflow runs formatting/tests, builds a current-OS runnable JAR, builds the matching native installer, and uploads both as verification artifacts on Linux, Windows, and macOS.

The tagged release workflow independently rebuilds and stages versioned JAR plus DEB/MSI/DMG assets. Native build success is not equivalent to production code signing/notarization or human platform acceptance testing.

## Privacy

The desktop client intentionally has no HealthMetric persistence layer.

It does not write:

- entered weight, height, or waist values;
- calculated BMI or ratio values;
- the adult-use choice;
- theme state;
- calculator navigation state.

External evidence, repository, and funding URLs open only when the user presses the corresponding button. The calculation core itself does not require network access.

Android privacy and portable-backup behavior remains documented separately in [`../PRIVACY.md`](../PRIVACY.md) and [`backup-format.md`](backup-format.md).

## Testing strategy

`DesktopNumbersTest` covers:

- dot-decimal parsing;
- comma-decimal parsing;
- whitespace trimming;
- malformed number rejection;
- non-finite number rejection;
- whole-number parsing for imperial feet.

`DesktopCalculationsTest` covers:

- metric BMI integration with the shared adult reference profile;
- imperial BMI conversion through the shared core;
- neutral waist-to-height presentation;
- field-specific invalid-text feedback;
- preservation of shared validation limits.

The shared module remains the primary unit-test location for mathematical correctness, conversion precision, boundary behavior, and evidence metadata.

## Accessibility expectations

Desktop controls use visible text labels rather than icon-only actions. Core actions are standard Compose Material controls that participate in keyboard focus order. Error and result meaning is conveyed with text, not color alone.

Before a tagged desktop distribution is published, manually verify:

- keyboard traversal through age gate, navigation, fields, theme switch, and actions;
- focus visibility;
- screen-reader naming on the target operating system;
- large display scaling;
- light/dark contrast;
- external-link behavior;
- that the under-18 path cannot access adult calculators.

## Platform release checks

Before promoting native installers:

- install the DEB on a supported Debian-compatible Linux environment and verify launch/uninstall;
- install the MSI on Windows and verify launch/uninstall plus expected Windows reputation/signing warnings;
- mount/install the DMG on macOS and verify launch plus expected Gatekeeper/notarization behavior;
- compare each native build's calculator results with the corresponding runnable JAR/shared tests;
- confirm closing/reopening each installed application resets all ephemeral session state;
- keep signing/notarization certificates, private keys, passwords, and credentials outside the repository.

## Release policy

Desktop artifacts are development/release-candidate outputs until manual host-platform accessibility, install/uninstall, and final release verification are complete.

Do not claim a desktop distribution as a medical device, diagnosis tool, or individualized health recommendation service.
