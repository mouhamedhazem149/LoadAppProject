package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.udacity.Models.FileItem

private val NOTIFICATION_ID = 0

fun NotificationManager.createChannel(channelId: String, channelName: String,channelDescription : String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setShowBadge(false)
        }

        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.description = channelDescription

        createNotificationChannel(notificationChannel)

    }
}

fun NotificationManager.sendNotification(messageTitle : String, messageBody: String, fileItem : FileItem, applicationContext: Context) {

    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val iconImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.ic_assistant_black_24dp
    )

    //https://developer.android.com/develop/ui/views/notifications/navigation
    val fileIntent = Intent(applicationContext, DetailActivity::class.java)
    fileIntent.putExtra(applicationContext.getString(R.string.file_package_key), fileItem)

    val filePendingIntent = TaskStackBuilder.create(applicationContext).run {
        addNextIntentWithParentStack(fileIntent)
        getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    val notifColor = when(fileItem.fileStatus) {
        DownloadManager.STATUS_PAUSED -> Color.YELLOW
        DownloadManager.STATUS_PENDING -> Color.YELLOW
        DownloadManager.STATUS_RUNNING -> Color.BLUE
        DownloadManager.STATUS_SUCCESSFUL -> Color.GREEN
        DownloadManager.STATUS_FAILED -> Color.RED
        else -> Color.YELLOW
    }

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )

        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(messageTitle)
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setColor(notifColor)
        .setAutoCancel(true)
        .setLargeIcon(iconImage)

        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.check_string),
            filePendingIntent
        )

        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
