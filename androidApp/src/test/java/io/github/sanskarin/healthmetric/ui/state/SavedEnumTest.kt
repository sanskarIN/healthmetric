package io.github.sanskarin.healthmetric.ui.state

import org.junit.Assert.assertEquals
import org.junit.Test

class SavedEnumTest {
    private enum class ExampleScreen {
        HOME,
        SETTINGS,
    }

    @Test
    fun restoresKnownEnumValue() {
        assertEquals(
            ExampleScreen.SETTINGS,
            savedEnumValueOrDefault("SETTINGS", ExampleScreen.HOME),
        )
    }

    @Test
    fun staleSavedValueFallsBackWithoutThrowing() {
        assertEquals(
            ExampleScreen.HOME,
            savedEnumValueOrDefault("REMOVED_SCREEN", ExampleScreen.HOME),
        )
    }

    @Test
    fun emptySavedValueFallsBackWithoutThrowing() {
        assertEquals(
            ExampleScreen.HOME,
            savedEnumValueOrDefault("", ExampleScreen.HOME),
        )
    }
}
