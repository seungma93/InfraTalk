package com.seungma.domain.repository

import com.seungma.infratalk.data.UserSingleton
import com.seungma.infratalk.data.datasource.remote.BookmarkDataSource
import com.seungma.infratalk.data.mapper.toEntity
import com.seungma.infratalk.data.model.request.board.BoardBookMarksDeleteRequest
import com.seungma.infratalk.data.model.request.board.BoardBookmarkDeleteRequest
import com.seungma.infratalk.data.model.request.board.BoardBookmarkInsertRequest
import com.seungma.infratalk.data.model.request.board.BoardBookmarkSelectRequest
import com.seungma.infratalk.data.model.request.comment.CommentBookmarkDeleteRequest
import com.seungma.infratalk.data.model.request.comment.CommentBookmarkInsertRequest
import com.seungma.infratalk.data.model.request.comment.CommentBookmarkSelectRequest
import com.seungma.infratalk.data.model.request.comment.CommentRelatedBookmarksDeleteRequest
import com.seungma.infratalk.domain.BoardBookmarksDeleteEntity
import com.seungma.infratalk.domain.BookmarkDataRepository
import com.seungma.infratalk.domain.BookmarkEntity
import com.seungma.infratalk.domain.CommentRelatedBookmarksEntity
import com.seungma.infratalk.presenter.board.form.BoardBookmarkAddForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarkDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarkLoadForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarksDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentBookmarkAddForm
import com.seungma.infratalk.presenter.board.form.CommentBookmarkDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentBookmarkLoadForm
import com.seungma.infratalk.presenter.board.form.CommentRelatedBookmarksDeleteForm
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
        commentRelatedBookmarksDeleteForm: CommentRelatedBookmarksDeleteForm
    ): CommentRelatedBookmarksEntity {
        return dataSource.deleteCommentRelatedBookMarks(
            CommentRelatedBookmarksDeleteRequest(
                commentAuthorEmail = commentRelatedBookmarksDeleteForm.commentAuthorEmail,
                commentCreateTime = commentRelatedBookmarksDeleteForm.commentCreateTime

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