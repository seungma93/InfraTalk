package com.freetalk.data.model.request

data class ChatMessageListLoadRequest(
    val chatRoomId: String,
    val reload: Boolean
)