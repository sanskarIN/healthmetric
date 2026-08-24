#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]

REQUIRED_PATHS = [
    "README.md",
    "LICENSE",
    "CONTRIBUTING.md",
    "CODE_OF_CONDUCT.md",
    "SECURITY.md",
    "SUPPORT.md",
    "PRIVACY.md",
    "CHANGELOG.md",
    "ROADMAP.md",
    "what_changed.md",
    ".gitignore",
    ".editorconfig",
    ".gitattributes",
    ".env.example",
    "shared/build.gradle.kts",
    "shared/src/commonMain/kotlin/io/github/sanskarin/healthmetric/domain/HealthMetricEngine.kt",
    "shared/src/commonTest/kotlin/io/github/sanskarin/healthmetric/domain/HealthMetricEngineTest.kt",
    "sharedUI/build.gradle.kts",
    "sharedUI/src/commonMain/kotlin/io/github/sanskarin/healthmetric/ui/HealthMetricCrossPlatformApp.kt",
    "sharedUI/src/commonMain/kotlin/io/github/sanskarin/healthmetric/ui/MeasurementInput.kt",
    "sharedUI/src/commonTest/kotlin/io/github/sanskarin/healthmetric/ui/MeasurementInputTest.kt",
    "androidApp/build.gradle.kts",
    "desktopApp/build.gradle.kts",
    "webApp/build.gradle.kts",
    "iosApp/project.yml",
    "iosApp/HealthMetric/HealthMetricApp.swift",
    "iosApp/HealthMetric/ContentView.swift",
    "docs/architecture.md",
    "docs/backup-format.md",
    "docs/setup.md",
    "docs/development.md",
    "docs/testing.md",
    "docs/release.md",
    "docs/troubleshooting.md",
    "docs/accessibility.md",
    "docs/performance.md",
    "docs/evidence.md",
    "docs/design-system.md",
    "docs/adr/0001-shared-domain-kmp.md",
    "docs/adr/0002-local-privacy-first-persistence.md",
    "docs/adr/0003-versioned-adult-reference-profiles.md",
    "docs/adr/0004-bounded-user-controlled-local-data.md",
    "scripts/check_release_version.py",
    ".github/workflows/ci.yml",
    ".github/workflows/android-instrumentation.yml",
    ".github/workflows/apple-shared.yml",
    ".github/workflows/cross-platform.yml",
    ".github/workflows/desktop-packages.yml",
    ".github/workflows/codeql.yml",
    ".github/workflows/dependency-review.yml",
    ".github/workflows/secret-scan.yml",
    ".github/workflows/release.yml",
]

FORBIDDEN_PATHS = [
    "docs/.noop-probe",
]

ANDROID_SETUP_WORKFLOWS = [
    ".github/workflows/ci.yml",
    ".github/workflows/android-instrumentation.yml",
    ".github/workflows/apple-shared.yml",
    ".github/workflows/cross-platform.yml",
    ".github/workflows/desktop-packages.yml",
    ".github/workflows/codeql.yml",
    ".github/workflows/release.yml",
]

RELEASE_BUNDLE_PATHS = [
    ".github/workflows/ci.yml",
    ".github/workflows/release.yml",
    "scripts/verify.sh",
    "scripts/verify.ps1",
]


def read(relative: str) -> str:
    return (ROOT / relative).read_text(encoding="utf-8")


