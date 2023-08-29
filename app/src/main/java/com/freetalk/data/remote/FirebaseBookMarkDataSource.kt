package com.freetalk.data.remote

import com.freetalk.data.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.Date
import javax.inject.Inject


interface BookMarkDataSource {
    suspend fun insertBookMark(insertBookMarkRequest: InsertBookMarkRequest): BookMarkResponse
    suspend fun deleteBookMark(deleteBookMarkRequest: DeleteBookMarkRequest): BookMarkResponse
    suspend fun selectBookMark(bookMarkSelectForm: BookMarkSelectForm): BookMarkResponse
}

data class InsertBookMarkForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)

data class DeleteBookMarkForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)

data class BookMarkSelectForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)

data class BookMarkResponse(
    val boardAuthorEmail: String? = null,
    val boardCreateTime: Date? = null,
    val userEmail: String? = null,
    val updateTime: Date? = null
)

data class InsertBookMarkRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)

data class DeleteBookMarkRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)

data class FirebaseInsertBookMarkRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val userEmail: String,
    val updateTime: Date
)

class FirebaseBookMarkRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : BookMarkDataSource {

    override suspend fun insertBookMark(insertBookMarkRequest: InsertBookMarkRequest): BookMarkResponse =
        with(insertBookMarkRequest) {
            return kotlin.runCatching {
                val firebaseInsertBookMarkRequest = FirebaseInsertBookMarkRequest(
                    boardAuthorEmail = boardAuthorEmail,
                    boardCreateTime = boardCreateTime,
                    userEmail = UserSingleton.userEntity.email,
                    updateTime = Date(System.currentTimeMillis())
                )
                database.collection("BookMark").add(firebaseInsertBookMarkRequest).await()

                BookMarkResponse(
                    boardAuthorEmail = boardAuthorEmail,
                    boardCreateTime = boardCreateTime,
                    userEmail = UserSingleton.userEntity.email,
                    updateTime = Date(System.currentTimeMillis())
                )
            }.onFailure {
                throw FailInsertBookMarkException("북마크 인서트에 실패했습니다")
            }.getOrThrow()
        }

    override suspend fun deleteBookMark(deleteBookMarkRequest: DeleteBookMarkRequest): BookMarkResponse =
        with(deleteBookMarkRequest) {
         return kotlin.runCatching {
             val snapshot = database.collection("BookMark")
                 .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                 .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                 .whereEqualTo("boardCreateTime", boardCreateTime)
                 .get().await()

             snapshot.documents.firstOrNull()?.let {
                 database.collection("BookMark").document(it.id).delete()
             } ?: throw FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")

             BookMarkResponse(
                 boardAuthorEmail = boardAuthorEmail,
                 boardCreateTime = boardCreateTime,
                 userEmail = UserSingleton.userEntity.email,
                 updateTime = Date(System.currentTimeMillis())
             )
         }.onFailure {
             throw FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")
         }.getOrThrow()
        }

    override suspend fun selectBookMark(bookMarkSelectForm: BookMarkSelectForm): BookMarkResponse =
        with(bookMarkSelectForm) {
            return kotlin.runCatching {
                val snapshot = database.collection("BookMark")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                snapshot.documents.firstOrNull()?.let {
                    BookMarkResponse(
                        boardAuthorEmail = boardAuthorEmail,
                        boardCreateTime = boardCreateTime,
                        userEmail = UserSingleton.userEntity.email,
                        updateTime = it.data?.get("updateTime") as? Date
                    )
                } ?: run {
                    BookMarkResponse(
                        boardAuthorEmail = "",
                        boardCreateTime = Date(),
                        userEmail = "",
                        updateTime = Date()
                    )
                }

            }.onFailure {
                throw FailLoadBookMarkException("좋아요 로드를 실패 했습니다")
            }.getOrThrow()

        }
}