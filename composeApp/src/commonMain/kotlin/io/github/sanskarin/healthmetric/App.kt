package io.github.sanskarin.healthmetric

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.sanskarin.healthmetric.domain.BmiCalculator
import io.github.sanskarin.healthmetric.domain.ImperialBodyInput
import io.github.sanskarin.healthmetric.domain.MetricBodyInput
import io.github.sanskarin.healthmetric.domain.WaistToHeightCalculator

private enum class CalculatorPage {
    BMI,
    WAIST_TO_HEIGHT,
}

private enum class MeasurementSystem {
    METRIC,
    IMPERIAL,
}

private data class CalculationOutput(
    val headline: String,
    val detail: String,
    val notice: String,
)

@Composable
fun App() {
    MaterialTheme {
        var adultConfirmed by remember { mutableStateOf(false) }

        Surface(modifier = Modifier.fillMaxSize()) {
            if (adultConfirmed) {
                HealthMetricHome()
            } else {
                AdultUseGate(onConfirm = { adultConfirmed = true })
            }
        }
    }
}

@Composable
private fun AdultUseGate(onConfirm: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().heightIn(max = 560.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "HealthMetric",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Adult educational health calculators",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "HealthMetric's BMI and waist-to-height tools are intended for adults age 18 or older. Results are educational screening information only and are not medical diagnoses, appearance scores, or personal body targets.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "If you are under 18, do not use these adult reference calculators. A qualified healthcare professional can provide age-appropriate guidance.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth()) {
                    Text("I am 18 or older")
                }
            }
        }
    }
}

@Composable
private fun HealthMetricHome() {
    var page by remember { mutableStateOf(CalculatorPage.BMI) }
    var system by remember { mutableStateOf(MeasurementSystem.METRIC) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "HealthMetric",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Private by design. Calculations run locally on this device.",
            style = MaterialTheme.typography.bodyMedium,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SelectionButton("BMI", page == CalculatorPage.BMI) { page = CalculatorPage.BMI }
            SelectionButton("Waist / height", page == CalculatorPage.WAIST_TO_HEIGHT) {
                page = CalculatorPage.WAIST_TO_HEIGHT
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SelectionButton("Metric", system == MeasurementSystem.METRIC) {
                system = MeasurementSystem.METRIC
            }
            SelectionButton("Imperial", system == MeasurementSystem.IMPERIAL) {
                system = MeasurementSystem.IMPERIAL
            }
        }

        when (page) {
            CalculatorPage.BMI -> BmiCalculatorCard(system)
            CalculatorPage.WAIST_TO_HEIGHT -> WaistToHeightCard(system)
        }

        Text("Made by the Sanskar", style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SelectionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    if (selected) {
        Button(onClick = onClick) { Text(text) }
    } else {
        OutlinedButton(onClick = onClick) { Text(text) }
    }
}

@Composable
private fun BmiCalculatorCard(system: MeasurementSystem) {
    var first by remember(system) { mutableStateOf("") }
    var second by remember(system) { mutableStateOf("") }
    var third by remember(system) { mutableStateOf("") }
    var output by remember(system) { mutableStateOf<CalculationOutput?>(null) }
    var errorMessage by remember(system) { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("BMI calculator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(
                "BMI is a population screening measure. It does not diagnose health and should not be used as an appearance goal.",
                style = MaterialTheme.typography.bodyMedium,
            )

            if (system == MeasurementSystem.METRIC) {
                MeasurementField("Weight (kg)", first) { first = it }
                MeasurementField("Height (cm)", second) { second = it }
            } else {
                MeasurementField("Weight (lb)", first) { first = it }
                MeasurementField("Height (feet)", second) { second = it }
                MeasurementField("Additional inches", third) { third = it }
            }

            Button(
                onClick = {
                    errorMessage = null
                    output = try {
                        if (system == MeasurementSystem.METRIC) {
                            val result = BmiCalculator.calculateMetric(
                                MetricBodyInput(
                                    weightKg = requiredDecimal(first, "Enter a valid weight."),
                                    heightCm = requiredDecimal(second, "Enter a valid height."),
                                ),
                            )
                            CalculationOutput(
                                headline = "BMI ${result.displayBmi}",
                                detail = "${result.band.label}. ${result.band.explanation}",
                                notice = result.educationalNotice,
                            )
                        } else {
                            val result = BmiCalculator.calculateImperial(
                                ImperialBodyInput(
                                    weightLb = requiredDecimal(first, "Enter a valid weight."),
                                    heightFeet = requiredInt(second, "Enter whole feet."),
                                    heightInches = requiredDecimal(third, "Enter valid inches."),
                                ),
                            )
                            CalculationOutput(
                                headline = "BMI ${result.displayBmi}",
                                detail = "${result.band.label}. ${result.band.explanation}",
                                notice = result.educationalNotice,
                            )
                        }
                    } catch (exception: IllegalArgumentException) {
                        errorMessage = exception.message ?: "Check the measurements and try again."
                        null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Calculate")
            }

            errorMessage?.let { ErrorCard(it) }
            output?.let { ResultCard(it) }
        }
    }
}

@Composable
private fun WaistToHeightCard(system: MeasurementSystem) {
    var waist by remember(system) { mutableStateOf("") }
    var height by remember(system) { mutableStateOf("") }
    var output by remember(system) { mutableStateOf<CalculationOutput?>(null) }
    var errorMessage by remember(system) { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Waist-to-height ratio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(
                "This is a simple adult screening measurement and cannot diagnose a health condition or define an appearance target.",
                style = MaterialTheme.typography.bodyMedium,
            )

            if (system == MeasurementSystem.METRIC) {
                MeasurementField("Waist (cm)", waist) { waist = it }
                MeasurementField("Height (cm)", height) { height = it }
            } else {
                MeasurementField("Waist (inches)", waist) { waist = it }
                MeasurementField("Height (total inches)", height) { height = it }
            }

            Button(
                onClick = {
                    errorMessage = null
                    output = try {
                        val waistValue = requiredDecimal(waist, "Enter a valid waist measurement.")
                        val heightValue = requiredDecimal(height, "Enter a valid height.")
                        val result = if (system == MeasurementSystem.METRIC) {
                            WaistToHeightCalculator.calculateMetric(waistValue, heightValue)
                        } else {
                            WaistToHeightCalculator.calculateImperial(waistValue, heightValue)
                        }
                        CalculationOutput(
                            headline = "Ratio ${result.displayRatio}",
                            detail = "Use this value only as general adult screening information and consider it alongside broader health context.",
                            notice = result.educationalNotice,
                        )
                    } catch (exception: IllegalArgumentException) {
                        errorMessage = exception.message ?: "Check the measurements and try again."
                        null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Calculate")
            }

            errorMessage?.let { ErrorCard(it) }
            output?.let { ResultCard(it) }
        }
    }
}

@Composable
private fun MeasurementField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= 16) onValueChange(it) },
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ResultCard(output: CalculationOutput) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(output.headline, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(output.detail, style = MaterialTheme.typography.bodyMedium)
            Text(output.notice, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

private fun requiredDecimal(value: String, message: String): Double =
    value.trim().replace(',', '.').toDoubleOrNull() ?: throw IllegalArgumentException(message)

private fun requiredInt(value: String, message: String): Int =
    value.trim().toIntOrNull() ?: throw IllegalArgumentException(message)
