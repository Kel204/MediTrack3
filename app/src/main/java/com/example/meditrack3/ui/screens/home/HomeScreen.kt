package com.example.meditrack3.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.meditrack3.navigation.Screen
import com.example.meditrack3.ui.viewmodels.MedicationViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController) {

    val viewModel: MedicationViewModel = viewModel()
    val medications by viewModel.medications.collectAsState()

    val today = LocalDate.now()
    val now = LocalTime.now()
    val todayName = today.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val todayKeyPrefix = today.toString()

    fun appliesToday(frequency: String): Boolean =
        when {
            frequency.contains("Everyday") -> true
            frequency.contains("Weekdays") ->
                today.dayOfWeek !in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
            frequency.contains("Weekends") ->
                today.dayOfWeek in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
            else -> frequency.contains(todayName)
        }

    val todaysDoses = medications
        .filter { it.reminderEnabled && appliesToday(it.frequency) }
        .flatMap { med ->
            med.reminderTime.split(",").map { time ->
                Triple(med, time.trim(), "$todayKeyPrefix-${med.id}-$time")
            }
        }
        .sortedBy { it.second }

    // 🔹 UI-only separation (no logic change)
    val pendingDoses = todaysDoses.filter {
        viewModel.getDoseStatus(it.third) == null
    }

    val completedDoses = todaysDoses.filter {
        viewModel.getDoseStatus(it.third) != null
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        /* ───────── Header ───────── */

        item {
            Column {
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Here’s what you need to take",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        /* ───────── Pending ───────── */

        items(pendingDoses) { (med, time, key) ->
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(med.name, fontWeight = FontWeight.SemiBold)
                            Text(med.dosage, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        AssistChip(onClick = {}, label = { Text(time) })
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        FilledTonalButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.setDoseStatus(key, true)
                                viewModel.markDoseTaken(med.id)
                            }
                        ) {
                            Icon(Icons.Default.Check, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Taken")
                        }

                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.setDoseStatus(key, false)
                            }
                        ) {
                            Icon(Icons.Default.Close, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Missed")
                        }
                    }
                }
            }
        }

        /* ───────── Completed ───────── */

        if (completedDoses.isNotEmpty()) {
            item {
                Text(
                    text = "Completed Today",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(completedDoses) { (med, time, key) ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(modifier = Modifier.weight(1f)) {
                            Text(med.name, fontWeight = FontWeight.SemiBold)
                            Text(time)
                        }

                        when (viewModel.getDoseStatus(key)) {
                            true -> Text(
                                "✓ Taken",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            false -> Text(
                                "✕ Missed",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )

                            null -> TODO()
                        }
                    }
                }
            }
        }

        /* ───────── Footer ───────── */

        item {
            Button(
                onClick = { navController.navigate(Screen.Medication.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View All Medications")
            }
        }
    }
}
