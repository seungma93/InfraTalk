package com.seungma.infratalk.domain.board.repository

import com.seungma.infratalk.domain.board.entity.BoardBookmarksDeleteEntity
import com.seungma.infratalk.domain.board.entity.BookmarkEntity
import com.seungma.infratalk.domain.board.entity.CommentRelatedBookmarksEntity
import com.seungma.infratalk.presenter.board.form.BoardBookmarkAddForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarkDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarkLoadForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarksDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentBookmarkAddForm
import com.seungma.infratalk.presenter.board.form.CommentBookmarkDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentBookmarkLoadForm
import com.seungma.infratalk.presenter.board.form.CommentRelatedBookmarksDeleteForm

interface BookmarkDataRepository {
    suspend fun addBoardBookmark(boardBookmarkAddForm: BoardBookmarkAddForm): BookmarkEntity
    suspend fun deleteBoardBookmark(boardBookmarkDeleteForm: BoardBookmarkDeleteForm): BookmarkEntity
    suspend fun loadBoardBookmark(boardBookmarkLoadForm: BoardBookmarkLoadForm): BookmarkEntity
    suspend fun addCommentBookmark(commentBookmarkAddForm: CommentBookmarkAddForm): BookmarkEntity
    suspend fun deleteCommentBookmark(commentBookmarkDeleteForm: CommentBookmarkDeleteForm): BookmarkEntity
    suspend fun loadCommentBookmark(commentBookmarkLoadForm: CommentBookmarkLoadForm): BookmarkEntity
    suspend fun deleteCommentRelatedBookmarks(
        commentRelatedBookmarksDeleteForm: CommentRelatedBookmarksDeleteForm
    ): CommentRelatedBookmarksEntity

    suspend fun deleteBoardBookmarks(
        boardBookmarksDeleteForm: BoardBookmarksDeleteForm
    ): BoardBookmarksDeleteEntity
}
