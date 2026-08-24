#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
ANDROID_BUILD = ROOT / "androidApp" / "build.gradle.kts"
CHANGELOG = ROOT / "CHANGELOG.md"

SEMVER_PATTERN = re.compile(
    r"^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)"
    r"(?:-([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?"
    r"(?:\+([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?$"
)
VERSION_NAME_PATTERN = re.compile(r'versionName\s*=\s*"([^"]+)"')
VERSION_CODE_PATTERN = re.compile(r"versionCode\s*=\s*(\d+)")


def fail(message: str) -> int:
    print(f"Release version check failed: {message}")
    return 1


def normalize_tag(tag: str) -> str | None:
    if not tag.startswith("v"):
        return None
    version = tag[1:]
    if not SEMVER_PATTERN.fullmatch(version):
        return None
    return version


def main() -> int:
    android_build = ANDROID_BUILD.read_text(encoding="utf-8")
    changelog = CHANGELOG.read_text(encoding="utf-8")

    version_name_match = VERSION_NAME_PATTERN.search(android_build)
    if version_name_match is None:
        return fail("androidApp/build.gradle.kts does not define versionName")

    version_name = version_name_match.group(1)
    if not SEMVER_PATTERN.fullmatch(version_name):
        return fail(f'Android versionName "{version_name}" is not valid Semantic Versioning')

    version_code_match = VERSION_CODE_PATTERN.search(android_build)
    if version_code_match is None:
        return fail("androidApp/build.gradle.kts does not define versionCode")

    version_code = int(version_code_match.group(1))
    if version_code < 1:
        return fail("Android versionCode must be a positive integer")

    if f"## [{version_name}]" not in changelog:
        return fail(f"CHANGELOG.md does not contain a [{version_name}] release section")

    if len(sys.argv) > 2:
        return fail("usage: check_release_version.py [v<semver>]")

    if len(sys.argv) == 2:
        tag_version = normalize_tag(sys.argv[1])
        if tag_version is None:
            return fail(f'"{sys.argv[1]}" is not a valid v-prefixed Semantic Versioning tag')
        if tag_version != version_name:
            return fail(
                f'tag version "{tag_version}" does not match Android versionName "{version_name}"'
            )

    print(
        "Release version check passed "
        f"(versionName={version_name}, versionCode={version_code}, changelog section present)."
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
