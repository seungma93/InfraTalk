package com.freetalk.domain.repository

import com.freetalk.domain.entity.BookmarkEntity
import com.freetalk.domain.entity.CommentRelatedBookmarksEntity
import com.freetalk.presenter.form.BoardBookmarkAddForm
import com.freetalk.presenter.form.BoardBookmarkDeleteForm
import com.freetalk.presenter.form.BoardBookmarkLoadForm
import com.freetalk.presenter.form.CommentBookmarkAddForm
import com.freetalk.presenter.form.CommentBookmarkDeleteForm
import com.freetalk.presenter.form.CommentBookmarkLoadForm
import com.freetalk.presenter.form.CommentRelatedBookmarksDeleteFrom

interface BookmarkDataRepository {
    suspend fun addBoardBookmark(boardBookmarkAddForm: BoardBookmarkAddForm): BookmarkEntity
    suspend fun deleteBoardBookmark(boardBookmarkDeleteForm: BoardBookmarkDeleteForm): BookmarkEntity
    suspend fun loadBoardBookmark(boardBookmarkLoadForm: BoardBookmarkLoadForm): BookmarkEntity
    suspend fun addCommentBookmark(commentBookmarkAddForm: CommentBookmarkAddForm): BookmarkEntity
    suspend fun deleteCommentBookmark(commentBookmarkDeleteForm: CommentBookmarkDeleteForm): BookmarkEntity
    suspend fun loadCommentBookmark(commentBookmarkLoadForm: CommentBookmarkLoadForm): BookmarkEntity
    suspend fun deleteCommentRelatedBookmarks(
        commentRelatedBookmarksDeleteForm: CommentRelatedBookmarksDeleteFrom
    ): CommentRelatedBookmarksEntity
}
