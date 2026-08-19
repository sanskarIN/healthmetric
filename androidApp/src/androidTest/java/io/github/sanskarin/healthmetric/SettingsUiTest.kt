package io.github.sanskarin.healthmetric

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import io.github.sanskarin.healthmetric.data.AppThemeMode
import io.github.sanskarin.healthmetric.ui.screens.SettingsScreen
import io.github.sanskarin.healthmetric.ui.testing.HealthMetricTestTags
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun privacyControlsDispatchExplicitUserActions() {
        var requestedHistoryEnabled = false
        var requestedRetentionLimit = 0
        var saveBackupRequested = false
        var shareBackupRequested = false

        composeRule.setContent {
            MaterialTheme {
                SettingsScreen(
                    historyEnabled = false,
                    historyRetentionLimit = 100,
                    themeMode = AppThemeMode.SYSTEM,
                    onHistoryEnabledChange = { requestedHistoryEnabled = it },
                    onHistoryRetentionLimitChange = { requestedRetentionLimit = it },
                    onThemeModeChange = {},
                    onSaveBackup = { saveBackupRequested = true },
                    onShareBackup = { shareBackupRequested = true },
                    onImport = {},
                    onDeleteAllData = {},
                    onOpenReleases = {},
                    onAbout = {},
                )
            }
        }

        composeRule.onNodeWithTag(HealthMetricTestTags.SETTINGS_HISTORY_SWITCH).performClick()
        composeRule.onNodeWithText("Keep up to 50").performClick()
        composeRule.onNodeWithText("Save JSON backup to a file").performScrollTo().performClick()
        composeRule.onNodeWithText("Share JSON backup").performScrollTo().performClick()

        composeRule.runOnIdle {
            assertTrue(requestedHistoryEnabled)
            assertEquals(50, requestedRetentionLimit)
            assertTrue(saveBackupRequested)
            assertTrue(shareBackupRequested)
        }
    }
}
