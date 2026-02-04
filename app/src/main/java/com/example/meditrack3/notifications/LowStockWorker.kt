package com.example.meditrack3.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.meditrack3.MediTrackApplication
import com.example.meditrack3.R
import com.example.meditrack3.data.database.MediTrackDatabase

class LowStockWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {

        val dao = MediTrackDatabase
            .getDatabase(applicationContext)
            .medicationDao()

        val medications = dao.getAllOnce()

        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        medications.forEach { med ->
            if (med.remainingQuantity <= med.lowStockThreshold) {

                val notification = NotificationCompat.Builder(
                    applicationContext,
                    MediTrackApplication.MEDICATION_CHANNEL_ID
                )
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("Low Medication Stock")
                    .setContentText(
                        "${med.name} is running low (${med.remainingQuantity} left)"
                    )
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build()

                manager.notify(med.id, notification)
            }
        }

        return Result.success()
    }
}
