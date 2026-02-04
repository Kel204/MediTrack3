package com.example.meditrack3.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.meditrack3.data.dao.MedicationDao
import com.example.meditrack3.data.entity.Medication


@Database(
    entities = [Medication::class],
    version = 3,
    exportSchema = false
)
abstract class MediTrackDatabase : RoomDatabase() {

    abstract fun medicationDao(): MedicationDao

    companion object {
        @Volatile
        private var INSTANCE: MediTrackDatabase? = null

        fun getDatabase(context: Context): MediTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MediTrackDatabase::class.java,
                    "meditrack_db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}
