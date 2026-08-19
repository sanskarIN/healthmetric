#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
STABLE_TAG_PATTERN = re.compile(r"^v(?:0|[1-9]\d*)\.(?:0|[1-9]\d*)\.(?:0|[1-9]\d*)$")
CHECKSUM_FILE_NAME = "SHA256SUMS.txt"


def expected_asset_names(tag: str) -> tuple[str, ...]:
    if STABLE_TAG_PATTERN.fullmatch(tag) is None:
        raise ValueError(f"Release asset verification requires vMAJOR.MINOR.PATCH; received {tag!r}.")

    return (
        f"healthmetric-{tag}-android-unsigned.apk",
        f"healthmetric-{tag}-android-unsigned.aab",
        f"healthmetric-{tag}-desktop-linux.jar",
        f"healthmetric-{tag}-desktop-linux.deb",
        f"healthmetric-{tag}-desktop-windows.jar",
        f"healthmetric-{tag}-desktop-windows.msi",
        f"healthmetric-{tag}-desktop-macos.jar",
        f"healthmetric-{tag}-desktop-macos.dmg",
    )


def verify_assets(directory: Path, tag: str) -> list[Path]:
    expected = set(expected_asset_names(tag))
    actual_paths = sorted(path for path in directory.iterdir() if path.is_file())
    actual = {path.name for path in actual_paths}

    missing = sorted(expected - actual)
    extra = sorted(actual - expected)
    failures: list[str] = []
    if missing:
        failures.append(f"Missing release assets: {', '.join(missing)}")
    if extra:
        failures.append(f"Unexpected release assets: {', '.join(extra)}")

    empty = sorted(path.name for path in actual_paths if path.stat().st_size <= 0)
    if empty:
        failures.append(f"Empty release assets: {', '.join(empty)}")

    if failures:
        raise ValueError("; ".join(failures))

    return [directory / name for name in sorted(expected)]


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as source:
        for chunk in iter(lambda: source.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def write_checksum_manifest(paths: list[Path], destination: Path) -> Path:
    lines = [f"{sha256(path)}  {path.name}" for path in sorted(paths, key=lambda item: item.name)]
    destination.write_text("\n".join(lines) + "\n", encoding="utf-8")
    return destination


def parser() -> argparse.ArgumentParser:
    result = argparse.ArgumentParser(description="Verify HealthMetric release assets before publication.")
    result.add_argument("--tag", required=True)
    result.add_argument("--directory", default="release-assets")
    result.add_argument("--write-checksums", action="store_true")
    return result


def main(argv: list[str]) -> int:
    args = parser().parse_args(argv[1:])
    directory = ROOT / args.directory

    try:
        paths = verify_assets(directory, args.tag)
        if args.write_checksums:
            manifest = write_checksum_manifest(paths, directory / CHECKSUM_FILE_NAME)
            print(manifest.relative_to(ROOT))
    except (OSError, ValueError) as error:
        print(f"Release asset verification failed: {error}")
        return 1

    print(f"Verified {len(paths)} release assets for {args.tag}.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
