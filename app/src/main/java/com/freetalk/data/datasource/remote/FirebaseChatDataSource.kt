package com.freetalk.data.datasource.remote

import com.freetalk.data.FailInsertException
import com.freetalk.data.FailSelectBoardContentException
import com.freetalk.data.model.request.ChatMessageSendRequest
import com.freetalk.data.model.request.ChatRoomCheckRequest
import com.freetalk.data.model.request.ChatRoomCreateRequest
import com.freetalk.data.model.response.ChatMessageSendResponse
import com.freetalk.data.model.response.ChatRoomCheckResponse
import com.freetalk.data.model.response.ChatRoomCreateResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


interface ChatDataSource {
    suspend fun createChatRoom(chatRoomCreateRequest: ChatRoomCreateRequest): ChatRoomCreateResponse
    suspend fun checkChatRoom(chatRoomCheckRequest: ChatRoomCheckRequest): ChatRoomCheckResponse
}


class FirebaseChatRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : ChatDataSource {

    override suspend fun createChatRoom(chatRoomCreateRequest: ChatRoomCreateRequest): ChatRoomCreateResponse {
        return kotlin.runCatching {
            chatRoomCreateRequest.createTime
            database.collection("ChatRoom")
                .document(chatRoomCreateRequest.member.joinToString(separator = "|"))
                .set(chatRoomCreateRequest)
                .await()

            ChatRoomCreateResponse(
                member = chatRoomCreateRequest.member,
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

                ChatRoomCheckResponse(
                    member = chatRoomCheckRequest.member,
                    isChatRoom = snapshot.documents.mapNotNull {
                        it.data?.get("member") as? List<String>
                    }.any { it.contains(member[1]) }
                )

            }.onFailure {
                throw FailSelectBoardContentException("보드 콘텐츠 셀렉트 실패")
            }.getOrThrow()

        }

    suspend fun sendChatMessage(chatMessageSendRequest: ChatMessageSendRequest): ChatMessageSendResponse =
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

}
