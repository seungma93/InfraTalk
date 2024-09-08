package com.seungma.domain.repository

import com.seungma.infratalk.data.datasource.remote.chat.ChatDataSource
import com.seungma.infratalk.data.datasource.remote.user.UserDataSource
import com.seungma.infratalk.data.mapper.toEntity
import com.seungma.infratalk.data.model.request.chat.ChatMessageListLoadRequest
import com.seungma.infratalk.data.model.request.chat.ChatMessageSendRequest
import com.seungma.infratalk.data.model.request.chat.ChatRoomCheckRequest
import com.seungma.infratalk.data.model.request.chat.ChatRoomCreateRequest
import com.seungma.infratalk.data.model.request.chat.ChatRoomLeaveRequest
import com.seungma.infratalk.data.model.request.chat.ChatRoomLoadRequest
import com.seungma.infratalk.data.model.request.chat.RealTimeChatMessageLoadRequest
import com.seungma.infratalk.data.model.request.chat.RealTimeChatRoomLoadRequest
import com.seungma.infratalk.data.model.request.user.UserSelectRequest
import com.seungma.infratalk.domain.chat.entity.ChatMessageListEntity
import com.seungma.infratalk.domain.chat.entity.ChatMessageSendEntity
import com.seungma.infratalk.domain.chat.entity.ChatRoomCheckEntity
import com.seungma.infratalk.domain.chat.entity.ChatRoomCreateEntity
import com.seungma.infratalk.domain.chat.entity.ChatRoomEntity
import com.seungma.infratalk.domain.chat.entity.ChatRoomLeaveEntity
import com.seungma.infratalk.domain.chat.entity.ChatRoomListEntity
import com.seungma.infratalk.domain.chat.repository.ChatDataRepository
import com.seungma.infratalk.presenter.chat.form.ChatMessageListLoadForm
import com.seungma.infratalk.presenter.chat.form.ChatMessageSendForm
import com.seungma.infratalk.presenter.chat.form.ChatRoomCheckForm
import com.seungma.infratalk.presenter.chat.form.ChatRoomCreateForm
import com.seungma.infratalk.presenter.chat.form.ChatRoomLeaveForm
import com.seungma.infratalk.presenter.chat.form.ChatRoomLoadForm
import com.seungma.infratalk.presenter.chat.form.RealTimeChatMessageLoadForm
import com.seungma.infratalk.presenter.chat.form.RealTimeChatRoomLoadForm
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatDataRepositoryImpl @Inject constructor(
    private val chatDataSource: ChatDataSource,
    private val userDataSource: UserDataSource
) : ChatDataRepository {
    override suspend fun createChatRoom(chatRoomCreateForm: ChatRoomCreateForm): ChatRoomCreateEntity =
        coroutineScope {
            val userNicknameList = chatRoomCreateForm.member.map {
                async {
                    userDataSource.selectUserInfo(
                        userSelectRequest = UserSelectRequest(
                            userEmail = it
                        )
                    )
                }
            }.map { deferred ->
                val userResponse = deferred.await()
                userResponse.nickname
            }

            val defaultRoomName = userNicknameList.joinToString(separator = ", ")

            chatDataSource.createChatRoom(
                chatRoomCreateRequest = ChatRoomCreateRequest(
                    roomName = defaultRoomName,
                    member = chatRoomCreateForm.member,
                    roomThumbnail = null
                )
            ).toEntity()
        }

    override suspend fun checkChatRoom(chatRoomCheckForm: ChatRoomCheckForm): ChatRoomCheckEntity {
        return chatDataSource.checkChatRoom(
            chatRoomCheckRequest = ChatRoomCheckRequest(
                member = chatRoomCheckForm.member
            )
        ).toEntity()
    }

    override suspend fun sendChatMessage(chatMessageSendForm: ChatMessageSendForm): ChatMessageSendEntity =
        with(chatMessageSendForm) {
            return chatDataSource.sendChatMessage(
                chatMessageSendRequest = ChatMessageSendRequest(
                    chatRoomId = chatRoomId,
                    senderEmail = userDataSource.getUserMe().email ?: error("유저 정보 없음"),
                    content = content
                )
            ).toEntity()

        }

    override suspend fun loadChatMessageList(chatMessageListLoadForm: ChatMessageListLoadForm): ChatMessageListEntity {
        return chatDataSource.loadChatMessageList(
            chatMessageListLoadRequest = ChatMessageListLoadRequest(
                chatRoomId = chatMessageListLoadForm.chatRoomId,
                reload = chatMessageListLoadForm.reload
            )
        ).toEntity()
    }

    override fun loadRealTimeChatMessage(realTimeChatMessageLoadForm: RealTimeChatMessageLoadForm): Flow<ChatMessageListEntity> {
        return chatDataSource.loadRealTimeChatMessage(
            realTimeChatMessageLoadRequest = RealTimeChatMessageLoadRequest(
                chatRoomId = realTimeChatMessageLoadForm.chatRoomId
            )
        ).map { it.toEntity() }
    }

    override suspend fun loadChatRoomList(): ChatRoomListEntity {
        return chatDataSource.loadChatRoomList().toEntity()
    }

    override fun loadRealTimeChatRoomList(): Flow<ChatRoomListEntity> {
        return chatDataSource.loadRealTimeChatRoomList().map { it.toEntity() }
    }

    override suspend fun loadChatRoom(chatRoomLoadForm: ChatRoomLoadForm): ChatRoomEntity {
        return chatDataSource.loadChatRoom(
            chatRoomLoadRequest = ChatRoomLoadRequest(
                chatRoomId = chatRoomLoadForm.chatRoomId
            )
        ).toEntity()
    }

    override suspend fun leaveChatRoom(chatRoomLeaveForm: ChatRoomLeaveForm): ChatRoomLeaveEntity {
        return chatDataSource.leaveChatRoom(chatRoomLeaveRequest = ChatRoomLeaveRequest(chatRoomId = chatRoomLeaveForm.chatRoomId))
            .toEntity()
    }

    override fun loadRealTimeChatRoom(realTimeChatRoomLoadForm: RealTimeChatRoomLoadForm): Flow<ChatRoomEntity> {
        return chatDataSource.loadRealTimeChatRoom(
            realTimeChatRoomLoadRequest = RealTimeChatRoomLoadRequest(
                chatRoomId = realTimeChatRoomLoadForm.chatRoomId
            )
        ).map { it.toEntity() }
    }
}