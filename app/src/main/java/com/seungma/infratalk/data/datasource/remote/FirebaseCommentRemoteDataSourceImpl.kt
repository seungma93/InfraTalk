package com.seungma.infratalk.data.datasource.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.seungma.infratalk.data.model.request.board.BoardRelatedAllCommentMetaListSelectRequest
import com.seungma.infratalk.data.model.request.comment.CommentDeleteRequest
import com.seungma.infratalk.data.model.request.comment.CommentMetaListSelectRequest
import com.seungma.infratalk.data.model.request.comment.MyCommentListLoadRequest
import com.seungma.infratalk.data.model.request.user.UserSelectRequest
import com.seungma.infratalk.data.model.response.comment.CommentDeleteResponse
import com.seungma.infratalk.data.model.response.comment.CommentMetaListResponse
import com.seungma.infratalk.data.model.response.comment.CommentMetaResponse
import com.seungma.infratalk.domain.image.ImagesResultEntity
import com.seungma.infratalk.presenter.board.form.CommentInsertRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject


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
                throw com.seungma.infratalk.data.FailInsertCommentException("댓글 인서트에 실패 했습니다")
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
                throw com.seungma.infratalk.data.FailSelectCommentsException("댓글 셀렉트에 실패 했습니다")
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
            throw com.seungma.infratalk.data.FailSelectCommentsException("댓글 셀렉트에 실패 했습니다")
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
            throw com.seungma.infratalk.data.FailDeleteCommentException("댓글 셀렉트에 실패 했습니다")
        }.getOrThrow()
    }

    private suspend fun getMyCommentDocuments(
        limit: Long,
        startAfter: DocumentSnapshot?
    ): QuerySnapshot {
        val userEmail = userDataSource.obtainUser().email ?: error("유저 정보 없음")
        val query = database.collection("Comment")
            .whereEqualTo("authorEmail", userEmail)
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
                        author = userDataSource.obtainUser(),
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
                throw com.seungma.infratalk.data.FailSelectException("셀렉트에 실패 했습니다", it)
            }.getOrThrow()
        }

    override suspend fun loadMyBookmarkCommentList(): CommentMetaListResponse = coroutineScope {
        kotlin.runCatching {
            val userEmail = userDataSource.obtainUser().email ?: error("유저 정보 없음")
            val snapshot = database.collection("CommentBookmark")
                .whereEqualTo("userEmail", userEmail)
                .get().await()
            snapshot.documents.map {
                val commentAuthorEmail = it.data?.get("commentAuthorEmail") as? String
                val commentCreateTime = it.data?.get("commentCreateTime") as? Timestamp
                commentAuthorEmail to commentCreateTime
            }.map { (commentAuthorEmail, commentCreateTime) ->
                val asyncComment = async {
                    database.collection("Comment")
                        .whereEqualTo("authorEmail", commentAuthorEmail)
                        .whereEqualTo("createTime", commentCreateTime)
                        .get().await()
                }
                asyncComment
            }.map {
                val commentSnapshot = it.await()
                val asyncUserInfo = commentSnapshot.documents.firstOrNull()?.let {
                    val authorEmail = it.data?.get("authorEmail")?.let { it as String } ?: error("")
                    async {
                        userDataSource
                            .selectUserInfo(UserSelectRequest(userEmail = authorEmail))
                    }
                } ?: run {
                    throw com.seungma.infratalk.data.FailSelectBoardContentException("보드 콘텐츠 셀렉트 실패")
                }
                commentSnapshot to asyncUserInfo
            }.map { (it, deferred) ->
                it.documents.firstNotNullOf {
                    CommentMetaResponse(
                        author = deferred.await(),
                        content = it.data?.get("content") as? String,
                        images = (it.data?.get("images") as? List<String>)?.let {
                            ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                        },
                        createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                        editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()
                    )
                }
            }.let {
                CommentMetaListResponse(it)
            }

        }.onFailure {
            throw com.seungma.infratalk.data.FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")
        }.getOrThrow()
    }

    override suspend fun loadMyLikeCommentList(): CommentMetaListResponse = coroutineScope {
        kotlin.runCatching {
            val userEmail = userDataSource.obtainUser().email ?: error("유저 정보 없음")
            val snapshot = database.collection("CommentLike")
                .whereEqualTo("userEmail", userEmail)
                .get().await()
            snapshot.documents.map {
                val commentAuthorEmail = it.data?.get("commentAuthorEmail") as? String
                val commentCreateTime = it.data?.get("commentCreateTime") as? Timestamp
                commentAuthorEmail to commentCreateTime
            }.map { (commentAuthorEmail, commentCreateTime) ->
                val asyncComment = async {
                    database.collection("Comment")
                        .whereEqualTo("authorEmail", commentAuthorEmail)
                        .whereEqualTo("createTime", commentCreateTime)
                        .get().await()
                }
                asyncComment
            }.map {
                val commentSnapshot = it.await()
                val asyncUserInfo = commentSnapshot.documents.firstOrNull()?.let {
                    val authorEmail = it.data?.get("authorEmail")?.let { it as String } ?: error("")
                    async {
                        userDataSource
                            .selectUserInfo(UserSelectRequest(userEmail = authorEmail))
                    }
                } ?: run {
                    throw com.seungma.infratalk.data.FailSelectBoardContentException("보드 콘텐츠 셀렉트 실패")
                }
                commentSnapshot to asyncUserInfo
            }.map { (it, deferred) ->
                it.documents.firstNotNullOf {
                    CommentMetaResponse(
                        author = deferred.await(),
                        content = it.data?.get("content") as? String,
                        images = (it.data?.get("images") as? List<String>)?.let {
                            ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                        },
                        createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                        editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()
                    )
                }
            }.let {
                CommentMetaListResponse(it)
            }

        }.onFailure {
            throw com.seungma.infratalk.data.FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")
        }.getOrThrow()
    }

}
