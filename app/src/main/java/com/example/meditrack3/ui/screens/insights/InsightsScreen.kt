package com.example.meditrack3.ui.screens.insights

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
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
        for (day in 1..lastDay) add(currentMonth.atDay(day))
    }

    val lowStock = medications.filter {
        it.remainingQuantity <= it.lowStockThreshold
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        /* ───────── HEADER ───────── */

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Insights",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Track your medication adherence",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }


        /* ───────── CALENDAR CARD ───────── */

        item {
            ElevatedCard(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(6.dp)
            ) {

                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    /* Month Header */

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        IconButton(
                            onClick = { currentMonth = currentMonth.minusMonths(1) }
                        ) {
                            Text("‹", style = MaterialTheme.typography.titleLarge)
                        }

                        Text(
                            "${
                                currentMonth.month.getDisplayName(
                                    TextStyle.FULL,
                                    Locale.getDefault()
                                )
                            } ${currentMonth.year}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        IconButton(
                            onClick = { currentMonth = currentMonth.plusMonths(1) }
                        ) {
                            Text("›", style = MaterialTheme.typography.titleLarge)
                        }
                    }

                    /* Weekdays */

                    Row(modifier = Modifier.fillMaxWidth()) {
                        DayOfWeek.values().forEach { day ->
                            Text(
                                day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                modifier = Modifier.weight(2f),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    /* Calendar Grid */

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(270.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        userScrollEnabled = false
                    ) {

                        items(days) { date ->

                            if (date == null) {
                                Box(Modifier.aspectRatio(1f))
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
                                        .clickable { selectedDate = date },
                                    contentAlignment = Alignment.Center
                                ) {

                                    /* Selected Background */

                                    if (isSelected) {
                                        Box(
                                            Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary)
                                        )
                                    }

                                    /* Today Outline */

                                    if (isToday && !isSelected) {
                                        Box(
                                            Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    1.5.dp,
                                                    MaterialTheme.colorScheme.primary,
                                                    CircleShape
                                                )
                                        )
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

                                        Text(
                                            date.dayOfMonth.toString(),
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimary
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )

                                        if (result.totalScheduled > 0) {

                                            Spacer(Modifier.height(4.dp))

                                            Box(
                                                Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when {
                                                            result.adherence == 100 ->
                                                                Color(0xFF2E7D32) // deep green
                                                            result.adherence >= 50 ->
                                                                Color(0xFFF9A825) // amber
                                                            else ->
                                                                Color(0xFFC62828) // deep red
                                                        }
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /* ───────── DAY SUMMARY ───────── */

        item {

            val result = calculateDayAdherence(
                selectedDate,
                medications,
                doseStatuses
            )

            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        selectedDate.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        "Taken ${result.totalTaken} of ${result.totalScheduled} doses",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    LinearProgressIndicator(
                        progress = result.adherence / 100f,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (result.missedMeds.isNotEmpty()) {
                        Divider()
                        Text("Missed:", fontWeight = FontWeight.Medium)
                        result.missedMeds.forEach {
                            Text("• $it")
                        }
                    }
                }
            }
        }

        /* ───────── LOW STOCK ───────── */

        if (lowStock.isNotEmpty()) {

            item {
                Text(
                    "Low Stock",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(
                items = lowStock,
                key = { it.id }
            ) { med ->

                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            med.name,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Remaining: ${med.remainingQuantity}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/* ───────── ADHERENCE CALCULATION FUNCTION ───────── */

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
