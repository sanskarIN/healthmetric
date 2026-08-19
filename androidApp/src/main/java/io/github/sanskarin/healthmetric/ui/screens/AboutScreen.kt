package io.github.sanskarin.healthmetric.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.sanskarin.healthmetric.BuildConfig

@Composable
fun AboutScreen(onOpenLink: (String) -> Unit) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = "About HealthMetric",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.semantics { heading() },
        )
        Text("Version ${BuildConfig.VERSION_NAME}")
        Text(
            text = "HealthMetric is an open-source, offline-first adult BMI and health measurement calculator. Its outputs are educational and non-diagnostic.",
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = "Made by the Sanskar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text("License: MIT")

        LinkButton("GitHub — sanskarIN", "https://github.com/sanskarIN", onOpenLink)
        LinkButton("Buy Me a Coffee — sanskarIN", "https://buymeacoffee.com/sanskarIN", onOpenLink)
        LinkButton("Business — sanskarin@outlook.in", "mailto:sanskarin@outlook.in", onOpenLink)
        LinkButton("Business — sanskarin.business@gmail.com", "mailto:sanskarin.business@gmail.com", onOpenLink)
        LinkButton("Support — supportramsandesh@gmail.com", "mailto:supportramsandesh@gmail.com", onOpenLink)

        Text(
            text = "Privacy: calculations can run without network access. History can be disabled, exported, restored, or deleted. The app does not include advertising trackers.",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun LinkButton(label: String, url: String, onOpenLink: (String) -> Unit) {
    OutlinedButton(
        onClick = { onOpenLink(url) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(label)
    }
}
