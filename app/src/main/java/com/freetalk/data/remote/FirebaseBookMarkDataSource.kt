package com.freetalk.data.remote

import com.freetalk.data.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject


interface BookMarkDataSource {
    suspend fun updateBookMark(bookMarkUpdateForm: BookMarkUpdateForm): BookMarkResponse
    suspend fun selectBookMark(bookMarkSelectForm: BookMarkSelectForm): BookMarkResponse
}

data class BookMarkUpdateForm(
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

class FirebaseBookMarkRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : BookMarkDataSource {

    override suspend fun updateBookMark(bookMarkUpdateForm: BookMarkUpdateForm): BookMarkResponse =
        with(bookMarkUpdateForm) {
            return kotlin.runCatching {
                val snapshot = database.collection("BookMark")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                val bookMarkResponse = BookMarkResponse(
                    boardAuthorEmail = boardAuthorEmail,
                    boardCreateTime = boardCreateTime,
                    userEmail = UserSingleton.userEntity.email,
                    updateTime = Date(System.currentTimeMillis())
                )

                snapshot.documents.firstOrNull()?.let {
                    database.collection("BookMark").document(it.id).delete()
                } ?: run {
                    database.collection("BookMark").add(bookMarkResponse).await()
                }

                bookMarkResponse
            }.onFailure {
                throw FailUpdateBookMarkException("북마크 업데이트를 실패 했습니다")
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