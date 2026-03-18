package com.example.meditrack3.data.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL =
        "https://raw.githubusercontent.com/Kel204/Medicines-Database/refs/heads/main/"

    val api: MedicationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MedicationApiService::class.java)
    }
}