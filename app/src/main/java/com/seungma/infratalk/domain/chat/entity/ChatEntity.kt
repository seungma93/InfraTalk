package com.seungma.infratalk.domain.chat.entity

import android.net.Uri
import com.seungma.infratalk.domain.user.entity.UserEntity
import java.io.Serializable
import java.util.Date

data class ChatRoomCreateEntity(
    val member: List<String>,
    val chatRoomId: String?,
    val isSuccess: Boolean
)

data class ChatRoomCheckEntity(
    val member: List<String>,
    val chatRoomId: String?,
    val isChatRoom: Boolean
)

data class ChatStartEntity(
    val chatPartner: String,
    val chatRoomId: String?,
    val isSuccess: Boolean
)

data class ChatPrimaryKeyEntity(
    val partnerEmail: String,
    val chatRoomId: String
) : Serializable

data class ChatMessageSendEntity(
    val senderEmail: String,
    val sendTime: Date,
    val content: String,
    val isSuccess: Boolean
)

data class ChatMessageSend(
    val isSuccess: Boolean
)

data class ChatMessageEntity(
    val sender: UserEntity,
    val sendTime: Date,
    val content: String,
    val chatRoomId: String,
    val isLastPage: Boolean
) {
    val chatMessagePrimaryKey: String get() = sender.email + sendTime
}

data class ChatMessageListEntity(
    val chatMessageList: List<ChatMessageEntity>
)

data class ChatRoomEntity(
    val primaryKey: String,
    val roomName: String,
    val roomThumbnail: Uri?,
    val createTime: Date,
    val member: List<String>?,
    val leaveMember: List<String>?,
    val lastChatMessageEntity: LastChatMessageEntity?
)

data class ChatRoomListEntity(
    val chatRoomList: List<ChatRoomEntity>
)

data class LastChatMessageEntity(
    val senderEmail: String,
    val content: String,
    val sendTime: Date
)

data class ChatRoomLeaveEntity(
    val isSuccess: Boolean
)

data class ChatRoomLeave(
    val isSuccess: Boolean
)