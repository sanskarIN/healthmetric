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
    "build.gradle.kts",
    "settings.gradle.kts",
    "gradle.properties",
    "gradle/libs.versions.toml",
    "androidApp/build.gradle.kts",
    "androidApp/proguard-rules.pro",
    "androidApp/src/main/AndroidManifest.xml",
    "androidApp/src/main/res/drawable/ic_healthmetric.xml",
    "androidApp/src/main/res/drawable/ic_healthmetric_foreground.xml",
    "androidApp/src/main/res/mipmap-anydpi-v26/ic_launcher.xml",
    "androidApp/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml",
    "androidApp/src/main/res/mipmap-anydpi-v33/ic_launcher.xml",
    "androidApp/src/main/res/mipmap-anydpi-v33/ic_launcher_round.xml",
    "androidApp/src/main/res/values/launcher_colors.xml",
    "androidApp/src/main/res/values/strings.xml",
    "androidApp/src/main/res/values/themes.xml",
    "androidApp/src/main/java/io/github/sanskarin/healthmetric/MainActivity.kt",
    "androidApp/src/main/java/io/github/sanskarin/healthmetric/data/BackupIo.kt",
    "androidApp/src/main/java/io/github/sanskarin/healthmetric/data/HealthMetricDataStore.kt",
    "androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/HealthMetricApp.kt",
    "androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/HealthMetricViewModel.kt",
    "androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/format/ChartScale.kt",
    "androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/state/SavedEnum.kt",
    "androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/testing/HealthMetricTestTags.kt",
    "androidApp/src/test/java/io/github/sanskarin/healthmetric/ui/format/ChartScaleTest.kt",
    "androidApp/src/test/java/io/github/sanskarin/healthmetric/ui/state/SavedEnumTest.kt",
    "androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/AboutNavigationUiTest.kt",
    "androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/AdultGateUiTest.kt",
    "androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/HealthMetricDataStoreTest.kt",
    "androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/ReleaseScreenshotCaptureTest.kt",
    "shared/build.gradle.kts",
    "shared/src/commonMain/kotlin/io/github/sanskarin/healthmetric/domain/Bmi.kt",
    "shared/src/commonMain/kotlin/io/github/sanskarin/healthmetric/domain/Units.kt",
    "shared/src/commonMain/kotlin/io/github/sanskarin/healthmetric/domain/Validation.kt",
    "shared/src/commonMain/kotlin/io/github/sanskarin/healthmetric/domain/WaistToHeight.kt",
    "desktopApp/build.gradle.kts",
    "desktopApp/src/main/kotlin/io/github/sanskarin/healthmetric/desktop/Main.kt",
    "desktopApp/src/main/kotlin/io/github/sanskarin/healthmetric/desktop/DesktopCalculations.kt",
    "desktopApp/src/main/kotlin/io/github/sanskarin/healthmetric/desktop/DesktopNumbers.kt",
    "desktopApp/src/test/kotlin/io/github/sanskarin/healthmetric/desktop/DesktopCalculationsTest.kt",
    "desktopApp/src/test/kotlin/io/github/sanskarin/healthmetric/desktop/DesktopNumbersTest.kt",
    "docs/architecture.md",
    "docs/backup-format.md",
    "docs/desktop.md",
    "docs/setup.md",
    "docs/development.md",
    "docs/testing.md",
    "docs/release.md",
    "docs/troubleshooting.md",
    "docs/accessibility.md",
    "docs/performance.md",
    "docs/evidence.md",
    "docs/design-system.md",
    "docs/github-governance.md",
    "docs/assets/logo.svg",
    "docs/assets/screenshots/README.md",
    "docs/adr/0001-shared-domain-kmp.md",
    "docs/adr/0002-local-privacy-first-persistence.md",
    "docs/adr/0003-versioned-adult-reference-profiles.md",
    "docs/adr/0004-bounded-user-controlled-local-data.md",
    "docs/adr/0005-ephemeral-desktop-client.md",
    "scripts/check_repository.py",
    "scripts/check_markdown_links.py",
    "scripts/check_release_version.py",
    "scripts/stage_release_assets.py",
    "scripts/verify_release_assets.py",
    "scripts/tests/test_check_release_version.py",
    "scripts/tests/test_stage_release_assets.py",
    "scripts/tests/test_verify_release_assets.py",
    "scripts/verify.sh",
    "scripts/verify.ps1",
    ".github/FUNDING.yml",
    ".github/dependabot.yml",
    ".github/PULL_REQUEST_TEMPLATE.md",
    ".github/RELEASE_TEMPLATE.md",
    ".github/ISSUE_TEMPLATE/bug_report.yml",
    ".github/ISSUE_TEMPLATE/config.yml",
    ".github/ISSUE_TEMPLATE/feature_request.yml",
    ".github/workflows/ci.yml",
    ".github/workflows/android-instrumentation.yml",
    ".github/workflows/apple-shared.yml",
    ".github/workflows/desktop.yml",
    ".github/workflows/codeql.yml",
    ".github/workflows/dependency-review.yml",
    ".github/workflows/secret-scan.yml",
    ".github/workflows/release.yml",
]

