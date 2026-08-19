package io.github.sanskarin.healthmetric

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import io.github.sanskarin.healthmetric.data.HealthMetricDataStore
import io.github.sanskarin.healthmetric.ui.testing.HealthMetricTestTags
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

        composeRule.onNodeWithTag(HealthMetricTestTags.ABOUT_OPEN).performClick()
        composeRule.onNodeWithText("About HealthMetric").assertIsDisplayed()
        composeRule.onNodeWithTag(HealthMetricTestTags.ABOUT_BACK).performClick()
        composeRule.onNodeWithText("Adult BMI calculator").assertIsDisplayed()

        composeRule.onNodeWithTag(HealthMetricTestTags.NAV_SETTINGS).performClick()
        composeRule.onNodeWithText("Privacy & data").assertIsDisplayed()
        composeRule.onNodeWithTag(HealthMetricTestTags.ABOUT_OPEN).performClick()
        composeRule.onNodeWithTag(HealthMetricTestTags.ABOUT_BACK).performClick()
        composeRule.onNodeWithText("Privacy & data").assertIsDisplayed()
    }
}
