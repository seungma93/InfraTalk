package com.seungma.infratalk.data.model.response.chat

import android.net.Uri
import java.util.Date

data class ChatRoomResponse(
    val primaryKey: String?,
    val roomName: String?,
    val roomThumbnail: Uri?,
    val createTime: Date?,
    val member: List<String>?,
    val leaveMember: List<String>?,
    val lastChatMessageResponse: LastChatMessageResponse?
)