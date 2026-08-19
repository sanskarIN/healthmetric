#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
STABLE_TAG_PATTERN = re.compile(
    r"^v(?P<major>0|[1-9]\d*)\.(?P<minor>0|[1-9]\d*)\.(?P<patch>0|[1-9]\d*)$",
)
ANDROID_VERSION_PATTERN = re.compile(r'^\s*versionName\s*=\s*"([^"]+)"\s*$', re.MULTILINE)
ANDROID_VERSION_CODE_PATTERN = re.compile(r"^\s*versionCode\s*=\s*(\d+)\s*$", re.MULTILINE)
DESKTOP_VERSION_PATTERN = re.compile(r'^\s*version\s*=\s*"([^"]+)"\s*$', re.MULTILINE)
DESKTOP_PACKAGE_VERSION_PATTERN = re.compile(
    r'^\s*packageVersion\s*=\s*"([^"]+)"\s*$',
    re.MULTILINE,
)


def read_version(path: Path, pattern: re.Pattern[str], label: str) -> str:
    match = pattern.search(path.read_text(encoding="utf-8"))
    if match is None:
        raise ValueError(f"Unable to find {label} in {path.relative_to(ROOT)}")
    return match.group(1)


def android_version_code_for(version: str) -> int:
    match = re.fullmatch(
        r"(?P<major>0|[1-9]\d*)\.(?P<minor>0|[1-9]\d*)\.(?P<patch>0|[1-9]\d*)",
        version,
    )
    if match is None:
        raise ValueError(f"Android version code mapping requires MAJOR.MINOR.PATCH; received {version!r}.")

    major = int(match.group("major"))
    minor = int(match.group("minor"))
    patch = int(match.group("patch"))
    if minor > 99 or patch > 99:
        raise ValueError(
            "Android version code mapping reserves two digits each for MINOR and PATCH; "
            f"received {version!r}.",
        )

    version_code = major * 10_000 + minor * 100 + patch
    if not 1 <= version_code <= 2_100_000_000:
        raise ValueError(f"Derived Android versionCode is outside the supported range: {version_code}.")
    return version_code


def validate_release_tag(tag: str) -> list[str]:
    failures: list[str] = []
    if STABLE_TAG_PATTERN.fullmatch(tag) is None:
        return [f"Release tag must use stable semantic version form vMAJOR.MINOR.PATCH; received {tag!r}."]

    tag_version = tag.removeprefix("v")
    android_build = ROOT / "androidApp/build.gradle.kts"
    desktop_build = ROOT / "desktopApp/build.gradle.kts"
    android_version = read_version(
        android_build,
        ANDROID_VERSION_PATTERN,
        "Android versionName",
    )
    android_version_code = int(
        read_version(
            android_build,
            ANDROID_VERSION_CODE_PATTERN,
            "Android versionCode",
        ),
    )
    desktop_version = read_version(
        desktop_build,
        DESKTOP_VERSION_PATTERN,
        "desktop project version",
    )
    desktop_package_version = read_version(
        desktop_build,
        DESKTOP_PACKAGE_VERSION_PATTERN,
        "desktop native packageVersion",
    )

    if android_version != tag_version:
        failures.append(
            f"Android versionName {android_version!r} does not match release tag version {tag_version!r}.",
        )
    if desktop_version != tag_version:
        failures.append(
            f"Desktop project version {desktop_version!r} does not match release tag version {tag_version!r}.",
        )
    if desktop_package_version != tag_version:
        failures.append(
            f"Desktop packageVersion {desktop_package_version!r} does not match release tag version {tag_version!r}.",
        )
    if android_version != desktop_version or desktop_version != desktop_package_version:
        failures.append(
            "Android versionName, desktop project version, and desktop native packageVersion must match.",
        )

    try:
        expected_version_code = android_version_code_for(android_version)
    except ValueError as error:
        failures.append(str(error))
    else:
        if android_version_code != expected_version_code:
            failures.append(
                f"Android versionCode {android_version_code} does not match "
                f"the documented semantic mapping for {android_version!r}: {expected_version_code}.",
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
