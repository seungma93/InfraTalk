package com.freetalk.data.datasource.remote

import android.net.Uri
import android.util.Log
import com.freetalk.data.*
import com.freetalk.data.model.request.BoardRelatedAllCommentMetaListSelectRequest
import com.freetalk.data.model.request.CommentDeleteRequest
import com.freetalk.data.model.request.CommentMetaListSelectRequest
import com.freetalk.data.model.request.MyBoardListLoadRequest
import com.freetalk.data.model.request.MyCommentListLoadRequest
import com.freetalk.data.model.request.UserSelectRequest
import com.freetalk.data.model.response.BoardMetaListResponse
import com.freetalk.data.model.response.BoardMetaResponse
import com.freetalk.data.model.response.CommentDeleteResponse
import com.freetalk.data.model.response.CommentMetaListResponse
import com.freetalk.data.model.response.CommentMetaResponse
import com.freetalk.domain.entity.ImagesResultEntity
import com.freetalk.presenter.form.CommentInsertRequest
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import toEntity
import java.util.*
import javax.inject.Inject


interface CommentDataSource {
    suspend fun insertComment(commentInsertRequest: CommentInsertRequest): CommentMetaResponse
    suspend fun selectCommentMetaList(commentMetaListSelectRequest: CommentMetaListSelectRequest): CommentMetaListResponse
    suspend fun selectRelatedAllCommentMetaList(
        boardRelatedAllCommentMetaListSelectRequest: BoardRelatedAllCommentMetaListSelectRequest
    ): CommentMetaListResponse
    suspend fun deleteComment(commentDeleteRequest: CommentDeleteRequest): CommentDeleteResponse
    suspend fun loadMyCommentList(myCommentListLoadRequest: MyCommentListLoadRequest): CommentMetaListResponse
}


class FirebaseCommentRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val userDataSource: UserDataSource
) : CommentDataSource {
    private var lastDocument: DocumentSnapshot? = null
    private var myCommentLastDocument: DocumentSnapshot? = null

    override suspend fun insertComment(commentInsertRequest: CommentInsertRequest): CommentMetaResponse =
        with(commentInsertRequest) {
            return kotlin.runCatching {
                database.collection("Comment")
                    .add(commentInsertRequest.copy(createTime = Date(System.currentTimeMillis())))
                    .await()
                val userResponse =
                    userDataSource.selectUserInfo(UserSelectRequest(userEmail = commentInsertRequest.authorEmail))
                CommentMetaResponse(
                    author = userResponse,
                    createTime = createTime,
                    content = content,
                    boardAuthorEmail = boardAuthorEmail,
                    boardCreateTime = boardCreateTime,
                    editTime = null
                )
            }.onFailure {
                throw FailInsertCommentException("댓글 인서트에 실패 했습니다")
            }.getOrThrow()
        }

    private suspend fun getCommentDocuments(
        commentMetaListSelectRequest: CommentMetaListSelectRequest,
        limit: Long,
        startAfter: DocumentSnapshot?
    ): QuerySnapshot {
        val query = database.collection("Comment")
            .whereEqualTo("boardAuthorEmail", commentMetaListSelectRequest.boardAuthorEmail)
            .whereEqualTo("boardCreateTime", commentMetaListSelectRequest.boardCreateTime)
            .orderBy("createTime", Query.Direction.ASCENDING)
            .limit(limit)
        return if (startAfter != null) {
            query.startAfter(startAfter).get().await()
        } else {
            query.get().await()
        }

    }

    override suspend fun selectCommentMetaList(commentMetaListSelectRequest: CommentMetaListSelectRequest): CommentMetaListResponse =
        coroutineScope {
            kotlin.runCatching {
                val snapshot = when (commentMetaListSelectRequest.reload) {
                    true -> getCommentDocuments(commentMetaListSelectRequest, 10, null)
                    false -> getCommentDocuments(commentMetaListSelectRequest, 10, lastDocument)
                }
                lastDocument = snapshot.documents.lastOrNull()
                snapshot.documents.map {
                    val authorEmail = it.data?.get("authorEmail")?.let { it as String } ?: error("")
                    val asyncUserInfo =
                        async { userDataSource.selectUserInfo(UserSelectRequest(userEmail = authorEmail)) }
                    it to asyncUserInfo
                }.map { (it, deferred) ->
                    CommentMetaResponse(
                        author = deferred.await(),
                        createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                        content = it.data?.get("content") as? String,
                        images = (it.data?.get("images") as? List<String>)?.let {
                            ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                        },
                        boardAuthorEmail = it.data?.get("boardAuthorEmail") as? String ?: "",
                        boardCreateTime = (it.data?.get("boardCreateTime") as? Timestamp)?.toDate(),
                        editTime = (it.data?.get("editTime") as? Timestamp)?.toDate(),
                        isLastPage = snapshot.size() < 10
                    )
                }.let {
                    CommentMetaListResponse(it)
                }
            }.onFailure {
                throw FailSelectCommentsException("댓글 셀렉트에 실패 했습니다")
                Log.d("comment", "데이터 소스 에러" + it.message)
            }.getOrThrow()
        }

    override suspend fun selectRelatedAllCommentMetaList(
        boardRelatedAllCommentMetaListSelectRequest: BoardRelatedAllCommentMetaListSelectRequest
    ): CommentMetaListResponse = coroutineScope {

        kotlin.runCatching {
            val query = database.collection("Comment")
                .whereEqualTo(
                    "boardAuthorEmail",
                    boardRelatedAllCommentMetaListSelectRequest.boardAuthorEmail
                )
                .whereEqualTo(
                    "boardCreateTime",
                    boardRelatedAllCommentMetaListSelectRequest.boardCreateTime
                ).orderBy("createTime", Query.Direction.ASCENDING)

            val snapshot = query.get().await()

            snapshot.documents.map {
                val authorEmail = it.data?.get("authorEmail")?.let { it as String } ?: error("")
                val asyncUserInfo =
                    async { userDataSource.selectUserInfo(UserSelectRequest(userEmail = authorEmail)) }
                it to asyncUserInfo
            }.map { (it, deferred) ->
                CommentMetaResponse(
                    author = deferred.await(),
                    createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                    content = it.data?.get("content") as? String,
                    images = (it.data?.get("images") as? List<String>)?.let {
                        ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                    },
                    boardAuthorEmail = it.data?.get("boardAuthorEmail") as? String ?: "",
                    boardCreateTime = (it.data?.get("boardCreateTime") as? Timestamp)?.toDate(),
                    editTime = (it.data?.get("editTime") as? Timestamp)?.toDate(),
                    isLastPage = true
                )
            }.let {
                CommentMetaListResponse(it)
            }
        }.onFailure {
            throw FailSelectCommentsException("댓글 셀렉트에 실패 했습니다")
        }.getOrThrow()
    }

    override suspend fun deleteComment(commentDeleteRequest: CommentDeleteRequest): CommentDeleteResponse {
        return kotlin.runCatching {
            database.collection("Comment")
                .whereEqualTo("authorEmail", commentDeleteRequest.commentAuthorEmail)
                .whereEqualTo("createTime", commentDeleteRequest.commentCreateTime)
                .get().await().apply {
                    documents.forEach { it.reference.delete().await() }
                }

            CommentDeleteResponse(
                commentAuthorEmail = commentDeleteRequest.commentAuthorEmail,
                commentCreateTime = commentDeleteRequest.commentCreateTime,
                isSuccess = true
            )
        }.onFailure {
            throw FailDeleteCommentException("댓글 셀렉트에 실패 했습니다")
        }.getOrThrow()
    }

    private suspend fun getMyCommentDocuments(
        limit: Long,
        startAfter: DocumentSnapshot?
    ): QuerySnapshot {
        val query = database.collection("Comment")
            .whereEqualTo("authorEmail", userDataSource.getUserInfo().email)
            .orderBy("createTime", Query.Direction.DESCENDING)
            .limit(limit)
        return if (startAfter != null) {
            query.startAfter(startAfter).get().await()
        } else {
            query.get().await()
        }
    }
    override suspend fun loadMyCommentList(myCommentListLoadRequest: MyCommentListLoadRequest): CommentMetaListResponse =
        coroutineScope {
            kotlin.runCatching {
                val snapshot = when (myCommentListLoadRequest.reload) {
                    true -> getMyCommentDocuments(10, null)
                    false -> getMyCommentDocuments(10, myCommentLastDocument)
                }
                myCommentLastDocument = snapshot.documents.lastOrNull()

                snapshot.documents.map {
                    CommentMetaResponse(
                        author = null,
                        createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                        content = it.data?.get("content") as? String,
                        images = (it.data?.get("images") as? List<String>)?.let {
                            ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                        },
                        boardAuthorEmail = it.data?.get("boardAuthorEmail") as? String ?: "",
                        boardCreateTime = (it.data?.get("boardCreateTime") as? Timestamp)?.toDate(),
                        editTime = (it.data?.get("editTime") as? Timestamp)?.toDate(),
                        isLastPage = snapshot.size() < 10
                    )
                }.let {
                    CommentMetaListResponse(it)
                }
            }.onFailure {
                throw FailSelectException("셀렉트에 실패 했습니다", it)
            }.getOrThrow()
        }
}
