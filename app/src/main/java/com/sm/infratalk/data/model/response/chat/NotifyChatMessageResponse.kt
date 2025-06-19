package com.sm.infratalk.data.model.response.chat

import java.util.Date

data class ChatMessageNotifyResponse(
    val roomId: String?,
    val sender: String?,
    val content: String?,
    val sendTimestamp: Date?
)