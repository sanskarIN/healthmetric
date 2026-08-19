package io.github.sanskarin.healthmetric

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.sanskarin.healthmetric.ui.screens.AdultOnlyScreen
import io.github.sanskarin.healthmetric.ui.screens.OnboardingScreen
import io.github.sanskarin.healthmetric.ui.testing.HealthMetricTestTags
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

        composeRule.onNodeWithTag(HealthMetricTestTags.ONBOARDING_UNDER_18).performClick()
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
        composeRule.onNodeWithTag(HealthMetricTestTags.ADULT_GATE_RETURN).assertDoesNotExist()
    }

    @Test
    fun adultOnlyScreenCanDispatchReturnToAgeSelection() {
        var returnRequested = false

        composeRule.setContent {
            MaterialTheme {
                AdultOnlyScreen(onReturnToAgeSelection = { returnRequested = true })
            }
        }

        composeRule.onNodeWithTag(HealthMetricTestTags.ADULT_GATE_RETURN).assertIsDisplayed().performClick()
        composeRule.runOnIdle { assertTrue(returnRequested) }
    }
}