def main() -> int:
    failures: list[str] = []

    for relative in REQUIRED_PATHS:
        if not (ROOT / relative).exists():
            failures.append(f"missing required path: {relative}")

    for relative in FORBIDDEN_PATHS:
        if (ROOT / relative).exists():
            failures.append(f"temporary/probe path must not be committed: {relative}")

    manifest = read("androidApp/src/main/AndroidManifest.xml")
    if "android.permission.INTERNET" in manifest:
        failures.append("AndroidManifest.xml must not request INTERNET for the offline core")
    if 'android:allowBackup="false"' not in manifest:
        failures.append("AndroidManifest.xml must keep android:allowBackup=\"false\"")
    if 'android:usesCleartextTraffic="false"' not in manifest:
        failures.append("AndroidManifest.xml must keep cleartext traffic disabled")

    android_build = read("androidApp/build.gradle.kts")
    if "kotlinOptions" in android_build:
        failures.append("androidApp must use the typed Kotlin compilerOptions DSL")
    if 'JvmTarget.fromTarget("17")' not in android_build:
        failures.append("androidApp must keep the Kotlin JVM target aligned to Java 17")

    readme = read("README.md")
    required_readme_fragments = [
        "Made by the Sanskar",
        "https://buymeacoffee.com/sanskarIN",
        "sanskarin@outlook.in",
        "sanskarin.business@gmail.com",
        "supportramsandesh@gmail.com",
        "MIT",
        "Windows",
        "macOS",
        "Linux",
        "Web",
        "iOS / iPadOS",
    ]
    for fragment in required_readme_fragments:
        if fragment not in readme:
            failures.append(f"README.md is missing required metadata/platform text: {fragment}")

    privacy = read("PRIVACY.md")
    for phrase in [
        "disabled by default",
        "adult-use confirmation",
        "1 MiB",
    ]:
        if phrase not in privacy:
            failures.append(f"PRIVACY.md is missing required privacy invariant text: {phrase}")

    shared_engine = read(
        "shared/src/commonMain/kotlin/io/github/sanskarin/healthmetric/domain/HealthMetricEngine.kt"
    )
    if "MINIMUM_SUPPORTED_AGE_YEARS: Int = 18" not in shared_engine:
        failures.append("HealthMetricEngine must keep the explicit adult 18+ eligibility boundary")
    for route in [
        "calculateAdultMetricBmi",
        "calculateAdultImperialBmi",
        "calculateAdultMetricWaistToHeight",
        "calculateAdultImperialWaistToHeight",
    ]:
        if route not in shared_engine:
            failures.append(f"HealthMetricEngine is missing required cross-platform route: {route}")

    shared_ui = read(
        "sharedUI/src/commonMain/kotlin/io/github/sanskarin/healthmetric/ui/HealthMetricCrossPlatformApp.kt"
    )
    for fragment in [
        "UnitSystem.METRIC",
        "UnitSystem.IMPERIAL",
        "MeasurementInput.sanitizeDecimal",
    ]:
        if fragment not in shared_ui:
            failures.append(f"sharedUI is missing required parity/input behavior: {fragment}")

    ios_project = read("iosApp/project.yml")
    if ":sharedUI:embedAndSignAppleFrameworkForXcode" not in ios_project:
        failures.append("iosApp/project.yml must build the sharedUI Apple framework")

    for workflow_path in ANDROID_SETUP_WORKFLOWS:
        workflow = read(workflow_path)
        if "android-actions/setup-android@v4" not in workflow:
            failures.append(
                f"{workflow_path} must provision Android command-line tools with setup-android@v4"
            )

    cross_platform_workflow = read(".github/workflows/cross-platform.yml")
    if ":sharedUI:desktopTest" not in cross_platform_workflow:
        failures.append("cross-platform CI must execute sharedUI desktop helper tests")

    for release_path in RELEASE_BUNDLE_PATHS:
        release_content = read(release_path)
        if ":androidApp:bundleRelease" not in release_content:
            failures.append(f"{release_path} must verify the Android release App Bundle")

    ci_workflow = read(".github/workflows/ci.yml")
    if "androidApp/build/outputs/bundle/release/*.aab" not in ci_workflow:
        failures.append("CI must upload the unsigned release App Bundle artifact")
    if "python3 scripts/check_release_version.py" not in ci_workflow:
        failures.append("CI must validate release version metadata")

    release_workflow = read(".github/workflows/release.yml")
    if "androidApp/build/outputs/bundle/release/*.aab" not in release_workflow:
        failures.append("tagged release workflow must publish the unsigned App Bundle")
    if 'python3 scripts/check_release_version.py "${GITHUB_REF_NAME}"' not in release_workflow:
        failures.append("tagged release workflow must reject version/tag mismatches")

    if failures:
        print("Repository invariant audit failed:")
        for failure in failures:
            print(f"- {failure}")
        return 1

    print(f"Repository invariant audit passed ({len(REQUIRED_PATHS)} required paths checked).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
