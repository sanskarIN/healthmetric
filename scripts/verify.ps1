$ErrorActionPreference = "Stop"

$GradleBin = if ($env:GRADLE_BIN) { $env:GRADLE_BIN } else { "gradle" }

function Invoke-Gradle {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$Tasks)
    & $GradleBin @Tasks
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

Invoke-Gradle :shared:ktlintCheck :composeApp:ktlintCheck :androidApp:ktlintCheck
Invoke-Gradle :shared:desktopTest
Invoke-Gradle :composeApp:compileKotlinDesktop
Invoke-Gradle :composeApp:jsBrowserProductionWebpack
Invoke-Gradle :composeApp:wasmJsBrowserProductionWebpack
Invoke-Gradle :composeApp:composeCompatibilityBrowserDistribution
Invoke-Gradle :androidApp:testDebugUnitTest
Invoke-Gradle :androidApp:lintRelease
Invoke-Gradle :androidApp:assembleDebug
Invoke-Gradle :androidApp:assembleRelease
Invoke-Gradle :androidApp:bundleRelease

Write-Host "`nHealthMetric cross-platform verification completed successfully."
