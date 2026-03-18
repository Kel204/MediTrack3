package com.example.meditrack3.data.service

import com.google.gson.annotations.SerializedName

data class MedicationDto(

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("activeSubstance")
    val activeSubstance: String? = null,

    @SerializedName("routeOfAdministration")
    val routeOfAdministration: String? = null,

    @SerializedName("category")
    val category: String? = null
)