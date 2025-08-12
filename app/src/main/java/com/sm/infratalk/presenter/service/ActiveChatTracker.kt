package com.sm.infratalk.presenter.service

import android.util.Log

// 현재 활성화된 채팅방 ID를 추적하는 object
object ActiveChatTracker {
    private var activeChatRoomId: String? = null

    fun setActiveChat(chatRoomId: String?) {
        activeChatRoomId = chatRoomId
        Log.d("ActiveChatTracker", "활성 채팅방 설정: $chatRoomId")
    }

    fun isActiveChatWith(chatRoomId: String): Boolean {
        return activeChatRoomId == chatRoomId
    }
}