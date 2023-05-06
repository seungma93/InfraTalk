package com.freetalk.data.remote

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.saveable.autoSaver
import com.freetalk.data.entity.BoardEntity
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


interface BoardDataSource {
    suspend fun insertContent(boardInsertForm: BoardInsetForm): BoardResponse
    suspend fun selectContents(lastDocument: DocumentSnapshot?): BoardListResponse
    suspend fun delete()
    suspend fun updateContent(boardUpdateForm: BoardUpdateForm): BoardResponse
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
    val content: String?,
    val images: List<Uri>?,
    val createTime: Date,
    val editTime: Date?
)

data class BoardResponse(
    val author: UserEntity? = null,
    val title: String? = null,
    val content: String? = null,
    val images: ImagesResultEntity? = null,
    val createTime: Date? = null,
    val editTime: Date? = null,
    val lastDocument: DocumentSnapshot? = null
)

data class BoardListResponse(
    val boardList: List<BoardResponse>? = null
)


class FirebaseBoardRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : BoardDataSource {

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

    override suspend fun selectContents(lastDocument: DocumentSnapshot?): BoardListResponse {

        val boardList = mutableListOf<BoardResponse>()
        Log.d("slectContents", "들어온 변수" + lastDocument?.data?.get("title"))
        return kotlin.runCatching {
            Log.d("BoardDataSource", "셀렉트콘텐츠")
            val snapshot = getBoardDocuments(10, lastDocument)

            val newLastDocument = snapshot.documents.lastOrNull()
            Log.d("BoardDataSource", newLastDocument?.data?.get("title").toString())
            snapshot.documents.map {

                val author = it.data?.get("author") as HashMap<String, Any>
                val email = author["email"] as String
                val nickname = author["nickname"] as String
                val image = (author["image"] as? String)?.let {
                    Uri.parse(it)
                } ?: null
                val title = it.data?.get("title") as String
                val content = it.data?.get("content") as String
                val images = (it.data?.get("image") as? List<String>)?.let {
                    ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                } ?: null
                val createTime = (it.data?.get("createTime") as Timestamp).toDate()
                val editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()
                val boardResponse = BoardResponse(
                    UserEntity(email, nickname, image),
                    title,
                    content,
                    images,
                    createTime,
                    editTime,
                    newLastDocument
                )
                boardList.add(boardResponse)
            }
            Log.d("BoardDataSource", snapshot.documents.count().toString())
            BoardListResponse(boardList)
        }.onFailure {
            Log.d("BoardDataSource", it.stackTrace.toString())
            throw FailSelectException("셀렉트에 실패 했습니다")
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

    override suspend fun delete() {
        TODO("Not yet implemented")
    }
}
