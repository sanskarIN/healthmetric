#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
STABLE_TAG_PATTERN = re.compile(r"^v(?P<version>0|[1-9]\d*)\.(?:0|[1-9]\d*)\.(?:0|[1-9]\d*)$")
ANDROID_VERSION_PATTERN = re.compile(r'^\s*versionName\s*=\s*"([^"]+)"\s*$', re.MULTILINE)
DESKTOP_VERSION_PATTERN = re.compile(r'^\s*version\s*=\s*"([^"]+)"\s*$', re.MULTILINE)


def read_version(path: Path, pattern: re.Pattern[str], label: str) -> str:
    match = pattern.search(path.read_text(encoding="utf-8"))
    if match is None:
        raise ValueError(f"Unable to find {label} in {path.relative_to(ROOT)}")
    return match.group(1)


def validate_release_tag(tag: str) -> list[str]:
    failures: list[str] = []
    match = STABLE_TAG_PATTERN.fullmatch(tag)
    if match is None:
        return [f"Release tag must use stable semantic version form vMAJOR.MINOR.PATCH; received {tag!r}."]

    tag_version = tag.removeprefix("v")
    android_version = read_version(
        ROOT / "androidApp/build.gradle.kts",
        ANDROID_VERSION_PATTERN,
        "Android versionName",
    )
    desktop_version = read_version(
        ROOT / "desktopApp/build.gradle.kts",
        DESKTOP_VERSION_PATTERN,
        "desktop project version",
    )

    if android_version != tag_version:
        failures.append(
            f"Android versionName {android_version!r} does not match release tag version {tag_version!r}.",
        )
    if desktop_version != tag_version:
        failures.append(
            f"Desktop project version {desktop_version!r} does not match release tag version {tag_version!r}.",
        )
    if android_version != desktop_version:
        failures.append(
            f"Android version {android_version!r} and desktop version {desktop_version!r} must match.",
        )

    return failures


def main(argv: list[str]) -> int:
    if len(argv) != 2:
        print("Usage: python3 scripts/check_release_version.py vMAJOR.MINOR.PATCH")
        return 2

    try:
        failures = validate_release_tag(argv[1])
    except (OSError, ValueError) as error:
        print(f"Release version check failed: {error}")
        return 1

    if failures:
        print("Release version check failed:")
        for failure in failures:
            print(f"- {failure}")
        return 1

    print(f"Release version check passed for {argv[1]}.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
