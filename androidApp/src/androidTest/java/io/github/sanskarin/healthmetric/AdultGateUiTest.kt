package io.github.sanskarin.healthmetric

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.sanskarin.healthmetric.ui.screens.AdultOnlyScreen
import io.github.sanskarin.healthmetric.ui.screens.OnboardingScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AdultGateUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun under18ChoiceDispatchesBlockedAdultReferencePath() {
        var under18Selected = false

        composeRule.setContent {
            MaterialTheme {
                OnboardingScreen(
                    onAdultConfirmed = {},
                    onUnder18 = { under18Selected = true },
                )
            }
        }

        composeRule.onNodeWithText("I am under 18").performClick()
        composeRule.runOnIdle { assertTrue(under18Selected) }
    }

    @Test
    fun adultOnlyScreenExplainsCalculatorIsUnavailable() {
        composeRule.setContent {
            MaterialTheme {
                AdultOnlyScreen()
            }
        }

        composeRule.onNodeWithText("Adult reference calculators unavailable").assertIsDisplayed()
        composeRule.onNodeWithText(
            "HealthMetric intentionally does not apply adult BMI or waist-to-height reference calculations to people under 18. Age-specific growth and health questions should be discussed with a parent, guardian, or qualified healthcare professional.",
        ).assertIsDisplayed()
    }
}
