package com.seungma.infratalk.data.repository

import com.seungma.infratalk.data.UserSingleton
import com.seungma.infratalk.data.datasource.remote.CommentDataSource
import com.seungma.infratalk.data.mapper.toEntity
import com.seungma.infratalk.data.model.request.board.BoardRelatedAllCommentMetaListSelectRequest
import com.seungma.infratalk.data.model.request.comment.CommentDeleteRequest
import com.seungma.infratalk.data.model.request.comment.CommentMetaListSelectRequest
import com.seungma.infratalk.data.model.request.comment.MyCommentListLoadRequest
import com.seungma.infratalk.domain.comment.entity.CommentDeleteEntity
import com.seungma.infratalk.domain.comment.entity.CommentMetaEntity
import com.seungma.infratalk.domain.comment.entity.CommentMetaListEntity
import com.seungma.infratalk.domain.comment.repository.CommentDataRepository
import com.seungma.infratalk.presenter.board.form.BoardRelatedAllCommentMetaListSelectForm
import com.seungma.infratalk.presenter.board.form.CommentDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentInsertForm
import com.seungma.infratalk.presenter.board.form.CommentInsertRequest
import com.seungma.infratalk.presenter.board.form.CommentMetaListLoadForm
import com.seungma.infratalk.presenter.mypage.form.MyCommentListLoadForm
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