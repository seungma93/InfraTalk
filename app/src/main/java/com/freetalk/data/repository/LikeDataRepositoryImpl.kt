package com.freetalk.domain.repository

import com.freetalk.data.UserSingleton
import com.freetalk.data.datasource.remote.LikeDataSource
import com.freetalk.data.mapper.toEntity
import com.freetalk.data.model.request.BoardLikeCountSelectRequest
import com.freetalk.data.model.request.BoardLikeDeleteRequest
import com.freetalk.data.model.request.BoardLikeInsertRequest
import com.freetalk.data.model.request.BoardLikeSelectRequest
import com.freetalk.data.model.request.CommentLikeCountSelectRequest
import com.freetalk.data.model.request.CommentLikeDeleteRequest
import com.freetalk.data.model.request.CommentLikeInsertRequest
import com.freetalk.data.model.request.CommentLikeSelectRequest
import com.freetalk.data.model.request.CommentRelatedLikesDeleteRequest
import com.freetalk.domain.entity.CommentRelatedLikesEntity
import com.freetalk.domain.entity.LikeCountEntity
import com.freetalk.domain.entity.LikeEntity
import com.freetalk.presenter.form.BoardLikeAddForm
import com.freetalk.presenter.form.BoardLikeCountLoadForm
import com.freetalk.presenter.form.BoardLikeDeleteForm
import com.freetalk.presenter.form.BoardLikeLoadForm
import com.freetalk.presenter.form.CommentLikeAddForm
import com.freetalk.presenter.form.CommentLikeCountLoadForm
import com.freetalk.presenter.form.CommentLikeDeleteForm
import com.freetalk.presenter.form.CommentLikeLoadForm
import com.freetalk.presenter.form.CommentRelatedLikesDeleteForm
import java.util.Date
import javax.inject.Inject


class LikeDataRepositoryImpl @Inject constructor(private val dataSource: LikeDataSource) :
    LikeDataRepository {

    override suspend fun addBoardLike(boardLikeAddForm: BoardLikeAddForm): LikeEntity {
        return dataSource.insertBoardLike(
            BoardLikeInsertRequest(
                boardAuthorEmail = boardLikeAddForm.boardAuthorEmail,
                boardCreateTime = boardLikeAddForm.boardCreateTime,
                userEmail = UserSingleton.userEntity.email,
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
                userEmail = UserSingleton.userEntity.email,
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
                boardAuthorEmail = commentRelatedLikesDeleteForm.boardAuthorEmail,
                boardCreateTime = commentRelatedLikesDeleteForm.boardCreateTime

            )
        ).toEntity()
    }

}