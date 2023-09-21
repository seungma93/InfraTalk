package com.freetalk.data.datasource.remote

import android.net.Uri
import com.freetalk.data.*
import com.freetalk.data.model.request.BoardRelatedAllCommentMetaListSelectRequest
import com.freetalk.data.model.request.CommentDeleteRequest
import com.freetalk.data.model.request.CommentMetaListSelectRequest
import com.freetalk.data.model.request.UserSelectRequest
import com.freetalk.data.model.response.CommentMetaListResponse
import com.freetalk.data.model.response.CommentMetaResponse
import com.freetalk.domain.entity.ImagesResultEntity
import com.freetalk.presenter.form.CommentInsertRequest
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject


interface CommentDataSource {
    suspend fun insertComment(commentInsertRequest: CommentInsertRequest): CommentMetaResponse
    suspend fun selectCommentMetaList(commentMetaListSelectRequest: CommentMetaListSelectRequest): CommentMetaListResponse
    suspend fun selectRelatedAllCommentMetaList(
        boardRelatedAllCommentMetaListSelectRequest:
        BoardRelatedAllCommentMetaListSelectRequest
    ): CommentMetaListResponse
    suspend fun deleteComment(commentDeleteRequest: CommentDeleteRequest): CommentMetaResponse
}


class FirebaseCommentRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val userDataSource: UserDataSource
) : CommentDataSource {
    private var lastDocument: DocumentSnapshot? = null

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

    override suspend fun selectCommentMetaList(commentMetaListSelectRequest: CommentMetaListSelectRequest): CommentMetaListResponse {
        return kotlin.runCatching {
            val snapshot = when (commentMetaListSelectRequest.reload) {
                true -> getCommentDocuments(commentMetaListSelectRequest, 10, null)
                false -> getCommentDocuments(commentMetaListSelectRequest, 10, lastDocument)
            }
            lastDocument = snapshot.documents.lastOrNull()

            snapshot.documents.map {
                val authorEmail = it.data?.get("authorEmail") as? String ?: ""
                val createTime = (it.data?.get("createTime") as? Timestamp)?.toDate()
                val userResponse =
                    userDataSource.selectUserInfo(UserSelectRequest(userEmail = authorEmail))
                val content = it.data?.get("content") as? String
                val images = (it.data?.get("images") as? List<String>)?.let {
                    ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                }
                val boardAuthorEmail = it.data?.get("boardAuthorEmail") as? String ?: ""
                val boardCreateTime = (it.data?.get("boardCreateTime") as? Timestamp)?.toDate()
                val editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()

                CommentMetaResponse(
                    author = userResponse,
                    createTime = createTime,
                    content = content,
                    images = images,
                    boardAuthorEmail = boardAuthorEmail,
                    boardCreateTime = boardCreateTime,
                    editTime = editTime
                )
            }.let {
                CommentMetaListResponse(it)
            }
        }.onFailure {
            throw FailSelectCommentsException("댓글 셀렉트에 실패 했습니다")
        }.getOrThrow()
    }

    override suspend fun selectRelatedAllCommentMetaList(
        boardRelatedAllCommentMetaListSelectRequest:
        BoardRelatedAllCommentMetaListSelectRequest
    ): CommentMetaListResponse {
        return kotlin.runCatching {
            val query = database.collection("Comment")
                .whereEqualTo(
                    "boardAuthorEmail",
                    boardRelatedAllCommentMetaListSelectRequest.boardAuthorEmail
                )
                .whereEqualTo(
                    "boardCreateTime",
                    boardRelatedAllCommentMetaListSelectRequest.boardCreateTime
                )

            val snapshot = query.get().await()

            snapshot.documents.map {
                val authorEmail = it.data?.get("authorEmail") as? String ?: ""
                val createTime = (it.data?.get("createTime") as? Timestamp)?.toDate()
                val userResponse =
                    userDataSource.selectUserInfo(UserSelectRequest(userEmail = authorEmail))
                val content = it.data?.get("content") as? String
                val images = (it.data?.get("images") as? List<String>)?.let {
                    ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                }
                val boardAuthorEmail = it.data?.get("boardAuthorEmail") as? String ?: ""
                val boardCreateTime = (it.data?.get("boardCreateTime") as? Timestamp)?.toDate()
                val editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()

                CommentMetaResponse(
                    author = userResponse,
                    createTime = createTime,
                    content = content,
                    images = images,
                    boardAuthorEmail = boardAuthorEmail,
                    boardCreateTime = boardCreateTime,
                    editTime = editTime
                )
            }.let {
                CommentMetaListResponse(it)
            }
        }.onFailure {
            throw FailSelectCommentsException("댓글 셀렉트에 실패 했습니다")
        }.getOrThrow()
    }

    override suspend fun deleteComment(commentDeleteRequest: CommentDeleteRequest): CommentMetaResponse {
        return kotlin.runCatching {
            database.collection("Comment")
                .whereEqualTo("authorEmail", commentDeleteRequest.commentAuthorEmail)
                .whereEqualTo("createTime", commentDeleteRequest.commentCreateTime)
                .get().await().let {
                    it.documents.firstOrNull()?.reference?.delete()?.await()
                }
            CommentMetaResponse(
                boardAuthorEmail = null,
                boardCreateTime = null,
                author = null,
                content = null,
                createTime = null,
                editTime = null
            )
        }.onFailure {
            throw FailDeleteCommentException("댓글 셀렉트에 실패 했습니다")
        }.getOrThrow()
    }
}
