package io.github.sanskarin.healthmetric.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.sanskarin.healthmetric.domain.HealthMetricEngine

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "HealthMetric",
    ) {
        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                HealthMetricDesktopApp()
            }
        }
    }
}

@Composable
private fun HealthMetricDesktopApp() {
    var confirmedAdult by remember { mutableStateOf(false) }
    var ageText by remember { mutableStateOf("") }
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
                .widthIn(max = 760.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "HealthMetric",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Privacy-first adult health measurement tools. Calculations stay on this device and are not uploaded.",
                style = MaterialTheme.typography.bodyLarge,
            )

            if (!confirmedAdult) {
                AdultGateCard(
                    ageText = ageText,
                    message = gateMessage,
                    onAgeChange = {
                        ageText = it.filter { character -> character.isDigit() }.take(3)
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
                                confirmedAdult = true
                                gateMessage = null
                            }
                        }
                    },
                )
            } else {
                MetricBmiCard()
                MetricWaistToHeightCard()
                Text(
                    text = "Educational screening information only. HealthMetric does not provide diagnoses, appearance scores, or body targets.",
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
private fun MetricBmiCard() {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = sanitizeDecimal(it) },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = heightText,
                    onValueChange = { heightText = sanitizeDecimal(it) },
                    label = { Text("Height (cm)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
            }
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
                                ageYears = 18,
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
private fun MetricWaistToHeightCard() {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = waistText,
                    onValueChange = { waistText = sanitizeDecimal(it) },
                    label = { Text("Waist (cm)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = heightText,
                    onValueChange = { heightText = sanitizeDecimal(it) },
                    label = { Text("Height (cm)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
            }
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
                                ageYears = 18,
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
            Spacer(modifier = Modifier.height(4.dp))
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
