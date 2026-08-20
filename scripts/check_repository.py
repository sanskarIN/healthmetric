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
    "composeApp/build.gradle.kts",
    "composeApp/src/commonMain/kotlin/io/github/sanskarin/healthmetric/App.kt",
    "composeApp/src/desktopMain/kotlin/io/github/sanskarin/healthmetric/Main.kt",
    "composeApp/src/iosMain/kotlin/io/github/sanskarin/healthmetric/HealthMetricViewControllerFactory.kt",
    "composeApp/src/webMain/kotlin/io/github/sanskarin/healthmetric/Main.kt",
    "iosApp/HealthMetric.xcodeproj/project.pbxproj",
    "iosApp/HealthMetric.xcodeproj/xcshareddata/xcschemes/HealthMetric.xcscheme",
    "iosApp/HealthMetricApp/HealthMetricApp.swift",
    "iosApp/HealthMetricApp/ContentView.swift",
    "iosApp/HealthMetricApp/Info.plist",
    "docs/architecture.md",
    "docs/backup-format.md",
    "docs/cross-platform.md",
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
    ".github/workflows/cross-platform.yml",
    ".github/workflows/android-instrumentation.yml",
    ".github/workflows/apple-shared.yml",
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
        "iPhone / iPad",
        "WebAssembly",
        "docs/cross-platform.md",
    ]
    for fragment in required_readme_fragments:
        if fragment not in readme:
            failures.append(f"README.md is missing required metadata/support claim: {fragment}")

    privacy = read("PRIVACY.md")
    for phrase in [
        "disabled by default",
        "adult-use confirmation",
        "1 MiB",
    ]:
        if phrase not in privacy:
            failures.append(f"PRIVACY.md is missing required privacy invariant text: {phrase}")

    settings = read("settings.gradle.kts")
    if 'include(":composeApp")' not in settings:
        failures.append("settings.gradle.kts must include the cross-platform composeApp module")

    shared_build = read("shared/build.gradle.kts")
    for target in ["iosArm64()", "iosSimulatorArm64()", "js", "wasmJs"]:
        if target not in shared_build:
            failures.append(f"shared/build.gradle.kts is missing target declaration: {target}")

    compose_build = read("composeApp/build.gradle.kts")
    for target in ["jvm(\"desktop\")", "iosArm64()", "iosSimulatorArm64()", "js", "wasmJs"]:
        if target not in compose_build:
            failures.append(f"composeApp/build.gradle.kts is missing target declaration: {target}")
    for package_format in ["TargetFormat.Dmg", "TargetFormat.Msi", "TargetFormat.Deb"]:
        if package_format not in compose_build:
            failures.append(f"composeApp desktop packaging is missing: {package_format}")

    cross_platform_ci = read(".github/workflows/cross-platform.yml")
    for runner in ["ubuntu-latest", "windows-latest", "macos-latest"]:
        if runner not in cross_platform_ci:
            failures.append(f"cross-platform CI is missing runner: {runner}")
    for task in [
        "jsBrowserProductionWebpack",
        "wasmJsBrowserProductionWebpack",
        "packageDistributionForCurrentOS",
        "linkDebugFrameworkIosSimulatorArm64",
        "xcodebuild",
    ]:
        if task not in cross_platform_ci:
            failures.append(f"cross-platform CI is missing verification task: {task}")

    if failures:
        print("Repository invariant audit failed:")
        for failure in failures:
            print(f"- {failure}")
        return 1

    print(f"Repository invariant audit passed ({len(REQUIRED_PATHS)} required paths checked).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
