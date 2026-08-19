package io.github.sanskarin.healthmetric

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import io.github.sanskarin.healthmetric.ui.screens.WaistToHeightScreen
import io.github.sanskarin.healthmetric.ui.testing.HealthMetricTestTags
import org.junit.Rule
import org.junit.Test

class WaistToHeightUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun metricCalculationShowsNeutralRatioResult() {
        composeRule.setContent {
            MaterialTheme {
                WaistToHeightScreen(historyEnabled = false, onRecord = {})
            }
        }

        composeRule.onNodeWithTag(HealthMetricTestTags.WAIST_VALUE).performTextInput("80")
        composeRule.onNodeWithTag(HealthMetricTestTags.WAIST_HEIGHT).performTextInput("170")
        composeRule.onNodeWithTag(HealthMetricTestTags.WAIST_CALCULATE).performClick()

        composeRule.onNodeWithTag(HealthMetricTestTags.WAIST_RESULT).assertIsDisplayed()
        composeRule.onNodeWithText("Ratio 0.47").assertIsDisplayed()
    }

    @Test
    fun missingWaistShowsValidationMessage() {
        composeRule.setContent {
            MaterialTheme {
                WaistToHeightScreen(historyEnabled = false, onRecord = {})
            }
        }

        composeRule.onNodeWithTag(HealthMetricTestTags.WAIST_HEIGHT).performTextInput("170")
        composeRule.onNodeWithTag(HealthMetricTestTags.WAIST_CALCULATE).performClick()

        composeRule.onNodeWithText("Enter a valid waist measurement.").assertIsDisplayed()
    }
}
