package com.freetalk.data.datasource.remote

import com.freetalk.data.FailDeleteLikeException
import com.freetalk.data.FailInsertLikeException
import com.freetalk.data.FailLoadLikeCountException
import com.freetalk.data.FailLoadLikeException
import com.freetalk.data.UserSingleton
import com.freetalk.data.model.request.BoardLikeCountSelectRequest
import com.freetalk.data.model.request.BoardLikeDeleteRequest
import com.freetalk.data.model.request.BoardLikeInsertRequest
import com.freetalk.data.model.request.BoardLikeSelectRequest
import com.freetalk.data.model.request.CommentLikeCountSelectRequest
import com.freetalk.data.model.request.CommentLikeDeleteRequest
import com.freetalk.data.model.request.CommentLikeInsertRequest
import com.freetalk.data.model.request.CommentLikeSelectRequest
import com.freetalk.data.model.request.CommentRelatedLikesDeleteRequest
import com.freetalk.data.model.response.CommentRelatedLikesResponse
import com.freetalk.data.model.response.LikeCountResponse
import com.freetalk.data.model.response.LikeResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject


interface LikeDataSource {
    suspend fun insertBoardLike(boardLikeInsertRequest: BoardLikeInsertRequest): LikeResponse
    suspend fun deleteBoardLike(boardLikeDeleteRequest: BoardLikeDeleteRequest): LikeResponse
    suspend fun selectBoardLike(boardLikeSelectRequest: BoardLikeSelectRequest): LikeResponse
    suspend fun selectBoardLikeCount(boardLikeCountSelectRequest: BoardLikeCountSelectRequest): LikeCountResponse

    suspend fun insertCommentLike(commentLikeInsertRequest: CommentLikeInsertRequest): LikeResponse
    suspend fun deleteCommentLike(commentLikeDeleteRequest: CommentLikeDeleteRequest): LikeResponse
    suspend fun selectCommentLike(commentLikeSelectRequest: CommentLikeSelectRequest): LikeResponse
    suspend fun selectCommentLikeCount(commentLikeCountSelectRequest: CommentLikeCountSelectRequest): LikeCountResponse
    suspend fun deleteCommentRelatedLikes(commentRelatedLikesDeleteRequest: CommentRelatedLikesDeleteRequest): CommentRelatedLikesResponse
}


class FirebaseLikeRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : LikeDataSource {

    override suspend fun insertBoardLike(boardLikeInsertRequest: BoardLikeInsertRequest): LikeResponse =
        with(boardLikeInsertRequest) {
            return kotlin.runCatching {
                database.collection("BoardLike")
                    .add(boardLikeInsertRequest.copy(updateTime = Date(System.currentTimeMillis())))
                    .await()
                LikeResponse(isLike = true)
            }.onFailure {
                throw FailInsertLikeException("좋아요 인서트를 실패 했습니다")
            }.getOrThrow()
        }

    override suspend fun deleteBoardLike(boardLikeDeleteRequest: BoardLikeDeleteRequest): LikeResponse =
        with(boardLikeDeleteRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("BoardLike")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                snapshot.documents.firstOrNull()?.let {
                    database.collection("BoardLike").document(it.id).delete().await()
                } ?: throw FailDeleteLikeException("좋아요 딜리트를 실패 했습니다")

                LikeResponse(isLike = false)
            }.onFailure {
                throw FailDeleteLikeException("좋아요 딜리트를 실패 했습니다")
            }.getOrThrow()
        }

    override suspend fun selectBoardLike(boardLikeSelectRequest: BoardLikeSelectRequest): LikeResponse =
        with(boardLikeSelectRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("BoardLike")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                LikeResponse(
                    isLike = when (snapshot.documents.firstOrNull()) {
                        null -> false
                        else -> true
                    }
                )

            }.onFailure {
                throw FailLoadLikeException("좋아요 로드를 실패 했습니다")
            }.getOrThrow()

        }

    override suspend fun selectBoardLikeCount(boardLikeCountSelectRequest: BoardLikeCountSelectRequest): LikeCountResponse =
        with(boardLikeCountSelectRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("BoardLike")
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()
                LikeCountResponse(
                    likeCount = snapshot.documents.size
                )
            }.onFailure {
                throw FailLoadLikeCountException("좋아요 카운트 로드를 실패 했습니다")
            }.getOrThrow()

        }

    override suspend fun insertCommentLike(commentLikeInsertRequest: CommentLikeInsertRequest): LikeResponse =
        with(commentLikeInsertRequest) {
            return kotlin.runCatching {
                database.collection("CommentLike")
                    .add(commentLikeInsertRequest.copy(updateTime = Date(System.currentTimeMillis())))
                    .await()
                LikeResponse(isLike = true)
            }.onFailure {
                throw FailInsertLikeException("좋아요 인서트를 실패 했습니다")
            }.getOrThrow()
        }

    override suspend fun deleteCommentLike(commentLikeDeleteRequest: CommentLikeDeleteRequest): LikeResponse =
        with(commentLikeDeleteRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("CommentLike")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("commentAuthorEmail", commentAuthorEmail)
                    .whereEqualTo("commentCreateTime", commentCreateTime)
                    .get().await()

                snapshot.documents.firstOrNull()?.let {
                    database.collection("CommentLike").document(it.id).delete().await()
                } ?: throw FailDeleteLikeException("좋아요 딜리트를 실패 했습니다")

                LikeResponse(isLike = false)
            }.onFailure {
                throw FailDeleteLikeException("좋아요 딜리트를 실패 했습니다")
            }.getOrThrow()
        }

    override suspend fun selectCommentLike(commentLikeSelectRequest: CommentLikeSelectRequest): LikeResponse =
        with(commentLikeSelectRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("CommentLike")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("commentAuthorEmail", commentAuthorEmail)
                    .whereEqualTo("commentCreateTime", commentCreateTime)
                    .get().await()

                LikeResponse(
                    isLike = when (snapshot.documents.firstOrNull()) {
                        null -> false
                        else -> true
                    }
                )
            }.onFailure {
                throw FailLoadLikeException("좋아요 로드를 실패 했습니다")
            }.getOrThrow()

        }

    override suspend fun selectCommentLikeCount(commentLikeCountSelectRequest: CommentLikeCountSelectRequest): LikeCountResponse =
        with(commentLikeCountSelectRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("CommentLike")
                    .whereEqualTo("commentAuthorEmail", commentAuthorEmail)
                    .whereEqualTo("commentCreateTime", commentCreateTime)
                    .get().await()
                LikeCountResponse(likeCount = snapshot.documents.size)
            }.onFailure {
                throw FailLoadLikeCountException("좋아요 카운트 로드를 실패 했습니다")
            }.getOrThrow()

        }

    override suspend fun deleteCommentRelatedLikes(commentRelatedLikesDeleteRequest: CommentRelatedLikesDeleteRequest): CommentRelatedLikesResponse =
        with(commentRelatedLikesDeleteRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("CommentLike")
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                    snapshot.documents.map {
                        database.collection("CommentLike").document(it.id).delete().await()
                    }

                CommentRelatedLikesResponse(isLikes = false)
            }.onFailure {
                throw FailDeleteLikeException("좋아요 딜리트를 실패 했습니다")
            }.getOrThrow()
        }
}