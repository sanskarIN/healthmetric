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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import io.github.sanskarin.healthmetric.R
import io.github.sanskarin.healthmetric.domain.BmiCalculator
import io.github.sanskarin.healthmetric.domain.BmiResult
import io.github.sanskarin.healthmetric.domain.ImperialBodyInput
import io.github.sanskarin.healthmetric.domain.MetricBodyInput
import io.github.sanskarin.healthmetric.domain.UnitSystem
import io.github.sanskarin.healthmetric.ui.components.MeasurementNumberField
import io.github.sanskarin.healthmetric.ui.format.LocalizedNumbers
import io.github.sanskarin.healthmetric.ui.testing.HealthMetricTestTags
import io.github.sanskarin.healthmetric.ui.theme.HealthMetricSpacing

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
            .padding(HealthMetricSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(HealthMetricSpacing.md),
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

        Row(horizontalArrangement = Arrangement.spacedBy(HealthMetricSpacing.xs)) {
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
            MeasurementNumberField(
                value = weight,
                onValueChange = { weight = it },
                label = stringResource(R.string.weight_kg),
                testTag = HealthMetricTestTags.BMI_WEIGHT,
            )
            MeasurementNumberField(
                value = heightCm,
                onValueChange = { heightCm = it },
                label = stringResource(R.string.height_cm),
                testTag = HealthMetricTestTags.BMI_HEIGHT_CM,
            )
        } else {
            MeasurementNumberField(
                value = weight,
                onValueChange = { weight = it },
                label = stringResource(R.string.weight_lb),
                testTag = HealthMetricTestTags.BMI_WEIGHT,
            )
            MeasurementNumberField(
                value = feet,
                onValueChange = { feet = it },
                label = stringResource(R.string.height_feet),
                wholeNumbersOnly = true,
                maxLength = 1,
                testTag = HealthMetricTestTags.BMI_HEIGHT_FEET,
            )
            MeasurementNumberField(
                value = inches,
                onValueChange = { inches = it },
                label = stringResource(R.string.height_inches_additional),
                testTag = HealthMetricTestTags.BMI_HEIGHT_INCHES,
            )
        }

        Button(
            onClick = {
                val calculation = runCatching {
                    when (unitSystem) {
                        UnitSystem.METRIC -> BmiCalculator.calculateMetric(
                            MetricBodyInput(
                                weightKg = LocalizedNumbers.parseDecimal(weight) ?: error(invalidWeight),
                                heightCm = LocalizedNumbers.parseDecimal(heightCm) ?: error(invalidHeight),
                            ),
                        )
                        UnitSystem.IMPERIAL -> BmiCalculator.calculateImperial(
                            ImperialBodyInput(
                                weightLb = LocalizedNumbers.parseDecimal(weight) ?: error(invalidWeight),
                                heightFeet = feet.toIntOrNull() ?: error(invalidFeet),
                                heightInches = LocalizedNumbers.parseDecimal(inches) ?: error(invalidInches),
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
            modifier = Modifier
                .fillMaxWidth()
                .testTag(HealthMetricTestTags.BMI_CALCULATE),
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(HealthMetricTestTags.BMI_RESULT),
            ) {
                Column(
                    modifier = Modifier.padding(HealthMetricSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(HealthMetricSpacing.xs),
                ) {
                    Text(
                        text = stringResource(
                            R.string.bmi_result,
                            LocalizedNumbers.format(bmiResult.displayBmi, maximumFractionDigits = 1),
                        ),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = bmiResult.band.label,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(bmiResult.band.explanation)
                    Spacer(Modifier.height(HealthMetricSpacing.xxs))
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
