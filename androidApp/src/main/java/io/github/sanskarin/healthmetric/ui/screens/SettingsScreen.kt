package io.github.sanskarin.healthmetric.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.sanskarin.healthmetric.BuildConfig
import io.github.sanskarin.healthmetric.R
import io.github.sanskarin.healthmetric.data.AppThemeMode
import io.github.sanskarin.healthmetric.data.HistoryRetentionPolicy
import io.github.sanskarin.healthmetric.ui.testing.HealthMetricTestTags

@Composable
fun SettingsScreen(
    historyEnabled: Boolean,
    historyRetentionLimit: Int,
    themeMode: AppThemeMode,
    onHistoryEnabledChange: (Boolean) -> Unit,
    onHistoryRetentionLimitChange: (Int) -> Unit,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onSaveBackup: () -> Unit,
    onShareBackup: () -> Unit,
    onImport: () -> Unit,
    onDeleteAllData: () -> Unit,
    onOpenReleases: () -> Unit,
    onAbout: () -> Unit,
) {
    var confirmDelete by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.semantics { heading() },
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.privacy_data), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.save_local_history))
                    Text(
                        text = stringResource(R.string.save_local_history_description),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Switch(
                    checked = historyEnabled,
                    onCheckedChange = onHistoryEnabledChange,
                    modifier = Modifier.testTag(HealthMetricTestTags.SETTINGS_HISTORY_SWITCH),
                )
            }

            Text(
                text = stringResource(R.string.history_retention_title),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = stringResource(R.string.history_retention_description),
                style = MaterialTheme.typography.bodySmall,
            )
            HistoryRetentionPolicy.allowedLimits.chunked(2).forEach { rowLimits ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowLimits.forEach { limit ->
                        FilterChip(
                            selected = historyRetentionLimit == limit,
                            onClick = { onHistoryRetentionLimitChange(limit) },
                            label = { Text(stringResource(R.string.history_retention_option, limit)) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            OutlinedButton(onClick = onSaveBackup, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save_json_backup))
            }
            OutlinedButton(onClick = onShareBackup, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.share_json_backup))
            }
            OutlinedButton(onClick = onImport, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.restore_json_backup))
            }
            Button(
                onClick = { confirmDelete = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.delete_all_local_data))
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.appearance), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = themeMode == mode,
                        onClick = { onThemeModeChange(mode) },
                        label = { Text(themeLabel(mode)) },
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.accessibility), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.accessibility_description),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.updates), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.installed_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodyMedium,
            )
            OutlinedButton(onClick = onOpenReleases, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.view_github_releases))
            }
        }

        OutlinedButton(onClick = onAbout, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.about_healthmetric))
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.delete_data_title)) },
            text = { Text(stringResource(R.string.delete_data_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmDelete = false
                        onDeleteAllData()
                    },
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun themeLabel(mode: AppThemeMode): String = when (mode) {
    AppThemeMode.SYSTEM -> stringResource(R.string.theme_system)
    AppThemeMode.LIGHT -> stringResource(R.string.theme_light)
    AppThemeMode.DARK -> stringResource(R.string.theme_dark)
}
