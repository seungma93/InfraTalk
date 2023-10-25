package com.freetalk.domain.entity

import android.net.Uri
import com.freetalk.data.model.response.UserResponse
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
): Serializable

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
    val roomId: String,
    val roomThumbnail: Uri?,
    val createTime: Date,
    val member: List<String>,
    val lastMessage: String?,
    val lastMessageTime: Date?
) {}

data class ChatRoomListEntity(
    val chatRoomList: List<ChatRoomEntity>
)