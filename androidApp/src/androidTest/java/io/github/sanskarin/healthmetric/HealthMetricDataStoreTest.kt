package io.github.sanskarin.healthmetric

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.sanskarin.healthmetric.data.AppThemeMode
import io.github.sanskarin.healthmetric.data.CalculatorKind
import io.github.sanskarin.healthmetric.data.HealthMetricDataStore
import io.github.sanskarin.healthmetric.data.HistoryEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
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
            dataStore.addHistory(
                entry(
                    id = "entry-$index",
                    value = 20.0 + index / 100.0,
                    timestampEpochMillis = 1_700_000_000_000L + index,
                ),
            )
        }

        val history = dataStore.history.first()
        assertEquals(50, history.size)
        assertEquals("entry-54", history.first().id)
        assertEquals("entry-5", history.last().id)
    }

    @Test
    fun exportRestoreRoundTripPreservesPortablePreferencesAndHistoryOnly() = runBlocking {
        dataStore.setHistoryEnabled(true)
        dataStore.setHistoryRetentionLimit(250)
        dataStore.setThemeMode(AppThemeMode.DARK)
        dataStore.completeOnboarding(adultUseConfirmed = true)
        dataStore.addHistory(entry(id = "round-trip", value = 23.4))

        val backup = dataStore.exportJson()
        val root = JSONObject(backup)
        assertFalse(root.has("historyEnabled"))
        assertFalse(root.has("adultUseConfirmed"))
        assertFalse(root.has("onboardingComplete"))

        dataStore.deleteAllLocalData()
        dataStore.restoreFromJson(backup)

        val preferences = dataStore.preferences.first()
        val history = dataStore.history.first()

        assertFalse(preferences.historyEnabled)
        assertEquals(250, preferences.historyRetentionLimit)
        assertEquals(AppThemeMode.DARK, preferences.themeMode)
        assertFalse(preferences.adultUseConfirmed)
        assertFalse(preferences.onboardingComplete)
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

    @Test
    fun restoringDeletedOlderEntryPreservesChronologicalOrder() = runBlocking {
        dataStore.setHistoryEnabled(true)
        val oldest = entry(
            id = "oldest",
            value = 21.0,
            timestampEpochMillis = 1_700_000_000_000L,
        )
        val middle = entry(
            id = "middle",
            value = 22.0,
            timestampEpochMillis = 1_700_000_001_000L,
        )
        val newest = entry(
            id = "newest",
            value = 23.0,
            timestampEpochMillis = 1_700_000_002_000L,
        )
        dataStore.addHistory(oldest)
        dataStore.addHistory(middle)
        dataStore.addHistory(newest)

        dataStore.deleteHistoryEntry(middle.id)
        dataStore.restoreHistoryEntry(middle)

        assertEquals(
            listOf("newest", "middle", "oldest"),
            dataStore.history.first().map(HistoryEntry::id),
        )
    }

    @Test
    fun restoreSalvagesValidEntriesDeduplicatesIdsAndOrdersNewestFirst() = runBlocking {
        val backup = """
            {
              "schemaVersion": 1,
              "historyEnabled": true,
              "historyRetentionLimit": 100,
              "themeMode": "SYSTEM",
              "adultUseConfirmed": true,
              "onboardingComplete": true,
              "history": [
                {
                  "id": "same-id",
                  "timestampEpochMillis": 1700000000000,
                  "calculator": "BMI",
                  "value": 22.1,
                  "summary": "First valid entry"
                },
                {
                  "id": "same-id",
                  "timestampEpochMillis": 1700000001000,
                  "calculator": "BMI",
                  "value": 23.1,
                  "summary": "Duplicate should be ignored"
                },
                {
                  "id": "",
                  "timestampEpochMillis": 1700000002000,
                  "calculator": "BMI",
                  "value": 24.1,
                  "summary": "Blank id should be ignored"
                },
                {
                  "id": "bad-calculator",
                  "timestampEpochMillis": 1700000003000,
                  "calculator": "UNKNOWN",
                  "value": 25.1,
                  "summary": "Unknown calculator should be ignored"
                },
                {
                  "id": "second-valid",
                  "timestampEpochMillis": 1700000004000,
                  "calculator": "WAIST_TO_HEIGHT",
                  "value": 0.47,
                  "summary": "Second valid entry"
                }
              ]
            }
        """.trimIndent()

        dataStore.restoreFromJson(backup)

        val history = dataStore.history.first()
        val preferences = dataStore.preferences.first()
        assertEquals(2, history.size)
        assertEquals(listOf("second-valid", "same-id"), history.map { it.id })
        assertEquals(0.47, history.first().value, 0.0001)
        assertFalse(preferences.historyEnabled)
        assertFalse(preferences.adultUseConfirmed)
        assertFalse(preferences.onboardingComplete)
    }

    @Test
    fun restorePreservesCurrentConsentAndAdultGateStateAgainstLegacyFields() = runBlocking {
        dataStore.setHistoryEnabled(true)
        dataStore.completeOnboarding(adultUseConfirmed = false)
        val legacyBackup = """
            {
              "schemaVersion": 1,
              "historyEnabled": false,
              "historyRetentionLimit": 100,
              "themeMode": "LIGHT",
              "adultUseConfirmed": true,
              "onboardingComplete": false,
              "history": []
            }
        """.trimIndent()

        dataStore.restoreFromJson(legacyBackup)

        val preferences = dataStore.preferences.first()
        assertTrue(preferences.historyEnabled)
        assertFalse(preferences.adultUseConfirmed)
        assertTrue(preferences.onboardingComplete)
        assertEquals(AppThemeMode.LIGHT, preferences.themeMode)
    }

    @Test
    fun resettingAdultChoicePreservesUnrelatedLocalPreferencesAndHistory() = runBlocking {
        dataStore.setHistoryEnabled(true)
        dataStore.setHistoryRetentionLimit(250)
        dataStore.setThemeMode(AppThemeMode.DARK)
        dataStore.completeOnboarding(adultUseConfirmed = true)
        dataStore.addHistory(entry(id = "preserved", value = 22.3))

        dataStore.resetAdultUseChoice()

        val preferences = dataStore.preferences.first()
        assertFalse(preferences.adultUseConfirmed)
        assertFalse(preferences.onboardingComplete)
        assertTrue(preferences.historyEnabled)
        assertEquals(250, preferences.historyRetentionLimit)
        assertEquals(AppThemeMode.DARK, preferences.themeMode)
        assertEquals("preserved", dataStore.history.first().single().id)
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidProgrammaticHistoryEntryIsRejected() = runBlocking {
        dataStore.setHistoryEnabled(true)
        dataStore.addHistory(entry(id = " ", value = 22.0))
    }

    private fun entry(
        id: String,
        value: Double,
        timestampEpochMillis: Long = 1_700_000_000_000L,
    ): HistoryEntry = HistoryEntry(
        id = id,
        timestampEpochMillis = timestampEpochMillis,
        calculator = CalculatorKind.BMI,
        value = value,
        summary = "Neutral test entry",
    )
}
