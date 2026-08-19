package io.github.sanskarin.healthmetric

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import io.github.sanskarin.healthmetric.data.HealthMetricDataStore
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test

class AboutNavigationUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @After
    fun clearLocalState() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        HealthMetricDataStore(context).deleteAllLocalData()
    }

    @Test
    fun aboutBackButtonReturnsToOriginScreen() {
        composeRule.onNodeWithText("I am 18 or older").performClick()
        composeRule.onNodeWithText("Adult BMI calculator").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("About").performClick()
        composeRule.onNodeWithText("About HealthMetric").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Adult BMI calculator").assertIsDisplayed()

        composeRule.onNodeWithText("Settings").performClick()
        composeRule.onNodeWithText("Privacy & data").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("About").performClick()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Privacy & data").assertIsDisplayed()
    }
}
