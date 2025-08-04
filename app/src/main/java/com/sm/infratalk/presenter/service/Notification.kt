package com.sm.infratalk.presenter.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sm.infratalk.R
import com.sm.infratalk.domain.chat.entity.ChatMessageNotifyEntity
import com.sm.infratalk.presenter.main.activity.MainActivity

fun createNotificationChannel(channelId: String, channelName: String, channelDescription: String, context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = channelDescription
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun createNotification(context: Context, channelId: String): Notification {
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(context, channelId)
        .setContentTitle("InfraTalk")
        .setContentText("메시지 알림이 활성화되어 있습니다")
        .setSmallIcon(R.drawable.ic_logo)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        .setAutoCancel(false)
        .setOngoing(true)
        .build()
}

fun updateNotification(chatMessageNotifyEntity: ChatMessageNotifyEntity, context: Context, channelId: String) {
    Log.d("ForegroundService", "updateNotification 호출: ${chatMessageNotifyEntity.content}")

    val groupKey = "message_notification_group"
    val notificationId = System.currentTimeMillis().toInt()

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        putExtra("room_id", chatMessageNotifyEntity.roomId)
        putExtra("sender_id", chatMessageNotifyEntity.sender)
    }

    Log.d("ForegroundService", "노티 Intent 생성: room_id=${chatMessageNotifyEntity.roomId}, sender_id=${chatMessageNotifyEntity.sender}")
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // 개별 알림 생성
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle(chatMessageNotifyEntity.sender)
        .setContentText(chatMessageNotifyEntity.content)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setContentIntent(pendingIntent)
        .setGroup(groupKey)
        .setAutoCancel(true)
        .build()

    // 그룹 요약 알림 생성
    val summaryNotification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle("")
        .setContentText("")
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setDefaults(0)
        .setSilent(true)
        .setGroup(groupKey)
        .setGroupSummary(true)
        .build()

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(notificationId, notification)  // 개별 알림
    notificationManager.notify(0, summaryNotification)       // 그룹 요약 알림
}