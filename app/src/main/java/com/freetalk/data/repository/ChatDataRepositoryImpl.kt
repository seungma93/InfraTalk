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
import com.freetalk.data.model.request.ChatRoomCheckRequest
import com.freetalk.data.model.request.ChatRoomCreateRequest
import com.freetalk.data.model.request.UserSelectRequest
import com.freetalk.domain.entity.BoardInsertEntity
import com.freetalk.domain.entity.BoardMetaEntity
import com.freetalk.domain.entity.BoardMetaListEntity
import com.freetalk.domain.entity.ChatRoomCheckEntity
import com.freetalk.domain.entity.ChatRoomCreateEntity
import com.freetalk.presenter.form.BoardContentInsertForm
import com.freetalk.presenter.form.BoardListLoadForm
import com.freetalk.presenter.form.BoardLoadForm
import com.freetalk.presenter.form.BoardUpdateForm
import com.freetalk.presenter.form.ChatRoomCheckForm
import com.freetalk.presenter.form.ChatRoomCreateForm
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

    suspend fun checkChatRoom(chatRoomCheckForm: ChatRoomCheckForm): ChatRoomCheckEntity {
        return chatDataSource.checkChatRoom(
            chatRoomCheckRequest = ChatRoomCheckRequest(
                member = chatRoomCheckForm.member
            )
        ).toEntity()
    }
}