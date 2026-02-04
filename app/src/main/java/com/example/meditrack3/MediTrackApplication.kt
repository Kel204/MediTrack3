package com.example.meditrack3

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MediTrackApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                MEDICATION_CHANNEL_ID,
                "Medication Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Medication reminders and low stock alerts"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val MEDICATION_CHANNEL_ID = "medication_channel"
    }
}
