package io.github.sanskarin.healthmetric.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
                        ageText = it.filter(Char::isDigit).take(3)
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
                MetricBmiCard(ageYears = adultAge)
                MetricWaistToHeightCard(ageYears = adultAge)
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
                onValueChange = { weightText = sanitizeDecimal(it) },
                label = { Text("Weight (kg)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = heightText,
                onValueChange = { heightText = sanitizeDecimal(it) },
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
                            resultText = buildString {
                                append("BMI: ")
                                append(summary.displayValue)
                                append("\n")
                                append(summary.referenceLabel)
                                append("\n\n")
                                append(summary.explanation)
                                append("\n\n")
                                append(summary.educationalNotice)
                            }
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
                onValueChange = { waistText = sanitizeDecimal(it) },
                label = { Text("Waist (cm)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = heightText,
                onValueChange = { heightText = sanitizeDecimal(it) },
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
                            resultText = "Ratio: ${summary.displayValue}\n\n${summary.educationalNotice}"
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
    }
}

private fun sanitizeDecimal(value: String): String {
    var separatorSeen = false
    return buildString {
        value.forEach { character ->
            when {
                character.isDigit() -> append(character)
                (character == '.' || character == ',') && !separatorSeen -> {
                    append('.')
                    separatorSeen = true
                }
            }
        }
    }
}
