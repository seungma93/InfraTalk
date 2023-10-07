package com.freetalk.data.datasource.remote

import android.net.Uri
import android.util.Log
import com.freetalk.data.FailInsertException
import com.freetalk.data.FailSelectBoardContentException
import com.freetalk.data.FailSelectException
import com.freetalk.data.FailUpdatetException
import com.freetalk.data.model.request.BoardInsertRequest
import com.freetalk.data.model.request.BoardMetaListSelectRequest
import com.freetalk.data.model.request.BoardSelectRequest
import com.freetalk.data.model.request.BoardUpdateRequest
import com.freetalk.data.model.request.ChatRoomCreateRequest
import com.freetalk.data.model.request.UserSelectRequest
import com.freetalk.data.model.response.BoardInsertResponse
import com.freetalk.data.model.response.BoardMetaListResponse
import com.freetalk.data.model.response.BoardMetaResponse
import com.freetalk.data.model.response.ChatRoomCreateResponse
import com.freetalk.domain.entity.ImagesResultEntity
import com.freetalk.domain.entity.UserEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import toEntity
import java.util.Date
import javax.inject.Inject


interface ChatDataSource {
    suspend fun createChatRoom(chatRoomCreateRequest: ChatRoomCreateRequest): ChatRoomCreateResponse
}


class FirebaseChatRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val userDataSource: UserDataSource
) : ChatDataSource {
    private var lastDocument: DocumentSnapshot? = null

    override suspend fun createChatRoom(chatRoomCreateRequest: ChatRoomCreateRequest): ChatRoomCreateResponse {
        return kotlin.runCatching {
            val createTime = chatRoomCreateRequest.createTime
            database.collection("ChatRoom")
                .add(chatRoomCreateRequest)
                .await()

            ChatRoomCreateResponse(
                member = chatRoomCreateRequest.member,
                isSuccess = true
            )
        }.onFailure {
            throw FailInsertException("인서트에 실패 했습니다")
        }.getOrThrow()
    }

}
