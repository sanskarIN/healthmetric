package io.github.sanskarin.healthmetric.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import io.github.sanskarin.healthmetric.ui.format.LocalizedNumbers

@Composable
fun MeasurementNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    wholeNumbersOnly: Boolean = false,
    maxLength: Int = 12,
    testTag: String? = null,
) {
    val fieldModifier = if (testTag == null) {
        Modifier.fillMaxWidth()
    } else {
        Modifier
            .fillMaxWidth()
            .testTag(testTag)
    }

    OutlinedTextField(
        value = value,
        onValueChange = { candidate ->
            if (
                LocalizedNumbers.isValidInput(
                    candidate = candidate,
                    wholeNumbersOnly = wholeNumbersOnly,
                    maxLength = maxLength,
                )
            ) {
                onValueChange(candidate)
            }
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (wholeNumbersOnly) KeyboardType.Number else KeyboardType.Decimal,
        ),
        modifier = fieldModifier,
    )
}
