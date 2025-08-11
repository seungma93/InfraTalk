package com.sm.infratalk.domain.chat.entity

import java.util.Date

data class ChatMessageNotifyEntity(
    val roomId: String,
    val roomName: String,
    val sender: String,
    val content: String,
    val sendTimestamp: Date
)