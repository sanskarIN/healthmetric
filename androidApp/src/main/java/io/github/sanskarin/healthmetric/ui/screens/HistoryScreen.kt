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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.sanskarin.healthmetric.R
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
    var confirmErase by remember { mutableStateOf(false) }
    val selectedKind = CalculatorKind.valueOf(selectedKindName)
    val filtered = history.filter { it.calculator == selectedKind }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.history_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .semantics { heading() },
            )
            Text(
                text = if (historyEnabled) {
                    stringResource(R.string.history_enabled_message)
                } else {
                    stringResource(R.string.history_disabled_message)
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
                    label = { Text(stringResource(R.string.history_bmi)) },
                )
                FilterChip(
                    selected = selectedKind == CalculatorKind.WAIST_TO_HEIGHT,
                    onClick = { selectedKindName = CalculatorKind.WAIST_TO_HEIGHT.name },
                    label = { Text(stringResource(R.string.history_waist)) },
                )
            }
        }
        if (filtered.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (selectedKind == CalculatorKind.BMI) {
                            stringResource(R.string.history_empty_bmi)
                        } else {
                            stringResource(R.string.history_empty_waist)
                        },
                        modifier = Modifier.padding(18.dp),
                    )
                }
            }
        } else {
            item {
                MeasurementChart(
                    entries = filtered.take(20).reversed(),
                    label = if (selectedKind == CalculatorKind.BMI) {
                        stringResource(R.string.history_item_bmi)
                    } else {
                        stringResource(R.string.history_item_waist)
                    },
                )
            }
            items(filtered, key = HistoryEntry::id) { entry ->
                HistoryCard(entry)
            }
        }
        if (history.isNotEmpty()) {
            item {
                Button(
                    onClick = { confirmErase = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                ) {
                    Text(stringResource(R.string.erase_history))
                }
            }
        }
    }

    if (confirmErase) {
        AlertDialog(
            onDismissRequest = { confirmErase = false },
            title = { Text(stringResource(R.string.erase_history_title)) },
            text = { Text(stringResource(R.string.erase_history_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmErase = false
                        onDeleteAll()
                    },
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmErase = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
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
        stringResource(
            R.string.chart_summary_one,
            label,
            formatValue(values.first()),
        )
    } else {
        stringResource(
            R.string.chart_summary_many,
            label,
            values.size,
            formatValue(values.first()),
            formatValue(values.last()),
            formatValue(minValue),
            formatValue(maxValue),
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.measurement_history),
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
                text = stringResource(R.string.chart_accessibility_note),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun HistoryCard(entry: HistoryEntry) {
    val label = when (entry.calculator) {
        CalculatorKind.BMI -> stringResource(R.string.history_item_bmi)
        CalculatorKind.WAIST_TO_HEIGHT -> stringResource(R.string.history_item_waist)
    }
    val formattedTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
        .format(Date(entry.timestampEpochMillis))

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(R.string.history_item_value, label, formatValue(entry.value)),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(entry.summary, style = MaterialTheme.typography.bodyMedium)
            Text(formattedTime, style = MaterialTheme.typography.labelMedium)
        }
    }
}

private fun formatValue(value: Double): String = ((value * 100.0).toInt() / 100.0).toString()
