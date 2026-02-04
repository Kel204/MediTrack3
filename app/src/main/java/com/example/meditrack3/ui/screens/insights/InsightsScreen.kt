package com.example.meditrack3.ui.screens.insights

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.meditrack3.ui.viewmodels.MedicationViewModel

@Composable
fun InsightsScreen(navController: NavController) {

    val viewModel: MedicationViewModel = viewModel()
    val medications by viewModel.medications.collectAsState()

    val totalMedications = medications.size
    val totalRemaining = medications.sumOf { it.remainingQuantity }
    val lowStockMedications =
        medications.filter { it.remainingQuantity <= it.lowStockThreshold }


        LazyColumn(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Text("Insights", style = MaterialTheme.typography.headlineSmall)
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Overview", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Medications tracked: $totalMedications")
                        Text("Total doses remaining: $totalRemaining")
                    }
                }
            }

            item {
                Text(
                    "Low Stock Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (lowStockMedications.isEmpty()) {
                item {
                    Text(
                        "All medications are sufficiently stocked 👍",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(lowStockMedications) { medication ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                medication.name,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                "Remaining: ${medication.remainingQuantity}",
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                "Renew soon",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }

