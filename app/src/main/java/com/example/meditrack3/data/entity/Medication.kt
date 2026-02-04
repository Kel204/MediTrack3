package com.example.meditrack3.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // ── Medication info ──────────────────────────
    val name: String,
    val dosage: String,

    // ── Scheduling ───────────────────────────────
    val frequency: String,          // e.g. Everyday, Weekdays, Mon, Tue
    val reminderTime: String,       // e.g. "08:00"
    val startDate: String,          // e.g. "01/10/2026"

    // ── Reminder behaviour ───────────────────────
    val reminderEnabled: Boolean = true,

    // ── Optional notes / instructions ────────────
    val notes: String? = null,

    // ── Stock tracking (NEW) ─────────────────────
    val totalQuantity: Int,         // e.g. 30 tablets supplied
    val remainingQuantity: Int,     // decreases as doses are taken
    val dosePerIntake: Int,         // e.g. 1 tablet per reminder

    // ── Low stock alert ──────────────────────────
    val lowStockThreshold: Int      // e.g. alert when ≤ 5 tablets
)
