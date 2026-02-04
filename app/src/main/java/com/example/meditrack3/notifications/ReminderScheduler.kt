package com.example.meditrack3.notifications

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleMedicationReminders(
        context: Context,
        medicationName: String,
        dosage: String,
        times: List<String>
    ) {
        times.forEach { time ->

            val localTime = LocalTime.parse(time)
            val delay = Duration.between(LocalTime.now(), localTime)
                .let { if (it.isNegative) it.plusDays(1) else it }

            val data = workDataOf(
                "medication_name" to medicationName,
                "dosage" to dosage
            )

            val request = OneTimeWorkRequestBuilder<MedicationReminderWorker>()
                .setInitialDelay(delay.toMinutes(), TimeUnit.MINUTES)
                .setInputData(data)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
