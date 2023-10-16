package com.freetalk.data.datasource.remote

import android.net.Uri
import android.util.Log
import com.freetalk.data.FailInsertException
import com.freetalk.data.FailSelectBoardContentException
import com.freetalk.data.FailSelectException
import com.freetalk.data.model.request.ChatMessageListLoadRequest
import com.freetalk.data.model.request.ChatMessageSendRequest
import com.freetalk.data.model.request.ChatRoomCheckRequest
import com.freetalk.data.model.request.ChatRoomCreateRequest
import com.freetalk.data.model.request.UserSelectRequest
import com.freetalk.data.model.response.BoardMetaListResponse
import com.freetalk.data.model.response.BoardMetaResponse
import com.freetalk.data.model.response.ChatMessageListResponse
import com.freetalk.data.model.response.ChatMessageResponse
import com.freetalk.data.model.response.ChatMessageSendResponse
import com.freetalk.data.model.response.ChatRoomCheckResponse
import com.freetalk.data.model.response.ChatRoomCreateResponse
import com.freetalk.domain.entity.ImagesResultEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


interface ChatDataSource {
    suspend fun createChatRoom(chatRoomCreateRequest: ChatRoomCreateRequest): ChatRoomCreateResponse
    suspend fun checkChatRoom(chatRoomCheckRequest: ChatRoomCheckRequest): ChatRoomCheckResponse
    suspend fun sendChatMessage(chatMessageSendRequest: ChatMessageSendRequest): ChatMessageSendResponse
    suspend fun loadChatMessageList(chatMessageListLoadRequest: ChatMessageListLoadRequest): ChatMessageListResponse
}


class FirebaseChatRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val userDataSource: UserDataSource
) : ChatDataSource {
    private var lastDocument: DocumentSnapshot? = null

    override suspend fun createChatRoom(chatRoomCreateRequest: ChatRoomCreateRequest): ChatRoomCreateResponse {
        return kotlin.runCatching {
            chatRoomCreateRequest.createTime
            val snapshot = database.collection("ChatRoom")
                .add(chatRoomCreateRequest)
                .await()

            ChatRoomCreateResponse(
                member = chatRoomCreateRequest.member,
                chatRoomId = snapshot.id,
                isSuccess = true
            )
        }.onFailure {
            throw FailInsertException("인서트에 실패 했습니다")
        }.getOrThrow()
    }

    override suspend fun checkChatRoom(chatRoomCheckRequest: ChatRoomCheckRequest): ChatRoomCheckResponse =
        with(chatRoomCheckRequest) {
            return kotlin.runCatching {

                val snapshot = database.collection("ChatRoom")
                    .whereArrayContains("member", member[0])
                    .get().await()

                val chatRoomId = snapshot.documents.mapNotNull { chatRoom ->
                    val members = chatRoom.data?.get("member") as? List<String>
                    if (members.isNullOrEmpty()) null else (chatRoom to members)
                }.find { (_, members) ->
                    members.contains(member[1])
                }?.let { (chatRoom, _) ->
                    chatRoom.id
                }

                ChatRoomCheckResponse(
                    member = chatRoomCheckRequest.member,
                    chatRoomId = chatRoomId,
                    isChatRoom = chatRoomId != null
                )

            }.onFailure {
                throw FailSelectBoardContentException("보드 콘텐츠 셀렉트 실패")
            }.getOrThrow()

        }

    override suspend fun sendChatMessage(chatMessageSendRequest: ChatMessageSendRequest): ChatMessageSendResponse =
        with(chatMessageSendRequest) {
            kotlin.runCatching {
                val sendTime = sendTime
                database.collection("ChatRoom")
                    .document(chatRoomId).collection("Chat")
                    .add(this)
                    .await()

                ChatMessageSendResponse(
                    senderEmail = senderEmail,
                    sendTime = sendTime,
                    content = content,
                    isSuccess = true
                )

            }.onFailure {

            }.getOrThrow()
        }

    private suspend fun getChatMessageDocuments(
        chatRoomId: String,
        limit: Long,
        startAfter: DocumentSnapshot?
    ): QuerySnapshot {
        val query = database.collection("ChatRoom")
            .document(chatRoomId)
            .collection("Chat")
            .orderBy("sendTime", Query.Direction.DESCENDING)
            .limit(limit)
        return if (startAfter != null) {
            query.startAfter(startAfter).get().await()
        } else {
            query.get().await()
        }
    }

    override suspend fun loadChatMessageList(chatMessageListLoadRequest: ChatMessageListLoadRequest): ChatMessageListResponse =
        coroutineScope {
            kotlin.runCatching {
                val snapshot = getChatMessageDocuments(
                    chatRoomId = chatMessageListLoadRequest.chatRoomId,
                    limit = 10,
                    startAfter = when (chatMessageListLoadRequest.reload) {
                        true -> lastDocument
                        false -> null
                    }
                )
                lastDocument = snapshot.documents.lastOrNull()

                snapshot.documents.map {
                    val senderEmail = it.data?.get("senderEmail")?.let { it as String } ?: error("")
                    val asyncUserInfo = async {
                        userDataSource
                            .selectUserInfo(UserSelectRequest(userEmail = senderEmail))
                    }
                    it to asyncUserInfo
                }.map { (it, deferred) ->
                    ChatMessageResponse(
                        sender = deferred.await(),
                        content = it.data?.get("content") as? String,
                        sendTime = (it.data?.get("sendTime") as? Timestamp)?.toDate(),
                        chatRoomId = it.data?.get("chatRoomId") as? String
                    )
                }.let {
                    ChatMessageListResponse(it)
                }
            }.onFailure {
                throw FailSelectException("셀렉트에 실패 했습니다", it)
            }.getOrThrow()
        }
    
}
