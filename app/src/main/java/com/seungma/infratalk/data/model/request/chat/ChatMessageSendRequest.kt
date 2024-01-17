package com.seungma.infratalk.data.model.request.chat

import java.util.Date

data class ChatMessageSendRequest(
    val chatRoomId: String,
    val senderEmail: String,
    val content: String
) {
    val sendTime: Date get() = Date(System.currentTimeMillis())
}