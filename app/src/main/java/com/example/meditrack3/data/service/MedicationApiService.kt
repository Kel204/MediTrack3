package com.example.meditrack3.data.service

import retrofit2.http.GET
import retrofit2.http.Query


interface MedicationApiService {

    @GET("drug/ndc.json")
    suspend fun searchMedication(
        @Query("search") search: String,
        @Query("limit") limit: Int = 10
    ): MedicationApiResponse
}