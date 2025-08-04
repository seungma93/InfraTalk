package com.sm.infratalk.presenter.service

import android.util.Log

// 현재 활성화된 채팅방 ID를 추적하는 object
object ActiveChatTracker {
    private var activeChatId: String? = null

    fun setActiveChat(chatId: String?) {
        activeChatId = chatId
        Log.d("ActiveChatTracker", "활성 채팅방 설정: $chatId")
    }

    fun isActiveChatWith(chatId: String): Boolean {
        return activeChatId == chatId
    }
}