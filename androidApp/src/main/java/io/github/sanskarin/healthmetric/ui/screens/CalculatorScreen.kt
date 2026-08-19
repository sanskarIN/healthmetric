package io.github.sanskarin.healthmetric.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.sanskarin.healthmetric.R
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
    var result by remember { mutableStateOf<BmiResult?>(null) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    val invalidWeight = stringResource(R.string.invalid_weight)
    val invalidHeight = stringResource(R.string.invalid_height)
    val invalidFeet = stringResource(R.string.invalid_feet)
    val invalidInches = stringResource(R.string.invalid_inches)
    val measurementError = stringResource(R.string.measurement_error)

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.bmi_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.bmi_intro),
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
                label = { Text(stringResource(R.string.metric)) },
            )
            FilterChip(
                selected = unitSystem == UnitSystem.IMPERIAL,
                onClick = {
                    unitSystemName = UnitSystem.IMPERIAL.name
                    result = null
                    error = null
                },
                label = { Text(stringResource(R.string.imperial)) },
            )
        }

        if (unitSystem == UnitSystem.METRIC) {
            NumberField(
                value = weight,
                onValueChange = { weight = it },
                label = stringResource(R.string.weight_kg),
            )
            NumberField(
                value = heightCm,
                onValueChange = { heightCm = it },
                label = stringResource(R.string.height_cm),
            )
        } else {
            NumberField(
                value = weight,
                onValueChange = { weight = it },
                label = stringResource(R.string.weight_lb),
            )
            NumberField(
                value = feet,
                onValueChange = { feet = it.filter(Char::isDigit).take(1) },
                label = stringResource(R.string.height_feet),
            )
            NumberField(
                value = inches,
                onValueChange = { inches = it },
                label = stringResource(R.string.height_inches_additional),
            )
        }

        Button(
            onClick = {
                val calculation = runCatching {
                    when (unitSystem) {
                        UnitSystem.METRIC -> BmiCalculator.calculateMetric(
                            MetricBodyInput(
                                weightKg = weight.toDoubleOrNull() ?: error(invalidWeight),
                                heightCm = heightCm.toDoubleOrNull() ?: error(invalidHeight),
                            ),
                        )
                        UnitSystem.IMPERIAL -> BmiCalculator.calculateImperial(
                            ImperialBodyInput(
                                weightLb = weight.toDoubleOrNull() ?: error(invalidWeight),
                                heightFeet = feet.toIntOrNull() ?: error(invalidFeet),
                                heightInches = inches.toDoubleOrNull() ?: error(invalidInches),
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
                    error = it.message ?: measurementError
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                if (historyEnabled) {
                    stringResource(R.string.calculate_save)
                } else {
                    stringResource(R.string.calculate)
                },
            )
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
                        text = stringResource(R.string.bmi_result, bmiResult.displayBmi.toString()),
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
                        text = stringResource(
                            R.string.reference_source,
                            bmiResult.reference.source.publisher,
                            bmiResult.reference.source.title,
                        ),
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
