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
import com.example.meditrack3.ui.viewmodels.MedicationViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditMedicationScreen(
    navController: NavController,
    medicationId: Int
) {
    val viewModel: MedicationViewModel = viewModel()
    val context = LocalContext.current
    val medication by viewModel.selectedMedication.collectAsState()

    LaunchedEffect(medicationId) {
        viewModel.loadMedicationById(medicationId)
    }

    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var totalQuantity by remember { mutableStateOf("") }
    var dosePerIntake by remember { mutableStateOf("") }
    var lowStockThreshold by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(true) }

    val reminderTimes = remember { mutableStateListOf<String>() }
    val selectedDays = remember { mutableStateListOf<String>() }

    val daysOfWeek = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")

    var dosageError by remember { mutableStateOf<String?>(null) }
    var quantityError by remember { mutableStateOf<String?>(null) }
    var doseError by remember { mutableStateOf<String?>(null) }
    var lowStockError by remember { mutableStateOf<String?>(null) }
    var reminderError by remember { mutableStateOf<String?>(null) }
    var repeatError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(medication) {
        medication?.let {
            name = it.name
            dosage = it.dosage
            instructions = it.notes ?: ""
            totalQuantity = it.totalQuantity.toString()
            dosePerIntake = it.dosePerIntake.toString()
            lowStockThreshold = it.lowStockThreshold.toString()
            reminderEnabled = it.reminderEnabled

            reminderTimes.clear()
            reminderTimes.addAll(it.reminderTime.split(","))

            selectedDays.clear()
            selectedDays.addAll(it.frequency.split(","))
        }
    }

    fun showTimePicker() {
        val now = Calendar.getInstance()
        TimePickerDialog(context, { _, h, m ->
            val time = "%02d:%02d".format(h, m)
            if (!reminderTimes.contains(time)) reminderTimes.add(time)
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            "Edit Medication",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        /* ───────── DETAILS ───────── */

        Card(
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                SectionHeader("Medication Details", Icons.Default.MedicalServices)

                OutlinedTextField(
                    value = name,
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Medication Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it; dosageError = null },
                    isError = dosageError != null,
                    supportingText = { dosageError?.let { Text(it) } },
                    label = { Text("Dosage") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = totalQuantity,
                    onValueChange = { totalQuantity = it.filter(Char::isDigit); quantityError = null },
                    isError = quantityError != null,
                    supportingText = { quantityError?.let { Text(it) } },
                    label = { Text("Total Quantity") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                    OutlinedTextField(
                        value = dosePerIntake,
                        onValueChange = { dosePerIntake = it.filter(Char::isDigit); doseError = null },
                        isError = doseError != null,
                        label = { Text("Dose") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = lowStockThreshold,
                        onValueChange = { lowStockThreshold = it.filter(Char::isDigit); lowStockError = null },
                        isError = lowStockError != null,
                        label = { Text("Low Stock") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        /* ───────── REMINDERS ───────── */

        Card(
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader("Reminders", Icons.Default.Alarm)
                    Switch(reminderEnabled, onCheckedChange = { reminderEnabled = it })
                }

                if (reminderEnabled) {

                    reminderTimes.forEachIndexed { i, time ->
                        AssistChip(
                            onClick = { reminderTimes.removeAt(i) },
                            label = { Text(time) },
                            trailingIcon = {
                                Icon(Icons.Default.Close, null)
                            }
                        )
                    }

                    OutlinedButton(onClick = { showTimePicker() }) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Add Time")
                    }

                    reminderError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }

                    Text("Repeat Days", fontWeight = FontWeight.SemiBold)

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
                                        repeatError = null
                                    }
                                },
                                label = { Text(day) }
                            )
                        }
                    }

                    repeatError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        /* ───────── SAVE BUTTON ───────── */

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.large,
            onClick = {

                var isValid = true

                if (dosage.isBlank()) { dosageError = "Required"; isValid = false }

                val qty = totalQuantity.toIntOrNull()
                if (qty == null || qty <= 0) { quantityError = "Invalid"; isValid = false }

                val dose = dosePerIntake.toIntOrNull()
                if (dose == null || dose <= 0) { doseError = "Invalid"; isValid = false }

                val low = lowStockThreshold.toIntOrNull()
                if (low == null || low < 0) { lowStockError = "Invalid"; isValid = false }

                if (reminderEnabled && reminderTimes.isEmpty()) {
                    reminderError = "Add a time"
                    isValid = false
                }

                if (reminderEnabled && selectedDays.isEmpty()) {
                    repeatError = "Select a day"
                    isValid = false
                }

                if (!isValid) return@Button

                val updated = medication?.copy(
                    dosage = dosage,
                    notes = instructions.ifBlank { null },
                    totalQuantity = totalQuantity.toInt(),
                    dosePerIntake = dosePerIntake.toInt(),
                    lowStockThreshold = lowStockThreshold.toInt(),
                    reminderEnabled = reminderEnabled,
                    reminderTime = reminderTimes.joinToString(),
                    frequency = selectedDays.joinToString()
                ) ?: return@Button

                viewModel.updateMedication(updated)
                navController.popBackStack()
            }
        ) {
            Text("Save Changes")
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.SemiBold)
    }
}