package com.seungma.infratalk.data.datasource.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.seungma.infratalk.data.model.request.board.BoardBookMarksDeleteRequest
import com.seungma.infratalk.data.model.request.board.BoardBookmarkDeleteRequest
import com.seungma.infratalk.data.model.request.board.BoardBookmarkInsertRequest
import com.seungma.infratalk.data.model.request.board.BoardBookmarkSelectRequest
import com.seungma.infratalk.data.model.request.comment.CommentBookmarkDeleteRequest
import com.seungma.infratalk.data.model.request.comment.CommentBookmarkInsertRequest
import com.seungma.infratalk.data.model.request.comment.CommentBookmarkSelectRequest
import com.seungma.infratalk.data.model.request.comment.CommentRelatedBookmarksDeleteRequest
import com.seungma.infratalk.data.model.response.board.BoardBookmarksDeleteResponse
import com.seungma.infratalk.data.model.response.bookmark.BookmarkResponse
import com.seungma.infratalk.data.model.response.comment.CommentRelatedBookmarksResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
    suspend fun deleteCommentRelatedBookMarks(
        commentRelatedBookmarksDeleteRequest: CommentRelatedBookmarksDeleteRequest
    ): CommentRelatedBookmarksResponse

    suspend fun deleteBoardBookMarks(
        boardBookMarksDeleteRequest: BoardBookMarksDeleteRequest
    ): BoardBookmarksDeleteResponse
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
                throw com.seungma.infratalk.data.FailInsertBookMarkException("북마크 인서트에 실패했습니다")
            }.getOrThrow()
        }

    override suspend fun deleteBoardBookmark(boardBookmarkDeleteRequest: BoardBookmarkDeleteRequest): BookmarkResponse =
        with(boardBookmarkDeleteRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("BoardBookmark")
                    .whereEqualTo(
                        "userEmail",
                        com.seungma.infratalk.data.UserSingleton.userEntity.email
                    )
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                snapshot.documents.firstOrNull()?.apply {
                    database.collection("BoardBookmark").document(id).delete().await()
                } ?: throw com.seungma.infratalk.data.FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")
                BookmarkResponse(isBookmark = false)
            }.onFailure {
                throw com.seungma.infratalk.data.FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")
            }.getOrThrow()
        }

    override suspend fun selectBoardBookmark(boardBookmarkSelectRequest: BoardBookmarkSelectRequest): BookmarkResponse =
        with(boardBookmarkSelectRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("BoardBookmark")
                    .whereEqualTo(
                        "userEmail",
                        com.seungma.infratalk.data.UserSingleton.userEntity.email
                    )
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()

                BookmarkResponse(
                    isBookmark = snapshot.documents.firstOrNull()?.let { true } ?: false
                )
            }.onFailure {
                throw com.seungma.infratalk.data.FailLoadBookMarkException("좋아요 로드를 실패 했습니다")
            }.getOrThrow()

        }

    override suspend fun insertCommentBookmark(commentBookmarkInsertRequest: CommentBookmarkInsertRequest): BookmarkResponse =
        with(commentBookmarkInsertRequest) {
            return kotlin.runCatching {
                database.collection("CommentBookmark").add(this).await()
                BookmarkResponse(isBookmark = true)
            }.onFailure {
                throw com.seungma.infratalk.data.FailInsertBookMarkException("북마크 인서트에 실패했습니다")
            }.getOrThrow()
        }

    override suspend fun deleteCommentBookmark(commentBookmarkDeleteRequest: CommentBookmarkDeleteRequest): BookmarkResponse =
        with(commentBookmarkDeleteRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("CommentBookmark")
                    .whereEqualTo(
                        "userEmail",
                        com.seungma.infratalk.data.UserSingleton.userEntity.email
                    )
                    .whereEqualTo("commentAuthorEmail", commentAuthorEmail)
                    .whereEqualTo("commentCreateTime", commentCreateTime)
                    .get().await()

                snapshot.documents.firstOrNull()?.apply {
                    database.collection("CommentBookmark").document(id).delete().await()
                } ?: throw com.seungma.infratalk.data.FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")

                BookmarkResponse(isBookmark = false)
            }.onFailure {
                throw com.seungma.infratalk.data.FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")
            }.getOrThrow()
        }

    override suspend fun selectCommentBookmark(commentBookmarkSelectRequest: CommentBookmarkSelectRequest): BookmarkResponse =
        with(commentBookmarkSelectRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("CommentBookmark")
                    .whereEqualTo(
                        "userEmail",
                        com.seungma.infratalk.data.UserSingleton.userEntity.email
                    )
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
                throw com.seungma.infratalk.data.FailLoadBookMarkException("좋아요 로드를 실패 했습니다")
            }.getOrThrow()

        }

    override suspend fun deleteCommentRelatedBookMarks(
        commentRelatedBookmarksDeleteRequest: CommentRelatedBookmarksDeleteRequest
    ): CommentRelatedBookmarksResponse = coroutineScope {
        with(commentRelatedBookmarksDeleteRequest) {
            kotlin.runCatching {
                val snapshot = database.collection("CommentBookmark")
                    .whereEqualTo("commentAuthorEmail", commentAuthorEmail)
                    .whereEqualTo("commentCreateTime", commentCreateTime)
                    .get().await()
                launch(Dispatchers.IO) {
                    snapshot.documents.map {
                        launch {
                            database.collection("CommentBookmark").document(it.id).delete()
                        }
                    }
                }.join()
                CommentRelatedBookmarksResponse(isBookmarks = false)
            }.onFailure {
                throw com.seungma.infratalk.data.FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")
            }.getOrThrow()
        }
    }

    override suspend fun deleteBoardBookMarks(
        boardBookMarksDeleteRequest: BoardBookMarksDeleteRequest
    ): BoardBookmarksDeleteResponse = coroutineScope {
        with(boardBookMarksDeleteRequest) {
            kotlin.runCatching {
                val snapshot = database.collection("BoardBookmark")
                    .whereEqualTo("boardAuthorEmail", boardAuthorEmail)
                    .whereEqualTo("boardCreateTime", boardCreateTime)
                    .get().await()
                launch(Dispatchers.IO) {
                    snapshot.documents.map {
                        launch {
                            database.collection("BoardBookmark").document(it.id).delete()
                        }
                    }
                }.join()
                BoardBookmarksDeleteResponse(isBoardBookmarks = false)
            }.onFailure {
                throw com.seungma.infratalk.data.FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")
            }.getOrThrow()
        }
    }
}