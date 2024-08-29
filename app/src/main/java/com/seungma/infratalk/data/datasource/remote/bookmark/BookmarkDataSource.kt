package com.seungma.infratalk.data.datasource.remote.bookmark

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