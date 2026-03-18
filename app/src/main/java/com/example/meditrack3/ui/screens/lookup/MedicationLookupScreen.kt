package com.example.meditrack3.ui.screens.lookup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.meditrack3.ui.viewmodels.MedicationLookupViewModel
import java.net.URLEncoder

@Composable
fun MedicationLookupScreen(
    navController: NavController,
    viewModel: MedicationLookupViewModel = viewModel()
) {

    val results by viewModel.results.collectAsState()
    var query by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // ✅ Load medicines once
    LaunchedEffect(Unit) {
        viewModel.loadMedicines()
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Medication Search",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔍 Search Bar
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.search(it)
            },
            label = { Text("Search medication...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 📊 UI States
        when {
            isLoading -> {
                CircularProgressIndicator()
            }

            query.isEmpty() -> {
                Text("Start typing to search medications...")
            }

            results.isEmpty() -> {
                Text("No results found")
            }

            else -> {
                LazyColumn {
                    items(results) { item ->
                        MedicationItem(
                            name = item.name,
                            description = item.description,
                            onClick = {

                                val encodedName = URLEncoder.encode(item.name, "UTF-8")
                                val encodedDetails = URLEncoder.encode(item.description ?: "", "UTF-8")

                                navController.navigate(
                                    "medication_add?name=$encodedName&details=$encodedDetails"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}