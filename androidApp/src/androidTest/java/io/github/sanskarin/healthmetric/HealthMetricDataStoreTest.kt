package io.github.sanskarin.healthmetric

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.sanskarin.healthmetric.data.AppThemeMode
import io.github.sanskarin.healthmetric.data.CalculatorKind
import io.github.sanskarin.healthmetric.data.HealthMetricDataStore
import io.github.sanskarin.healthmetric.data.HistoryEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HealthMetricDataStoreTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var dataStore: HealthMetricDataStore

    @Before
    fun setUp() = runBlocking {
        dataStore = HealthMetricDataStore(context)
        dataStore.deleteAllLocalData()
    }

    @After
    fun tearDown() = runBlocking {
        dataStore.deleteAllLocalData()
    }

    @Test
    fun historyIsNotStoredWithoutExplicitOptIn() = runBlocking {
        dataStore.addHistory(entry(id = "not-stored", value = 22.0))

        assertTrue(dataStore.history.first().isEmpty())
        assertFalse(dataStore.preferences.first().historyEnabled)
    }

    @Test
    fun configuredRetentionLimitTrimsOlderEntries() = runBlocking {
        dataStore.setHistoryEnabled(true)
        dataStore.setHistoryRetentionLimit(50)

        repeat(55) { index ->
            dataStore.addHistory(entry(id = "entry-$index", value = 20.0 + index / 100.0))
        }

        val history = dataStore.history.first()
        assertEquals(50, history.size)
        assertEquals("entry-54", history.first().id)
        assertEquals("entry-5", history.last().id)
    }

    @Test
    fun exportRestoreRoundTripPreservesSupportedPreferencesAndHistory() = runBlocking {
        dataStore.setHistoryEnabled(true)
        dataStore.setHistoryRetentionLimit(250)
        dataStore.setThemeMode(AppThemeMode.DARK)
        dataStore.completeOnboarding(adultUseConfirmed = true)
        dataStore.addHistory(entry(id = "round-trip", value = 23.4))

        val backup = dataStore.exportJson()
        dataStore.deleteAllLocalData()
        dataStore.restoreFromJson(backup)

        val preferences = dataStore.preferences.first()
        val history = dataStore.history.first()

        assertTrue(preferences.historyEnabled)
        assertEquals(250, preferences.historyRetentionLimit)
        assertEquals(AppThemeMode.DARK, preferences.themeMode)
        assertTrue(preferences.adultUseConfirmed)
        assertTrue(preferences.onboardingComplete)
        assertEquals(1, history.size)
        assertEquals("round-trip", history.single().id)
        assertEquals(23.4, history.single().value, 0.0001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun unsupportedBackupSchemaIsRejected() = runBlocking {
        dataStore.restoreFromJson("{\"schemaVersion\":999,\"history\":[]}")
    }

    @Test
    fun entryCanBeDeletedAndRestoredWithoutChangingHistoryPreference() = runBlocking {
        dataStore.setHistoryEnabled(true)
        val saved = entry(id = "undo-me", value = 21.8)
        dataStore.addHistory(saved)

        dataStore.setHistoryEnabled(false)
        dataStore.deleteHistoryEntry(saved.id)
        assertTrue(dataStore.history.first().isEmpty())

        dataStore.restoreHistoryEntry(saved)
        assertEquals(saved.id, dataStore.history.first().single().id)
        assertFalse(dataStore.preferences.first().historyEnabled)
    }

    private fun entry(id: String, value: Double): HistoryEntry = HistoryEntry(
        id = id,
        timestampEpochMillis = 1_700_000_000_000L,
        calculator = CalculatorKind.BMI,
        value = value,
        summary = "Neutral test entry",
    )
}
