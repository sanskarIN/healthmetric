package io.github.sanskarin.healthmetric.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.window.rememberWindowState
import io.github.sanskarin.healthmetric.domain.BmiReferenceProfile
import java.awt.Desktop
import java.net.URI

private enum class AdultGateState {
    UNDECIDED,
    ADULT,
    UNDER_18,
}

private enum class DesktopSection {
    BMI,
    WAIST_TO_HEIGHT,
    ABOUT,
}

private enum class DesktopUnitSystem {
    METRIC,
    IMPERIAL,
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "HealthMetric",
        state = rememberWindowState(width = 1080.dp, height = 760.dp),
    ) {
        HealthMetricDesktopApp()
    }
}

@Composable
private fun HealthMetricDesktopApp() {
    var darkTheme by remember { mutableStateOf(false) }
    var adultGate by remember { mutableStateOf(AdultGateState.UNDECIDED) }

    MaterialTheme(
        colorScheme = if (darkTheme) {
            androidx.compose.material3.darkColorScheme()
        } else {
            androidx.compose.material3.lightColorScheme()
        },
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                DesktopHeader(
                    darkTheme = darkTheme,
                    onDarkThemeChanged = { darkTheme = it },
                )
                HorizontalDivider()

                when (adultGate) {
                    AdultGateState.UNDECIDED -> AdultGate(
                        onAdultConfirmed = { adultGate = AdultGateState.ADULT },
                        onUnder18 = { adultGate = AdultGateState.UNDER_18 },
                    )

                    AdultGateState.UNDER_18 -> AdultReferenceUnavailable(
                        onReturn = { adultGate = AdultGateState.UNDECIDED },
                    )

                    AdultGateState.ADULT -> AdultWorkspace(
                        onResetAdultGate = { adultGate = AdultGateState.UNDECIDED },
                    )
                }
            }
        }
    }
}

@Composable
private fun DesktopHeader(
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "HealthMetric",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Privacy-first adult health measurement calculator",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Dark theme")
            Spacer(Modifier.width(10.dp))
            Switch(
                checked = darkTheme,
                onCheckedChange = onDarkThemeChanged,
            )
        }
    }
}

@Composable
private fun AdultGate(
    onAdultConfirmed: () -> Unit,
    onUnder18: () -> Unit,
) {
    CenteredContent {
        Text(
            text = "Adult-use notice",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "HealthMetric's BMI and waist-to-height reference tools are intended for adults " +
                "age 18 or older. Results are educational screening information only and are not " +
                "medical diagnoses or appearance goals.",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onAdultConfirmed) {
                Text("I am 18 or older")
            }
            OutlinedButton(onClick = onUnder18) {
                Text("I am under 18")
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "No measurement history is stored by the desktop client. Closing the app clears " +
                "entered measurements and results.",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun AdultReferenceUnavailable(onReturn: () -> Unit) {
    CenteredContent {
        Text(
            text = "Adult reference calculators unavailable",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "HealthMetric does not apply its adult BMI or waist-to-height reference tools " +
                "to people under 18.",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "The desktop client does not store this age-selection choice or any measurement data.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = onReturn) {
            Text("Return to age selection")
        }
    }
}

@Composable
private fun AdultWorkspace(onResetAdultGate: () -> Unit) {
    var section by remember { mutableStateOf(DesktopSection.BMI) }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .width(240.dp)
                .fillMaxHeight()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SectionButton(
                label = "BMI calculator",
                selected = section == DesktopSection.BMI,
                onClick = { section = DesktopSection.BMI },
            )
            SectionButton(
                label = "Waist-to-height",
                selected = section == DesktopSection.WAIST_TO_HEIGHT,
                onClick = { section = DesktopSection.WAIST_TO_HEIGHT },
            )
            SectionButton(
                label = "About & evidence",
                selected = section == DesktopSection.ABOUT,
                onClick = { section = DesktopSection.ABOUT },
            )
            Spacer(Modifier.weight(1f))
            OutlinedButton(
                onClick = onResetAdultGate,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Reset adult-use choice")
            }
        }

        VerticalDivider(modifier = Modifier.fillMaxHeight())

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            when (section) {
                DesktopSection.BMI -> BmiDesktopScreen()
                DesktopSection.WAIST_TO_HEIGHT -> WaistToHeightDesktopScreen()
                DesktopSection.ABOUT -> AboutDesktopScreen()
            }
        }
    }
}

