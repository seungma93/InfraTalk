package com.freetalk.data.model.response

import java.util.Date

data class LastChatMessageResponse(
    val senderEmail: String?,
    val content: String?,
    val sendTime: Date?
)