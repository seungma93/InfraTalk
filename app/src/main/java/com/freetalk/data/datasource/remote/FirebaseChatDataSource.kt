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
import com.freetalk.data.model.request.RealTimeChatMessageLoadRequest
import com.freetalk.data.model.request.UserSelectRequest
import com.freetalk.data.model.response.ChatMessageListResponse
import com.freetalk.data.model.response.ChatMessageResponse
import com.freetalk.data.model.response.ChatMessageSendResponse
import com.freetalk.data.model.response.ChatRoomCheckResponse
import com.freetalk.data.model.response.ChatRoomCreateResponse
import com.freetalk.data.model.response.ChatRoomListResponse
import com.freetalk.data.model.response.ChatRoomResponse
import com.freetalk.data.model.response.LastChatMessageResponse
import com.freetalk.domain.entity.BoardMetaEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.sql.Time
import javax.inject.Inject


interface ChatDataSource {
    suspend fun createChatRoom(chatRoomCreateRequest: ChatRoomCreateRequest): ChatRoomCreateResponse
    suspend fun checkChatRoom(chatRoomCheckRequest: ChatRoomCheckRequest): ChatRoomCheckResponse
    suspend fun sendChatMessage(chatMessageSendRequest: ChatMessageSendRequest): ChatMessageSendResponse
    suspend fun loadChatMessageList(chatMessageListLoadRequest: ChatMessageListLoadRequest): ChatMessageListResponse
    fun loadRealTimeChatMessage(realTimeChatMessageLoadRequest: RealTimeChatMessageLoadRequest): Flow<ChatMessageListResponse>
    suspend fun loadChatRoomList(): ChatRoomListResponse
    fun loadRealTimeChatRoom(): Flow<ChatRoomListResponse>
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
                    limit = 20,
                    startAfter = when (chatMessageListLoadRequest.reload) {
                        true -> null
                        false -> lastDocument
                    }
                )
                lastDocument = snapshot.documents.lastOrNull()
                Log.d("seungma", "Snapshot 사이즈" + snapshot.size())
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
                        chatRoomId = it.data?.get("chatRoomId") as? String,
                        isLastPage = snapshot.size() < 20
                    )
                }.let {
                    ChatMessageListResponse(it)
                }
            }.onFailure {
                throw FailSelectException("셀렉트에 실패 했습니다", it)
            }.getOrThrow()
        }

    override fun loadRealTimeChatMessage(realTimeChatMessageLoadRequest: RealTimeChatMessageLoadRequest): Flow<ChatMessageListResponse> {
        return callbackFlow {
            kotlin.runCatching {
                val snapshotListener = database.collection("ChatRoom")
                    .document(realTimeChatMessageLoadRequest.chatRoomId)
                    .collection("Chat")
                    .whereGreaterThanOrEqualTo("sendTime", Timestamp.now())
                    .orderBy("sendTime", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            //close(error.cause ?: error(""))
                            return@addSnapshotListener
                        }

                        val documents =
                            snapshot?.documentChanges?.filter { it.type == DocumentChange.Type.ADDED }
                                ?.map { it.document }
                                ?: emptyList()

                        trySend(documents)
                    }

                awaitClose {
                    snapshotListener.remove()
                }
            }.onFailure {
                it.printStackTrace()

                if (it is CancellationException) {
                    Log.d("seungma", it.stackTraceToString() + it.javaClass.toString())
                } else throw FailSelectException("셀렉트에 실패 했습니다", it)

            }.getOrThrow()
        }.map { documents ->
            coroutineScope {
                documents.map { document ->
                    val senderEmail = document.getString("senderEmail") ?: ""
                    val userInfo =
                        async { userDataSource.selectUserInfo(UserSelectRequest(userEmail = senderEmail)) }
                    userInfo to document
                }.map { (userInfo, document) ->
                    ChatMessageResponse(
                        sender = userInfo.await(),
                        content = document.getString("content"),
                        sendTime = document.getTimestamp("sendTime")?.toDate(),
                        chatRoomId = document.getString("chatRoomId"),
                        isLastPage = true
                    )
                }.let {
                    ChatMessageListResponse(it)
                }
            }
        }
    }

    override suspend fun loadChatRoomList(): ChatRoomListResponse {
        return kotlin.runCatching {
            val snapshot = database.collection("ChatRoom")
                .whereArrayContains("member", userDataSource.getUserInfo().email)
                .get().await()

            snapshot.documents.map {

                val chatSnapshot = database.collection("ChatRoom")
                    .document(it.id)
                    .collection("Chat")
                    .orderBy("sendTime", Query.Direction.DESCENDING)
                    .limit(1).get().await()

                val chatDocument = chatSnapshot.firstOrNull()

                ChatRoomResponse(
                    primaryKey = it.id,
                    roomId = it.data?.get("roomId") as? String,
                    roomThumbnail = it.data?.get("roomThumbnail") as? Uri,
                    createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                    member = it.data?.get("member") as? List<String>,
                    lastChatMessageResponse = LastChatMessageResponse(
                        senderEmail = chatDocument?.data?.get("senderEmail") as? String,
                        content = chatDocument?.data?.get("content") as? String,
                        sendTime = (chatDocument?.data?.get("sendTime") as? Timestamp)?.toDate()
                    )
                )
            }.sortedWith(
                compareByDescending {
                    it.lastChatMessageResponse?.sendTime
                        ?: it.createTime // sendTime이 null이면 createTime으로 비교, 아니면 sendTime으로 비교
                }
            ).let {
                ChatRoomListResponse(it)
            }

        }.onFailure {
            throw FailSelectException("셀렉트에 실패 했습니다", it)
        }.getOrThrow()
    }

    override fun loadRealTimeChatRoom(): Flow<ChatRoomListResponse> {
        return callbackFlow {
            kotlin.runCatching {

                val chatRoomListener = database.collection("ChatRoom")
                    .whereArrayContains("member", userDataSource.getUserInfo().email)
                    .whereGreaterThanOrEqualTo("createTime", Timestamp.now())
                    .orderBy("createTime", Query.Direction.DESCENDING)
                    .addSnapshotListener { chatSnapshot, chatError ->
                        if (chatError != null) {

                            return@addSnapshotListener
                        }

                        val documents =
                            chatSnapshot?.documentChanges?.filter { it.type == DocumentChange.Type.ADDED }
                                ?.map { it.document }
                                ?: emptyList()


                        val chatRoomListResponse = documents.map {
                            ChatRoomResponse(
                                primaryKey = it.id,
                                roomId = it.getString("roomId"),
                                roomThumbnail = it.get("roomThumbnail") as? Uri,
                                createTime = it.getTimestamp("createTime")
                                    ?.toDate(),
                                member = it.data?.get("member") as? List<String>,
                                lastChatMessageResponse = null
                            )
                        }.let {
                            ChatRoomListResponse(it)
                        }
                        trySend(chatRoomListResponse)
                    }

                val chatListener = database.collection("ChatRoom")
                    .whereArrayContains("member", userDataSource.getUserInfo().email)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            return@addSnapshotListener
                        }

                        Log.d("seungma", "실시간 채팅방 로드" + snapshot?.documentChanges?.size)

                        for (documentChange in snapshot?.documentChanges ?: emptyList()) {

                            val chatRoomDocument = documentChange.document
                            val chatRoomId = chatRoomDocument.id



                            database.collection("ChatRoom/$chatRoomId/Chat")
                                .whereGreaterThanOrEqualTo("sendTime", Timestamp.now())
                                .orderBy("sendTime", Query.Direction.DESCENDING)
                                .addSnapshotListener { chatSnapshot, chatError ->
                                    if (chatError != null) {

                                        return@addSnapshotListener
                                    }

                                    val documents =
                                        chatSnapshot?.documentChanges?.filter { it.type == DocumentChange.Type.ADDED }
                                            ?.map { it.document }
                                            ?: emptyList()


                                    val chatRoomListResponse = documents.map {
                                        ChatRoomResponse(
                                            primaryKey = chatRoomDocument.id,
                                            roomId = chatRoomDocument.getString("roomId"),
                                            roomThumbnail = chatRoomDocument.get("roomThumbnail") as? Uri,
                                            createTime = chatRoomDocument.getTimestamp("createTime")
                                                ?.toDate(),
                                            member = chatRoomDocument.data?.get("member") as? List<String>,
                                            lastChatMessageResponse = LastChatMessageResponse(
                                                senderEmail = it.getString("senderEmail"),
                                                content = it.getString("content"),
                                                sendTime = it.getTimestamp("sendTime")?.toDate()
                                            )
                                        )
                                    }.let {
                                        ChatRoomListResponse(it)
                                    }
                                    trySend(chatRoomListResponse)
                                }
                        }
                    }

                awaitClose {
                    chatRoomListener.remove()
                    chatListener.remove()
                }

            }.onFailure {

                if (it is CancellationException) {
                    Log.d("seungma", it.stackTraceToString() + it.javaClass.toString())
                } else throw FailSelectException("셀렉트에 실패 했습니다", it)

            }.getOrThrow()


        }
    }

}
