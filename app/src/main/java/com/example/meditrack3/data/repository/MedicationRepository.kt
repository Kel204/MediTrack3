package com.example.meditrack3.data.repository

import android.app.Application
import com.example.meditrack3.auth.AuthManager
import com.example.meditrack3.data.database.MediTrackDatabase
import com.example.meditrack3.data.entity.Medication
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class MedicationRepository(application: Application) {

    private val dao =
        MediTrackDatabase.getDatabase(application).medicationDao()

    private val firestore = FirebaseFirestore.getInstance()

    private fun userCollection() =
        firestore.collection("users")
            .document(AuthManager.currentUser.value?.uid ?: "guest")
            .collection("medications")

    /* ───────── Room ───────── */

    fun getAllMedications(): Flow<List<Medication>> =
        dao.getAllMedications()

    suspend fun getMedicationById(id: Int): Medication? =
        dao.getMedicationById(id)

    suspend fun clearLocalData() {
        dao.clearAll()
    }

    /* ───────── Insert (FIXED) ───────── */

    suspend fun insertMedication(medication: Medication) {

        // 1️⃣ Insert locally first
        val generatedId = dao.insertMedication(medication).toInt()

        // 2️⃣ Copy with real ID
        val medicationWithId = medication.copy(id = generatedId)

        // 3️⃣ Sync to Firebase
        syncToFirebase(medicationWithId)
    }

    /* ───────── Update ───────── */

    suspend fun updateMedication(medication: Medication) {
        dao.updateMedication(medication)
        syncToFirebase(medication)
    }

    /* ───────── Delete ───────── */

    suspend fun deleteMedication(medication: Medication) {
        dao.deleteMedication(medication)

        userCollection()
            .document(medication.id.toString())
            .delete()
            .await()
    }

    /* ───────── Firebase Sync ───────── */

    private suspend fun syncToFirebase(medication: Medication) {

        userCollection()
            .document(medication.id.toString())
            .set(medication)
            .await()
    }

    /* ───────── Restore From Firebase ───────── */

    suspend fun restoreFromFirebase() {

        val snapshot = userCollection().get().await()

        snapshot.documents.forEach { document ->

            val medication = document.toObject(Medication::class.java)

            medication?.let {
                dao.insertMedication(it)
            }
        }
    }
}
