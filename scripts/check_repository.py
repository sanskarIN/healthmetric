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
    "docs/github-governance.md",
    "docs/assets/screenshots/README.md",
    "docs/adr/0001-shared-domain-kmp.md",
    "docs/adr/0002-local-privacy-first-persistence.md",
    "docs/adr/0003-versioned-adult-reference-profiles.md",
    "docs/adr/0004-bounded-user-controlled-local-data.md",
    "scripts/check_repository.py",
    "scripts/check_markdown_links.py",
    "scripts/verify.sh",
    "scripts/verify.ps1",
    "androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/ReleaseScreenshotCaptureTest.kt",
    ".github/workflows/ci.yml",
    ".github/workflows/android-instrumentation.yml",
    ".github/workflows/apple-shared.yml",
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

    readme = read("README.md")
    required_readme_fragments = [
        "Made by the Sanskar",
        "https://buymeacoffee.com/sanskarIN",
        "sanskarin@outlook.in",
        "sanskarin.business@gmail.com",
        "supportramsandesh@gmail.com",
        "MIT",
    ]
    for fragment in required_readme_fragments:
        if fragment not in readme:
            failures.append(f"README.md is missing required metadata: {fragment}")

    privacy_lower = read("PRIVACY.md").lower()
    for phrase in [
        "disabled by default",
        "adult-use confirmation",
        "1 mib",
    ]:
        if phrase not in privacy_lower:
            failures.append(f"PRIVACY.md is missing required privacy invariant text: {phrase}")

    aab_requirements = [
        (".github/workflows/ci.yml", ":androidApp:bundleRelease", "App Bundle build task"),
        (".github/workflows/ci.yml", "androidApp/build/outputs/bundle/release/*.aab", "App Bundle artifact"),
        (".github/workflows/release.yml", ":androidApp:bundleRelease", "App Bundle build task"),
        (".github/workflows/release.yml", "androidApp/build/outputs/bundle/release/*.aab", "App Bundle release attachment"),
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
