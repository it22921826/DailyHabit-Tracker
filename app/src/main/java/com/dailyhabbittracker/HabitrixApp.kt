package com.dailyhabbittracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.dailyhabbittracker.tracker.data.WellnessRepository
import com.google.android.material.color.DynamicColors

class HabitrixApp : Application() {
    lateinit var repository: WellnessRepository
        private set

    override fun onCreate() {
        super.onCreate()
        // Apply Material dynamic color where available (Android 12+)
        DynamicColors.applyToActivitiesIfAvailable(this)
        repository = WellnessRepository(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_HYDRATION,
                getString(R.string.channel_hydration_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = getString(R.string.channel_hydration_desc) }
            val mgr = getSystemService(NotificationManager::class.java)
            mgr.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_HYDRATION = " Habitrix_hydration"
    }
}
