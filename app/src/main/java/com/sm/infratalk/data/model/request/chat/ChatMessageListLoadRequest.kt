package com.sm.infratalk.data.model.request.chat

data class ChatMessageListLoadRequest(
    val chatRoomId: String,
    val reload: Boolean
)