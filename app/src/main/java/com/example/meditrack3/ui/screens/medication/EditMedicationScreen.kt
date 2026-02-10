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

    /* ───────── Load medication ───────── */

    LaunchedEffect(medicationId) {
        viewModel.loadMedicationById(medicationId)
    }

    /* ───────── State ───────── */

    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var totalQuantity by remember { mutableStateOf("") }
    var dosePerIntake by remember { mutableStateOf("") }
    var lowStockThreshold by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(true) }

    val reminderTimes = remember { mutableStateListOf<String>() }
    val selectedDays = remember { mutableStateListOf<String>() }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    var startDate by remember {
        mutableStateOf(LocalDate.now().format(dateFormatter))
    }

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri")
    val weekends = listOf("Sat", "Sun")

    /* ───────── Prefill once loaded ───────── */

    LaunchedEffect(medication) {
        medication?.let {
            name = it.name
            dosage = it.dosage
            instructions = it.notes ?: ""
            totalQuantity = it.totalQuantity.toString()
            dosePerIntake = it.dosePerIntake.toString()
            lowStockThreshold = it.lowStockThreshold.toString()
            reminderEnabled = it.reminderEnabled
            startDate = it.startDate

            reminderTimes.clear()
            reminderTimes.addAll(it.reminderTime.split(","))

            selectedDays.clear()
            selectedDays.addAll(it.frequency.split(","))
        }
    }

    /* ───────── Pickers ───────── */

    fun showTimePicker() {
        val now = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val time = String.format("%02d:%02d", hour, minute)
                if (!reminderTimes.contains(time)) reminderTimes.add(time)
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

    /* ───────── UI ───────── */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        Text(
            text = "Edit Medication",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { },
                    enabled = false,
                    label = { Text("Medication Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = totalQuantity,
                    onValueChange = { totalQuantity = it.filter(Char::isDigit) },
                    label = { Text("Total Quantity") },
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
                    label = { Text("Low Stock Threshold") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            onClick = {

                val updated = medication?.copy(
                    dosage = dosage,
                    notes = instructions.ifBlank { null },
                    totalQuantity = totalQuantity.toInt(),
                    dosePerIntake = dosePerIntake.toInt(),
                    lowStockThreshold = lowStockThreshold.toInt(),
                    reminderEnabled = reminderEnabled,
                    reminderTime = reminderTimes.joinToString(),
                    frequency = selectedDays.joinToString(),
                    startDate = startDate
                ) ?: return@Button

                viewModel.updateMedication(updated)
                navController.popBackStack()
            }
        ) {
            Text("Save Changes")
        }
    }
}
