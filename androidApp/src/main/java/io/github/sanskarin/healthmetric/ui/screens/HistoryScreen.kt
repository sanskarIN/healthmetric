package io.github.sanskarin.healthmetric.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.sanskarin.healthmetric.data.CalculatorKind
import io.github.sanskarin.healthmetric.data.HistoryEntry
import java.text.DateFormat
import java.util.Date

@Composable
fun HistoryScreen(
    history: List<HistoryEntry>,
    historyEnabled: Boolean,
    onDeleteAll: () -> Unit,
) {
    var selectedKindName by rememberSaveable { mutableStateOf(CalculatorKind.BMI.name) }
    val selectedKind = CalculatorKind.valueOf(selectedKindName)
    val filtered = history.filter { it.calculator == selectedKind }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Local history",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .semantics { heading() },
            )
            Text(
                text = if (historyEnabled) {
                    "History is stored only on this device and can be disabled or erased at any time."
                } else {
                    "History saving is disabled. Existing local entries remain until you erase them."
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedKind == CalculatorKind.BMI,
                    onClick = { selectedKindName = CalculatorKind.BMI.name },
                    label = { Text("BMI") },
                )
                FilterChip(
                    selected = selectedKind == CalculatorKind.WAIST_TO_HEIGHT,
                    onClick = { selectedKindName = CalculatorKind.WAIST_TO_HEIGHT.name },
                    label = { Text("Waist ratio") },
                )
            }
        }
        if (filtered.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No saved ${if (selectedKind == CalculatorKind.BMI) "BMI" else "waist ratio"} measurements yet.",
                        modifier = Modifier.padding(18.dp),
                    )
                }
            }
        } else {
            item {
                MeasurementChart(
                    entries = filtered.take(20).reversed(),
                    label = if (selectedKind == CalculatorKind.BMI) "BMI" else "waist-to-height ratio",
                )
            }
            items(filtered, key = HistoryEntry::id) { entry ->
                HistoryCard(entry)
            }
        }
        if (history.isNotEmpty()) {
            item {
                Button(
                    onClick = onDeleteAll,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                ) {
                    Text("Erase all local history")
                }
            }
        }
    }
}

@Composable
private fun MeasurementChart(entries: List<HistoryEntry>, label: String) {
    val lineColor = MaterialTheme.colorScheme.primary
    val axisColor = MaterialTheme.colorScheme.outline
    val values = entries.map { it.value }
    val minValue = values.minOrNull() ?: 0.0
    val maxValue = values.maxOrNull() ?: 1.0
    val range = (maxValue - minValue).takeIf { it > 0.0001 } ?: 1.0
    val summary = if (values.size == 1) {
        "$label history chart with one value: ${formatValue(values.first())}."
    } else {
        "$label history chart with ${values.size} values, from ${formatValue(values.first())} to ${formatValue(values.last())}. Minimum ${formatValue(minValue)}, maximum ${formatValue(maxValue)}."
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Measurement history",
                style = MaterialTheme.typography.titleMedium,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(top = 12.dp)
                    .semantics { contentDescription = summary },
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawLine(
                        color = axisColor,
                        start = Offset(0f, size.height - 1f),
                        end = Offset(size.width, size.height - 1f),
                        strokeWidth = 1f,
                    )
                    if (values.size == 1) {
                        drawCircle(
                            color = lineColor,
                            radius = 6f,
                            center = Offset(size.width / 2f, size.height / 2f),
                        )
                    } else {
                        val points = values.mapIndexed { index, value ->
                            val x = index.toFloat() / values.lastIndex.toFloat() * size.width
                            val normalized = ((value - minValue) / range).toFloat()
                            val y = size.height - (normalized * size.height)
                            Offset(x, y)
                        }
                        points.zipWithNext().forEach { (start, end) ->
                            drawLine(
                                color = lineColor,
                                start = start,
                                end = end,
                                strokeWidth = 4f,
                            )
                        }
                        points.forEach { point ->
                            drawCircle(color = lineColor, radius = 5f, center = point)
                        }
                    }
                }
            }
            Text(
                text = "Chart values are also summarized for screen readers; color is not used to assign health meaning.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun HistoryCard(entry: HistoryEntry) {
    val label = when (entry.calculator) {
        CalculatorKind.BMI -> "BMI"
        CalculatorKind.WAIST_TO_HEIGHT -> "Waist-to-height ratio"
    }
    val formattedTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
        .format(Date(entry.timestampEpochMillis))

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "$label ${formatValue(entry.value)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(entry.summary, style = MaterialTheme.typography.bodyMedium)
            Text(formattedTime, style = MaterialTheme.typography.labelMedium)
        }
    }
}

private fun formatValue(value: Double): String = ((value * 100.0).toInt() / 100.0).toString()
