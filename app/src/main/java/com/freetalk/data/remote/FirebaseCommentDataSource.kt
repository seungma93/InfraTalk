package com.freetalk.data.remote

import android.net.Uri
import android.util.Log
import com.freetalk.data.*
import com.freetalk.data.entity.ImagesResultEntity
import com.freetalk.data.entity.UserEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject


interface CommentDataSource {
    suspend fun insertComment(wrapperCommentInsertForm: WrapperCommentInsertForm): CommentResponse
    suspend fun selectComments(commentsSelectForm: CommentsSelectForm): CommentListResponse
    suspend fun selectCommentContent(commentContentSelectForm: CommentContentSelectForm): CommentResponse
}

data class CommentInsertForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val content: String
)

data class WrapperCommentInsertForm(
    val commentInsertForm: CommentInsertForm,
    val userSingleton: UserSingleton
)

data class CommentsSelectForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val reload: Boolean
)

data class CommentContentSelectForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)

data class CommentResponse(
    val boardAuthorEmail: String? = null,
    val boardCreateTime: Date? = null,
    val author: UserEntity? = null,
    val content: String? = null,
    val createTime: Date? = null,
    val editTime: Date? = null
)

data class WrapperCommentResponse(
    val commentResponse: CommentResponse? = null,
    val isLike: Boolean? = null,
    val likeCount: Int? = null
)

data class CommentListResponse(
    val commentList: List<WrapperCommentResponse>? = null
)

class FirebaseCommentRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : CommentDataSource {
    private var lastDocument: DocumentSnapshot? = null

    override suspend fun insertComment(wrapperCommentInsertForm: WrapperCommentInsertForm): CommentResponse =
        with(wrapperCommentInsertForm) {

            return kotlin.runCatching {
                val createTime = Date(System.currentTimeMillis())
                val insert = mapOf(
                    "boardAuthorEmail" to commentInsertForm.boardAuthorEmail,
                    "boardCreateTime" to commentInsertForm.boardCreateTime,
                    "author" to userSingleton,
                    "content" to commentInsertForm.content,
                    "createTime" to createTime,
                    "editTime" to null
                )
                database.collection("Comment").add(insert).await()
                CommentResponse(
                    boardAuthorEmail = commentInsertForm.boardAuthorEmail,
                    boardCreateTime = commentInsertForm.boardCreateTime,
                    author = userSingleton.userEntity,
                    content = commentInsertForm.content,
                    createTime = createTime,
                    editTime = null
                )
            }.onFailure {
                throw FailInsertException("인서트에 실패 했습니다")
            }.getOrThrow()
        }

    private suspend fun getCommentDocuments(
        commentsSelectForm: CommentsSelectForm,
        limit: Long,
        startAfter: DocumentSnapshot?
    ): QuerySnapshot {
        Log.d("BoardDataSource", "겟보드다큐먼트")
        val query = database.collection("Comment")
            .whereEqualTo("boardAuthorEmail", commentsSelectForm.boardAuthorEmail)
            .whereEqualTo("boardCreateTime", commentsSelectForm.boardCreateTime)
            .orderBy("createTime", Query.Direction.DESCENDING)
            .limit(limit)
        return if (startAfter != null) {
            query.startAfter(startAfter).get().await()
        } else {
            query.get().await()
        }

    }

