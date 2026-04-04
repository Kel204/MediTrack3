package com.example.meditrack3.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.meditrack3.MainActivity
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

        // 👉 Intent to open app + navigate
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("navigate_to", "home")
            putExtra("medication_name", medicationName) // optional
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            MediTrackApplication.MEDICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher) // better than launcher icon
            .setContentTitle("Medication Reminder")
            .setContentText("Time to take $medicationName ($dosage)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        manager.notify(System.currentTimeMillis().toInt(), notification)

        return Result.success()
    }
}