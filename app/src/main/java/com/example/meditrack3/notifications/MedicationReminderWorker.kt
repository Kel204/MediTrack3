package com.example.meditrack3.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.meditrack3.MediTrackApplication
import com.example.meditrack3.R

class MedicationReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {

        val medicationName =
            inputData.getString("medication_name") ?: return Result.failure()

        val dosage =
            inputData.getString("dosage") ?: ""

        val notification = NotificationCompat.Builder(
            applicationContext,
            MediTrackApplication.MEDICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("Medication Reminder")
            .setContentText("Time to take $medicationName $dosage")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        manager.notify(System.currentTimeMillis().toInt(), notification)

        return Result.success()
    }
}