    override suspend fun selectComments(commentsSelectForm: CommentsSelectForm): CommentListResponse {
        Log.d("BoardDataSource", "셀렉트콘텐츠")

        return kotlin.runCatching {

            val snapshot = when (commentsSelectForm.reload) {
                true -> getCommentDocuments(commentsSelectForm, 10, null)
                false -> getCommentDocuments(commentsSelectForm, 10, lastDocument)
            }

            lastDocument = snapshot.documents.lastOrNull()
            Log.d("BoardDataSource", snapshot.documents.count().toString())

            snapshot.documents.map {
                val boardAuthorEmail = it.data?.get("boardAuthorEmail") as? String ?: ""
                val boardCreateTime = (it.data?.get("boardCreateTime") as? Timestamp)?.toDate()
                val author = it.data?.get("author") as? HashMap<String, Any>
                val email = author?.get("email") as? String ?: ""
                val nickname = author?.get("nickname") as? String ?: ""
                val image = (author?.get("image") as? String)?.let {
                    Uri.parse(it)
                }
                val content = it.data?.get("content") as? String
                val createTime = (it.data?.get("createTime") as? Timestamp)?.toDate()
                val editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()


                val likeSnapshot = database.collection("Like")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("boardAuthorEmail", email)
                    .whereEqualTo("boardCreateTime", createTime)
                    .get().await()

                val likeCountSnapshot = database.collection("Like")
                    .whereEqualTo("boardAuthorEmail", email)
                    .whereEqualTo("boardCreateTime", createTime)
                    .get().await()

                val commentResponse = CommentResponse(
                    boardAuthorEmail,
                    boardCreateTime,
                    UserEntity(email, nickname, image),
                    content,
                    createTime,
                    editTime
                )
                WrapperCommentResponse(
                    commentResponse = commentResponse,
                    isLike = likeSnapshot.documents.isNotEmpty(),
                    likeCount = likeCountSnapshot.size()
                )
            }.let {
                CommentListResponse(it)
            }
        }.onFailure {
            Log.d("BoardDataSource", it.stackTrace.toString())
            throw FailSelectException("셀렉트에 실패 했습니다", it)
        }.getOrThrow()
    }

/*
    override suspend fun updateContent(boardUpdateForm: BoardUpdateForm): BoardResponse {
        return kotlin.runCatching {
            database.collection("Board")
                .whereEqualTo("author.email", boardUpdateForm.author.email)
                .whereEqualTo("createTime", boardUpdateForm.createTime).get().await().let {

                    boardUpdateForm.images?.let { images ->

                        boardUpdateForm.editTime?.let { date ->

                            when (boardUpdateForm.content) {
                                null -> {
                                    val updates = mapOf(
                                        "images" to boardUpdateForm.images,
                                        "editTime" to boardUpdateForm.editTime
                                    )
                                    it.documents[0].reference.update(updates).await()
                                }
                                else -> {
                                    val updates = mapOf(
                                        "content" to boardUpdateForm.content,
                                        "images" to boardUpdateForm.images,
                                        "editTime" to boardUpdateForm.editTime
                                    )
                                    it.documents[0].reference.update(updates).await()
                                }
                            }

                        } ?: run {
                            val updates = mapOf(
                                "images" to boardUpdateForm.images
                            )
                            it.documents[0].reference.update(updates).await()

                        }

                    } ?: run {
                        val updates = mapOf(
                            "content" to boardUpdateForm.content,
                            "editTime" to boardUpdateForm.editTime
                        )
                        it.documents[0].reference.update(updates).await()

                    }
                }
            BoardResponse(
                boardUpdateForm.author,
                boardUpdateForm.title,
                boardUpdateForm.content,
                null,
                boardUpdateForm.createTime,
                boardUpdateForm.editTime
            )

        }.onFailure {
            throw FailUpdatetException("업데이트 실패")
        }.getOrThrow()
    }
 */

    override suspend fun selectCommentContent(commentContentSelectForm: CommentContentSelectForm): CommentResponse =
        with(commentContentSelectForm) {
            return kotlin.runCatching {
                val snapshot = database.collection("Comment")
                    .whereEqualTo("author.email", commentAuthorEmail)
                    .whereEqualTo("createTime", commentCreateTime).get().await()

                snapshot.documents.firstOrNull()?.let {
                    val author = it.data?.get("author") as? HashMap<String, Any>
                    val email = author?.get("email") as? String ?: ""
                    val nickname = author?.get("nickname") as? String ?: ""
                    val image = (author?.get("image") as? String)?.let {
                        Uri.parse(it)
                    }
                    val content = it.data?.get("content") as? String
                    val createTime = (it.data?.get("createTime") as? Timestamp)?.toDate()
                    val editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()

                    CommentResponse(
                        boardAuthorEmail = boardAuthorEmail,
                        boardCreateTime,
                        author = UserEntity(email, nickname, image),
                        content = content,
                        createTime = createTime,
                        editTime = editTime
                    )
                } ?: run {
                    throw FailSelectBoardContentException("보드 콘텐츠 셀렉트 실패")
                }
            }.onFailure {
                throw FailSelectBoardContentException("보드 콘텐츠 셀렉트 실패")
            }.getOrThrow()

        }

}
