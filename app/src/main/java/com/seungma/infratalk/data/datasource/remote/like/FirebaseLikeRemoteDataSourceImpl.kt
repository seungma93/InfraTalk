package com.seungma.infratalk.data.datasource.remote.like

import com.google.firebase.firestore.FirebaseFirestore
import com.seungma.infratalk.data.model.request.board.BoardLikeCountSelectRequest
import com.seungma.infratalk.data.model.request.board.BoardLikeDeleteRequest
import com.seungma.infratalk.data.model.request.board.BoardLikeInsertRequest
import com.seungma.infratalk.data.model.request.board.BoardLikeSelectRequest
import com.seungma.infratalk.data.model.request.board.BoardLikesDeleteRequest
import com.seungma.infratalk.data.model.request.comment.CommentLikeCountSelectRequest
import com.seungma.infratalk.data.model.request.comment.CommentLikeDeleteRequest
import com.seungma.infratalk.data.model.request.comment.CommentLikeInsertRequest
import com.seungma.infratalk.data.model.request.comment.CommentLikeSelectRequest
import com.seungma.infratalk.data.model.request.comment.CommentRelatedLikesDeleteRequest
import com.seungma.infratalk.data.model.response.board.BoardLikesDeleteResponse
import com.seungma.infratalk.data.model.response.comment.CommentRelatedLikesResponse
import com.seungma.infratalk.data.model.response.like.LikeCountResponse
import com.seungma.infratalk.data.model.response.like.LikeResponse
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject


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
                throw com.seungma.infratalk.data.FailInsertLikeException("좋아요 인서트를 실패 했습니다")
            }.getOrThrow()
        }

    override suspend fun deleteBoardLike(boardLikeDeleteRequest: BoardLikeDeleteRequest): LikeResponse =
        with(boardLikeDeleteRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("BoardLike")
                    .whereEqualTo(
                        "userEmail",
                        com.seungma.infratalk.data.UserSingleton.userEntity.email
                    )
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                snapshot.documents.firstOrNull()?.apply {
                    database.collection("BoardLike").document(id).delete().await()
                } ?: throw com.seungma.infratalk.data.FailDeleteLikeException("좋아요 딜리트를 실패 했습니다")

                LikeResponse(isLike = false)
            }.onFailure {
                throw com.seungma.infratalk.data.FailDeleteLikeException("좋아요 딜리트를 실패 했습니다")
            }.getOrThrow()
        }

    override suspend fun selectBoardLike(boardLikeSelectRequest: BoardLikeSelectRequest): LikeResponse =
        with(boardLikeSelectRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("BoardLike")
                    .whereEqualTo(
                        "userEmail",
                        com.seungma.infratalk.data.UserSingleton.userEntity.email
                    )
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
                throw com.seungma.infratalk.data.FailLoadLikeException("좋아요 로드를 실패 했습니다")
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
                throw com.seungma.infratalk.data.FailLoadLikeCountException("좋아요 카운트 로드를 실패 했습니다")
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
                throw com.seungma.infratalk.data.FailInsertLikeException("좋아요 인서트를 실패 했습니다")
            }.getOrThrow()
        }

    override suspend fun deleteCommentLike(commentLikeDeleteRequest: CommentLikeDeleteRequest): LikeResponse =
        with(commentLikeDeleteRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("CommentLike")
                    .whereEqualTo(
                        "userEmail",
                        com.seungma.infratalk.data.UserSingleton.userEntity.email
                    )
                    .whereEqualTo("commentAuthorEmail", commentAuthorEmail)
                    .whereEqualTo("commentCreateTime", commentCreateTime)
                    .get().await()

                snapshot.documents.firstOrNull()?.apply {
                    database.collection("CommentLike").document(id).delete().await()
                } ?: throw com.seungma.infratalk.data.FailDeleteLikeException("좋아요 딜리트를 실패 했습니다")

                LikeResponse(isLike = false)
            }.onFailure {
                throw com.seungma.infratalk.data.FailDeleteLikeException("좋아요 딜리트를 실패 했습니다")
            }.getOrThrow()
        }

    override suspend fun selectCommentLike(commentLikeSelectRequest: CommentLikeSelectRequest): LikeResponse =
        with(commentLikeSelectRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("CommentLike")
                    .whereEqualTo(
                        "userEmail",
                        com.seungma.infratalk.data.UserSingleton.userEntity.email
                    )
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
                throw com.seungma.infratalk.data.FailLoadLikeException("좋아요 로드를 실패 했습니다")
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
                throw com.seungma.infratalk.data.FailLoadLikeCountException("좋아요 카운트 로드를 실패 했습니다")
            }.getOrThrow()

        }

    override suspend fun deleteCommentRelatedLikes(commentRelatedLikesDeleteRequest: CommentRelatedLikesDeleteRequest): CommentRelatedLikesResponse =
        with(commentRelatedLikesDeleteRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("CommentLike")
                    .whereEqualTo("commentAuthorEmail", commentAuthorEmail)
                    .whereEqualTo("commentCreateTime", commentCreateTime)
                    .get().await()

                snapshot.documents.map {
                    database.collection("CommentLike").document(it.id).delete().await()
                }

                CommentRelatedLikesResponse(isLikes = false)
            }.onFailure {
                throw com.seungma.infratalk.data.FailDeleteLikeException("좋아요 딜리트를 실패 했습니다")
            }.getOrThrow()
        }

    override suspend fun deleteBoardLikes(boardLikesDeleteRequest: BoardLikesDeleteRequest): BoardLikesDeleteResponse =
        with(boardLikesDeleteRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("BoardLike")
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                snapshot.documents.map {
                    database.collection("BoardLike").document(it.id).delete().await()
                }

                BoardLikesDeleteResponse(isBoardLikes = false)
            }.onFailure {
                throw com.seungma.infratalk.data.FailDeleteLikeException("좋아요 딜리트를 실패 했습니다")
            }.getOrThrow()
        }
}