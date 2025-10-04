package com.dailyhabbittracker.tracker.work

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dailyhabbittracker.R
import com.dailyhabbittracker.HabitrixApp

class HydrationReminderWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val notifMgr = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, HabitrixApp.CHANNEL_HYDRATION)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(applicationContext.getString(R.string.hydration_notification_title))
            .setContentText(applicationContext.getString(R.string.hydration_notification_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notifMgr.notify(NOTIF_ID, notification)
        return Result.success()
    }
    companion object { const val NOTIF_ID = 3001 }
}

