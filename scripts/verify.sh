#!/usr/bin/env bash
set -euo pipefail

GRADLE_BIN="${GRADLE_BIN:-gradle}"

python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py

"${GRADLE_BIN}" :shared:ktlintCheck :androidApp:ktlintCheck :desktopApp:ktlintCheck
"${GRADLE_BIN}" :shared:desktopTest
"${GRADLE_BIN}" :desktopApp:test
"${GRADLE_BIN}" :desktopApp:packageUberJarForCurrentOS
"${GRADLE_BIN}" :androidApp:testDebugUnitTest
"${GRADLE_BIN}" :androidApp:lintRelease
"${GRADLE_BIN}" :androidApp:assembleDebug
"${GRADLE_BIN}" :androidApp:assembleRelease
"${GRADLE_BIN}" :androidApp:bundleRelease

printf '\nHealthMetric verification completed successfully.\n'
