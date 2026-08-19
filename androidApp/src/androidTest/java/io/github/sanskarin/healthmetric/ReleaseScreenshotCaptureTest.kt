package io.github.sanskarin.healthmetric

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import io.github.sanskarin.healthmetric.data.HealthMetricDataStore
import io.github.sanskarin.healthmetric.ui.testing.HealthMetricTestTags
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.FileOutputStream

class ReleaseScreenshotCaptureTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val targetContext = instrumentation.targetContext
    private val screenshotDirectory: File
        get() = File(requireNotNull(targetContext.getExternalFilesDir(null)), SCREENSHOT_DIRECTORY)

    @Before
    fun clearPreviousScreenshots() {
        screenshotDirectory.deleteRecursively()
        check(screenshotDirectory.mkdirs() || screenshotDirectory.isDirectory) {
            "Could not create release screenshot directory."
        }
    }

    @After
    fun clearLocalState() = runBlocking {
        HealthMetricDataStore(targetContext).deleteAllLocalData()
    }

    @Test
    fun captureReleaseEvidenceSetFromRealApp() {
        composeRule.onNodeWithText("I am 18 or older").assertIsDisplayed()
        capture("01-onboarding.png")

        composeRule.onNodeWithText("I am 18 or older").performClick()
        composeRule.onNodeWithText("Adult BMI calculator").assertIsDisplayed()
        capture("02-bmi-metric.png")

        composeRule.onNodeWithTag(HealthMetricTestTags.NAV_SETTINGS).performClick()
        composeRule.onNodeWithText("Privacy & data").assertIsDisplayed()
        composeRule.onNodeWithTag(HealthMetricTestTags.SETTINGS_HISTORY_SWITCH).performClick()
        composeRule.waitForIdle()
        capture("06-settings.png")

        composeRule.onNodeWithTag(HealthMetricTestTags.NAV_BMI).performClick()
        composeRule.onNodeWithTag(HealthMetricTestTags.BMI_WEIGHT).performTextInput("70")
        composeRule.onNodeWithTag(HealthMetricTestTags.BMI_HEIGHT_CM).performTextInput("175")
        composeRule.onNodeWithTag(HealthMetricTestTags.BMI_CALCULATE).performClick()
        composeRule.onNodeWithTag(HealthMetricTestTags.BMI_RESULT).assertIsDisplayed()
        capture("03-bmi-result.png")

        composeRule.onNodeWithTag(HealthMetricTestTags.NAV_WAIST).performClick()
        composeRule.onNodeWithTag(HealthMetricTestTags.WAIST_VALUE).performTextInput("80")
        composeRule.onNodeWithTag(HealthMetricTestTags.WAIST_HEIGHT).performTextInput("175")
        composeRule.onNodeWithTag(HealthMetricTestTags.WAIST_CALCULATE).performClick()
        composeRule.onNodeWithTag(HealthMetricTestTags.WAIST_RESULT).assertIsDisplayed()
        capture("04-waist-ratio.png")

        composeRule.onNodeWithTag(HealthMetricTestTags.NAV_HISTORY).performClick()
        composeRule.onNodeWithTag(HealthMetricTestTags.HISTORY_LIST).assertIsDisplayed()
        capture("05-history.png")

        composeRule.onNodeWithTag(HealthMetricTestTags.ABOUT_OPEN).performClick()
        composeRule.onNodeWithText("About HealthMetric").assertIsDisplayed()
        capture("07-about.png")
        composeRule.onNodeWithTag(HealthMetricTestTags.ABOUT_BACK).performClick()

        composeRule.onNodeWithTag(HealthMetricTestTags.NAV_SETTINGS).performClick()
        composeRule.onNodeWithText("Dark").performScrollTo().performClick()
        composeRule.waitForIdle()
        capture("08-dark-theme.png")
    }

    private fun capture(fileName: String) {
        composeRule.waitForIdle()
        val bitmap = checkNotNull(instrumentation.uiAutomation.takeScreenshot()) {
            "Android UiAutomation could not capture $fileName."
        }
        val outputFile = File(screenshotDirectory, fileName)
        FileOutputStream(outputFile).use { output ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, output)) {
                "Could not encode $fileName as PNG."
            }
        }
        bitmap.recycle()
        check(outputFile.isFile && outputFile.length() > 0L) {
            "Screenshot $fileName was not written successfully."
        }
    }

    companion object {
        private const val SCREENSHOT_DIRECTORY = "release-screenshots"
        private const val PNG_QUALITY = 100
    }
}
