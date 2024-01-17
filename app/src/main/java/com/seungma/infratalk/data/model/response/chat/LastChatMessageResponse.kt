package com.seungma.infratalk.data.model.response.chat

import java.util.Date

data class LastChatMessageResponse(
    val senderEmail: String?,
    val content: String?,
    val sendTime: Date?
)