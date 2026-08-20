$ErrorActionPreference = "Stop"

$GradleBin = if ($env:GRADLE_BIN) { $env:GRADLE_BIN } else { "gradle" }

function Invoke-Gradle {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$Arguments)
    & $GradleBin @Arguments
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

Invoke-Gradle :shared:ktlintCheck :sharedUI:ktlintCheck :androidApp:ktlintCheck :desktopApp:ktlintCheck :webApp:ktlintCheck
Invoke-Gradle :shared:desktopTest
Invoke-Gradle :shared:compileKotlinJs :shared:compileKotlinWasmJs
Invoke-Gradle :sharedUI:compileKotlinDesktop :sharedUI:compileKotlinJs :sharedUI:compileKotlinWasmJs
Invoke-Gradle :desktopApp:compileKotlin
Invoke-Gradle :webApp:jsBrowserProductionWebpack :webApp:wasmJsBrowserProductionWebpack
Invoke-Gradle :androidApp:testDebugUnitTest
Invoke-Gradle :androidApp:lintRelease
Invoke-Gradle :androidApp:assembleDebug
Invoke-Gradle :androidApp:assembleRelease

Write-Host "`nHealthMetric cross-platform verification completed successfully."
