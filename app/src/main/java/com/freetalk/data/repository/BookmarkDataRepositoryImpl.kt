package com.freetalk.domain.repository

import com.freetalk.data.UserSingleton
import com.freetalk.data.datasource.remote.BookmarkDataSource
import com.freetalk.data.mapper.toEntity
import com.freetalk.data.model.request.BoardBookMarksDeleteRequest
import com.freetalk.data.model.request.BoardBookmarkDeleteRequest
import com.freetalk.data.model.request.BoardBookmarkInsertRequest
import com.freetalk.data.model.request.BoardBookmarkSelectRequest
import com.freetalk.data.model.request.CommentBookmarkDeleteRequest
import com.freetalk.data.model.request.CommentBookmarkInsertRequest
import com.freetalk.data.model.request.CommentBookmarkSelectRequest
import com.freetalk.data.model.request.CommentRelatedBookmarksDeleteRequest
import com.freetalk.domain.entity.BoardBookmarksDeleteEntity
import com.freetalk.domain.entity.BookmarkEntity
import com.freetalk.domain.entity.CommentRelatedBookmarksEntity
import com.freetalk.presenter.form.BoardBookmarkAddForm
import com.freetalk.presenter.form.BoardBookmarkDeleteForm
import com.freetalk.presenter.form.BoardBookmarkLoadForm
import com.freetalk.presenter.form.BoardBookmarksDeleteForm
import com.freetalk.presenter.form.CommentBookmarkAddForm
import com.freetalk.presenter.form.CommentBookmarkDeleteForm
import com.freetalk.presenter.form.CommentBookmarkLoadForm
import com.freetalk.presenter.form.CommentRelatedBookmarksDeleteFrom
import java.util.Date
import javax.inject.Inject

class BookmarkDataRepositoryImpl @Inject constructor(private val dataSource: BookmarkDataSource) :
    BookmarkDataRepository {
    override suspend fun addBoardBookmark(boardBookmarkAddForm: BoardBookmarkAddForm): BookmarkEntity {
        return dataSource.insertBoardBookmark(
            BoardBookmarkInsertRequest(
                boardAuthorEmail = boardBookmarkAddForm.boardAuthorEmail,
                boardCreateTime = boardBookmarkAddForm.boardCreateTime,
                userEmail = UserSingleton.userEntity.email,
                updateTime = Date()
            )
        ).toEntity()
    }

    override suspend fun deleteBoardBookmark(boardBookmarkDeleteForm: BoardBookmarkDeleteForm): BookmarkEntity {
        return dataSource.deleteBoardBookmark(
            BoardBookmarkDeleteRequest(
                boardAuthorEmail = boardBookmarkDeleteForm.boardAuthorEmail,
                boardCreateTime = boardBookmarkDeleteForm.boardCreateTime
            )
        ).toEntity()
    }

    override suspend fun loadBoardBookmark(boardBookmarkLoadForm: BoardBookmarkLoadForm): BookmarkEntity {
        return dataSource.selectBoardBookmark(
            BoardBookmarkSelectRequest(
                boardAuthorEmail = boardBookmarkLoadForm.boardAuthorEmail,
                boardCreateTime = boardBookmarkLoadForm.boardCreateTime
            )
        ).toEntity()
    }

    override suspend fun addCommentBookmark(commentBookmarkAddForm: CommentBookmarkAddForm): BookmarkEntity {
        return dataSource.insertCommentBookmark(
            CommentBookmarkInsertRequest(
                commentAuthorEmail = commentBookmarkAddForm.commentAuthorEmail,
                commentCreateTime = commentBookmarkAddForm.commentCreateTime,
                userEmail = UserSingleton.userEntity.email,
                updateTime = Date()
            )
        ).toEntity()
    }

    override suspend fun deleteCommentBookmark(commentBookmarkDeleteForm: CommentBookmarkDeleteForm): BookmarkEntity {
        return dataSource.deleteCommentBookmark(
            CommentBookmarkDeleteRequest(
                commentAuthorEmail = commentBookmarkDeleteForm.commentAuthorEmail,
                commentCreateTime = commentBookmarkDeleteForm.commentCreateTime
            )
        ).toEntity()
    }

    override suspend fun loadCommentBookmark(commentBookmarkLoadForm: CommentBookmarkLoadForm): BookmarkEntity {
        return dataSource.selectCommentBookmark(
            CommentBookmarkSelectRequest(
                commentAuthorEmail = commentBookmarkLoadForm.commentAuthorEmail,
                commentCreateTime = commentBookmarkLoadForm.commentCreateTime
            )
        ).toEntity()
    }

    override suspend fun deleteCommentRelatedBookmarks(
        commentRelatedBookmarksDeleteForm: CommentRelatedBookmarksDeleteFrom
    ): CommentRelatedBookmarksEntity {
        return dataSource.deleteCommentRelatedBookMarks(
            CommentRelatedBookmarksDeleteRequest(
                boardAuthorEmail = commentRelatedBookmarksDeleteForm.boardAuthorEmail,
                boardCreateTime = commentRelatedBookmarksDeleteForm.boardCreateTime

            )
        ).toEntity()
    }

    override suspend fun deleteBoardBookmarks(
        boardBookmarksDeleteForm: BoardBookmarksDeleteForm
    ): BoardBookmarksDeleteEntity {
        return dataSource.deleteBoardBookMarks(
            boardBookMarksDeleteRequest = BoardBookMarksDeleteRequest(
                boardAuthorEmail = boardBookmarksDeleteForm.boardAuthorEmail,
                boardCreateTime = boardBookmarksDeleteForm.boardCreateTime
            )
        ).toEntity()
    }
}