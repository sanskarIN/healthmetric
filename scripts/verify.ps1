$ErrorActionPreference = "Stop"

$GradleBin = if ($env:GRADLE_BIN) { $env:GRADLE_BIN } else { "gradle" }
$PythonBin = if ($env:PYTHON_BIN) { $env:PYTHON_BIN } else { "python" }

& $PythonBin scripts/check_repository.py
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $PythonBin scripts/check_markdown_links.py
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleBin :shared:ktlintCheck :androidApp:ktlintCheck :desktopApp:ktlintCheck
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleBin :shared:desktopTest
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleBin :desktopApp:test
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleBin :desktopApp:packageUberJarForCurrentOS
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleBin :androidApp:testDebugUnitTest
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleBin :androidApp:lintRelease
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleBin :androidApp:assembleDebug
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleBin :androidApp:assembleRelease
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleBin :androidApp:bundleRelease
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "`nHealthMetric verification completed successfully."
