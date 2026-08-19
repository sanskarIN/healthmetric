$ErrorActionPreference = "Stop"

$GradleBin = if ($env:GRADLE_BIN) { $env:GRADLE_BIN } else { "gradle" }

& $GradleBin :shared:ktlintCheck :androidApp:ktlintCheck
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleBin :shared:desktopTest
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleBin :androidApp:testDebugUnitTest
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleBin :androidApp:lintRelease
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleBin :androidApp:assembleDebug
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleBin :androidApp:assembleRelease
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "`nHealthMetric verification completed successfully."
