#!/usr/bin/env python3
from __future__ import annotations

import argparse
from pathlib import Path
import re
import shutil
import sys

ROOT = Path(__file__).resolve().parents[1]
STABLE_TAG_PATTERN = re.compile(r"^v(?:0|[1-9]\d*)\.(?:0|[1-9]\d*)\.(?:0|[1-9]\d*)$")
PLATFORM_PACKAGES = {
    "linux": ("deb", "deb"),
    "windows": ("msi", "msi"),
    "macos": ("dmg", "dmg"),
}


def require_stable_tag(tag: str) -> None:
    if STABLE_TAG_PATTERN.fullmatch(tag) is None:
        raise ValueError(f"Release asset staging requires vMAJOR.MINOR.PATCH; received {tag!r}.")


def require_single_file(root: Path, pattern: str, label: str) -> Path:
    matches = sorted(path for path in root.glob(pattern) if path.is_file())
    if len(matches) != 1:
        raise ValueError(
            f"Expected exactly one {label} matching {pattern!r}; found {len(matches)}.",
        )
    if matches[0].stat().st_size <= 0:
        raise ValueError(f"{label} is empty: {matches[0]}")
    return matches[0]


def copy_asset(source: Path, destination: Path) -> Path:
    destination.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(source, destination)
    if destination.stat().st_size <= 0:
        raise ValueError(f"Staged release asset is empty: {destination}")
    return destination


def stage_android(root: Path, destination: Path, tag: str) -> list[Path]:
    require_stable_tag(tag)
    apk = require_single_file(
        root,
        "androidApp/build/outputs/apk/release/*-unsigned.apk",
        "unsigned Android APK",
    )
    aab = require_single_file(
        root,
        "androidApp/build/outputs/bundle/release/*.aab",
        "unsigned Android App Bundle",
    )
    return [
        copy_asset(apk, destination / f"healthmetric-{tag}-android-unsigned.apk"),
        copy_asset(aab, destination / f"healthmetric-{tag}-android-unsigned.aab"),
    ]


def stage_desktop(root: Path, destination: Path, tag: str, platform: str) -> list[Path]:
    require_stable_tag(tag)
    if platform not in PLATFORM_PACKAGES:
        raise ValueError(f"Unsupported desktop platform: {platform!r}.")

    native_dir, native_ext = PLATFORM_PACKAGES[platform]
    jar = require_single_file(
        root,
        "desktopApp/build/compose/jars/*.jar",
        f"{platform} desktop runnable JAR",
    )
    native = require_single_file(
        root,
        f"desktopApp/build/compose/binaries/main/{native_dir}/*.{native_ext}",
        f"{platform} desktop native package",
    )
    return [
        copy_asset(jar, destination / f"healthmetric-{tag}-desktop-{platform}.jar"),
        copy_asset(
            native,
            destination / f"healthmetric-{tag}-desktop-{platform}.{native_ext}",
        ),
    ]


def parser() -> argparse.ArgumentParser:
    result = argparse.ArgumentParser(description="Stage deterministic HealthMetric release assets.")
    result.add_argument("kind", choices=("android", "desktop"))
    result.add_argument("--tag", required=True)
    result.add_argument("--platform", choices=tuple(PLATFORM_PACKAGES))
    result.add_argument("--destination", default="release-assets")
    return result


def main(argv: list[str]) -> int:
    args = parser().parse_args(argv[1:])
    destination = ROOT / args.destination

    try:
        if args.kind == "android":
            if args.platform is not None:
                raise ValueError("--platform is only valid for desktop staging.")
            staged = stage_android(ROOT, destination, args.tag)
        else:
            if args.platform is None:
                raise ValueError("Desktop staging requires --platform.")
            staged = stage_desktop(ROOT, destination, args.tag, args.platform)
    except (OSError, ValueError) as error:
        print(f"Release asset staging failed: {error}")
        return 1

    for path in staged:
        print(path.relative_to(ROOT))
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
