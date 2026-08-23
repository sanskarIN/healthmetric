package io.github.sanskarin.healthmetric.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
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
import io.github.sanskarin.healthmetric.domain.HealthMetricEngine
import io.github.sanskarin.healthmetric.domain.UnitSystem

@Composable
fun HealthMetricCrossPlatformApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            HealthMetricContent()
        }
    }
}

@Composable
private fun HealthMetricContent() {
    var ageText by remember { mutableStateOf("") }
    var confirmedAge by remember { mutableStateOf<Int?>(null) }
    var gateMessage by remember { mutableStateOf<String?>(null) }
    var unitSystem by remember { mutableStateOf(UnitSystem.METRIC) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 720.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "HealthMetric",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Privacy-first adult health measurement tools. Calculations run locally in this app and are not uploaded by these cross-platform clients.",
                style = MaterialTheme.typography.bodyLarge,
            )

            val adultAge = confirmedAge
            if (adultAge == null) {
                AdultGateCard(
                    ageText = ageText,
                    message = gateMessage,
                    onAgeChange = {
                        ageText = MeasurementInput.sanitizeWholeNumber(it, maxLength = 3)
                        gateMessage = null
                    },
                    onContinue = {
                        val age = ageText.toIntOrNull()
                        when {
                            age == null -> gateMessage = "Enter your age to continue."
                            !HealthMetricEngine.isAdultAgeEligible(age) -> {
                                gateMessage = "These reference calculators are available only for adults age 18 or older."
                            }
                            else -> {
                                confirmedAge = age
                                gateMessage = null
                            }
                        }
                    },
                )
            } else {
                UnitSystemSelector(
                    selected = unitSystem,
                    onSelected = { unitSystem = it },
                )

                when (unitSystem) {
                    UnitSystem.METRIC -> {
                        MetricBmiCard(ageYears = adultAge)
                        MetricWaistToHeightCard(ageYears = adultAge)
                    }
                    UnitSystem.IMPERIAL -> {
                        ImperialBmiCard(ageYears = adultAge)
                        ImperialWaistToHeightCard(ageYears = adultAge)
                    }
                }

                Button(
                    onClick = {
                        confirmedAge = null
                        ageText = ""
                    },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text("Change age")
                }
                Text(
                    text = "Educational screening information only. HealthMetric does not provide diagnoses, appearance scores, or personal body targets.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun UnitSystemSelector(
    selected: UnitSystem,
    onSelected: (UnitSystem) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Measurement units",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                UnitSystemButton(
                    label = "Metric",
                    selected = selected == UnitSystem.METRIC,
                    onClick = { onSelected(UnitSystem.METRIC) },
                )
                UnitSystemButton(
                    label = "Imperial",
                    selected = selected == UnitSystem.IMPERIAL,
                    onClick = { onSelected(UnitSystem.IMPERIAL) },
                )
            }
        }
    }
}

@Composable
private fun UnitSystemButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    if (selected) {
        Button(onClick = onClick) {
            Text(label)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(label)
        }
    }
}

