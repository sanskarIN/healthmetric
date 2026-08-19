package io.github.sanskarin.healthmetric.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.sanskarin.healthmetric.domain.BmiCalculator
import io.github.sanskarin.healthmetric.domain.BmiResult
import io.github.sanskarin.healthmetric.domain.ImperialBodyInput
import io.github.sanskarin.healthmetric.domain.MetricBodyInput
import io.github.sanskarin.healthmetric.domain.UnitSystem

@Composable
fun CalculatorScreen(
    historyEnabled: Boolean,
    onRecord: (BmiResult) -> Unit,
) {
    var unitSystemName by rememberSaveable { mutableStateOf(UnitSystem.METRIC.name) }
    val unitSystem = UnitSystem.valueOf(unitSystemName)
    var weight by rememberSaveable { mutableStateOf("") }
    var heightCm by rememberSaveable { mutableStateOf("") }
    var feet by rememberSaveable { mutableStateOf("") }
    var inches by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf<BmiResult?>(null) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Adult BMI calculator",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = "BMI is a population screening measure. HealthMetric does not use it as a diagnosis, appearance score, or personal body goal.",
            style = MaterialTheme.typography.bodyMedium,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = unitSystem == UnitSystem.METRIC,
                onClick = {
                    unitSystemName = UnitSystem.METRIC.name
                    result = null
                    error = null
                },
                label = { Text("Metric") },
            )
            FilterChip(
                selected = unitSystem == UnitSystem.IMPERIAL,
                onClick = {
                    unitSystemName = UnitSystem.IMPERIAL.name
                    result = null
                    error = null
                },
                label = { Text("Imperial") },
            )
        }

        if (unitSystem == UnitSystem.METRIC) {
            NumberField(
                value = weight,
                onValueChange = { weight = it },
                label = "Weight (kg)",
            )
            NumberField(
                value = heightCm,
                onValueChange = { heightCm = it },
                label = "Height (cm)",
            )
        } else {
            NumberField(
                value = weight,
                onValueChange = { weight = it },
                label = "Weight (lb)",
            )
            NumberField(
                value = feet,
                onValueChange = { feet = it.filter(Char::isDigit).take(1) },
                label = "Height (feet)",
            )
            NumberField(
                value = inches,
                onValueChange = { inches = it },
                label = "Additional height (inches)",
            )
        }

        Button(
            onClick = {
                val calculation = runCatching {
                    when (unitSystem) {
                        UnitSystem.METRIC -> BmiCalculator.calculateMetric(
                            MetricBodyInput(
                                weightKg = weight.toDoubleOrNull()
                                    ?: error("Enter a valid weight."),
                                heightCm = heightCm.toDoubleOrNull()
                                    ?: error("Enter a valid height."),
                            ),
                        )
                        UnitSystem.IMPERIAL -> BmiCalculator.calculateImperial(
                            ImperialBodyInput(
                                weightLb = weight.toDoubleOrNull()
                                    ?: error("Enter a valid weight."),
                                heightFeet = feet.toIntOrNull()
                                    ?: error("Enter valid feet."),
                                heightInches = inches.toDoubleOrNull()
                                    ?: error("Enter valid inches."),
                            ),
                        )
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
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        result?.let { bmiResult ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "BMI ${bmiResult.displayBmi}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = bmiResult.band.label,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(bmiResult.band.explanation)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = bmiResult.educationalNotice,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "Reference: ${bmiResult.reference.source.publisher} — ${bmiResult.reference.source.title}",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun NumberField(
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
