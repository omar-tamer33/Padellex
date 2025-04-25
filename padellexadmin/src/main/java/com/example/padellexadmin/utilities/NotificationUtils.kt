package com.example.padellexadmin.utilities

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.padellexadmin.R
import kotlin.random.Random


object NotificationUtils {
    @SuppressLint("NotificationPermission")
    fun showLocalNotification(context: Context, message: String) {
        val channelId = "default_channel_id"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }


        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Notification")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_schedule)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 300, 150, 300))
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }

}