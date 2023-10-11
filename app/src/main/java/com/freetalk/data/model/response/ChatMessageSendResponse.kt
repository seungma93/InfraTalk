package com.freetalk.data.model.response

import java.util.Date

data class ChatMessageSendResponse(
    val senderEmail: String,
    val sendTime: Date,
    val content: String,
    val isSuccess: Boolean
)