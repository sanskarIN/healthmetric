package io.github.sanskarin.healthmetric

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class OnboardingUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun freshInstallShowsAdultUseNotice() {
        composeRule.onNodeWithText("HealthMetric").assertIsDisplayed()
        composeRule.onNodeWithText("I am 18 or older").assertIsDisplayed()
        composeRule.onNodeWithText("I am under 18").assertIsDisplayed()
    }
}
