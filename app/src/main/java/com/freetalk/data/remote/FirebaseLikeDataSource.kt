package com.freetalk.data.remote

import com.freetalk.data.FailLoadLikeCountException
import com.freetalk.data.FailLoadLikeException
import com.freetalk.data.FailUpdateLikeException
import com.freetalk.data.UserSingleton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject


interface LikeDataSource {
    suspend fun updateLike(likeUpdateForm: LikeUpdateForm): LikeResponse
    suspend fun selectLike(likeSelectForm: LikeSelectForm): LikeResponse
    suspend fun selectLikeCount(likeCountSelectForm: LikeCountSelectForm): LikeCountResponse
}

data class LikeUpdateForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
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

    override suspend fun updateLike(likeUpdateForm: LikeUpdateForm): LikeResponse =
        with(likeUpdateForm) {
            return kotlin.runCatching {
                val snapshot = database.collection("Like")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                val likeResponse = LikeResponse(
                    boardAuthorEmail = boardAuthorEmail,
                    boardCreateTime = boardCreateTime,
                    userEmail = UserSingleton.userEntity.email,
                    updateTime = Date(System.currentTimeMillis())
                )

                snapshot.documents.firstOrNull()?.let {
                    database.collection("Like").document(it.id).delete()
                } ?: run {
                    database.collection("Like").add(likeResponse).await()
                }

                likeResponse
            }.onFailure {
               throw FailUpdateLikeException("좋아요 업데이트를 실패 했습니다")
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
                throw FailLoadLikeException("좋아요 ㄴ로드를 실패 했습니다")
            }.getOrThrow()

        }

    override suspend fun selectLikeCount(likeCountSelectForm: LikeCountSelectForm): LikeCountResponse = with(likeCountSelectForm) {
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