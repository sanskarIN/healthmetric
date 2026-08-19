package io.github.sanskarin.healthmetric

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import io.github.sanskarin.healthmetric.ui.screens.CalculatorScreen
import io.github.sanskarin.healthmetric.ui.testing.HealthMetricTestTags
import org.junit.Rule
import org.junit.Test

class CalculatorUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun metricCalculationShowsNeutralResult() {
        composeRule.setContent {
            MaterialTheme {
                CalculatorScreen(historyEnabled = false, onRecord = {})
            }
        }

        composeRule.onNodeWithTag(HealthMetricTestTags.BMI_WEIGHT).performTextInput("70")
        composeRule.onNodeWithTag(HealthMetricTestTags.BMI_HEIGHT_CM).performTextInput("175")
        composeRule.onNodeWithTag(HealthMetricTestTags.BMI_CALCULATE).performClick()

        composeRule.onNodeWithTag(HealthMetricTestTags.BMI_RESULT).assertIsDisplayed()
        composeRule.onNodeWithText("BMI 22.9").assertIsDisplayed()
        composeRule.onNodeWithText("Within adult reference range").assertIsDisplayed()
    }

    @Test
    fun missingWeightShowsValidationMessage() {
        composeRule.setContent {
            MaterialTheme {
                CalculatorScreen(historyEnabled = false, onRecord = {})
            }
        }

        composeRule.onNodeWithTag(HealthMetricTestTags.BMI_HEIGHT_CM).performTextInput("175")
        composeRule.onNodeWithTag(HealthMetricTestTags.BMI_CALCULATE).performClick()

        composeRule.onNodeWithText("Enter a valid weight.").assertIsDisplayed()
    }
}
