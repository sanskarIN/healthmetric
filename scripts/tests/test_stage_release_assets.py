from __future__ import annotations

from pathlib import Path
import sys
import tempfile
import unittest

SCRIPTS_DIR = Path(__file__).resolve().parents[1]
if str(SCRIPTS_DIR) not in sys.path:
    sys.path.insert(0, str(SCRIPTS_DIR))

from stage_release_assets import (  # noqa: E402
    PLATFORM_PACKAGES,
    require_single_file,
    stage_android,
    stage_desktop,
)


class StageReleaseAssetsTest(unittest.TestCase):
    def write_file(self, root: Path, relative: str, content: bytes = b"artifact") -> Path:
        path = root / relative
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_bytes(content)
        return path

    def test_android_staging_uses_versioned_deterministic_names(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            self.write_file(root, "androidApp/build/outputs/apk/release/app-release-unsigned.apk")
            self.write_file(root, "androidApp/build/outputs/bundle/release/app-release.aab")

            staged = stage_android(root, root / "release-assets", "v0.1.0")

            self.assertEqual(
                [
                    "healthmetric-v0.1.0-android-unsigned.apk",
                    "healthmetric-v0.1.0-android-unsigned.aab",
                ],
                [path.name for path in staged],
            )
            self.assertTrue(all(path.read_bytes() == b"artifact" for path in staged))

    def test_desktop_staging_supports_every_release_platform(self) -> None:
        for platform, (native_dir, native_ext) in PLATFORM_PACKAGES.items():
            with self.subTest(platform=platform), tempfile.TemporaryDirectory() as temp_dir:
                root = Path(temp_dir)
                self.write_file(root, "desktopApp/build/compose/jars/HealthMetric.jar")
                self.write_file(
                    root,
                    f"desktopApp/build/compose/binaries/main/{native_dir}/HealthMetric.{native_ext}",
                )

                staged = stage_desktop(root, root / "release-assets", "v0.1.0", platform)

                self.assertEqual(
                    [
                        f"healthmetric-v0.1.0-desktop-{platform}.jar",
                        f"healthmetric-v0.1.0-desktop-{platform}.{native_ext}",
                    ],
                    [path.name for path in staged],
                )

    def test_duplicate_build_outputs_fail_closed(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            self.write_file(root, "build/one.jar")
            self.write_file(root, "build/two.jar")

            with self.assertRaisesRegex(ValueError, "exactly one"):
                require_single_file(root, "build/*.jar", "test artifact")

    def test_empty_build_output_fails_closed(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            self.write_file(root, "build/empty.jar", content=b"")

            with self.assertRaisesRegex(ValueError, "empty"):
                require_single_file(root, "build/*.jar", "test artifact")

    def test_invalid_release_tag_is_rejected_before_copying(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            self.write_file(root, "androidApp/build/outputs/apk/release/app-release-unsigned.apk")
            self.write_file(root, "androidApp/build/outputs/bundle/release/app-release.aab")

            with self.assertRaisesRegex(ValueError, "vMAJOR.MINOR.PATCH"):
                stage_android(root, root / "release-assets", "latest")


if __name__ == "__main__":
    unittest.main()
