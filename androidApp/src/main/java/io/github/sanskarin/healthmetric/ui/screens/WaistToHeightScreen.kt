package io.github.sanskarin.healthmetric.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import io.github.sanskarin.healthmetric.domain.WaistToHeightCalculator
import io.github.sanskarin.healthmetric.domain.WaistToHeightResult
import io.github.sanskarin.healthmetric.ui.components.MeasurementNumberField
import io.github.sanskarin.healthmetric.ui.format.LocalizedNumbers
import io.github.sanskarin.healthmetric.ui.testing.HealthMetricTestTags
import io.github.sanskarin.healthmetric.ui.theme.HealthMetricSpacing

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

    val invalidWaist = stringResource(R.string.invalid_waist)
    val invalidHeight = stringResource(R.string.invalid_height)
    val measurementError = stringResource(R.string.measurement_error)

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(HealthMetricSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(HealthMetricSpacing.md),
    ) {
        Text(
            text = stringResource(R.string.waist_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.waist_intro),
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(HealthMetricSpacing.xs)) {
            FilterChip(
                selected = useMetric,
                onClick = { useMetric = true; result = null; error = null },
                label = { Text(stringResource(R.string.centimetres)) },
            )
            FilterChip(
                selected = !useMetric,
                onClick = { useMetric = false; result = null; error = null },
                label = { Text(stringResource(R.string.inches)) },
            )
        }
        MeasurementNumberField(
            value = waist,
            onValueChange = { waist = it },
            label = if (useMetric) {
                stringResource(R.string.waist_cm)
            } else {
                stringResource(R.string.waist_inches)
            },
            testTag = HealthMetricTestTags.WAIST_VALUE,
        )
        MeasurementNumberField(
            value = height,
            onValueChange = { height = it },
            label = if (useMetric) {
                stringResource(R.string.height_cm)
            } else {
                stringResource(R.string.height_inches)
            },
            testTag = HealthMetricTestTags.WAIST_HEIGHT,
        )
        Button(
            onClick = {
                val calculation = runCatching {
                    val waistValue = LocalizedNumbers.parseDecimal(waist) ?: error(invalidWaist)
                    val heightValue = LocalizedNumbers.parseDecimal(height) ?: error(invalidHeight)
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
                    error = it.message ?: measurementError
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(HealthMetricTestTags.WAIST_CALCULATE),
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
            )
        }
        result?.let { ratioResult ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(HealthMetricTestTags.WAIST_RESULT),
            ) {
                Column(
                    modifier = Modifier.padding(HealthMetricSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(HealthMetricSpacing.xs),
                ) {
                    Text(
                        text = stringResource(
                            R.string.ratio_result,
                            LocalizedNumbers.format(ratioResult.displayRatio, maximumFractionDigits = 2),
                        ),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = stringResource(R.string.ratio_neutral_note),
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
