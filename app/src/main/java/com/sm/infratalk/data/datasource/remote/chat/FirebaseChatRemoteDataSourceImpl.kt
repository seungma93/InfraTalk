package com.sm.infratalk.data.datasource.remote.chat

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.sm.infratalk.data.datasource.remote.user.UserDataSource
import com.sm.infratalk.data.model.request.chat.ChatMessageListLoadRequest
import com.sm.infratalk.data.model.request.chat.ChatMessageNotifyRequest
import com.sm.infratalk.data.model.request.chat.ChatMessageSendRequest
import com.sm.infratalk.data.model.request.chat.ChatRoomCheckRequest
import com.sm.infratalk.data.model.request.chat.ChatRoomCreateRequest
import com.sm.infratalk.data.model.request.chat.ChatRoomLeaveRequest
import com.sm.infratalk.data.model.request.chat.ChatRoomLoadRequest
import com.sm.infratalk.data.model.request.chat.RealTimeChatMessageLoadRequest
import com.sm.infratalk.data.model.request.chat.RealTimeChatRoomLoadRequest
import com.sm.infratalk.data.model.request.user.UserSelectRequest
import com.sm.infratalk.data.model.response.chat.ChatMessageListResponse
import com.sm.infratalk.data.model.response.chat.ChatMessageResponse
import com.sm.infratalk.data.model.response.chat.ChatMessageSendResponse
import com.sm.infratalk.data.model.response.chat.ChatRoomCheckResponse
import com.sm.infratalk.data.model.response.chat.ChatRoomCreateResponse
import com.sm.infratalk.data.model.response.chat.ChatRoomLeaveResponse
import com.sm.infratalk.data.model.response.chat.ChatRoomListResponse
import com.sm.infratalk.data.model.response.chat.ChatRoomResponse
import com.sm.infratalk.data.model.response.chat.LastChatMessageResponse
import com.sm.infratalk.data.model.response.chat.NotifyChatMessageResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject



class FirebaseChatRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val userDataSource: UserDataSource
) : ChatDataSource {
    private var lastDocument: DocumentSnapshot? = null
    //private val _chatEvent = MutableSharedFlow<NotifyChatEvent>()
    //val chatEvent: SharedFlow<NotifyChatEvent> get() = _chatEvent.asSharedFlow()

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
            throw com.sm.infratalk.data.FailInsertException("인서트에 실패 했습니다")
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
                throw com.sm.infratalk.data.FailSelectBoardContentException("보드 콘텐츠 셀렉트 실패")
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
                throw com.sm.infratalk.data.FailSelectException("셀렉트에 실패 했습니다", it)
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
                } else throw com.sm.infratalk.data.FailSelectException(
                    "셀렉트에 실패 했습니다",
                    it
                )

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
            val userEmail = userDataSource.getUserMe().email ?: error("유저 정보 없음")
            val snapshot = database.collection("ChatRoom")
                .whereArrayContains("member", userEmail)
                .get().await()
            Log.d("seungma", "채팅방 갯수" + snapshot.documents.size)
            snapshot.documents.map {

                val chatSnapshot = database.collection("ChatRoom")
                    .document(it.id)
                    .collection("Chat")
                    .orderBy("sendTime", Query.Direction.DESCENDING)
                    .limit(1).get().await()

                val chatDocument = chatSnapshot.firstOrNull()

                ChatRoomResponse(
                    primaryKey = it.id,
                    roomName = it.data?.get("roomName") as? String,
                    roomThumbnail = it.data?.get("roomThumbnail") as? Uri,
                    createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                    member = it.data?.get("member") as? List<String>,
                    leaveMember = it.data?.get("leaveMember") as? List<String>,
                    lastChatMessageResponse = if (chatDocument?.data?.get("senderEmail") != null) {
                        LastChatMessageResponse(
                            senderEmail = chatDocument.data["senderEmail"] as String,
                            content = chatDocument.data["content"] as? String,
                            sendTime = (chatDocument.data["sendTime"] as? Timestamp)?.toDate()
                        )
                    } else {
                        null
                    }
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
            throw com.sm.infratalk.data.FailSelectException("셀렉트에 실패 했습니다", it)
        }.getOrThrow()
    }

    override fun loadRealTimeChatRoomList(): Flow<ChatRoomListResponse> {
        return callbackFlow {
            kotlin.runCatching {

                val userEmail = userDataSource.getUserMe().email ?: error("유저 정보 존재 하지 않음.")

                val chatRoomListener = database.collection("ChatRoom")
                    .whereArrayContains("member", userEmail)
                    .whereGreaterThanOrEqualTo("createTime", Timestamp.now())
                    .orderBy("createTime", Query.Direction.DESCENDING)
                    .addSnapshotListener { chatSnapshot, chatError ->
                        if (chatError != null) {

                            return@addSnapshotListener
                        }

                        val documents =
                            chatSnapshot?.documentChanges?.filter { it.type == DocumentChange.Type.ADDED || it.type == DocumentChange.Type.MODIFIED }
                                ?.map { it.document }
                                ?: emptyList()


                        val chatRoomListResponse = documents.map {
                            ChatRoomResponse(
                                primaryKey = it.id,
                                roomName = it.getString("roomName"),
                                roomThumbnail = it.get("roomThumbnail") as? Uri,
                                createTime = it.getTimestamp("createTime")
                                    ?.toDate(),
                                member = it.data?.get("member") as? List<String>,
                                leaveMember = it.data?.get("leaveMember") as? List<String>,
                                lastChatMessageResponse = null
                            )
                        }.let {
                            ChatRoomListResponse(it)
                        }
                        trySend(chatRoomListResponse)
                    }

                val chatListener = database.collection("ChatRoom")
                    .whereArrayContains("member", userEmail)
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
                                            roomName = chatRoomDocument.getString("roomName"),
                                            roomThumbnail = chatRoomDocument.get("roomThumbnail") as? Uri,
                                            createTime = chatRoomDocument.getTimestamp("createTime")
                                                ?.toDate(),
                                            member = chatRoomDocument.data?.get("member") as? List<String>,
                                            leaveMember = chatRoomDocument.data?.get("leaveMember") as? List<String>,
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
                } else throw com.sm.infratalk.data.FailSelectException(
                    "셀렉트에 실패 했습니다",
                    it
                )

            }.getOrThrow()


        }
    }

    override suspend fun loadChatRoom(chatRoomLoadRequest: ChatRoomLoadRequest): ChatRoomResponse {
        return kotlin.runCatching {
            val snapshot = database.collection("ChatRoom")
                .document(chatRoomLoadRequest.chatRoomId)
                .get().await()

            snapshot?.let {
                ChatRoomResponse(
                    primaryKey = it.id,
                    roomName = it.data?.get("roomName") as? String,
                    roomThumbnail = it.data?.get("roomThumbnail") as? Uri,
                    createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                    member = it.data?.get("member") as? List<String>,
                    leaveMember = it.data?.get("leaveMember") as? List<String>,
                    lastChatMessageResponse = null
                )
            } ?: run {
                throw error("")//FailSelectException("셀렉트에 실패 했습니다", it)
            }


        }.onFailure {

            if (it is CancellationException) {
                Log.d("seungma", it.stackTraceToString() + it.javaClass.toString())
            } else throw com.sm.infratalk.data.FailSelectException("셀렉트에 실패 했습니다", it)

        }.getOrThrow()

    }

    override fun loadRealTimeChatRoom(realTimeChatRoomLoadRequest: RealTimeChatRoomLoadRequest): Flow<ChatRoomResponse> {
        return callbackFlow {
            kotlin.runCatching {
                val snapshotListener = database.collection("ChatRoom")
                    .document(realTimeChatRoomLoadRequest.chatRoomId)
                    .addSnapshotListener { snapshot, e ->

                        if (e != null) {

                            return@addSnapshotListener
                        }

                        if (snapshot != null && snapshot.exists()) {
                            // ChatRoom 데이터를 가져와서 ChatRoom 객체로 변환
                            val chatRoomResponse = snapshot?.let {
                                ChatRoomResponse(
                                    primaryKey = it.id,
                                    roomName = it.data?.get("roomName") as? String,
                                    roomThumbnail = it.data?.get("roomThumbnail") as? Uri,
                                    createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                                    member = it.data?.get("member") as? List<String>,
                                    leaveMember = it.data?.get("leaveMember") as? List<String>,
                                    lastChatMessageResponse = null
                                )
                            } ?: run {
                                throw error("")//FailSelectException("셀렉트에 실패 했습니다", it)
                            }
                            trySend(chatRoomResponse)
                        }
                    }

                awaitClose {
                    snapshotListener.remove()
                }
            }.onFailure {

                if (it is CancellationException) {
                    Log.d("seungma", it.stackTraceToString() + it.javaClass.toString())
                } else throw com.sm.infratalk.data.FailSelectException(
                    "셀렉트에 실패 했습니다",
                    it
                )

            }.getOrThrow()

        }
    }

    /**
     * 사용자가 속한 채팅방의 새로운 메시지를 실시간으로 감지하여 알림을 보내는 함수
     * @param chatMessageNotifyRequest 알림을 받을 사용자의 이메일이 포함된 요청 객체
     * @return Flow<NotifyChatMessageResponse> 새로운 메시지가 있을 때마다 알림을 전송하는 Flow
     */
    override fun notifyChatMessage(chatMessageNotifyRequest: ChatMessageNotifyRequest): Flow<NotifyChatMessageResponse> {
        return callbackFlow {
            // 모든 리스너를 관리하기 위한 리스트
            val listeners = mutableListOf<ListenerRegistration>()
            
            kotlin.runCatching {
                // 1. 사용자가 속한 채팅방들의 변경사항을 감지하는 리스너
                val chatRoomListener = database.collection("ChatRoom")
                    .whereArrayContains("member", chatMessageNotifyRequest.email)
                    .addSnapshotListener { chatRoomSnapshot, chatRoomError ->
                        if (chatRoomError != null) {
                            return@addSnapshotListener
                        }

                        // 2. 각 채팅방의 변경사항에 대해 처리
                        chatRoomSnapshot?.documentChanges?.forEach { chatRoomChange ->
                            val chatRoomId = chatRoomChange.document.id
                            
                            // 3. 각 채팅방의 새로운 메시지를 감지하는 리스너
                            val chatListener = database.collection("ChatRoom")
                                .document(chatRoomId)
                                .collection("Chat")
                                .whereGreaterThanOrEqualTo("sendTime", Timestamp.now())
                                .orderBy("sendTime", Query.Direction.DESCENDING)
                                .addSnapshotListener { chatSnapshot, chatError ->
                                    if (chatError != null) {
                                        return@addSnapshotListener
                                    }

                                    // 4. 새로 추가된 메시지만 필터링하여 전송
                                    chatSnapshot?.documentChanges
                                        ?.filter { it.type == DocumentChange.Type.ADDED }
                                        ?.forEach { chatChange ->
                                            val document = chatChange.document
                                            trySend(document)
                                        }
                                }
                            
                            // 5. 채팅 메시지 리스너를 관리 리스트에 추가
                            listeners.add(chatListener)
                        }
                    }
                
                // 6. 채팅방 리스너도 관리 리스트에 추가
                listeners.add(chatRoomListener)

                // 7. Flow가 종료될 때 모든 리스너 제거
                awaitClose {
                    listeners.forEach { it.remove() }
                }
            }.onFailure {
                it.printStackTrace()

                if (it is CancellationException) {
                    Log.d("seungma", it.stackTraceToString() + it.javaClass.toString())
                } else throw com.sm.infratalk.data.FailSelectException(
                    "셀렉트에 실패 했습니다",
                    it
                )

            }.getOrThrow()
        }.map { document ->
            // 8. 문서를 NotifyChatMessageResponse 형태로 변환
            NotifyChatMessageResponse(
                roomId = document.getString("chatRoomId") ?: "",      // 채팅방 ID
                sender = document.getString("senderEmail") ?: "",     // 메시지 발신자 이메일
                content = document.getString("content") ?: "",        // 메시지 내용
                sendTimestamp = document.getTimestamp("sendTime")?.toDate() ?: Timestamp.now().toDate()  // 전송 시간
            )
        }
    }

    override suspend fun leaveChatRoom(chatRoomLeaveRequest: ChatRoomLeaveRequest): ChatRoomLeaveResponse {
        return kotlin.runCatching {
            val snapshot = database.collection("ChatRoom")
                .document(chatRoomLeaveRequest.chatRoomId)
                .get().await()

            snapshot?.let {
                val member = it.data?.get("member") as? List<String>
                val leaveMember = it.data?.get("leaveMember") as? List<String>
                val user = userDataSource.getUserMe().email ?: error("유저 정보 없음")
                val updateField = hashMapOf(
                    "member" to member?.filterNot { item -> item == user },
                    "leaveMember" to when (leaveMember.isNullOrEmpty()) {
                        false -> {
                            val mutableList = leaveMember.toMutableList()
                            mutableList.add(user)
                            mutableList.toList()
                        }

                        true -> member?.filter { item -> item == user }
                    },
                    "roomName" to when (member?.size) {
                        2 -> "대화 상대 없음"
                        else -> "빈 대화"
                    }
                )
                database.collection("ChatRoom")
                    .document(chatRoomLeaveRequest.chatRoomId)
                    .update(updateField)
                    .await()

                ChatRoomLeaveResponse(
                    isSuccess = true
                )

            } ?: run {
                throw error("")
            }
        }.onFailure {
            throw com.sm.infratalk.data.FailSelectException("셀렉트에 실패 했습니다", it)
        }.getOrThrow()
    }


}
