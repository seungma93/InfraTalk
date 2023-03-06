package com.freetalk.data.remote

import android.net.Uri
import android.util.Log
import com.freetalk.data.UserSingleton
import com.freetalk.data.entity.BoardEntity
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
    suspend fun insert(boardEntity: BoardEntity): BoardInsertData
    suspend fun select(): BoardSelectData
    suspend fun delete()
    suspend fun update()
}

data class BoardInsertData(
    val failedImageList: List<Uri>?,
    val response: BoardResponse
)

data class BoardSelectData(
    val boardList: List<BoardEntity>?,
    val response: BoardResponse
)

data class ImageUris(
    val inputList: List<Uri>,
    val outputList: List<Uri?>
)

sealed class BoardResponse() {
    data class InsertSuccess(val code: String) : BoardResponse()
    data class InsertFail(val code: String) : BoardResponse()
    data class SelectSuccess(val code: String) : BoardResponse()
    data class SelectFail(val code: String) : BoardResponse()
}

class FirebaseBoardRemoteDataSourceImpl(
    private val database: FirebaseFirestore,
    private val storage: FirebaseStorage
) : BoardDataSource {
    private val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

    override suspend fun insert(boardEntity: BoardEntity): BoardInsertData {
        //val model = hashMapOf<String, Any?>()
        boardEntity.apply {

            return when (image.isEmpty()) {
                true -> {
                    Log.v("FirebaseBoardDataSource", "이미지 없음")
                    insertData(boardEntity, null)
                }
                false -> {
                    val imageUris = uploadImages(image)
                    val failedImageList = mutableListOf<Uri>()
                    imageUris.outputList.mapIndexed { i, uri ->
                        if (uri == null) {
                            failedImageList.add(imageUris.inputList[i])
                        }
                    }
                    Log.v("FirebaseBoardDataSource", "이미지 있음")
                    val boardEntity = BoardEntity(
                        author,
                        title,
                        content,
                        imageUris.outputList.filterNotNull(),
                        createTime,
                        editTime
                    )
                    insertData(boardEntity, failedImageList)
                }
            }
        }
    }

    private suspend fun insertData(
        model: BoardEntity,
        failedImageList: List<Uri>?
    ): BoardInsertData {
        return kotlin.runCatching {
            database.collection("Board").add(model).await()
            BoardInsertData(failedImageList, BoardResponse.InsertSuccess("인서트 성공"))
        }.getOrElse {
            BoardInsertData(failedImageList, BoardResponse.InsertFail("인서트 실패"))
        }
    }

    private suspend fun uploadImages(inputList: List<Uri>): ImageUris = coroutineScope {

        val outputList = inputList.mapIndexed { i, uri ->
            val imgFileName = "IMAGE_" + (i + 1) + "_" + timeStamp + "_.png"
            async { uploadImage(imgFileName, uri) }
        }.awaitAll()
        ImageUris(inputList, outputList)
    }

    private suspend fun uploadImage(fileName: String, uri: Uri): Uri? {
        return kotlin.runCatching {
            val storageRef = storage.reference.child("images").child(fileName)
            val res = storageRef.putFile(uri).await()
            val downloadUri = res.storage.downloadUrl.await()
            downloadUri
        }.getOrNull()
    }

    override suspend fun select(): BoardSelectData {
        val boardList = mutableListOf<BoardEntity>()
        return kotlin.runCatching {
            val snapshot =
                database.collection("Board").orderBy("createTime").limit(10).get().await()
            snapshot.documents.map {
                val boardEntity = BoardEntity(
                    (it.data?.get("author") as HashMap<String, Any>).
                            ,
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

    override suspend fun delete() {
        TODO("Not yet implemented")
    }

    override suspend fun update() {
        TODO("Not yet implemented")
    }
}