@Composable
private fun AdultGateCard(
    ageText: String,
    message: String?,
    onAgeChange: (String) -> Unit,
    onContinue: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Adult-use confirmation",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "HealthMetric's BMI and waist-to-height reference tools are intended for adults age 18 or older.",
            )
            OutlinedTextField(
                value = ageText,
                onValueChange = onAgeChange,
                label = { Text("Age in years") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            message?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Button(
                onClick = onContinue,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
private fun MetricBmiCard(ageYears: Int) {
    var weightText by remember { mutableStateOf("") }
    var heightText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Adult BMI — metric",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = MeasurementInput.sanitizeDecimal(it) },
                label = { Text("Weight (kg)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = heightText,
                onValueChange = { heightText = MeasurementInput.sanitizeDecimal(it) },
                label = { Text("Height (cm)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = {
                    val weight = weightText.toDoubleOrNull()
                    val height = heightText.toDoubleOrNull()
                    if (weight == null || height == null) {
                        resultText = null
                        errorText = "Enter valid numeric measurements."
                    } else {
                        runCatching {
                            HealthMetricEngine.calculateAdultMetricBmi(
                                ageYears = ageYears,
                                weightKg = weight,
                                heightCm = height,
                            )
                        }.onSuccess { summary ->
                            errorText = null
                            resultText = bmiSummaryText(
                                displayValue = summary.displayValue,
                                referenceLabel = summary.referenceLabel,
                                explanation = summary.explanation,
                                educationalNotice = summary.educationalNotice,
                            )
                        }.onFailure { error ->
                            resultText = null
                            errorText = error.message ?: "Unable to calculate with these measurements."
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Calculate BMI")
            }
            CalculationFeedback(errorText = errorText, resultText = resultText)
        }
    }
}

@Composable
private fun ImperialBmiCard(ageYears: Int) {
    var weightText by remember { mutableStateOf("") }
    var feetText by remember { mutableStateOf("") }
    var inchesText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Adult BMI — imperial",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = MeasurementInput.sanitizeDecimal(it) },
                label = { Text("Weight (lb)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = feetText,
                onValueChange = {
                    feetText = MeasurementInput.sanitizeWholeNumber(it, maxLength = 2)
                },
                label = { Text("Height (feet)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = inchesText,
                onValueChange = { inchesText = MeasurementInput.sanitizeDecimal(it) },
                label = { Text("Additional height (inches)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = {
                    val weight = weightText.toDoubleOrNull()
                    val feet = feetText.toIntOrNull()
                    val inches = inchesText.toDoubleOrNull()
                    if (weight == null || feet == null || inches == null) {
                        resultText = null
                        errorText = "Enter valid numeric measurements."
                    } else {
                        runCatching {
                            HealthMetricEngine.calculateAdultImperialBmi(
                                ageYears = ageYears,
                                weightLb = weight,
                                heightFeet = feet,
                                heightInches = inches,
                            )
                        }.onSuccess { summary ->
                            errorText = null
                            resultText = bmiSummaryText(
                                displayValue = summary.displayValue,
                                referenceLabel = summary.referenceLabel,
                                explanation = summary.explanation,
                                educationalNotice = summary.educationalNotice,
                            )
                        }.onFailure { error ->
                            resultText = null
                            errorText = error.message ?: "Unable to calculate with these measurements."
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Calculate BMI")
            }
            CalculationFeedback(errorText = errorText, resultText = resultText)
        }
    }
}

@Composable
private fun MetricWaistToHeightCard(ageYears: Int) {
    var waistText by remember { mutableStateOf("") }
    var heightText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Adult waist-to-height ratio — metric",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            OutlinedTextField(
                value = waistText,
                onValueChange = { waistText = MeasurementInput.sanitizeDecimal(it) },
                label = { Text("Waist (cm)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = heightText,
                onValueChange = { heightText = MeasurementInput.sanitizeDecimal(it) },
                label = { Text("Height (cm)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = {
                    val waist = waistText.toDoubleOrNull()
                    val height = heightText.toDoubleOrNull()
                    if (waist == null || height == null) {
                        resultText = null
                        errorText = "Enter valid numeric measurements."
                    } else {
                        runCatching {
                            HealthMetricEngine.calculateAdultMetricWaistToHeight(
                                ageYears = ageYears,
                                waistCm = waist,
                                heightCm = height,
                            )
                        }.onSuccess { summary ->
                            errorText = null
                            resultText = waistSummaryText(
                                displayValue = summary.displayValue,
                                educationalNotice = summary.educationalNotice,
                            )
                        }.onFailure { error ->
                            resultText = null
                            errorText = error.message ?: "Unable to calculate with these measurements."
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Calculate ratio")
            }
            CalculationFeedback(errorText = errorText, resultText = resultText)
        }
    }
}

@Composable
private fun ImperialWaistToHeightCard(ageYears: Int) {
    var waistText by remember { mutableStateOf("") }
    var feetText by remember { mutableStateOf("") }
    var inchesText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Adult waist-to-height ratio — imperial",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            OutlinedTextField(
                value = waistText,
                onValueChange = { waistText = MeasurementInput.sanitizeDecimal(it) },
                label = { Text("Waist (inches)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = feetText,
                onValueChange = {
                    feetText = MeasurementInput.sanitizeWholeNumber(it, maxLength = 2)
                },
                label = { Text("Height (feet)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = inchesText,
                onValueChange = { inchesText = MeasurementInput.sanitizeDecimal(it) },
                label = { Text("Additional height (inches)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = {
                    val waist = waistText.toDoubleOrNull()
                    val feet = feetText.toIntOrNull()
                    val inches = inchesText.toDoubleOrNull()
                    if (waist == null || feet == null || inches == null) {
                        resultText = null
                        errorText = "Enter valid numeric measurements."
                    } else {
                        val totalHeightInches = MeasurementInput.imperialHeightInches(
                            feet = feet,
                            additionalInches = inches,
                        )
                        runCatching {
                            HealthMetricEngine.calculateAdultImperialWaistToHeight(
                                ageYears = ageYears,
                                waistInches = waist,
                                heightInches = totalHeightInches,
                            )
                        }.onSuccess { summary ->
                            errorText = null
                            resultText = waistSummaryText(
                                displayValue = summary.displayValue,
                                educationalNotice = summary.educationalNotice,
                            )
                        }.onFailure { error ->
                            resultText = null
                            errorText = error.message ?: "Unable to calculate with these measurements."
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Calculate ratio")
            }
            CalculationFeedback(errorText = errorText, resultText = resultText)
        }
    }
}

@Composable
private fun CalculationFeedback(
    errorText: String?,
    resultText: String?,
) {
    errorText?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
        )
    }
    resultText?.let {
        HorizontalDivider()
        Text(it)
    }
}

private fun bmiSummaryText(
    displayValue: Double,
    referenceLabel: String,
    explanation: String,
    educationalNotice: String,
): String = buildString {
    append("BMI: ")
    append(displayValue)
    append("\n")
    append(referenceLabel)
    append("\n\n")
    append(explanation)
    append("\n\n")
    append(educationalNotice)
}

private fun waistSummaryText(
    displayValue: Double,
    educationalNotice: String,
): String = "Ratio: $displayValue\n\n$educationalNotice"
