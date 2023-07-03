package com.freetalk.data.remote

import com.freetalk.data.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject


interface LikeDataSource {
    suspend fun insertLike(insertLikeRequest: InsertLikeRequest): LikeResponse
    suspend fun deleteLike(deleteLikeRequest: DeleteLikeRequest): LikeResponse
    suspend fun selectLike(likeSelectForm: LikeSelectForm): LikeResponse
    suspend fun selectLikeCount(likeCountSelectForm: LikeCountSelectForm): LikeCountResponse
}

data class InsertLikeRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)

data class InsertLikeForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)

data class DeleteLikeForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)

data class DeleteLikeRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)

data class FirebaseInsertLikeRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val userEmail: String,
    val updateTime: Date,
)

data class LikeSelectForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)

data class LikeCountSelectForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)

data class LikeResponse(
    val boardAuthorEmail: String? = null,
    val boardCreateTime: Date? = null,
    val userEmail: String? = null,
    val updateTime: Date? = null
)

data class LikeCountResponse(
    val boardAuthorEmail: String? = null,
    val boardCreateTime: Date? = null,
    val likeCount: Int? = null
)


class FirebaseLikeRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : LikeDataSource {

    override suspend fun insertLike(insertLikeRequest: InsertLikeRequest): LikeResponse =
        with(insertLikeRequest) {
            return kotlin.runCatching {
                val firebaseInsertLikeRequest = FirebaseInsertLikeRequest(
                    boardAuthorEmail = boardAuthorEmail,
                    boardCreateTime = boardCreateTime,
                    userEmail = UserSingleton.userEntity.email,
                    updateTime = Date(System.currentTimeMillis()),
                )
                database.collection("Like").add(firebaseInsertLikeRequest).await()
                LikeResponse(
                    boardAuthorEmail = boardAuthorEmail,
                    boardCreateTime = boardCreateTime,
                    userEmail = UserSingleton.userEntity.email,
                    updateTime = Date(System.currentTimeMillis())
                )
            }.onFailure {
                throw FailInsertLikeException("좋아요 인서트를 실패 했습니다")
            }.getOrThrow()
        }

    override suspend fun deleteLike(deleteLikeRequest: DeleteLikeRequest): LikeResponse =
        with(deleteLikeRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("Like")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                snapshot.documents.firstOrNull()?.let {
                    database.collection("Like").document(it.id).delete()
                } ?: throw FailDeleteLikeException("좋아요 딜리트를 실패 했습니다")

                LikeResponse(
                    boardAuthorEmail = boardAuthorEmail,
                    boardCreateTime = boardCreateTime,
                    userEmail = UserSingleton.userEntity.email,
                    updateTime = Date(System.currentTimeMillis())
                )
            }.onFailure {
                throw FailDeleteLikeException("좋아요 딜리트를 실패 했습니다")
            }.getOrThrow()
        }

    override suspend fun selectLike(likeSelectForm: LikeSelectForm): LikeResponse =
        with(likeSelectForm) {
            return kotlin.runCatching {
                val snapshot = database.collection("Like")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                snapshot.documents.firstOrNull()?.let {
                    LikeResponse(
                        boardAuthorEmail = boardAuthorEmail,
                        boardCreateTime = boardCreateTime,
                        userEmail = UserSingleton.userEntity.email,
                        updateTime = it.data?.get("updateTime") as? Date
                    )
                } ?: run {
                    LikeResponse(
                        boardAuthorEmail = "",
                        boardCreateTime = Date(),
                        userEmail = "",
                        updateTime = Date()
                    )
                }

            }.onFailure {
                throw FailLoadLikeException("좋아요 로드를 실패 했습니다")
            }.getOrThrow()

        }

    override suspend fun selectLikeCount(likeCountSelectForm: LikeCountSelectForm): LikeCountResponse =
        with(likeCountSelectForm) {
            return kotlin.runCatching {
                val snapshot = database.collection("Like")
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()
                LikeCountResponse(
                    boardAuthorEmail = boardAuthorEmail,
                    boardCreateTime = boardCreateTime,
                    likeCount = snapshot.documents.size
                )
            }.onFailure {
                throw FailLoadLikeCountException("좋아요 카운트 로드를 실패 했습니다")
            }.getOrThrow()

        }
}