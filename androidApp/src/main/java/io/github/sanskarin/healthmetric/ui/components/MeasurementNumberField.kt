package io.github.sanskarin.healthmetric.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun MeasurementNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    wholeNumbersOnly: Boolean = false,
    maxLength: Int = 12,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { candidate ->
            val validCharacters = if (wholeNumbersOnly) {
                candidate.all(Char::isDigit)
            } else {
                candidate.all { it.isDigit() || it == '.' } && candidate.count { it == '.' } <= 1
            }
            if (candidate.length <= maxLength && validCharacters) {
                onValueChange(candidate)
            }
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (wholeNumbersOnly) KeyboardType.Number else KeyboardType.Decimal,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}
