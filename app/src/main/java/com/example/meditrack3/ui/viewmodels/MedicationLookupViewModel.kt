package com.example.meditrack3.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meditrack3.data.service.ApiClient
import com.example.meditrack3.data.service.MedicationDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicationLookupViewModel : ViewModel() {

    private val _results = MutableStateFlow<List<LookupResult>>(emptyList())
    val results: StateFlow<List<LookupResult>> = _results

    // FIXED TYPE HERE
    private var allMedicines: List<MedicationDto> = emptyList()

    // Load data once from GitHub
    fun loadMedicines() {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.getMedicines()
                allMedicines = response.medicines
            } catch (e: Exception) {
                _results.value = listOf(
                    LookupResult(
                        name = "Error loading data",
                        description = e.message
                    )
                )
            }
        }
    }

    // Search locally (FAST)
    fun search(query: String) {
        val cleaned = query.trim()

        if (cleaned.isEmpty()) {
            _results.value = emptyList()
            return
        }

        val filtered = allMedicines.filter {
            it.name?.contains(cleaned, ignoreCase = true) == true
        }

        if (filtered.isNotEmpty()) {
            _results.value = filtered.take(20).map { medicine ->
                LookupResult(
                    name = medicine.name ?: cleaned,
                    description = buildString {
                        medicine.activeSubstance?.let {
                            append("Active: $it\n")
                        }
                        medicine.routeOfAdministration?.let {
                            append("Route: $it\n")
                        }
                        medicine.category?.let {
                            append("Category: $it")
                        }
                    }.ifBlank { null }
                )
            }
        } else {
            _results.value = listOf(
                LookupResult(
                    name = cleaned.replaceFirstChar { it.uppercase() },
                    description = "Medication not found"
                )
            )
        }
    }
}

data class LookupResult(
    val name: String,
    val description: String?
)