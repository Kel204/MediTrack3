package com.example.meditrack3.ui.screens.insights

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.meditrack3.data.entity.DayResult
import com.example.meditrack3.data.entity.Medication
import com.example.meditrack3.ui.viewmodels.MedicationViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InsightsScreen(navController: NavController) {

    val viewModel: MedicationViewModel = viewModel()
    val medications by viewModel.medications.collectAsState()
    val doseStatuses = viewModel.getAllDoseStatuses()

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDay = currentMonth.lengthOfMonth()
    val startOffset = firstDayOfMonth.dayOfWeek.value - 1

    val days = buildList<LocalDate?> {
        repeat(startOffset) { add(null) }
        for (day in 1..lastDay) {
            add(currentMonth.atDay(day))
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        /* ───────── TITLE ───────── */

        item {
            Text(
                "Insights",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        /* ───────── CALENDAR CARD ───────── */

        item {

            ElevatedCard(
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    /* MONTH HEADER */

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        TextButton(
                            onClick = { currentMonth = currentMonth.minusMonths(1) }
                        ) { Text("‹") }

                        Text(
                            "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        TextButton(
                            onClick = { currentMonth = currentMonth.plusMonths(1) }
                        ) { Text("›") }
                    }

                    /* WEEKDAY HEADER */

                    Row(modifier = Modifier.fillMaxWidth()) {
                        DayOfWeek.values().forEach { day ->
                            Text(
                                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    /* CALENDAR GRID */

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp), // 👈 CRITICAL: fixed height prevents crash
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        userScrollEnabled = false // 👈 important
                    ) {

                        items(days) { date ->

                            if (date == null) {
                                Box(modifier = Modifier.aspectRatio(1f))
                            } else {

                                val result = calculateDayAdherence(
                                    date,
                                    medications,
                                    doseStatuses
                                )

                                val isSelected = selectedDate == date
                                val isToday = date == LocalDate.now()

                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected)
                                                MaterialTheme.colorScheme.primaryContainer
                                            else
                                                Color.Transparent
                                        )
                                        .clickable { selectedDate = date },
                                    contentAlignment = Alignment.Center
                                ) {

                                    val adherenceColor = when {
                                        result.totalScheduled == 0 ->
                                            Color.Transparent
                                        result.adherence == 100 ->
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        result.adherence >= 50 ->
                                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                        else ->
                                            MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(adherenceColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /* ───────── SELECTED DAY SUMMARY ───────── */

        item {

            val result = calculateDayAdherence(
                selectedDate,
                medications,
                doseStatuses
            )

            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        selectedDate.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text("Taken: ${result.totalTaken} / ${result.totalScheduled}")
                    Text("Adherence: ${result.adherence}%")

                    if (result.missedMeds.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Missed:",
                            fontWeight = FontWeight.Medium
                        )
                        result.missedMeds.forEach {
                            Text(
                                "• $it",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        /* ───────── LOW STOCK SECTION ───────── */

        val lowStock = medications.filter {
            it.remainingQuantity <= it.lowStockThreshold
        }

        if (lowStock.isNotEmpty()) {

            item {
                Text(
                    "Low Stock Medications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(lowStock.size) { index ->
                val med = lowStock[index]

                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            med.name,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Remaining: ${med.remainingQuantity}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

/* ───────── ADHERENCE CALCULATION ───────── */

@RequiresApi(Build.VERSION_CODES.O)
fun calculateDayAdherence(
    date: LocalDate,
    medications: List<Medication>,
    doseStatuses: Map<String, Boolean?>
): DayResult {

    var totalScheduled = 0
    var totalTaken = 0
    val missed = mutableListOf<String>()

    medications.forEach { med ->
        val times = med.reminderTime.split(",").filter { it.isNotBlank() }

        times.forEach { time ->
            totalScheduled++
            val key = "${date}-${med.id}-$time"

            if (doseStatuses[key] == true) {
                totalTaken++
            } else {
                missed.add(med.name)
            }
        }
    }

    val adherence =
        if (totalScheduled > 0)
            (totalTaken * 100) / totalScheduled
        else 0

    return DayResult(
        totalScheduled,
        totalTaken,
        adherence,
        Color.Transparent,
        missed.distinct()
    )
}
