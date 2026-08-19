package io.github.sanskarin.healthmetric.ui.state

internal inline fun <reified T : Enum<T>> savedEnumValueOrDefault(
    name: String,
    fallback: T,
): T = enumValues<T>().firstOrNull { value -> value.name == name } ?: fallback
