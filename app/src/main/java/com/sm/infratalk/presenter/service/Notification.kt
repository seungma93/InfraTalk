package com.sm.infratalk.presenter.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sm.infratalk.R
import com.sm.infratalk.presenter.main.activity.MainActivity

// 현재 활성화된 채팅방 ID를 추적하는 object
object ActiveChatTracker {
    private var activeChatId: String? = null

    fun setActiveChat(chatId: String?) {
        activeChatId = chatId
        Log.d("ActiveChatTracker", "활성 채팅방 설정: $chatId")
    }

    fun getActiveChat(): String? = activeChatId

    fun isActiveChatWith(chatId: String): Boolean {
        return activeChatId == chatId
    }
}

fun showNotification(context: Context, sender: String, message: String) {
    Log.d("seungma", "쇼 노티")

    val channelId = "message_notification_channel"
    val groupKey = "message_notification_group"
    val notificationId = System.currentTimeMillis().toInt()
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra("open_chat", true) // 메시지 화면을 열기 위한 플래그 전달
        putExtra("chat_id", sender) // 특정 채팅방을 열기 위해 채팅방 ID 전달
    }
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    // Notification 생성
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle(sender)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setContentIntent(pendingIntent) // 알림 클릭 시 실행할 Intent 설정
        .setGroup(groupKey)
        .setAutoCancel(true) // 클릭 시 알림 자동 제거
        .build()
    // 그룹 요약 알림 생성
    val summaryNotification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle("")
        .setContentText("")
        .setPriority(NotificationCompat.PRIORITY_MIN) // 헤드업 제거
        .setDefaults(0) // 헤드업 제거
        .setSilent(true) // 헤드업 제거
        .setGroup(groupKey)
        .setGroupSummary(true)
        .build()

    // Notification Channel 생성 (API 26 이상 필요)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "message_notification_channel"
        val descriptionText = "Channel Description"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        // 시스템에 채널 등록
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Notification 표시
    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, notification) // 개별 알림
        notify(0, summaryNotification) // 그룹 요약 알림
    }
}