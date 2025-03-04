package com.sm.domain.repository

import com.sm.infratalk.data.datasource.remote.like.LikeDataSource
import com.sm.infratalk.data.mapper.toEntity
import com.sm.infratalk.data.model.request.board.BoardLikeCountSelectRequest
import com.sm.infratalk.data.model.request.board.BoardLikeDeleteRequest
import com.sm.infratalk.data.model.request.board.BoardLikeInsertRequest
import com.sm.infratalk.data.model.request.board.BoardLikeSelectRequest
import com.sm.infratalk.data.model.request.board.BoardLikesDeleteRequest
import com.sm.infratalk.data.model.request.comment.CommentLikeCountSelectRequest
import com.sm.infratalk.data.model.request.comment.CommentLikeDeleteRequest
import com.sm.infratalk.data.model.request.comment.CommentLikeInsertRequest
import com.sm.infratalk.data.model.request.comment.CommentLikeSelectRequest
import com.sm.infratalk.data.model.request.comment.CommentRelatedLikesDeleteRequest
import com.sm.infratalk.domain.board.entity.BoardLikesDeleteEntity
import com.sm.infratalk.domain.board.entity.CommentRelatedLikesEntity
import com.sm.infratalk.domain.board.entity.LikeCountEntity
import com.sm.infratalk.domain.board.entity.LikeEntity
import com.sm.infratalk.domain.board.repository.LikeDataRepository
import com.sm.infratalk.domain.user.repository.UserDataRepository
import com.sm.infratalk.presenter.board.form.BoardLikeAddForm
import com.sm.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.sm.infratalk.presenter.board.form.BoardLikeDeleteForm
import com.sm.infratalk.presenter.board.form.BoardLikeLoadForm
import com.sm.infratalk.presenter.board.form.BoardLikesDeleteForm
import com.sm.infratalk.presenter.board.form.CommentLikeAddForm
import com.sm.infratalk.presenter.board.form.CommentLikeCountLoadForm
import com.sm.infratalk.presenter.board.form.CommentLikeDeleteForm
import com.sm.infratalk.presenter.board.form.CommentLikeLoadForm
import com.sm.infratalk.presenter.board.form.CommentRelatedLikesDeleteForm
import java.util.Date
import javax.inject.Inject


class LikeDataRepositoryImpl @Inject constructor(private val dataSource: LikeDataSource, private val userDataRepository: UserDataRepository) :
    LikeDataRepository {

    override suspend fun addBoardLike(boardLikeAddForm: BoardLikeAddForm): LikeEntity {
        return dataSource.insertBoardLike(
            BoardLikeInsertRequest(
                boardAuthorEmail = boardLikeAddForm.boardAuthorEmail,
                boardCreateTime = boardLikeAddForm.boardCreateTime,
                userEmail = userDataRepository.getUserMe().email,
                updateTime = Date()
            )
        ).toEntity()
    }

    override suspend fun deleteBoardLike(boardLikeDeleteForm: BoardLikeDeleteForm): LikeEntity {
        return dataSource.deleteBoardLike(
            BoardLikeDeleteRequest(
                boardAuthorEmail = boardLikeDeleteForm.boardAuthorEmail,
                boardCreateTime = boardLikeDeleteForm.boardCreateTime
            )
        ).toEntity()
    }

    override suspend fun loadBoardLike(boardLikeLoadForm: BoardLikeLoadForm): LikeEntity {
        return dataSource.selectBoardLike(
            BoardLikeSelectRequest(
                boardAuthorEmail = boardLikeLoadForm.boardAuthorEmail,
                boardCreateTime = boardLikeLoadForm.boardCreateTime
            )
        ).toEntity()
    }

    override suspend fun loadBoardLikeCount(boardLikeCountLoadForm: BoardLikeCountLoadForm): LikeCountEntity {

        return dataSource.selectBoardLikeCount(
            BoardLikeCountSelectRequest(
                boardAuthorEmail = boardLikeCountLoadForm.boardAuthorEmail,
                boardCreateTime = boardLikeCountLoadForm.boardCreateTime
            )
        ).toEntity()
    }

    override suspend fun addCommentLike(commentLikeAddForm: CommentLikeAddForm): LikeEntity {
        return dataSource.insertCommentLike(
            CommentLikeInsertRequest(
                commentAuthorEmail = commentLikeAddForm.commentAuthorEmail,
                commentCreateTime = commentLikeAddForm.commentCreateTime,
                userEmail = userDataRepository.getUserMe().email,
                updateTime = Date()
            )
        ).toEntity()
    }

    override suspend fun deleteCommentLike(commentLikeDeleteForm: CommentLikeDeleteForm): LikeEntity {
        return dataSource.deleteCommentLike(
            CommentLikeDeleteRequest(
                commentAuthorEmail = commentLikeDeleteForm.commentAuthorEmail,
                commentCreateTime = commentLikeDeleteForm.commentCreateTime
            )
        ).toEntity()
    }

    override suspend fun loadCommentLike(commentLikeLoadForm: CommentLikeLoadForm): LikeEntity {
        return dataSource.selectCommentLike(
            CommentLikeSelectRequest(
                commentAuthorEmail = commentLikeLoadForm.commentAuthorEmail,
                commentCreateTime = commentLikeLoadForm.commentCreateTime
            )
        ).toEntity()
    }

    override suspend fun loadCommentLikeCount(commentLikeCountLoadForm: CommentLikeCountLoadForm): LikeCountEntity {

        return dataSource.selectCommentLikeCount(
            CommentLikeCountSelectRequest(
                commentAuthorEmail = commentLikeCountLoadForm.commentAuthorEmail,
                commentCreateTime = commentLikeCountLoadForm.commentCreateTime
            )
        ).toEntity()
    }

    override suspend fun deleteCommentRelatedLikes(
        commentRelatedLikesDeleteForm: CommentRelatedLikesDeleteForm
    ): CommentRelatedLikesEntity {
        return dataSource.deleteCommentRelatedLikes(
            CommentRelatedLikesDeleteRequest(
                commentAuthorEmail = commentRelatedLikesDeleteForm.commentAuthorEmail,
                commentCreateTime = commentRelatedLikesDeleteForm.commentCreateTime

            )
        ).toEntity()
    }

    override suspend fun deleteBoardLikes(
        boardLikesDeleteForm: BoardLikesDeleteForm
    ): BoardLikesDeleteEntity {
        return dataSource.deleteBoardLikes(
            boardLikesDeleteRequest = BoardLikesDeleteRequest(
                boardAuthorEmail = boardLikesDeleteForm.boardAuthorEmail,
                boardCreateTime = boardLikesDeleteForm.boardCreateTime

            )
        ).toEntity()
    }
}