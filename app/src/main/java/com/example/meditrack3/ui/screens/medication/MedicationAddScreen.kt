package com.example.meditrack3.ui.screens.medication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.meditrack3.data.entity.Medication
import com.example.meditrack3.notifications.ReminderScheduler
import com.example.meditrack3.ui.viewmodels.MedicationViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MedicationAddScreen(navController: NavController) {

    val viewModel: MedicationViewModel = viewModel()
    val context = LocalContext.current

    /* ───────── State ───────── */

    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }

    var totalQuantity by remember { mutableStateOf("") }
    var dosePerIntake by remember { mutableStateOf("1") }
    var lowStockThreshold by remember { mutableStateOf("5") }

    var reminderEnabled by remember { mutableStateOf(true) }
    val reminderTimes = remember { mutableStateListOf<String>() }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    var startDate by remember {
        mutableStateOf(LocalDate.now().format(dateFormatter))
    }

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri")
    val weekends = listOf("Sat", "Sun")
    val selectedDays = remember { mutableStateListOf<String>() }

    /* ───────── Pickers ───────── */

    fun showTimePicker() {
        val now = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val time = String.format("%02d:%02d", hour, minute)
                if (!reminderTimes.contains(time)) {
                    reminderTimes.add(time)
                    reminderTimes.sort()
                }
            },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            true
        ).show()
    }

    fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                startDate = LocalDate.of(year, month + 1, day)
                    .format(dateFormatter)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun getStartDateLabel(date: String): String {
        val selected = LocalDate.parse(date, dateFormatter)
        val today = LocalDate.now()
        return when (selected) {
            today -> "Starts today"
            today.plusDays(1) -> "Starts tomorrow"
            else -> "Starts on ${selected.format(DateTimeFormatter.ofPattern("d MMM yyyy"))}"
        }
    }

    /* ───────── UI ───────── */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        Text(
            text = "Medication Reminder",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        /* ───────── Medication Details ───────── */

        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader("Medication Details", Icons.Default.MedicalServices)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medication Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage (e.g. 500mg)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = totalQuantity,
                    onValueChange = { totalQuantity = it.filter(Char::isDigit) },
                    label = { Text("Total Quantity Supplied") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dosePerIntake,
                    onValueChange = { dosePerIntake = it.filter(Char::isDigit) },
                    label = { Text("Dose per Intake") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = lowStockThreshold,
                    onValueChange = { lowStockThreshold = it.filter(Char::isDigit) },
                    label = { Text("Low Stock Alert Threshold") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        /* ───────── Reminders ───────── */

        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader("Reminders", Icons.Default.Alarm)
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it }
                    )
                }

                if (reminderEnabled) {

                    Text("Reminder Times", fontWeight = FontWeight.SemiBold)

                    reminderTimes.forEachIndexed { index, time ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(time)
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { reminderTimes.removeAt(index) }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    }

                    OutlinedButton(onClick = { showTimePicker() }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add Time")
                    }

                    Text("Repeat", fontWeight = FontWeight.SemiBold)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PresetChip("Everyday") {
                            selectedDays.clear(); selectedDays.addAll(daysOfWeek)
                        }
                        PresetChip("Weekdays") {
                            selectedDays.clear(); selectedDays.addAll(weekdays)
                        }
                        PresetChip("Weekends") {
                            selectedDays.clear(); selectedDays.addAll(weekends)
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.height(120.dp)
                    ) {
                        items(daysOfWeek) { day ->
                            FilterChip(
                                selected = selectedDays.contains(day),
                                onClick = {
                                    if (selectedDays.contains(day)) {
                                        selectedDays.remove(day)
                                    } else {
                                        selectedDays.add(day)
                                    }
                                },
                                label = { Text(day) }
                            )
                        }
                    }
                }
            }
        }

        /* ───────── Schedule ───────── */

        Card {
            Column(modifier = Modifier.padding(16.dp)) {

                SectionHeader("Schedule", Icons.Default.CalendarToday)

                Spacer(Modifier.height(12.dp))

                val startDateLabel = remember(startDate) {
                    getStartDateLabel(startDate)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker() }
                ) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = {},
                        enabled = false,
                        label = { Text("Start Date") },
                        supportingText = { Text(startDateLabel) },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                        },
                        trailingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        /* ───────── Save ───────── */

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            onClick = {

                val totalQty = totalQuantity.toIntOrNull() ?: return@Button
                val dose = dosePerIntake.toIntOrNull() ?: 1
                val lowStock = lowStockThreshold.toIntOrNull() ?: 5

                viewModel.addMedication(
                    Medication(
                        name = name,
                        dosage = dosage,
                        frequency = when {
                            selectedDays.size == 7 -> "Everyday"
                            selectedDays.containsAll(weekdays) && selectedDays.size == 5 -> "Weekdays"
                            selectedDays.containsAll(weekends) && selectedDays.size == 2 -> "Weekends"
                            else -> selectedDays.joinToString()
                        },
                        reminderTime = reminderTimes.joinToString(),
                        startDate = startDate,
                        reminderEnabled = reminderEnabled,
                        notes = instructions.ifBlank { null },
                        totalQuantity = totalQty,
                        remainingQuantity = totalQty,
                        dosePerIntake = dose,
                        lowStockThreshold = lowStock
                    )
                )

                // ✅ WorkManager scheduling
                if (reminderEnabled) {
                    ReminderScheduler.scheduleMedicationReminders(
                        context = context,
                        medicationName = name,
                        dosage = dosage,
                        times = reminderTimes
                    )
                }

                navController.popBackStack()
            }
        ) {
            Text("Save Reminder")
        }
    }
}

/* ───────── Helpers ───────── */

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun PresetChip(label: String, onClick: () -> Unit) {
    AssistChip(onClick = onClick, label = { Text(label) })
}
