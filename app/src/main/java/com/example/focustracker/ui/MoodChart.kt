package com.example.focustracker.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.focustracker.ui.theme.FocusTrackerTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class MoodEntry(
    val date: LocalDate,
    val mood: Int,
    val notes: String = ""
)

enum class ChartType {
    WEEKLY, MONTHLY, YEARLY
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodAnalyticsScreen(
    onBackPress: () -> Unit
) {
    var selectedChartType by remember { mutableStateOf(ChartType.WEEKLY) }

    val moodEntries = remember {
        generateSampleMoodData()
    }

    val filteredEntries = remember(selectedChartType) {
        when (selectedChartType) {
            ChartType.WEEKLY -> moodEntries.takeLast(7)
            ChartType.MONTHLY -> moodEntries.takeLast(30)
            ChartType.YEARLY -> moodEntries.takeLast(365)
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val gradientColors = listOf(
        primaryColor.copy(alpha = 0.1f),
        primaryColor.copy(alpha = 0.3f),
        primaryColor.copy(alpha = 0.1f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(gradientColors)
            )
    ) {
        Text(
            text = "This section is under development. This is just a sample.",
            style = TextStyle(
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red.copy(alpha = 0.6f)
            ),
            modifier = Modifier
                .rotate(25f)
                .background(Color.White.copy(alpha = 0.5f))
                .zIndex(1f)
                .padding(10.dp)
                .align(Alignment.Center)
        )
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Mood Analytics",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPress,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ChartTypeSelector(
                        selectedType = selectedChartType,
                        onTypeSelected = { selectedChartType = it }
                    )
                }

                item {
                    MoodLineChart(
                        entries = filteredEntries,
                        chartType = selectedChartType
                    )
                }

                item {
                    MoodStatistics(entries = filteredEntries)
                }

                item {
                    MoodDistributionChart(entries = filteredEntries)
                }

                item {
                    RecentMoodEntries(entries = moodEntries.takeLast(5))
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ChartTypeSelector(
    selectedType: ChartType,
    onTypeSelected: (ChartType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ChartType.values().forEach { type ->
                val isSelected = selectedType == type
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTypeSelected(type) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (isSelected) 8.dp else 2.dp
                    )
                ) {
                    Text(
                        text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(12.dp),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }

                if (type != ChartType.values().last()) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
private fun MoodLineChart(
    entries: List<MoodEntry>,
    chartType: ChartType
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000), label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Mood Trend",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (entries.isEmpty()) return@Canvas

                val width = size.width
                val height = size.height
                val padding = 40.dp.toPx()

                val chartWidth = width - padding * 2
                val chartHeight = height - padding * 2

                // Draw Y-axis labels (mood scale 1-10)
                for (i in 1..10) {
                    val y = height - padding - (i - 1) * (chartHeight / 9)
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.3f),
                        start = Offset(padding, y),
                        end = Offset(width - padding, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw mood line
                if (entries.size > 1) {
                    val path = Path()
                    val gradientPath = Path()

                    entries.forEachIndexed { index, entry ->
                        val x = padding + (index * (chartWidth / (entries.size - 1).coerceAtLeast(1)))
                        val y = height - padding - ((entry.mood - 1) * (chartHeight / 9))

                        val animatedX = padding + (index * (chartWidth / (entries.size - 1).coerceAtLeast(1))) * animatedProgress
                        val animatedY = height - padding - ((entry.mood - 1) * (chartHeight / 9))

                        if (index == 0) {
                            path.moveTo(animatedX, animatedY)
                            gradientPath.moveTo(animatedX, animatedY)
                        } else {
                            path.lineTo(animatedX, animatedY)
                            gradientPath.lineTo(animatedX, animatedY)
                        }

                        // Draw mood points
                        if (animatedProgress > index.toFloat() / entries.size) {
                            drawCircle(
                                color = getMoodColor(entry.mood),
                                radius = 8.dp.toPx(),
                                center = Offset(animatedX, animatedY)
                            )

                            drawCircle(
                                color = Color.White,
                                radius = 4.dp.toPx(),
                                center = Offset(animatedX, animatedY)
                            )
                        }
                    }

                    // Draw gradient fill
                    gradientPath.lineTo(width - padding, height - padding)
                    gradientPath.lineTo(padding, height - padding)
                    gradientPath.close()

                    drawPath(
                        path = gradientPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF6366F1).copy(alpha = 0.3f),
                                Color(0xFF6366F1).copy(alpha = 0.1f)
                            )
                        )
                    )

                    // Draw main line
                    drawPath(
                        path = path,
                        color = Color(0xFF6366F1),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

@Composable
private fun MoodStatistics(entries: List<MoodEntry>) {
    if (entries.isEmpty()) return

    val averageMood = entries.map { it.mood }.average()
    val highestMood = entries.maxOf { it.mood }
    val lowestMood = entries.minOf { it.mood }
    val totalEntries = entries.size

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    listOf(
                        "Average" to String.format("%.1f", averageMood),
                        "Highest" to "$highestMood",
                        "Lowest" to "$lowestMood",
                        "Entries" to "$totalEntries"
                    )
                ) { (label, value) ->
                    StatisticCard(label = label, value = value)
                }
            }
        }
    }
}

@Composable
private fun StatisticCard(label: String, value: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
private fun MoodDistributionChart(entries: List<MoodEntry>) {
    if (entries.isEmpty()) return

    val moodCounts = entries.groupBy { it.mood }.mapValues { it.value.size }
    val maxCount = moodCounts.values.maxOrNull() ?: 1

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Mood Distribution",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2 - 40.dp.toPx()

                if (moodCounts.isNotEmpty()) {
                    val totalEntries = entries.size
                    var startAngle = 0f

                    moodCounts.forEach { (mood, count) ->
                        val sweepAngle = (count.toFloat() / totalEntries) * 360f
                        val color = getMoodColor(mood)

                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2)
                        )

                        // Draw mood emoji in the center of each slice
                        val middleAngle = startAngle + sweepAngle / 2
                        val textRadius = radius * 0.7f
                        val textX = center.x + textRadius * cos(Math.toRadians(middleAngle.toDouble())).toFloat()
                        val textY = center.y + textRadius * sin(Math.toRadians(middleAngle.toDouble())).toFloat()

                        startAngle += sweepAngle
                    }
                }
            }

