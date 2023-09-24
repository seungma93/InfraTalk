package com.freetalk.data.datasource.remote

import com.freetalk.data.FailDeleteBookMarkException
import com.freetalk.data.FailInsertBookMarkException
import com.freetalk.data.FailLoadBookMarkException
import com.freetalk.data.UserSingleton
import com.freetalk.data.model.request.BoardBookmarkDeleteRequest
import com.freetalk.data.model.request.BoardBookmarkInsertRequest
import com.freetalk.data.model.request.BoardBookmarkSelectRequest
import com.freetalk.data.model.request.CommentBookmarkDeleteRequest
import com.freetalk.data.model.request.CommentBookmarkInsertRequest
import com.freetalk.data.model.request.CommentBookmarkSelectRequest
import com.freetalk.data.model.request.CommentRelatedBookmarksDeleteRequest
import com.freetalk.data.model.response.BookmarkResponse
import com.freetalk.data.model.response.CommentRelatedBookmarksResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject


interface BookmarkDataSource {
    suspend fun insertBoardBookmark(boardBookmarkInsertRequest: BoardBookmarkInsertRequest): BookmarkResponse
    suspend fun deleteBoardBookmark(boardBookmarkDeleteRequest: BoardBookmarkDeleteRequest): BookmarkResponse
    suspend fun selectBoardBookmark(boardBookmarkSelectRequest: BoardBookmarkSelectRequest): BookmarkResponse

    suspend fun insertCommentBookmark(commentBookmarkInsertRequest: CommentBookmarkInsertRequest): BookmarkResponse
    suspend fun deleteCommentBookmark(commentBookmarkDeleteRequest: CommentBookmarkDeleteRequest): BookmarkResponse
    suspend fun selectCommentBookmark(commentBookmarkSelectRequest: CommentBookmarkSelectRequest): BookmarkResponse
    suspend fun deleteCommentRelatedBookMarks(commentRelatedBookmarksDeleteRequest: CommentRelatedBookmarksDeleteRequest): CommentRelatedBookmarksResponse
}

class FirebaseBookmarkRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : BookmarkDataSource {

    override suspend fun insertBoardBookmark(boardBookmarkInsertRequest: BoardBookmarkInsertRequest): BookmarkResponse =
        with(boardBookmarkInsertRequest) {
            return kotlin.runCatching {
                database.collection("BoardBookmark").add(
                    boardBookmarkInsertRequest.copy(updateTime = Date(System.currentTimeMillis()))
                ).await()
                BookmarkResponse(isBookmark = true)
            }.onFailure {
                throw FailInsertBookMarkException("북마크 인서트에 실패했습니다")
            }.getOrThrow()
        }

    override suspend fun deleteBoardBookmark(boardBookmarkDeleteRequest: BoardBookmarkDeleteRequest): BookmarkResponse =
        with(boardBookmarkDeleteRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("BoardBookmark")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                snapshot.documents.firstOrNull()?.apply {
                    database.collection("BoardBookMark").document(id).delete()
                } ?: throw FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")
                BookmarkResponse(isBookmark = false)
            }.onFailure {
                throw FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")
            }.getOrThrow()
        }

    override suspend fun selectBoardBookmark(boardBookmarkSelectRequest: BoardBookmarkSelectRequest): BookmarkResponse =
        with(boardBookmarkSelectRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("BoardBookmark")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                BookmarkResponse(
                    isBookmark = when (snapshot.documents.firstOrNull()) {
                        null -> false
                        else -> true
                    }
                )
            }.onFailure {
                throw FailLoadBookMarkException("좋아요 로드를 실패 했습니다")
            }.getOrThrow()

        }

    override suspend fun insertCommentBookmark(commentBookmarkInsertRequest: CommentBookmarkInsertRequest): BookmarkResponse =
        with(commentBookmarkInsertRequest) {
            return kotlin.runCatching {
                database.collection("CommentBookmark").add(this).await()
                BookmarkResponse(isBookmark = true)
            }.onFailure {
                throw FailInsertBookMarkException("북마크 인서트에 실패했습니다")
            }.getOrThrow()
        }

    override suspend fun deleteCommentBookmark(commentBookmarkDeleteRequest: CommentBookmarkDeleteRequest): BookmarkResponse =
        with(commentBookmarkDeleteRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("CommentBookmark")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("commentAuthorEmail", commentAuthorEmail)
                    .whereEqualTo("commentCreateTime", commentCreateTime)
                    .get().await()

                snapshot.documents.firstOrNull()?.let {
                    database.collection("CommentBookmark").document(it.id).delete().await()
                } ?: throw FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")

                BookmarkResponse(isBookmark = false)
            }.onFailure {
                throw FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")
            }.getOrThrow()
        }

    override suspend fun selectCommentBookmark(commentBookmarkSelectRequest: CommentBookmarkSelectRequest): BookmarkResponse =
        with(commentBookmarkSelectRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("CommentBookmark")
                    .whereEqualTo("userEmail", UserSingleton.userEntity.email)
                    .whereEqualTo("commentAuthorEmail", commentAuthorEmail)
                    .whereEqualTo("commentCreateTime", commentCreateTime)
                    .get().await()

                BookmarkResponse(
                    isBookmark = when (snapshot.documents.firstOrNull()) {
                        null -> false
                        else -> true
                    }
                )

            }.onFailure {
                throw FailLoadBookMarkException("좋아요 로드를 실패 했습니다")
            }.getOrThrow()

        }

    override suspend fun deleteCommentRelatedBookMarks(commentRelatedBookmarksDeleteRequest: CommentRelatedBookmarksDeleteRequest): CommentRelatedBookmarksResponse =
        with(commentRelatedBookmarksDeleteRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("CommentBookmark")
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                snapshot.documents.map {
                    database.collection("CommentBookmark").document(it.id).delete().await()
                }
                CommentRelatedBookmarksResponse(isBookmarks = false)

            }.onFailure {
                throw FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")
            }.getOrThrow()
        }
}