FORBIDDEN_PATHS = [
    "docs/.noop-probe",
]


def read(relative: str) -> str:
    return (ROOT / relative).read_text(encoding="utf-8")


def require_fragment(
    failures: list[str],
    relative: str,
    fragment: str,
    description: str,
) -> None:
    if fragment not in read(relative):
        failures.append(f"{relative} must contain {description}: {fragment}")


def main() -> int:
    failures: list[str] = []

    for relative in REQUIRED_PATHS:
        if not (ROOT / relative).exists():
            failures.append(f"missing required path: {relative}")

    for relative in FORBIDDEN_PATHS:
        if (ROOT / relative).exists():
            failures.append(f"forbidden temporary path is committed: {relative}")

    manifest = read("androidApp/src/main/AndroidManifest.xml")
    if "android.permission.INTERNET" in manifest:
        failures.append("AndroidManifest.xml must not request INTERNET for the offline core")
    if 'android:allowBackup="false"' not in manifest:
        failures.append("AndroidManifest.xml must keep android:allowBackup=\"false\"")
    if 'android:usesCleartextTraffic="false"' not in manifest:
        failures.append("AndroidManifest.xml must keep cleartext traffic disabled")

    settings = read("settings.gradle.kts")
    if 'include(":desktopApp")' not in settings:
        failures.append("settings.gradle.kts must include the desktop application module")

    desktop_build = read("desktopApp/build.gradle.kts")
    for fragment in [
        'implementation(project(":shared"))',
        "compose.desktop.currentOs",
        "TargetFormat.Dmg",
        "TargetFormat.Msi",
        "TargetFormat.Deb",
        "HealthMetric",
    ]:
        if fragment not in desktop_build:
            failures.append(f"desktopApp/build.gradle.kts is missing required configuration: {fragment}")

    desktop_main = read("desktopApp/src/main/kotlin/io/github/sanskarin/healthmetric/desktop/Main.kt")
    for fragment in [
        "I am 18 or older",
        "I am under 18",
        "adult BMI",
        "adult waist-to-height",
        "does not persist measurement inputs",
        "Made by the Sanskar",
    ]:
        if fragment not in desktop_main:
            failures.append(f"desktop Main.kt is missing required safety/product text: {fragment}")

    android_app = read("androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/HealthMetricApp.kt")
    for fragment in [
        "resetAdultUseChoice",
        "savedEnumValueOrDefault",
    ]:
        if fragment not in android_app:
            failures.append(f"HealthMetricApp.kt is missing Android state-safety invariant: {fragment}")

    history_screen = read("androidApp/src/main/java/io/github/sanskarin/healthmetric/ui/screens/HistoryScreen.kt")
    for fragment in [
        "ChartScale.normalize",
        "savedEnumValueOrDefault",
    ]:
        if fragment not in history_screen:
            failures.append(f"HistoryScreen.kt is missing Android history safety invariant: {fragment}")

    readme = read("README.md")
    required_readme_fragments = [
        "Made by the Sanskar",
        "https://buymeacoffee.com/sanskarIN",
        "sanskarin@outlook.in",
        "sanskarin.business@gmail.com",
        "supportramsandesh@gmail.com",
        "MIT",
        "docs/desktop.md",
    ]
    for fragment in required_readme_fragments:
        if fragment not in readme:
            failures.append(f"README.md is missing required metadata: {fragment}")

    privacy_lower = read("PRIVACY.md").lower()
    for phrase in [
        "disabled by default",
        "adult-use confirmation",
        "1 mib",
        "desktop client",
    ]:
        if phrase not in privacy_lower:
            failures.append(f"PRIVACY.md is missing required privacy invariant text: {phrase}")

    aab_requirements = [
        (".github/workflows/ci.yml", ":androidApp:bundleRelease", "App Bundle build task"),
        (".github/workflows/ci.yml", "androidApp/build/outputs/bundle/release/*.aab", "App Bundle artifact"),
        (".github/workflows/release.yml", ":androidApp:bundleRelease", "App Bundle build task"),
        ("scripts/stage_release_assets.py", "androidApp/build/outputs/bundle/release/*.aab", "App Bundle release staging"),
        ("scripts/verify.sh", ":androidApp:bundleRelease", "App Bundle verification task"),
        ("scripts/verify.ps1", ":androidApp:bundleRelease", "App Bundle verification task"),
    ]
    for relative, fragment, description in aab_requirements:
        require_fragment(failures, relative, fragment, description)

    screenshot_workflow = ".github/workflows/android-instrumentation.yml"
    for fragment in [
        "android-release-screenshots",
        "build/release-screenshots/*.png",
        "adb pull",
    ]:
        require_fragment(failures, screenshot_workflow, fragment, "release screenshot evidence")

    screenshot_test = read(
        "androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/ReleaseScreenshotCaptureTest.kt",
    )
    for file_name in [
        "01-onboarding.png",
        "02-bmi-metric.png",
        "03-bmi-result.png",
        "04-waist-ratio.png",
        "05-history.png",
        "06-settings.png",
        "07-about.png",
        "08-dark-theme.png",
    ]:
        if file_name not in screenshot_test:
            failures.append(f"release screenshot test is missing required capture: {file_name}")

    desktop_workflow = ".github/workflows/desktop.yml"
    for fragment in [
        ":desktopApp:test",
        ":desktopApp:packageUberJarForCurrentOS",
        "packageDeb",
        "packageMsi",
        "packageDmg",
        "*.deb",
        "*.msi",
        "*.dmg",
    ]:
        require_fragment(failures, desktop_workflow, fragment, "desktop verification/package configuration")

    ci_workflow = ".github/workflows/ci.yml"
    for fragment in [
        'python3 -m unittest discover -s scripts/tests -p "test_*.py"',
        ":shared:desktopTest",
        ":desktopApp:test",
        ":androidApp:testDebugUnitTest",
        ":androidApp:lintRelease",
    ]:
        require_fragment(failures, ci_workflow, fragment, "continuous verification coverage")

    for verification_script in ["scripts/verify.sh", "scripts/verify.ps1"]:
        for fragment in [
            "unittest",
            "scripts/tests",
            ":shared:desktopTest",
            ":desktopApp:test",
            ":androidApp:testDebugUnitTest",
            ":androidApp:lintRelease",
        ]:
            require_fragment(failures, verification_script, fragment, "local verification coverage")

    release_workflow = ".github/workflows/release.yml"
    for fragment in [
        "fetch-depth: 0",
        "scripts/check_release_version.py",
        "scripts/stage_release_assets.py",
        "scripts/verify_release_assets.py",
        "--write-checksums",
        "--verify-tag",
        "packageDeb",
        "packageMsi",
        "packageDmg",
        "contents: read",
        "contents: write",
    ]:
        require_fragment(failures, release_workflow, fragment, "hardened release configuration")

    release_staging = read("scripts/stage_release_assets.py")
    for fragment in [
        "Expected exactly one",
        "healthmetric-{tag}-android-unsigned.apk",
        "healthmetric-{tag}-desktop-{platform}.jar",
    ]:
        if fragment not in release_staging:
            failures.append(f"stage_release_assets.py is missing deterministic staging invariant: {fragment}")

    release_verifier = read("scripts/verify_release_assets.py")
    for fragment in [
        "SHA256SUMS.txt",
        "Missing release assets",
        "Unexpected release assets",
        "Empty release assets",
    ]:
        if fragment not in release_verifier:
            failures.append(f"verify_release_assets.py is missing release verification invariant: {fragment}")

    if failures:
        print("Repository invariant audit failed:")
        for failure in failures:
            print(f"- {failure}")
        return 1

    print(
        "Repository invariant audit passed "
        f"({len(REQUIRED_PATHS)} required paths, {len(FORBIDDEN_PATHS)} forbidden paths checked).",
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
