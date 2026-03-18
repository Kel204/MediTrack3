package com.example.meditrack3.data.service

import retrofit2.http.GET

interface MedicationApiService {

    @GET("medicines.json")
    suspend fun getMedicines(): MedicationApiResponse
}