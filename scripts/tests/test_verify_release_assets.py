from __future__ import annotations

import hashlib
from pathlib import Path
import sys
import tempfile
import unittest

SCRIPTS_DIR = Path(__file__).resolve().parents[1]
if str(SCRIPTS_DIR) not in sys.path:
    sys.path.insert(0, str(SCRIPTS_DIR))

from verify_release_assets import (  # noqa: E402
    CHECKSUM_FILE_NAME,
    expected_asset_names,
    verify_assets,
    write_checksum_manifest,
)


class VerifyReleaseAssetsTest(unittest.TestCase):
    def populate_expected(self, directory: Path, tag: str = "v0.1.0") -> None:
        for index, name in enumerate(expected_asset_names(tag), start=1):
            (directory / name).write_bytes(f"asset-{index}".encode())

    def test_exact_nonempty_asset_set_passes(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            directory = Path(temp_dir)
            self.populate_expected(directory)

            verified = verify_assets(directory, "v0.1.0")

            self.assertEqual(8, len(verified))
            self.assertEqual(set(expected_asset_names("v0.1.0")), {path.name for path in verified})

    def test_missing_asset_fails_closed(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            directory = Path(temp_dir)
            self.populate_expected(directory)
            (directory / expected_asset_names("v0.1.0")[0]).unlink()

            with self.assertRaisesRegex(ValueError, "Missing release assets"):
                verify_assets(directory, "v0.1.0")

    def test_extra_asset_fails_closed(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            directory = Path(temp_dir)
            self.populate_expected(directory)
            (directory / "unexpected.txt").write_text("extra", encoding="utf-8")

            with self.assertRaisesRegex(ValueError, "Unexpected release assets"):
                verify_assets(directory, "v0.1.0")

    def test_empty_asset_fails_closed(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            directory = Path(temp_dir)
            self.populate_expected(directory)
            (directory / expected_asset_names("v0.1.0")[0]).write_bytes(b"")

            with self.assertRaisesRegex(ValueError, "Empty release assets"):
                verify_assets(directory, "v0.1.0")

    def test_checksum_manifest_is_sorted_and_sha256_verified(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            directory = Path(temp_dir)
            self.populate_expected(directory)
            verified = verify_assets(directory, "v0.1.0")

            manifest = write_checksum_manifest(verified, directory / CHECKSUM_FILE_NAME)
            lines = manifest.read_text(encoding="utf-8").splitlines()

            self.assertEqual(8, len(lines))
            expected_names = sorted(expected_asset_names("v0.1.0"))
            self.assertEqual(expected_names, [line.split("  ", 1)[1] for line in lines])
            first_path = directory / expected_names[0]
            expected_digest = hashlib.sha256(first_path.read_bytes()).hexdigest()
            self.assertEqual(expected_digest, lines[0].split("  ", 1)[0])

    def test_invalid_tag_is_rejected(self) -> None:
        with self.assertRaisesRegex(ValueError, "vMAJOR.MINOR.PATCH"):
            expected_asset_names("latest")


if __name__ == "__main__":
    unittest.main()
