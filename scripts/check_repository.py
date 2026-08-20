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
    "sharedUI/build.gradle.kts",
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

    ios_project = read("iosApp/project.yml")
    if ":sharedUI:embedAndSignAppleFrameworkForXcode" not in ios_project:
        failures.append("iosApp/project.yml must build the sharedUI Apple framework")

    if failures:
        print("Repository invariant audit failed:")
        for failure in failures:
            print(f"- {failure}")
        return 1

    print(f"Repository invariant audit passed ({len(REQUIRED_PATHS)} required paths checked).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
