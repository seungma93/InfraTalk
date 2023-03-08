package com.freetalk.data.remote

import android.net.Uri
import android.util.Log
import com.freetalk.data.UserSingleton
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.ImagesEntity
import com.freetalk.data.entity.UserEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.resume


interface BoardDataSource {
    suspend fun insertContent(boardInsertForm: BoardInsetForm): BoardResponse
    //suspend fun select(): BoardSelectData
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
    val images: List<Uri>,
    val createTime: Date,
    val editTime: Date?
)


data class BoardSelectData(
    val boardList: List<BoardEntity>?,
    val response: BoardResponse
)


data class BoardResponse(
    val author: UserEntity? = null,
    val title: String? = null,
    val content: String? = null,
    val images: ImagesEntity? = null,
    val createTime: Date? = null,
    val editTime: Date? = null
)


class FirebaseBoardRemoteDataSourceImpl(
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

/*
    override suspend fun select(): BoardSelectData {
        val boardList = mutableListOf<BoardEntity>()
        return kotlin.runCatching {
            val snapshot =
                database.collection("Board").orderBy("createTime").limit(10).get().await()
            snapshot.documents.map {
                val boardEntity = BoardEntity(
                    (it.data?.get("author") as HashMap<String, Any>).,
                    it.data?.get("title") as String,
                    it.data?.get("context") as String,
                    (it.data?.get("image") as List<String>).map {
                        Uri.parse(it)
                    } as List<Uri>,
                    (it.data?.get("createTime") as Timestamp).toDate(),
                    (it.data?.get("editTIme") as? Timestamp)?.toDate()
                )
                boardList.add(boardEntity)
            }
            BoardSelectData(boardList, BoardResponse.SelectSuccess("셀릭트 성공"))
        }.getOrElse {
            BoardSelectData(null, BoardResponse.SelectFail("셀릭트 실패" + it.printStackTrace()))
        }
    }

 */

    override suspend fun updateContent(boardUpdateForm: BoardUpdateForm): BoardResponse {
        return kotlin.runCatching {
            FirebaseFirestore.getInstance().collection("Board")
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
