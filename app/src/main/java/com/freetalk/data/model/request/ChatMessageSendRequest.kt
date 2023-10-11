package com.freetalk.data.model.request

import java.util.Date

data class ChatMessageSendRequest(
    val chatRoomId: String,
    val senderEmail: String,
    val content: String
) {
    val sendTime: Date get() = Date(System.currentTimeMillis())
}