            // Legend
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                items(moodCounts.toList()) { (mood, count) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = getMoodColor(mood).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = getMoodColor(mood),
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${getMoodEmoji(mood.toFloat())} $count",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun RecentMoodEntries(entries: List<MoodEntry>) {
    if (entries.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Recent Entries",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            entries.reversed().forEach { entry ->
                MoodEntryItem(entry = entry)
                if (entry != entries.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MoodEntryItem(entry: MoodEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = getMoodEmoji(entry.mood.toFloat()),
            fontSize = 24.sp,
            modifier = Modifier.padding(end = 12.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Mood: ${entry.mood}/10",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = entry.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
            if (entry.notes.isNotEmpty()) {
                Text(
                    text = entry.notes,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

private fun getMoodColor(mood: Int): Color {
    return when (mood) {
        1, 2 -> Color(0xFFEF4444)
        3, 4 -> Color(0xFFF97316)
        5, 6 -> Color(0xFFEAB308)
        7, 8 -> Color(0xFF22C55E)
        9, 10 -> Color(0xFF3B82F6)
        else -> Color(0xFF6B7280)
    }
}

private fun getMoodEmoji(mood: Float): String {
    return when (mood.toInt()) {
        1 -> "😢"
        2 -> "😕"
        3 -> "😐"
        4 -> "🙂"
        5 -> "😊"
        6 -> "😄"
        7 -> "😁"
        8 -> "🤗"
        9 -> "🥰"
        10 -> "🤩"
        else -> "😊"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun generateSampleMoodData(): List<MoodEntry> {
    val entries = mutableListOf<MoodEntry>()
    val today = LocalDate.now()

    for (i in 29 downTo 0) {
        val date = today.minusDays(i.toLong())
        val mood = Random.nextInt(1, 11)
        val notes = when (mood) {
            in 1..3 -> listOf("Rough day", "Feeling down", "Not great").random()
            in 4..6 -> listOf("Okay day", "Average mood", "Could be better").random()
            in 7..10 -> listOf("Great day!", "Feeling good", "Productive day").random()
            else -> ""
        }

        entries.add(MoodEntry(date, mood, notes))
    }

    return entries
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun MoodAnalyticsScreenPreview() {
    FocusTrackerTheme {
        MoodAnalyticsScreen(
            onBackPress = {}
        )
    }
}