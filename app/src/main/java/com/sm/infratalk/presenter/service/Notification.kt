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