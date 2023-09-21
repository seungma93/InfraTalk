package com.freetalk.data.datasource.remote

import android.net.Uri
import com.freetalk.data.FailInsertException
import com.freetalk.data.FailSelectBoardContentException
import com.freetalk.data.FailSelectException
import com.freetalk.data.FailUpdatetException
import com.freetalk.data.model.request.BoardInsertRequest
import com.freetalk.data.model.request.BoardMetaListSelectRequest
import com.freetalk.data.model.request.BoardSelectRequest
import com.freetalk.data.model.request.BoardUpdateRequest
import com.freetalk.data.model.response.BoardMetaListResponse
import com.freetalk.data.model.response.BoardMetaResponse
import com.freetalk.domain.entity.ImagesResultEntity
import com.freetalk.domain.entity.UserEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject


interface BoardDataSource {
    suspend fun insertBoard(boardInsertRequest: BoardInsertRequest): BoardMetaResponse
    suspend fun updateBoard(boardUpdateRequest: BoardUpdateRequest): BoardMetaResponse
    suspend fun selectBoard(boardSelectRequest: BoardSelectRequest): BoardMetaResponse
    suspend fun selectBoardMetaList(boardMetaListSelectRequest: BoardMetaListSelectRequest): BoardMetaListResponse
}


class FirebaseBoardRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : BoardDataSource {
    private var lastDocument: DocumentSnapshot? = null

    override suspend fun insertBoard(boardInsertRequest: BoardInsertRequest): BoardMetaResponse =
        with(boardInsertRequest) {
            return kotlin.runCatching {
                database.collection("Board").add(this).await()
                BoardMetaResponse(
                    author = author,
                    title = title,
                    content = content,
                    images = null,
                    createTime = Date(System.currentTimeMillis()),
                    editTime = null
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

    override suspend fun selectBoardMetaList(boardMetaListSelectRequest: BoardMetaListSelectRequest): BoardMetaListResponse {
        return kotlin.runCatching {
            val snapshot = when (boardMetaListSelectRequest.reload) {
                true -> getBoardDocuments(10, null)
                false -> getBoardDocuments(10, lastDocument)
            }
            lastDocument = snapshot.documents.lastOrNull()

            snapshot.documents.map {
                val author = it.data?.get("author") as? HashMap<*, *>
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

                BoardMetaResponse(
                    UserEntity(email, nickname, image),
                    title,
                    content,
                    images,
                    createTime,
                    editTime
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

                        boardUpdateRequest.editTime?.let { date ->

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
                    BoardMetaResponse(
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
}
