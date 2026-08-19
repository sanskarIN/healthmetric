package io.github.sanskarin.healthmetric.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.sanskarin.healthmetric.domain.WaistToHeightCalculator
import io.github.sanskarin.healthmetric.domain.WaistToHeightResult

@Composable
fun WaistToHeightScreen(
    historyEnabled: Boolean,
    onRecord: (WaistToHeightResult) -> Unit,
) {
    var useMetric by rememberSaveable { mutableStateOf(true) }
    var waist by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }
    var result by remember { mutableStateOf<WaistToHeightResult?>(null) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Adult waist-to-height ratio",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = "A neutral measurement tool for adults. The result is not a diagnosis and is not presented as an appearance target.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = useMetric,
                onClick = { useMetric = true; result = null; error = null },
                label = { Text("Centimetres") },
            )
            FilterChip(
                selected = !useMetric,
                onClick = { useMetric = false; result = null; error = null },
                label = { Text("Inches") },
            )
        }
        RatioNumberField(
            value = waist,
            onValueChange = { waist = it },
            label = if (useMetric) "Waist (cm)" else "Waist (inches)",
        )
        RatioNumberField(
            value = height,
            onValueChange = { height = it },
            label = if (useMetric) "Height (cm)" else "Height (inches)",
        )
        Button(
            onClick = {
                val calculation = runCatching {
                    val waistValue = waist.toDoubleOrNull() ?: error("Enter a valid waist measurement.")
                    val heightValue = height.toDoubleOrNull() ?: error("Enter a valid height.")
                    if (useMetric) {
                        WaistToHeightCalculator.calculateMetric(waistValue, heightValue)
                    } else {
                        WaistToHeightCalculator.calculateImperial(waistValue, heightValue)
                    }
                }
                calculation.onSuccess {
                    result = it
                    error = null
                    onRecord(it)
                }.onFailure {
                    result = null
                    error = it.message ?: "Check the measurements and try again."
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (historyEnabled) "Calculate and save locally" else "Calculate")
        }
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
            )
        }
        result?.let { ratioResult ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Ratio ${ratioResult.displayRatio}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "This value is shown neutrally without appearance rankings or pressure-oriented goals.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = ratioResult.educationalNotice,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun RatioNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { candidate ->
            if (candidate.length <= 12 && candidate.all { it.isDigit() || it == '.' }) {
                onValueChange(candidate)
            }
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
    )
}
