from __future__ import annotations

from pathlib import Path
import sys
import unittest

SCRIPTS_DIR = Path(__file__).resolve().parents[1]
if str(SCRIPTS_DIR) not in sys.path:
    sys.path.insert(0, str(SCRIPTS_DIR))

from check_release_version import (  # noqa: E402
    ANDROID_VERSION_CODE_PATTERN,
    ANDROID_VERSION_PATTERN,
    DESKTOP_PACKAGE_VERSION_PATTERN,
    DESKTOP_VERSION_PATTERN,
    ROOT,
    android_version_code_for,
    read_version,
    validate_release_tag,
)


class ReleaseVersionCheckTest(unittest.TestCase):
    def current_version(self) -> str:
        return read_version(
            ROOT / "androidApp/build.gradle.kts",
            ANDROID_VERSION_PATTERN,
            "Android versionName",
        )

    def current_version_code(self) -> int:
        return int(
            read_version(
                ROOT / "androidApp/build.gradle.kts",
                ANDROID_VERSION_CODE_PATTERN,
                "Android versionCode",
            ),
        )

    def desktop_version(self) -> str:
        return read_version(
            ROOT / "desktopApp/build.gradle.kts",
            DESKTOP_VERSION_PATTERN,
            "desktop project version",
        )

    def desktop_package_version(self) -> str:
        return read_version(
            ROOT / "desktopApp/build.gradle.kts",
            DESKTOP_PACKAGE_VERSION_PATTERN,
            "desktop native packageVersion",
        )

    def test_current_project_version_accepts_matching_stable_tag(self) -> None:
        self.assertEqual([], validate_release_tag(f"v{self.current_version()}"))

    def test_current_release_versions_are_all_2_0_12(self) -> None:
        self.assertEqual("2.0.12", self.current_version())
        self.assertEqual("2.0.12", self.desktop_version())
        self.assertEqual("2.0.12", self.desktop_package_version())

    def test_current_android_version_code_matches_semantic_version(self) -> None:
        self.assertEqual(
            android_version_code_for(self.current_version()),
            self.current_version_code(),
        )

    def test_version_code_mapping_for_2_0_12(self) -> None:
        self.assertEqual(20_012, android_version_code_for("2.0.12"))

    def test_version_code_mapping_rejects_large_minor_component(self) -> None:
        with self.assertRaisesRegex(ValueError, "reserves two digits"):
            android_version_code_for("2.100.0")

    def test_version_code_mapping_rejects_large_patch_component(self) -> None:
        with self.assertRaisesRegex(ValueError, "reserves two digits"):
            android_version_code_for("2.0.100")

    def test_tag_requires_v_prefix(self) -> None:
        failures = validate_release_tag(self.current_version())
        self.assertTrue(any("vMAJOR.MINOR.PATCH" in failure for failure in failures))

    def test_tag_rejects_incomplete_version(self) -> None:
        failures = validate_release_tag("v0.1")
        self.assertTrue(any("vMAJOR.MINOR.PATCH" in failure for failure in failures))

    def test_tag_rejects_leading_zero_components(self) -> None:
        failures = validate_release_tag("v00.1.0")
        self.assertTrue(any("vMAJOR.MINOR.PATCH" in failure for failure in failures))

    def test_tag_rejects_unconfigured_prerelease_suffix(self) -> None:
        failures = validate_release_tag(f"v{self.current_version()}-rc.1")
        self.assertTrue(any("vMAJOR.MINOR.PATCH" in failure for failure in failures))

    def test_tag_must_match_android_and_desktop_versions(self) -> None:
        major, minor, patch = (int(part) for part in self.current_version().split("."))
        mismatched_tag = f"v{major}.{minor}.{patch + 1}"
        failures = validate_release_tag(mismatched_tag)

        self.assertTrue(any("Android versionName" in failure for failure in failures))
        self.assertTrue(any("Desktop project version" in failure for failure in failures))
        self.assertTrue(any("Desktop packageVersion" in failure for failure in failures))


if __name__ == "__main__":
    unittest.main()
