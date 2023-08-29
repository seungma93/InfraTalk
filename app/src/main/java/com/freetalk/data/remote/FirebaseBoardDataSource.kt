package com.freetalk.data.remote

import android.net.Uri
import android.util.Log
import com.freetalk.data.*
import com.freetalk.data.entity.BookMarkEntity
import com.freetalk.data.entity.ImagesResultEntity
import com.freetalk.data.entity.LikeEntity
import com.freetalk.data.entity.UserEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject


interface BoardDataSource {
    suspend fun insertContent(boardInsertForm: BoardInsetForm): BoardResponse
    suspend fun selectContents(boardSelectForm: BoardSelectForm): BoardListResponse
    suspend fun delete()
    suspend fun updateContent(boardUpdateForm: BoardUpdateForm): BoardResponse
    suspend fun selectBoardContent(boardContentSelectForm: BoardContentSelectForm): BoardResponse
}

data class BoardInsetForm(
    val author: UserEntity,
    val title: String,
    val content: String,
    val createTime: Date,
    val editTime: Date?
)

data class BoardUpdateForm(
    val author: UserEntity,
    val title: String,
    val content: String = "",
    val images: List<Uri> = emptyList(),
    val createTime: Date,
    val editTime: Date?
)

data class BoardSelectForm(
    val reload: Boolean
)

data class BoardResponse(
    val author: UserEntity? = null,
    val title: String? = null,
    val content: String? = null,
    val images: ImagesResultEntity? = null,
    val createTime: Date? = null,
    val editTime: Date? = null
)

data class BoardListResponse(
    val boardList: List<BoardResponse>? = null
)


data class WrapperBoardResponse(
    val boardResponse: BoardResponse? = null,
    val bookMarkEntity: BookMarkEntity? = null,
    val likeEntity: LikeEntity? = null,
    val likeCount: Int? = null
)

data class BoardContentSelectForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)

class FirebaseBoardRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : BoardDataSource {
    private var lastDocument: DocumentSnapshot? = null

    override suspend fun insertContent(boardInsertForm: BoardInsetForm): BoardResponse {

        return kotlin.runCatching {
            database.collection("Board").add(boardInsertForm).await()
            BoardResponse(
                boardInsertForm.author,
                boardInsertForm.title,
                boardInsertForm.content,
                null,
                boardInsertForm.createTime,
                null
            )
        }.onFailure {
            throw FailInsertException("인서트에 실패 했습니다")
        }.getOrThrow()
    }

    private suspend fun getBoardDocuments(
        limit: Long,
        startAfter: DocumentSnapshot?
    ): QuerySnapshot {
        Log.d("BoardDataSource", "겟보드다큐먼트")
        val query = database.collection("Board").orderBy("createTime", Query.Direction.DESCENDING)
            .limit(limit)
        return if (startAfter != null) {
            Log.d("getBoardDocument", startAfter.data?.get("title").toString())
            query.startAfter(startAfter).get().await()
        } else {
            query.get().await()
        }
    }

    override suspend fun selectContents(boardSelectForm: BoardSelectForm): BoardListResponse {
        Log.d("BoardDataSource", "셀렉트콘텐츠")

        return kotlin.runCatching {

            val snapshot = when (boardSelectForm.reload) {
                true -> getBoardDocuments(10, null)
                false -> getBoardDocuments(10, lastDocument)
            }
            lastDocument = snapshot.documents.lastOrNull()
            Log.d("BoardDataSource", snapshot.documents.count().toString())

            snapshot.documents.map {
                val author = it.data?.get("author") as? HashMap<String, Any>
                val email = author?.get("email") as? String ?: ""
                val nickname = author?.get("nickname") as? String ?: ""
                val image = (author?.get("image") as? String)?.let {
                    Uri.parse(it)
                }
                val title = it.data?.get("title") as? String
                val content = it.data?.get("content") as? String
                val images = (it.data?.get("images") as? List<String>)?.let {
                    ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                }
                val createTime = (it.data?.get("createTime") as? Timestamp)?.toDate()
                val editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()

/*
                val likeSnapshot = database.collection("Like")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("boardAuthorEmail", email)
                    .whereEqualTo("boardCreateTime", createTime)
                    .get().await()

                val likeCountSnapshot = database.collection("Like")
                    .whereEqualTo("boardAuthorEmail", email)
                    .whereEqualTo("boardCreateTime", createTime)
                    .get().await()

                val bookMarkSnapshot = database.collection("BookMark")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("boardAuthorEmail", email)
                    .whereEqualTo("boardCreateTime", createTime)
                    .get().await()

 */

                BoardResponse(
                    UserEntity(email, nickname, image),
                    title,
                    content,
                    images,
                    createTime,
                    editTime
                )
            }.let {
                BoardListResponse(it)
            }
        }.onFailure {
            Log.d("BoardDataSource", it.stackTrace.toString())
            throw FailSelectException("셀렉트에 실패 했습니다", it)
        }.getOrThrow()
    }


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

    override suspend fun selectBoardContent(boardContentSelectForm: BoardContentSelectForm): BoardResponse =
        with(boardContentSelectForm) {
            return kotlin.runCatching {
                val snapshot = database.collection("Board")
                    .whereEqualTo("author.email", boardAuthorEmail)
                    .whereEqualTo("createTime", boardCreateTime).get().await()

                snapshot.documents.firstOrNull()?.let {
                    val author = it.data?.get("author") as? HashMap<String, Any>
                    val email = author?.get("email") as? String ?: ""
                    val nickname = author?.get("nickname") as? String ?: ""
                    val image = (author?.get("image") as? String)?.let {
                        Uri.parse(it)
                    }
                    val title = it.data?.get("title") as? String
                    val content = it.data?.get("content") as? String
                    val images = (it.data?.get("images") as? List<String>)?.let {
                        ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                    }
                    val createTime = (it.data?.get("createTime") as? Timestamp)?.toDate()
                    val editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()
                    BoardResponse(
                        author = UserEntity(email, nickname, image),
                        title = title,
                        content = content,
                        images = images,
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


    override suspend fun delete() {
        TODO("Not yet implemented")
    }
}
