package com.freetalk.domain.repository

import com.freetalk.data.UserSingleton
import com.freetalk.data.datasource.remote.BoardDataSource
import com.freetalk.data.datasource.remote.ChatDataSource
import com.freetalk.data.datasource.remote.UserDataSource
import com.freetalk.data.mapper.toEntity
import com.freetalk.data.model.request.BoardInsertRequest
import com.freetalk.data.model.request.BoardMetaListSelectRequest
import com.freetalk.data.model.request.BoardSelectRequest
import com.freetalk.data.model.request.BoardUpdateRequest
import com.freetalk.data.model.request.ChatMessageListLoadRequest
import com.freetalk.data.model.request.ChatMessageSendRequest
import com.freetalk.data.model.request.ChatRoomCheckRequest
import com.freetalk.data.model.request.ChatRoomCreateRequest
import com.freetalk.data.model.request.RealTimeChatMessageLoadRequest
import com.freetalk.data.model.request.UserSelectRequest
import com.freetalk.domain.entity.BoardInsertEntity
import com.freetalk.domain.entity.BoardMetaEntity
import com.freetalk.domain.entity.BoardMetaListEntity
import com.freetalk.domain.entity.ChatMessageListEntity
import com.freetalk.domain.entity.ChatMessageSendEntity
import com.freetalk.domain.entity.ChatRoomCheckEntity
import com.freetalk.domain.entity.ChatRoomCreateEntity
import com.freetalk.domain.entity.ChatRoomListEntity
import com.freetalk.presenter.form.BoardContentInsertForm
import com.freetalk.presenter.form.BoardListLoadForm
import com.freetalk.presenter.form.BoardLoadForm
import com.freetalk.presenter.form.BoardUpdateForm
import com.freetalk.presenter.form.ChatMessageListLoadForm
import com.freetalk.presenter.form.ChatMessageSendForm
import com.freetalk.presenter.form.ChatRoomCheckForm
import com.freetalk.presenter.form.ChatRoomCreateForm
import com.freetalk.presenter.form.RealTimeChatMessageLoadForm
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import toEntity
import java.util.Date
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

            chatDataSource.createChatRoom(
                chatRoomCreateRequest = ChatRoomCreateRequest(
                    roomID = userNicknameList.joinToString(separator = "|"),
                    member = chatRoomCreateForm.member,
                    roomThumbnail = null,
                    chatDocument = null,
                    lastMessage = null,
                    lastMessageTime = null
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
                    senderEmail = userDataSource.getUserInfo().email,
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
}