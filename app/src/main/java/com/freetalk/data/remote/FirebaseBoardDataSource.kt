package com.freetalk.data.remote

import android.net.Uri
import android.util.Log
import com.freetalk.data.entity.BoardEntity
import com.freetalk.presenter.activity.EndPoint
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


interface BoardDataSource {
    suspend fun insert(boardEntity: BoardEntity): Respond
    suspend fun select(): BoardData
    suspend fun delete()
    suspend fun update()
}

data class Respond(
    val respond: BoardRespond
)

data class BoardData(
    val boardList: List<BoardEntity>,
    val respond: BoardRespond
)

sealed class BoardRespond() {
    data class InsertSuccess(val code: String) : BoardRespond()
    data class SelectSuccess(val code: String) : BoardRespond()
}

class FirebaseBoardRemoteDataSourceImpl(
    private val database: FirebaseFirestore,
    private val storage: FirebaseStorage
) : BoardDataSource {

    override suspend fun insert(boardEntity: BoardEntity): Respond {
        val model = hashMapOf<String, Any?>()
        boardEntity.apply {
            when (image.isEmpty()) {
                true -> {
                    Log.v("FirebaseBoardDataSource", "이미지 없음")
                    model["author"] = author
                    model["title"] = title
                    model["context"] = context
                    model["image"] = null
                    model["createTime"] = createTime
                    model["editTime"] = editTime
                }
                false -> {
                    Log.v("FirebaseBoardDataSource", "이미지 있음")
                    model["author"] = author
                    model["title"] = title
                    model["context"] = context
                    model["image"] = uploadImages(image)
                    model["createTime"] = createTime
                    model["editTime"] = editTime
                }
            }
            return insertData(model)
        }
    }

    private suspend fun insertData(model: Map<String, Any?>) = suspendCoroutine {
        database.collection("Board").add(model)
            .addOnCompleteListener { task ->
                Log.v("FirebaseBoardDataSource", "인서트 성공")
                it.resume(Respond(BoardRespond.InsertSuccess("인서트 성공")))
            }.addOnFailureListener { e ->
                Log.v(
                    "FirebaseBoardDataSource",
                    "Error writing document",
                    e
                )
                it.resumeWithException(e)
            }
    }

    private suspend fun uploadImages(uri: List<Uri>): List<Uri> {
        return uri.mapIndexed { i, uri ->
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imgFileName = "IMAGE_" + (i + 1) + "_" + timeStamp + "_.png"
            uploadImage(imgFileName, uri)
        }
    }

    private suspend fun uploadImage(fileName: String, uri: Uri): Uri {
        return suspendCoroutine { continuation ->
            val storageRef = storage.reference.child("images").child(fileName)
            storageRef.putFile(uri).addOnCompleteListener {
                Log.v("FirebaseBoardDataSource", "이미지 업로드 성공")
                it.result.metadata?.reference?.downloadUrl?.addOnCompleteListener { task ->
                    continuation.resume(task.result)
                }?.addOnFailureListener { e ->
                    storageRef.delete().addOnCompleteListener {

                    }
                    Log.v(
                        "FirebaseBoardDataSource",
                        "Error writing document",
                        e
                    )
                    continuation.resumeWithException(e)
                }
            }.addOnFailureListener { e ->
                Log.v(
                    "FirebaseBoardDataSource",
                    "Error writing document",
                    e
                )
                continuation.resumeWithException(e)
            }
        }
    }

    override suspend fun select(): BoardData {
        val boardList = mutableListOf<BoardEntity>()
        return suspendCoroutine { continuation ->
            database.collection("Board").orderBy("createTime").limit(10)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    boardList.clear()
                    if(querySnapshot == null) {
                        Log.v("FirebaseBoardDataSource", "셀렉트 실패")
                    }
                    for (snapshot in querySnapshot!!.documents) {
                        val a = BoardEntity(
                            snapshot.data?.get("author") as String,
                            snapshot.data?.get("title") as String,
                            snapshot.data?.get("context") as String,
                            (snapshot.data?.get("image") as List<String>).map {
                                Uri.parse(it)
                            } as List<Uri>,
                            (snapshot.data?.get("createTime") as Timestamp).toDate(),
                            (snapshot.data?.get("editTIme") as? Timestamp)?.toDate(),

                        )


                        //var item = snapshot.toObject(BoardEntity::class.java)
                        boardList.add(a)
                        Log.v("FirebaseBoardDataSource", a.title)
                    }
                    Log.v("FirebaseBoardDataSource", "셀렉트 성공")
                    continuation.resume(BoardData(boardList, BoardRespond.SelectSuccess("셀렉트 성공") ))
                }

        }
    }

    override suspend fun delete() {
        TODO("Not yet implemented")
    }

    override suspend fun update() {
        TODO("Not yet implemented")
    }

}