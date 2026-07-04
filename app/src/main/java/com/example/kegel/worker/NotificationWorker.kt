package com.example.kegel.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "kegel_channel"
        if (notificationManager.getNotificationChannel(channelId) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(channelId, "Kegel lembretes", NotificationManager.IMPORTANCE_HIGH)
            )
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Hora do seu autocuidado")
            .setContentText(inputData.getString(KEY_CONTENT_TEXT))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(inputData.getInt(KEY_NOTIFICATION_ID, 0), notification)
        return Result.success()
    }

    companion object {
        const val KEY_NOTIFICATION_ID = "notification_id"
        const val KEY_CONTENT_TEXT = "content_text"
    }
}
