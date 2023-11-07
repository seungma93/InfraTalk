package com.freetalk.data.mapper

import android.net.Uri
import com.freetalk.data.model.response.ChatMessageListResponse
import com.freetalk.data.model.response.ChatMessageResponse
import com.freetalk.data.model.response.ChatMessageSendResponse
import com.freetalk.data.model.response.ChatRoomCheckResponse
import com.freetalk.data.model.response.ChatRoomCreateResponse
import com.freetalk.data.model.response.ChatRoomListResponse
import com.freetalk.data.model.response.ChatRoomResponse
import com.freetalk.data.model.response.LastChatMessageResponse
import com.freetalk.domain.entity.ChatMessageEntity
import com.freetalk.domain.entity.ChatMessageListEntity
import com.freetalk.domain.entity.ChatMessageSendEntity
import com.freetalk.domain.entity.ChatRoomCheckEntity
import com.freetalk.domain.entity.ChatRoomCreateEntity
import com.freetalk.domain.entity.ChatRoomEntity
import com.freetalk.domain.entity.ChatRoomListEntity
import com.freetalk.domain.entity.LastChatMessageEntity
import toEntity
import java.util.Date

fun ChatRoomCreateResponse.toEntity(): ChatRoomCreateEntity {
    return ChatRoomCreateEntity(
        member = member.orEmpty(),
        chatRoomId = chatRoomId.orEmpty(),
        isSuccess = isSuccess ?: false
    )
}

fun ChatRoomCheckResponse.toEntity(): ChatRoomCheckEntity {
    return ChatRoomCheckEntity(
        member = member.orEmpty(),
        chatRoomId = chatRoomId.orEmpty(),
        isChatRoom = isChatRoom ?: false
    )
}

fun ChatMessageSendResponse.toEntity(): ChatMessageSendEntity {
    return ChatMessageSendEntity(
        senderEmail = senderEmail.orEmpty(),
        sendTime = sendTime ?: Date(),
        content = content.orEmpty(),
        isSuccess = isSuccess ?: false
    )
}

fun ChatMessageResponse.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        sender = sender?.let { it.toEntity() } ?: error(""),
        sendTime = sendTime ?: Date(),
        content = content.orEmpty(),
        chatRoomId = chatRoomId ?: error(""),
        isLastPage = isLastPage ?: false
    )
}

fun ChatMessageListResponse.toEntity(): ChatMessageListEntity {
    return ChatMessageListEntity(
        chatMessageList = chatMessageList?.let { list ->
            list.map { it.toEntity() }
        } ?: emptyList()
    )
}

fun ChatRoomResponse.toEntity(): ChatRoomEntity {
    return ChatRoomEntity(
        primaryKey = primaryKey ?: error(""),
        roomName = roomName.orEmpty(),
        roomThumbnail = roomThumbnail ?: Uri.parse(""),
        createTime = createTime ?: Date(),
        member = member ?: error(""),
        lastChatMessageEntity = lastChatMessageResponse?.toEntity()
    )
}

fun ChatRoomListResponse.toEntity(): ChatRoomListEntity {
    return ChatRoomListEntity(
        chatRoomList = chatRoomList?.let { list ->
            list.map { it.toEntity() }
        } ?: emptyList()
    )
}

fun LastChatMessageResponse.toEntity(): LastChatMessageEntity {
    return LastChatMessageEntity(
        senderEmail = senderEmail.orEmpty(),
        content = content.orEmpty(),
        sendTime = sendTime ?: Date()
    )
}