package com.freetalk.data.datasource.remote

import android.net.Uri
import android.util.Log
import com.freetalk.data.FailDeleteCommentException
import com.freetalk.data.FailInsertException
import com.freetalk.data.FailSelectBoardContentException
import com.freetalk.data.FailSelectException
import com.freetalk.data.FailUpdatetException
import com.freetalk.data.model.request.BoardDeleteRequest
import com.freetalk.data.model.request.BoardInsertRequest
import com.freetalk.data.model.request.BoardMetaListSelectRequest
import com.freetalk.data.model.request.BoardSelectRequest
import com.freetalk.data.model.request.BoardUpdateRequest
import com.freetalk.data.model.request.CommentDeleteRequest
import com.freetalk.data.model.request.MyBoardListLoadRequest
import com.freetalk.data.model.request.UserSelectRequest
import com.freetalk.data.model.response.BoardDeleteResponse
import com.freetalk.data.model.response.BoardInsertResponse
import com.freetalk.data.model.response.BoardMetaListResponse
import com.freetalk.data.model.response.BoardMetaResponse
import com.freetalk.data.model.response.CommentDeleteResponse
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


interface BoardDataSource {
    suspend fun insertBoard(boardInsertRequest: BoardInsertRequest): BoardInsertResponse
    suspend fun updateBoard(boardUpdateRequest: BoardUpdateRequest): BoardMetaResponse
    suspend fun selectBoard(boardSelectRequest: BoardSelectRequest): BoardMetaResponse
    suspend fun selectBoardMetaList(boardMetaListSelectRequest: BoardMetaListSelectRequest): BoardMetaListResponse
    suspend fun loadMyBoardList(myBoardListLoadRequest: MyBoardListLoadRequest): BoardMetaListResponse
    suspend fun deleteBoard(boardDeleteRequest: BoardDeleteRequest): BoardDeleteResponse
}


class FirebaseBoardRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val userDataSource: UserDataSource
) : BoardDataSource {
    private var lastDocument: DocumentSnapshot? = null
    private var myBoardLastDocument: DocumentSnapshot? = null

    override suspend fun insertBoard(boardInsertRequest: BoardInsertRequest): BoardInsertResponse {
        return kotlin.runCatching {
            val createTime = boardInsertRequest.createTime
            database.collection("Board")
                .add(boardInsertRequest)
                .await()

            BoardInsertResponse(
                boardAuthorEmail = boardInsertRequest.authorEmail,
                boardCreteTime = createTime,
                isSuccess = true
            )
        }.onFailure {
            throw FailInsertException("인서트에 실패 했습니다")
        }.getOrThrow()
    }

    private suspend fun getBoardDocuments(
        limit: Long,
        startAfter: DocumentSnapshot?
    ): QuerySnapshot {
        val query = database.collection("Board").orderBy("createTime", Query.Direction.DESCENDING)
            .limit(limit)
        return if (startAfter != null) {
            query.startAfter(startAfter).get().await()
        } else {
            query.get().await()
        }
    }

    override suspend fun selectBoardMetaList(boardMetaListSelectRequest: BoardMetaListSelectRequest): BoardMetaListResponse =
        coroutineScope {
            kotlin.runCatching {
                val snapshot = when (boardMetaListSelectRequest.reload) {
                    true -> getBoardDocuments(10, null)
                    false -> getBoardDocuments(10, lastDocument)
                }
                lastDocument = snapshot.documents.lastOrNull()

                snapshot.documents.map {
                    val authorEmail = it.data?.get("authorEmail")?.let { it as String } ?: error("")
                    val asyncUserInfo = async {
                        userDataSource
                            .selectUserInfo(UserSelectRequest(userEmail = authorEmail))
                    }
                    it to asyncUserInfo
                }.map { (it, deferred) ->
                    BoardMetaResponse(
                        author = deferred.await().toEntity(),
                        title = it.data?.get("title") as? String,
                        content = it.data?.get("content") as? String,
                        images = (it.data?.get("images") as? List<String>)?.let {
                            ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                        },
                        createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                        editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()
                    )
                }.let {
                    BoardMetaListResponse(it)
                }
            }.onFailure {
                throw FailSelectException("셀렉트에 실패 했습니다", it)
            }.getOrThrow()
        }


    override suspend fun updateBoard(boardUpdateRequest: BoardUpdateRequest): BoardMetaResponse {
        return kotlin.runCatching {
            database.collection("Board")
                .whereEqualTo("author.email", boardUpdateRequest.author.email)
                .whereEqualTo("createTime", boardUpdateRequest.createTime).get().await().let {

                    boardUpdateRequest.images?.let { images ->

                        boardUpdateRequest.editTime.let { date ->

                            when (boardUpdateRequest.content) {
                                null -> {
                                    val updates = mapOf(
                                        "images" to boardUpdateRequest.images,
                                        "editTime" to boardUpdateRequest.editTime
                                    )
                                    it.documents[0].reference.update(updates).await()
                                }

                                else -> {
                                    val updates = mapOf(
                                        "content" to boardUpdateRequest.content,
                                        "images" to boardUpdateRequest.images,
                                        "editTime" to boardUpdateRequest.editTime
                                    )
                                    it.documents[0].reference.update(updates).await()
                                }
                            }

                        } ?: run {
                            val updates = mapOf(
                                "images" to boardUpdateRequest.images
                            )
                            it.documents[0].reference.update(updates).await()

                        }

                    } ?: run {
                        val updates = mapOf(
                            "content" to boardUpdateRequest.content,
                            "editTime" to boardUpdateRequest.editTime
                        )
                        it.documents[0].reference.update(updates).await()

                    }
                }
            BoardMetaResponse(
                boardUpdateRequest.author,
                boardUpdateRequest.title,
                boardUpdateRequest.content,
                null,
                boardUpdateRequest.createTime,
                boardUpdateRequest.editTime
            )

        }.onFailure {
            throw FailUpdatetException("업데이트 실패")
        }.getOrThrow()
    }

    override suspend fun selectBoard(boardSelectRequest: BoardSelectRequest): BoardMetaResponse =
        with(boardSelectRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("Board")
                    .whereEqualTo("authorEmail", boardAuthorEmail)
                    .whereEqualTo("createTime", boardCreateTime).get().await()

                snapshot.documents.firstOrNull()?.let {
                    val authorEmail = it.data?.get("authorEmail")?.let { it as String } ?: error("")
                    BoardMetaResponse(
                        author = userDataSource
                            .selectUserInfo(UserSelectRequest(userEmail = authorEmail))
                            .toEntity(),
                        title = it.data?.get("title") as? String,
                        content = it.data?.get("content") as? String,
                        images = (it.data?.get("images") as? List<String>)?.let {
                            ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                        },
                        createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                        editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()
                    )
                } ?: run {
                    throw FailSelectBoardContentException("보드 콘텐츠 셀렉트 실패")
                }
            }.onFailure {
                throw FailSelectBoardContentException("보드 콘텐츠 셀렉트 실패")
            }.getOrThrow()

        }

    private suspend fun getMyBoardDocuments(
        limit: Long,
        startAfter: DocumentSnapshot?
    ): QuerySnapshot {
        val query = database.collection("Board")
            .whereEqualTo("authorEmail", userDataSource.getUserInfo().email)
            .orderBy("createTime", Query.Direction.DESCENDING)
            .limit(limit)
        return if (startAfter != null) {
            query.startAfter(startAfter).get().await()
        } else {
            query.get().await()
        }
    }

    override suspend fun loadMyBoardList(myBoardListLoadRequest: MyBoardListLoadRequest): BoardMetaListResponse =
        coroutineScope {
            kotlin.runCatching {
                val snapshot = when (myBoardListLoadRequest.reload) {
                    true -> getMyBoardDocuments(10, null)
                    false -> getMyBoardDocuments(10, myBoardLastDocument)
                }
                myBoardLastDocument = snapshot.documents.lastOrNull()

                snapshot.documents.map {
                    BoardMetaResponse(
                        author = userDataSource.getUserInfo(),
                        title = it.data?.get("title") as? String,
                        content = it.data?.get("content") as? String,
                        images = (it.data?.get("images") as? List<String>)?.let {
                            ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                        },
                        createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                        editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()
                    )
                }.let {
                    BoardMetaListResponse(it)
                }
            }.onFailure {
                throw FailSelectException("셀렉트에 실패 했습니다", it)
            }.getOrThrow()
        }

    override suspend fun deleteBoard(boardDeleteRequest: BoardDeleteRequest): BoardDeleteResponse {
        return kotlin.runCatching {
            database.collection("Board")
                .whereEqualTo("authorEmail", boardDeleteRequest.boardAuthorEmail)
                .whereEqualTo("createTime", boardDeleteRequest.boardCreateTime)
                .get().await().apply {
                    documents.forEach { it.reference.delete().await() }
                }

            BoardDeleteResponse(
                boardAuthorEmail = boardDeleteRequest.boardAuthorEmail,
                boardCreateTime = boardDeleteRequest.boardCreateTime,
                isSuccess = true
            )
        }.onFailure {
            throw FailDeleteCommentException("댓글 셀렉트에 실패 했습니다")
        }.getOrThrow()
    }

}
