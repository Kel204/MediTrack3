package com.example.meditrack3.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meditrack3.data.service.ApiClient
import com.example.meditrack3.data.service.irishOtcMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicationLookupViewModel : ViewModel() {

    private val _results = MutableStateFlow<List<LookupResult>>(emptyList())
    val results: StateFlow<List<LookupResult>> = _results

    fun search(query: String) {
        viewModelScope.launch {
            try {
                val cleaned = query.trim().lowercase()

                // 🇮🇪 Resolve Irish OTC → generic
                val resolved = irishOtcMap[cleaned] ?: cleaned

                // ✅ MOST RELIABLE NDC SEARCH
                val searchQuery = "active_ingredients.name:$resolved"

                val response = ApiClient.api.searchMedication(searchQuery)

                if (response.results.isNotEmpty()) {
                    _results.value = response.results
                        .distinctBy { it.brand_name ?: it.generic_name }
                        .map { result ->
                            LookupResult(
                                name = result.brand_name
                                    ?: result.generic_name
                                    ?: resolved.replaceFirstChar { it.uppercase() },

                                description = buildString {
                                    result.dosage_form?.let {
                                        append("Form: $it\n")
                                    }
                                    result.active_ingredients
                                        ?.firstOrNull()
                                        ?.let {
                                            append("Active: ${it.name} ${it.strength}")
                                        }
                                }.ifBlank { null }
                            )
                        }
                } else {
                    // ✅ GUARANTEED fallback result
                    _results.value = listOf(
                        LookupResult(
                            name = resolved.replaceFirstChar { it.uppercase() },
                            description = "Generic medication (manual lookup fallback)"
                        )
                    )
                }

            } catch (e: Exception) {
                _results.value = listOf(
                    LookupResult(
                        name = query,
                        description = "Manual entry recommended"
                    )
                )
            }
        }
    }

}

data class LookupResult(
    val name: String,
    val description: String?
)
