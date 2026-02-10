package com.example.meditrack3.data.dao

import androidx.room.*
import com.example.meditrack3.data.entity.Medication
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    /* ───────── Insert / Update ───────── */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: Medication)

    @Update
    suspend fun updateMedication(medication: Medication)

    @Delete
    suspend fun deleteMedication(medication: Medication)

    /* ───────── UI (Flow-based) ───────── */

    @Query("SELECT * FROM medications ORDER BY name ASC")
    fun getAllMedications(): Flow<List<Medication>>

    /* ───────── Background / WorkManager ───────── */

    // ✅ Used by LowStockWorker
    @Query("SELECT * FROM medications")
    fun getAllOnce(): List<Medication>

    // ✅ Used by ReminderWorker / actions
    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getMedicationById(id: Int): Medication?

    @Query("DELETE FROM medications")
    suspend fun deleteAll()

    @Query("SELECT * FROM medications")
    suspend fun getAllAtOnce(): List<Medication>
}
