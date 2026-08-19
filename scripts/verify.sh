#!/usr/bin/env bash
set -euo pipefail

GRADLE_BIN="${GRADLE_BIN:-gradle}"

"${GRADLE_BIN}" :shared:ktlintCheck :androidApp:ktlintCheck
"${GRADLE_BIN}" :shared:desktopTest
"${GRADLE_BIN}" :androidApp:testDebugUnitTest
"${GRADLE_BIN}" :androidApp:lintRelease
"${GRADLE_BIN}" :androidApp:assembleDebug
"${GRADLE_BIN}" :androidApp:assembleRelease

printf '\nHealthMetric verification completed successfully.\n'
