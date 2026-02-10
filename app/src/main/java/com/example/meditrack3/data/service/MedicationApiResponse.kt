package com.example.meditrack3.data.service

data class MedicationApiResponse(
    val results: List<NdcResult> = emptyList()
)

data class NdcResult(
    val brand_name: String? = null,
    val generic_name: String? = null,
    val dosage_form: String? = null,
    val route: List<String>? = null,
    val active_ingredients: List<ActiveIngredient>? = null
)

data class ActiveIngredient(
    val name: String? = null,
    val strength: String? = null
)