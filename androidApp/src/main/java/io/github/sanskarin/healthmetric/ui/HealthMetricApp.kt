package io.github.sanskarin.healthmetric.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.sanskarin.healthmetric.R
import io.github.sanskarin.healthmetric.data.CalculatorKind
import io.github.sanskarin.healthmetric.data.SafeLogger
import io.github.sanskarin.healthmetric.ui.screens.AboutScreen
import io.github.sanskarin.healthmetric.ui.screens.AdultOnlyScreen
import io.github.sanskarin.healthmetric.ui.screens.CalculatorScreen
import io.github.sanskarin.healthmetric.ui.screens.HistoryScreen
import io.github.sanskarin.healthmetric.ui.screens.OnboardingScreen
import io.github.sanskarin.healthmetric.ui.screens.SettingsScreen
import io.github.sanskarin.healthmetric.ui.screens.WaistToHeightScreen
import kotlinx.coroutines.launch

private enum class AppScreen {
    CALCULATOR,
    WAIST,
    HISTORY,
    SETTINGS,
    ABOUT,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthMetricApp(
    state: HealthMetricUiState,
    viewModel: HealthMetricViewModel,
) {
    if (!state.isReady) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (!state.preferences.onboardingComplete) {
        OnboardingScreen(
            onAdultConfirmed = { viewModel.completeOnboarding(true) },
            onUnder18 = { viewModel.completeOnboarding(false) },
        )
        return
    }

    if (!state.preferences.adultUseConfirmed) {
        AdultOnlyScreen()
        return
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var screenName by rememberSaveable { mutableStateOf(AppScreen.CALCULATOR.name) }
    val screen = AppScreen.valueOf(screenName)

    val restoreSuccess = stringResource(R.string.restore_success)
    val restoreInvalid = stringResource(R.string.restore_invalid)
    val exportFailed = stringResource(R.string.export_failed)
    val readBackupFailed = stringResource(R.string.read_backup_failed)
    val openLinkFailed = stringResource(R.string.open_link_failed)
    val exportChooserFailed = stringResource(R.string.export_chooser_failed)
    val exportSubject = stringResource(R.string.export_subject)
    val exportChooserTitle = stringResource(R.string.export_chooser_title)
    val ratioHistorySummary = stringResource(R.string.ratio_history_summary)

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                ?: error(readBackupFailed)
        }.onSuccess { json ->
            viewModel.restoreData(json) { success ->
                scope.launch {
                    snackbarHostState.showSnackbar(if (success) restoreSuccess else restoreInvalid)
                }
            }
        }.onFailure { error ->
            SafeLogger.warn(SafeLogger.Event.RESTORE_FAILED, error)
            scope.launch { snackbarHostState.showSnackbar(readBackupFailed) }
        }
    }

    fun openUri(rawUri: String) {
        runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(rawUri)))
        }.onFailure { error ->
            SafeLogger.warn(SafeLogger.Event.LINK_OPEN_FAILED, error)
            scope.launch { snackbarHostState.showSnackbar(openLinkFailed) }
        }
    }

    fun exportData() {
        viewModel.exportData(
            onReady = { json ->
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_SUBJECT, exportSubject)
                    putExtra(Intent.EXTRA_TEXT, json)
                }
                runCatching {
                    context.startActivity(Intent.createChooser(intent, exportChooserTitle))
                }.onFailure { error ->
                    SafeLogger.warn(SafeLogger.Event.EXPORT_FAILED, error)
                    scope.launch { snackbarHostState.showSnackbar(exportChooserFailed) }
                }
            },
            onError = { scope.launch { snackbarHostState.showSnackbar(exportFailed) } },
        )
    }

    val screenTitle = when (screen) {
        AppScreen.CALCULATOR -> stringResource(R.string.screen_bmi)
        AppScreen.WAIST -> stringResource(R.string.screen_waist_ratio)
        AppScreen.HISTORY -> stringResource(R.string.screen_history)
        AppScreen.SETTINGS -> stringResource(R.string.screen_settings)
        AppScreen.ABOUT -> stringResource(R.string.screen_about)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.top_bar_title, screenTitle)) },
                actions = {
                    if (screen != AppScreen.ABOUT) {
                        IconButton(onClick = { screenName = AppScreen.ABOUT.name }) {
                            Icon(
                                Icons.Outlined.Info,
                                contentDescription = stringResource(R.string.about_content_description),
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (screen != AppScreen.ABOUT) {
                NavigationBar {
                    NavigationBarItem(
                        selected = screen == AppScreen.CALCULATOR,
                        onClick = { screenName = AppScreen.CALCULATOR.name },
                        icon = { Icon(Icons.Outlined.Calculate, contentDescription = null) },
                        label = { Text(stringResource(R.string.screen_bmi)) },
                    )
                    NavigationBarItem(
                        selected = screen == AppScreen.WAIST,
                        onClick = { screenName = AppScreen.WAIST.name },
                        icon = { Icon(Icons.Outlined.Straighten, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_ratio)) },
                    )
                    NavigationBarItem(
                        selected = screen == AppScreen.HISTORY,
                        onClick = { screenName = AppScreen.HISTORY.name },
                        icon = { Icon(Icons.Outlined.History, contentDescription = null) },
                        label = { Text(stringResource(R.string.screen_history)) },
                    )
                    NavigationBarItem(
                        selected = screen == AppScreen.SETTINGS,
                        onClick = { screenName = AppScreen.SETTINGS.name },
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                        label = { Text(stringResource(R.string.screen_settings)) },
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp),
            ) {
                when (screen) {
                    AppScreen.CALCULATOR -> CalculatorScreen(
                        historyEnabled = state.preferences.historyEnabled,
                        onRecord = { result ->
                            viewModel.recordCalculation(
                                kind = CalculatorKind.BMI,
                                value = result.displayBmi,
                                summary = result.band.label,
                            )
                        },
                    )
                    AppScreen.WAIST -> WaistToHeightScreen(
                        historyEnabled = state.preferences.historyEnabled,
                        onRecord = { result ->
                            viewModel.recordCalculation(
                                kind = CalculatorKind.WAIST_TO_HEIGHT,
                                value = result.displayRatio,
                                summary = ratioHistorySummary,
                            )
                        },
                    )
                    AppScreen.HISTORY -> HistoryScreen(
                        history = state.history,
                        historyEnabled = state.preferences.historyEnabled,
                        onDeleteAll = viewModel::deleteHistory,
                    )
                    AppScreen.SETTINGS -> SettingsScreen(
                        historyEnabled = state.preferences.historyEnabled,
                        themeMode = state.preferences.themeMode,
                        onHistoryEnabledChange = viewModel::setHistoryEnabled,
                        onThemeModeChange = viewModel::setThemeMode,
                        onExport = ::exportData,
                        onImport = { importLauncher.launch(arrayOf("application/json", "text/plain")) },
                        onDeleteAllData = viewModel::deleteAllLocalData,
                        onOpenReleases = { openUri("https://github.com/sanskarIN/healthmetric/releases") },
                        onAbout = { screenName = AppScreen.ABOUT.name },
                    )
                    AppScreen.ABOUT -> AboutScreen(onOpenLink = ::openUri)
                }
            }
        }
    }
}
