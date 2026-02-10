package com.example.meditrack3.data.repository

import android.app.Application
import android.util.Log
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

    // ✅ FIXED: use AuthManager helper, NOT auth directly
    private fun userCollection() =
        firestore.collection("users")
            .document(AuthManager.getUserId() ?: "guest")
            .collection("medications")

    /* ───────── Room (Local) ───────── */

    fun getAllMedications(): Flow<List<Medication>> =
        dao.getAllMedications()

    suspend fun getMedicationById(id: Int): Medication? =
        dao.getMedicationById(id)

    /* ───────── Room + Firebase Sync ───────── */

    suspend fun insertMedication(medication: Medication) {
        val id = dao.insertMedication(medication)

        val withId = medication.copy(id = id.toInt())

        userCollection()
            .document(withId.id.toString())
            .set(withId)
            .await()
    }

    suspend fun updateMedication(medication: Medication) {
        dao.updateMedication(medication)
        syncToFirebase(medication)
    }

    suspend fun deleteMedication(medication: Medication) {
        dao.deleteMedication(medication)
        deleteFromFirebase(medication)
    }

    /* ───────── Firebase Helpers ───────── */

    private suspend fun syncToFirebase(medication: Medication) {
        try {
            userCollection()
                .document(medication.id.toString())
                .set(medication)
                .await()

            Log.d("FIRESTORE_TEST", "Saved ${medication.name} for user ${AuthManager.getUserId()}")
        } catch (e: Exception) {
            Log.e("FIRESTORE_TEST", "Firestore write FAILED", e)
        }
    }

    private suspend fun deleteFromFirebase(medication: Medication) {
        userCollection()
            .document(medication.id.toString())
            .delete()
            .await()
    }

    suspend fun clearLocalData() {
        dao.deleteAll()
    }

    suspend fun restoreFromFirebase() {
        val snapshot = userCollection()
            .get()
            .await()

        snapshot.documents.forEach { document ->
            val medication = document.toObject(Medication::class.java)
            medication?.let {
                dao.insertMedication(it)
            }
        }
    }

    suspend fun syncAllToFirebase() {
        val localMedications = dao.getAllAtOnce()

        localMedications.forEach { medication ->
            userCollection()
                .document(medication.id.toString())
                .set(medication)
                .await()
        }
    }

}
