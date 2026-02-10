package com.example.meditrack3.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // ── Medication info ──────────────────────────
    val name: String = "",
    val dosage: String = "",

    // ── Scheduling ───────────────────────────────
    val frequency: String = "",
    val reminderTime: String = "",
    val startDate: String = "",

    // ── Reminder behaviour ───────────────────────
    val reminderEnabled: Boolean = true,

    // ── Optional notes ───────────────────────────
    val notes: String? = null,

    // ── Stock tracking ───────────────────────────
    val totalQuantity: Int = 0,
    val remainingQuantity: Int = 0,
    val dosePerIntake: Int = 0,

    // ── Low stock alert ──────────────────────────
    val lowStockThreshold: Int = 0
)

