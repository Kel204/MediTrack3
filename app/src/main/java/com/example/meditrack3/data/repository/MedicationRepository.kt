package com.example.meditrack3.data.repository

import android.app.Application
import com.example.meditrack3.data.database.MediTrackDatabase
import com.example.meditrack3.data.entity.Medication
import kotlinx.coroutines.flow.Flow

class MedicationRepository(application: Application) {

    private val dao =
        MediTrackDatabase.getDatabase(application).medicationDao()

    fun getAllMedications(): Flow<List<Medication>> =
        dao.getAllMedications()

    suspend fun getMedicationById(id: Int): Medication? =
        dao.getMedicationById(id)

    suspend fun insertMedication(medication: Medication) =
        dao.insertMedication(medication)

    suspend fun updateMedication(medication: Medication) =
        dao.updateMedication(medication)

    suspend fun deleteMedication(medication: Medication) =
        dao.deleteMedication(medication)
}
