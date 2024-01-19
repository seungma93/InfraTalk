package com.seungma.infratalk.data.mapper

import com.seungma.infratalk.data.model.response.chat.ChatMessageListResponse
import com.seungma.infratalk.data.model.response.chat.ChatMessageResponse
import com.seungma.infratalk.data.model.response.chat.ChatMessageSendResponse
import com.seungma.infratalk.data.model.response.chat.ChatRoomCheckResponse
import com.seungma.infratalk.data.model.response.chat.ChatRoomCreateResponse
import com.seungma.infratalk.data.model.response.chat.ChatRoomLeaveResponse
import com.seungma.infratalk.data.model.response.chat.ChatRoomListResponse
import com.seungma.infratalk.data.model.response.chat.ChatRoomResponse
import com.seungma.infratalk.data.model.response.chat.LastChatMessageResponse
import com.seungma.infratalk.domain.chat.entity.ChatMessageEntity
import com.seungma.infratalk.domain.chat.entity.ChatMessageListEntity
import com.seungma.infratalk.domain.chat.entity.ChatMessageSendEntity
import com.seungma.infratalk.domain.chat.entity.ChatRoomCheckEntity
import com.seungma.infratalk.domain.chat.entity.ChatRoomCreateEntity
import com.seungma.infratalk.domain.chat.entity.ChatRoomEntity
import com.seungma.infratalk.domain.chat.entity.ChatRoomLeaveEntity
import com.seungma.infratalk.domain.chat.entity.ChatRoomListEntity
import com.seungma.infratalk.domain.chat.entity.LastChatMessageEntity
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
        roomThumbnail = roomThumbnail,
        createTime = createTime ?: Date(),
        member = member ?: emptyList(),
        leaveMember = leaveMember ?: emptyList(),
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

fun ChatRoomLeaveResponse.toEntity(): ChatRoomLeaveEntity {
    return ChatRoomLeaveEntity(
        isSuccess = isSuccess ?: false
    )
}