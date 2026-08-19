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
    "docs/adr/0001-shared-domain-kmp.md",
    "docs/adr/0002-local-privacy-first-persistence.md",
    "docs/adr/0003-versioned-adult-reference-profiles.md",
    "docs/adr/0004-bounded-user-controlled-local-data.md",
    "desktopApp/build.gradle.kts",
    "desktopApp/src/main/kotlin/io/github/sanskarin/healthmetric/desktop/Main.kt",
    "desktopApp/src/main/kotlin/io/github/sanskarin/healthmetric/desktop/DesktopCalculations.kt",
    "desktopApp/src/main/kotlin/io/github/sanskarin/healthmetric/desktop/DesktopNumbers.kt",
    "desktopApp/src/test/kotlin/io/github/sanskarin/healthmetric/desktop/DesktopCalculationsTest.kt",
    "desktopApp/src/test/kotlin/io/github/sanskarin/healthmetric/desktop/DesktopNumbersTest.kt",
    ".github/workflows/ci.yml",
    ".github/workflows/android-instrumentation.yml",
    ".github/workflows/apple-shared.yml",
    ".github/workflows/desktop.yml",
    ".github/workflows/codeql.yml",
    ".github/workflows/dependency-review.yml",
    ".github/workflows/secret-scan.yml",
    ".github/workflows/release.yml",
]


def read(relative: str) -> str:
    return (ROOT / relative).read_text(encoding="utf-8")


def main() -> int:
    failures: list[str] = []

    for relative in REQUIRED_PATHS:
        if not (ROOT / relative).exists():
            failures.append(f"missing required path: {relative}")

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
        "HealthMetric",
    ]:
        if fragment not in desktop_build:
            failures.append(f"desktopApp/build.gradle.kts is missing required configuration: {fragment}")

    desktop_main = read("desktopApp/src/main/kotlin/io/github/sanskarin/healthmetric/desktop/Main.kt")
    required_desktop_fragments = [
        "I am 18 or older",
        "I am under 18",
        "adult BMI",
        "adult waist-to-height",
        "does not persist measurement inputs",
        "Made by the Sanskar",
    ]
    for fragment in required_desktop_fragments:
        if fragment not in desktop_main:
            failures.append(f"desktop Main.kt is missing required safety/product text: {fragment}")

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

    privacy = read("PRIVACY.md")
    for phrase in [
        "disabled by default",
        "adult-use confirmation",
        "1 MiB",
    ]:
        if phrase not in privacy:
            failures.append(f"PRIVACY.md is missing required privacy invariant text: {phrase}")

    if failures:
        print("Repository invariant audit failed:")
        for failure in failures:
            print(f"- {failure}")
        return 1

    print(f"Repository invariant audit passed ({len(REQUIRED_PATHS)} required paths checked).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
