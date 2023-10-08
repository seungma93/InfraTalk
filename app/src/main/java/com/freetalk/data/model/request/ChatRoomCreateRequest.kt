package com.freetalk.data.model.request

import android.net.Uri
import java.util.Date

data class ChatRoomCreateRequest(
    val roomID: String,
    val member: List<String>,
    val roomThumbnail: Uri?,
    val chatDocument: String?,
    val lastMessage: String?,
    val lastMessageTime: Date?
    ) {
    val createTime: Date get() = Date(System.currentTimeMillis())
}