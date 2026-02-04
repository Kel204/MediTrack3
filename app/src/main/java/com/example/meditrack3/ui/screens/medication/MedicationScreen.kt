package com.example.meditrack3.ui.screens.medication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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

@Composable
fun MedicationScreen(navController: NavController) {

    val viewModel: MedicationViewModel = viewModel()
    val medications by viewModel.medications.collectAsState()

    // ── Group medications by frequency/day ──
    val groupedMedications = medications.groupBy { it.frequency }

    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            groupedMedications.forEach { (day, medsForDay) ->

                item {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(medsForDay) { medication ->

                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {

                            Text(
                                medication.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text("Dosage: ${medication.dosage}")
                            Text("Time: ${medication.reminderTime}")
                            Text("Remaining: ${medication.remainingQuantity}")

                            if (medication.remainingQuantity <= medication.lowStockThreshold) {
                                Text(
                                    "Low stock – renew soon",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {

                                TextButton(
                                    onClick = {
                                        // You can wire this to an edit screen later
                                        navController.navigate(
                                            Screen.MedicationAdd.route
                                        )
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    Spacer(Modifier.width(4.dp))
                                    Text("Edit")
                                }

                                TextButton(
                                    onClick = {
                                        viewModel.deleteMedication(medication)
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    Spacer(Modifier.width(4.dp))
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Add Medication FAB ──
        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.MedicationAdd.route)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Medication")
        }
    }
}
