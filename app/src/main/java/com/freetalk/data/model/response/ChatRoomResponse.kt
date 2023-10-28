package com.freetalk.data.model.response

import android.net.Uri
import java.util.Date

data class ChatRoomResponse(
    val primaryKey: String?,
    val roomId: String?,
    val roomThumbnail: Uri?,
    val createTime: Date?,
    val member: List<String>?,
    val lastChatMessageResponse: LastChatMessageResponse?
)