package io.github.sanskarin.healthmetric

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import io.github.sanskarin.healthmetric.data.CalculatorKind
import io.github.sanskarin.healthmetric.data.HistoryEntry
import io.github.sanskarin.healthmetric.ui.screens.HistoryScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HistoryUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun entryDeleteAndEraseAllRequireExpectedUserActions() {
        val entry = HistoryEntry(
            id = "history-test",
            timestampEpochMillis = 1_700_000_000_000L,
            calculator = CalculatorKind.BMI,
            value = 22.4,
            summary = "Within adult reference range",
        )
        var deletedEntryId: String? = null
        var deleteAllRequested = false

        composeRule.setContent {
            MaterialTheme {
                HistoryScreen(
                    history = listOf(entry),
                    historyEnabled = true,
                    onDeleteEntry = { deletedEntryId = it.id },
                    onDeleteAll = { deleteAllRequested = true },
                )
            }
        }

        composeRule.onNodeWithContentDescription("Delete this history entry").performClick()
        composeRule.runOnIdle { assertEquals(entry.id, deletedEntryId) }

        composeRule.onNodeWithText("Erase all local history").performScrollTo().performClick()
        composeRule.onNodeWithText("Erase all local history?").assertIsDisplayed()
        composeRule.onNodeWithText("Delete").performClick()

        composeRule.runOnIdle { assertTrue(deleteAllRequested) }
    }
}
