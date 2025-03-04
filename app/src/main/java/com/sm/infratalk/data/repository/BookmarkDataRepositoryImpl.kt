package com.sm.domain.repository


import com.sm.infratalk.data.datasource.remote.bookmark.BookmarkDataSource
import com.sm.infratalk.data.mapper.toEntity
import com.sm.infratalk.data.model.request.board.BoardBookMarksDeleteRequest
import com.sm.infratalk.data.model.request.board.BoardBookmarkDeleteRequest
import com.sm.infratalk.data.model.request.board.BoardBookmarkInsertRequest
import com.sm.infratalk.data.model.request.board.BoardBookmarkSelectRequest
import com.sm.infratalk.data.model.request.comment.CommentBookmarkDeleteRequest
import com.sm.infratalk.data.model.request.comment.CommentBookmarkInsertRequest
import com.sm.infratalk.data.model.request.comment.CommentBookmarkSelectRequest
import com.sm.infratalk.data.model.request.comment.CommentRelatedBookmarksDeleteRequest
import com.sm.infratalk.domain.board.entity.BoardBookmarksDeleteEntity
import com.sm.infratalk.domain.board.entity.BookmarkEntity
import com.sm.infratalk.domain.board.entity.CommentRelatedBookmarksEntity
import com.sm.infratalk.domain.board.repository.BookmarkDataRepository
import com.sm.infratalk.domain.user.repository.UserDataRepository
import com.sm.infratalk.presenter.board.form.BoardBookmarkAddForm
import com.sm.infratalk.presenter.board.form.BoardBookmarkDeleteForm
import com.sm.infratalk.presenter.board.form.BoardBookmarkLoadForm
import com.sm.infratalk.presenter.board.form.BoardBookmarksDeleteForm
import com.sm.infratalk.presenter.board.form.CommentBookmarkAddForm
import com.sm.infratalk.presenter.board.form.CommentBookmarkDeleteForm
import com.sm.infratalk.presenter.board.form.CommentBookmarkLoadForm
import com.sm.infratalk.presenter.board.form.CommentRelatedBookmarksDeleteForm
import java.util.Date
import javax.inject.Inject

class BookmarkDataRepositoryImpl @Inject constructor(private val dataSource: BookmarkDataSource, private val userDataRepository: UserDataRepository) :
    BookmarkDataRepository {
    override suspend fun addBoardBookmark(boardBookmarkAddForm: BoardBookmarkAddForm): BookmarkEntity {
        return dataSource.insertBoardBookmark(
            BoardBookmarkInsertRequest(
                boardAuthorEmail = boardBookmarkAddForm.boardAuthorEmail,
                boardCreateTime = boardBookmarkAddForm.boardCreateTime,
                userEmail = userDataRepository.getUserMe().email,
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
                userEmail = userDataRepository.getUserMe().email,
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