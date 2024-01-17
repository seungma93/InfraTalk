package com.seungma.infratalk.data.model.response.chat

import java.util.Date

data class ChatMessageSendResponse(
    val senderEmail: String?,
    val sendTime: Date?,
    val content: String?,
    val isSuccess: Boolean?
)