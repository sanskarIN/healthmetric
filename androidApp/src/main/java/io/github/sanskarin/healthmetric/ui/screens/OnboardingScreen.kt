package io.github.sanskarin.healthmetric.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import io.github.sanskarin.healthmetric.R
import io.github.sanskarin.healthmetric.ui.theme.HealthMetricSpacing

@Composable
fun OnboardingScreen(
    onAdultConfirmed: () -> Unit,
    onUnder18: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = HealthMetricSpacing.xl,
                    vertical = HealthMetricSpacing.hero,
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Outlined.HealthAndSafety,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(HealthMetricSpacing.lg))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(Modifier.height(HealthMetricSpacing.sm))
            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(HealthMetricSpacing.xl))
            Text(
                text = stringResource(R.string.onboarding_privacy_intro),
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.height(HealthMetricSpacing.sm))
            Text(
                text = stringResource(R.string.onboarding_offline_intro),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(HealthMetricSpacing.xl))
            Button(
                onClick = onAdultConfirmed,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.onboarding_adult_confirm))
            }
            Spacer(Modifier.height(HealthMetricSpacing.sm))
            OutlinedButton(
                onClick = onUnder18,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.onboarding_under_18))
            }
        }
    }
}

@Composable
fun AdultOnlyScreen(onReturnToAgeSelection: (() -> Unit)? = null) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(HealthMetricSpacing.xl),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.adult_only_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(Modifier.height(HealthMetricSpacing.sm))
            Text(
                text = stringResource(R.string.adult_only_body),
                style = MaterialTheme.typography.bodyLarge,
            )
            onReturnToAgeSelection?.let { onReturn ->
                Spacer(Modifier.height(HealthMetricSpacing.lg))
                OutlinedButton(
                    onClick = onReturn,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.adult_only_return))
                }
            }
        }
    }
}
