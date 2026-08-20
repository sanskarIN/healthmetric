#!/usr/bin/env bash
set -euo pipefail

GRADLE_BIN="${GRADLE_BIN:-gradle}"

"${GRADLE_BIN}" :shared:ktlintCheck :composeApp:ktlintCheck :androidApp:ktlintCheck
"${GRADLE_BIN}" :shared:desktopTest
"${GRADLE_BIN}" :composeApp:compileKotlinDesktop
"${GRADLE_BIN}" :composeApp:jsBrowserProductionWebpack
"${GRADLE_BIN}" :composeApp:wasmJsBrowserProductionWebpack
"${GRADLE_BIN}" :composeApp:composeCompatibilityBrowserDistribution
"${GRADLE_BIN}" :androidApp:testDebugUnitTest
"${GRADLE_BIN}" :androidApp:lintRelease
"${GRADLE_BIN}" :androidApp:assembleDebug
"${GRADLE_BIN}" :androidApp:assembleRelease
"${GRADLE_BIN}" :androidApp:bundleRelease

if [[ "$(uname -s)" == "Darwin" ]]; then
    "${GRADLE_BIN}" :composeApp:linkDebugFrameworkIosSimulatorArm64
fi

printf '\nHealthMetric cross-platform verification completed successfully.\n'
