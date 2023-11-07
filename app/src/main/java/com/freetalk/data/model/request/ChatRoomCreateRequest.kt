package com.freetalk.data.model.request

import android.net.Uri
import java.util.Date

data class ChatRoomCreateRequest(
    val roomName: String,
    val member: List<String>,
    val roomThumbnail: Uri?
    ) {
    val createTime: Date get() = Date(System.currentTimeMillis())
}