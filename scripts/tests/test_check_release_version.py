from __future__ import annotations

from pathlib import Path
import sys
import unittest

SCRIPTS_DIR = Path(__file__).resolve().parents[1]
if str(SCRIPTS_DIR) not in sys.path:
    sys.path.insert(0, str(SCRIPTS_DIR))

from check_release_version import validate_release_tag  # noqa: E402


class ReleaseVersionCheckTest(unittest.TestCase):
    def test_current_project_version_accepts_matching_stable_tag(self) -> None:
        self.assertEqual([], validate_release_tag("v0.1.0"))

    def test_tag_requires_v_prefix(self) -> None:
        failures = validate_release_tag("0.1.0")
        self.assertTrue(any("vMAJOR.MINOR.PATCH" in failure for failure in failures))

    def test_tag_rejects_incomplete_version(self) -> None:
        failures = validate_release_tag("v0.1")
        self.assertTrue(any("vMAJOR.MINOR.PATCH" in failure for failure in failures))

    def test_tag_rejects_leading_zero_components(self) -> None:
        failures = validate_release_tag("v00.1.0")
        self.assertTrue(any("vMAJOR.MINOR.PATCH" in failure for failure in failures))

    def test_tag_rejects_unconfigured_prerelease_suffix(self) -> None:
        failures = validate_release_tag("v0.1.0-rc.1")
        self.assertTrue(any("vMAJOR.MINOR.PATCH" in failure for failure in failures))

    def test_tag_must_match_android_and_desktop_versions(self) -> None:
        failures = validate_release_tag("v0.2.0")
        self.assertTrue(any("Android versionName" in failure for failure in failures))
        self.assertTrue(any("Desktop project version" in failure for failure in failures))


if __name__ == "__main__":
    unittest.main()
