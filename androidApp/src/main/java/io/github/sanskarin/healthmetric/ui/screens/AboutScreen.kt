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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.sanskarin.healthmetric.BuildConfig
import io.github.sanskarin.healthmetric.R

@Composable
fun AboutScreen(onOpenLink: (String) -> Unit) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = stringResource(R.string.about_healthmetric),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.semantics { heading() },
        )
        Text(stringResource(R.string.about_version, BuildConfig.VERSION_NAME))
        Text(
            text = stringResource(R.string.about_description),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.made_by),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(stringResource(R.string.license_mit))

        LinkButton(stringResource(R.string.github_sanskar), "https://github.com/sanskarIN", onOpenLink)
        LinkButton(stringResource(R.string.bmc_sanskar), "https://buymeacoffee.com/sanskarIN", onOpenLink)
        LinkButton(stringResource(R.string.business_outlook), "mailto:sanskarin@outlook.in", onOpenLink)
        LinkButton(stringResource(R.string.business_gmail), "mailto:sanskarin.business@gmail.com", onOpenLink)
        LinkButton(stringResource(R.string.support_email), "mailto:supportramsandesh@gmail.com", onOpenLink)

        Text(
            text = stringResource(R.string.about_privacy),
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
