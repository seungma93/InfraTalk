package com.freetalk.data.repository

import com.freetalk.data.UserSingleton
import com.freetalk.data.datasource.remote.CommentDataSource
import com.freetalk.data.mapper.toEntity
import com.freetalk.data.model.request.BoardRelatedAllCommentMetaListSelectRequest
import com.freetalk.data.model.request.CommentDeleteRequest
import com.freetalk.data.model.request.CommentMetaListSelectRequest
import com.freetalk.data.model.request.MyCommentListLoadRequest
import com.freetalk.domain.entity.CommentDeleteEntity
import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.entity.CommentMetaEntity
import com.freetalk.domain.entity.CommentMetaListEntity
import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.repository.CommentDataRepository
import com.freetalk.presenter.form.BoardRelatedAllCommentMetaListSelectForm
import com.freetalk.presenter.form.CommentDeleteForm
import com.freetalk.presenter.form.CommentInsertForm
import com.freetalk.presenter.form.CommentInsertRequest
import com.freetalk.presenter.form.CommentMetaListLoadForm
import com.freetalk.presenter.form.MyCommentListLoadForm
import java.util.Date
import javax.inject.Inject


class CommentDataRepositoryImpl @Inject constructor(
    private val commentDataSource: CommentDataSource
) : CommentDataRepository {
    override suspend fun insertComment(commentInsertForm: CommentInsertForm): CommentMetaEntity =
        with(commentInsertForm) {

            return commentDataSource.insertComment(
                CommentInsertRequest(
                    authorEmail = UserSingleton.userEntity.email,
                    createTime = Date(),
                    content = content,
                    boardAuthorEmail = boardAuthorEmail,
                    boardCreateTime = boardCreateTime,
                    editTime = Date()
                )
            ).toEntity()
        }

    override suspend fun loadCommentMetaList(commentMetaListLoadForm: CommentMetaListLoadForm): CommentMetaListEntity {
        return commentDataSource.selectCommentMetaList(
            commentMetaListSelectRequest = CommentMetaListSelectRequest(
                boardAuthorEmail = commentMetaListLoadForm.boardAuthorEmail,
                boardCreateTime = commentMetaListLoadForm.boardCreateTime,
                reload = commentMetaListLoadForm.reload
            )
        ).toEntity()
    }

    override suspend fun loadBoardRelatedAllCommentMetaList(
        boardRelatedAllCommentMetaListSelectForm: BoardRelatedAllCommentMetaListSelectForm
    ): CommentMetaListEntity =
        with(boardRelatedAllCommentMetaListSelectForm) {
            return commentDataSource.selectRelatedAllCommentMetaList(
                BoardRelatedAllCommentMetaListSelectRequest(
                    boardAuthorEmail = boardAuthorEmail,
                    boardCreateTime = boardCreateTime
                )
            ).toEntity()
        }

    override suspend fun deleteComment(commentDeleteForm: CommentDeleteForm): CommentDeleteEntity {
        return commentDataSource.deleteComment(
            commentDeleteRequest = CommentDeleteRequest(
                commentAuthorEmail = commentDeleteForm.commentAuthorEmail,
                commentCreateTime = commentDeleteForm.commentCreateTime
            )
        ).toEntity()
    }

    override suspend fun loadMyCommentList(myCommentListLoadForm: MyCommentListLoadForm): CommentMetaListEntity {
        return commentDataSource.loadMyCommentList(
            myCommentListLoadRequest = MyCommentListLoadRequest(
                reload = myCommentListLoadForm.reload
            )
        ).toEntity()
    }

    override suspend fun loadMyBookmarkCommentList(): CommentMetaListEntity {
        return commentDataSource.loadMyBookmarkCommentList().toEntity()
    }

    override suspend fun loadMyLikeCommentList(): CommentMetaListEntity {
        return commentDataSource.loadMyLikeCommentList().toEntity()
    }

}