@Composable
private fun SectionButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(label)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(label)
        }
    }
}

@Composable
private fun BmiDesktopScreen() {
    var unitSystem by remember { mutableStateOf(DesktopUnitSystem.METRIC) }
    var metricWeight by remember { mutableStateOf("") }
    var metricHeight by remember { mutableStateOf("") }
    var imperialWeight by remember { mutableStateOf("") }
    var heightFeet by remember { mutableStateOf("") }
    var heightInches by remember { mutableStateOf("") }
    var outcome by remember { mutableStateOf<DesktopCalculationOutcome?>(null) }

    ScrollableScreen {
        Text(
            text = "Adult BMI calculator",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "BMI is a population screening measure. HealthMetric does not use it as a " +
                "diagnosis, appearance score, or personal body target.",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(20.dp))

        UnitSelector(
            selected = unitSystem,
            onSelected = {
                unitSystem = it
                outcome = null
            },
        )
        Spacer(Modifier.height(20.dp))

        when (unitSystem) {
            DesktopUnitSystem.METRIC -> {
                OutlinedTextField(
                    value = metricWeight,
                    onValueChange = { metricWeight = it },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = metricHeight,
                    onValueChange = { metricHeight = it },
                    label = { Text("Height (cm)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = {
                        outcome = DesktopCalculations.metricBmi(
                            weightKg = metricWeight,
                            heightCm = metricHeight,
                        )
                    },
                ) {
                    Text("Calculate BMI")
                }
            }

            DesktopUnitSystem.IMPERIAL -> {
                OutlinedTextField(
                    value = imperialWeight,
                    onValueChange = { imperialWeight = it },
                    label = { Text("Weight (lb)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = heightFeet,
                        onValueChange = { heightFeet = it },
                        label = { Text("Height feet") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = heightInches,
                        onValueChange = { heightInches = it },
                        label = { Text("Remaining inches") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = {
                        outcome = DesktopCalculations.imperialBmi(
                            weightLb = imperialWeight,
                            feet = heightFeet,
                            inches = heightInches,
                        )
                    },
                ) {
                    Text("Calculate BMI")
                }
            }
        }

        outcome?.let {
            Spacer(Modifier.height(24.dp))
            CalculationResult(it)
        }
    }
}

@Composable
private fun WaistToHeightDesktopScreen() {
    var unitSystem by remember { mutableStateOf(DesktopUnitSystem.METRIC) }
    var metricWaist by remember { mutableStateOf("") }
    var metricHeight by remember { mutableStateOf("") }
    var imperialWaist by remember { mutableStateOf("") }
    var heightFeet by remember { mutableStateOf("") }
    var heightInches by remember { mutableStateOf("") }
    var outcome by remember { mutableStateOf<DesktopCalculationOutcome?>(null) }

    ScrollableScreen {
        Text(
            text = "Adult waist-to-height calculator",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "This tool reports the mathematical waist-to-height ratio for adult educational " +
                "screening. It does not rank appearance or set a body target.",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(20.dp))

        UnitSelector(
            selected = unitSystem,
            onSelected = {
                unitSystem = it
                outcome = null
            },
        )
        Spacer(Modifier.height(20.dp))

        when (unitSystem) {
            DesktopUnitSystem.METRIC -> {
                OutlinedTextField(
                    value = metricWaist,
                    onValueChange = { metricWaist = it },
                    label = { Text("Waist measurement (cm)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = metricHeight,
                    onValueChange = { metricHeight = it },
                    label = { Text("Height (cm)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = {
                        outcome = DesktopCalculations.metricWaistToHeight(
                            waistCm = metricWaist,
                            heightCm = metricHeight,
                        )
                    },
                ) {
                    Text("Calculate ratio")
                }
            }

            DesktopUnitSystem.IMPERIAL -> {
                OutlinedTextField(
                    value = imperialWaist,
                    onValueChange = { imperialWaist = it },
                    label = { Text("Waist measurement (in)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = heightFeet,
                        onValueChange = { heightFeet = it },
                        label = { Text("Height feet") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = heightInches,
                        onValueChange = { heightInches = it },
                        label = { Text("Remaining inches") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = {
                        outcome = DesktopCalculations.imperialWaistToHeight(
                            waistInches = imperialWaist,
                            heightFeet = heightFeet,
                            heightInches = heightInches,
                        )
                    },
                ) {
                    Text("Calculate ratio")
                }
            }
        }

        outcome?.let {
            Spacer(Modifier.height(24.dp))
            CalculationResult(it)
        }
    }
}

@Composable
private fun UnitSelector(
    selected: DesktopUnitSystem,
    onSelected: (DesktopUnitSystem) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selected == DesktopUnitSystem.METRIC,
                onClick = { onSelected(DesktopUnitSystem.METRIC) },
            )
            Text("Metric")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selected == DesktopUnitSystem.IMPERIAL,
                onClick = { onSelected(DesktopUnitSystem.IMPERIAL) },
            )
            Text("Imperial")
        }
    }
}

@Composable
private fun CalculationResult(outcome: DesktopCalculationOutcome) {
    when (outcome) {
        is DesktopCalculationOutcome.Failure -> {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        text = "Check the entered measurements",
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(outcome.message)
                }
            }
        }

        is DesktopCalculationOutcome.Success -> {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        text = outcome.valueLabel,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = outcome.contextLabel,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(outcome.explanation)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = outcome.notice,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutDesktopScreen() {
    val source = BmiReferenceProfile.AdultGeneralReference.source

    ScrollableScreen {
        Text(
            text = "About HealthMetric",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "HealthMetric is an open-source, privacy-first adult health measurement calculator. " +
                "The desktop client uses the same tested Kotlin Multiplatform calculation core as " +
                "the Android application.",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(22.dp))

        Text(
            text = "Desktop privacy",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "The desktop client does not persist measurement inputs, results, or the adult-use " +
                "selection. Closing the app clears the in-memory screen state. External links open " +
                "only after an explicit button press.",
        )
        Spacer(Modifier.height(22.dp))

        Text(
            text = "Adult BMI evidence reference",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(8.dp))
        Text("${source.publisher}: ${source.title}")
        Text("Reviewed in project metadata: ${source.reviewedOnIsoDate}")
        Text(source.note)
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = { openExternal(source.url) }) {
            Text("Open evidence source")
        }
        Spacer(Modifier.height(22.dp))

        Text(
            text = "Project & support",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(8.dp))
        Text("Made by the Sanskar")
        Text("Business: sanskarin@outlook.in")
        Text("Business: sanskarin.business@gmail.com")
        Text("Support: supportramsandesh@gmail.com")
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = { openExternal("https://github.com/sanskarIN/healthmetric") },
            ) {
                Text("GitHub repository")
            }
            OutlinedButton(
                onClick = { openExternal("https://buymeacoffee.com/sanskarIN") },
            ) {
                Text("Buy Me a Coffee")
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "MIT licensed. Core calculations work offline and do not require an account.",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun CenteredContent(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .width(720.dp)
                .padding(32.dp),
            content = content,
        )
    }
}

@Composable
private fun ScrollableScreen(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 26.dp),
        content = content,
    )
}

private fun openExternal(url: String) {
    runCatching {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI(url))
        }
    }
}
