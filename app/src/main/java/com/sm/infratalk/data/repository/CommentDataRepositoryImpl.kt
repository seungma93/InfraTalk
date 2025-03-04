package com.sm.infratalk.data.repository

import com.sm.infratalk.data.datasource.remote.comment.CommentDataSource
import com.sm.infratalk.data.mapper.toEntity
import com.sm.infratalk.data.model.request.board.BoardRelatedAllCommentMetaListSelectRequest
import com.sm.infratalk.data.model.request.comment.CommentDeleteRequest
import com.sm.infratalk.data.model.request.comment.CommentMetaListSelectRequest
import com.sm.infratalk.data.model.request.comment.MyCommentListLoadRequest
import com.sm.infratalk.domain.comment.entity.CommentDeleteEntity
import com.sm.infratalk.domain.comment.entity.CommentMetaEntity
import com.sm.infratalk.domain.comment.entity.CommentMetaListEntity
import com.sm.infratalk.domain.comment.repository.CommentDataRepository
import com.sm.infratalk.domain.user.repository.UserDataRepository
import com.sm.infratalk.presenter.board.form.BoardRelatedAllCommentMetaListSelectForm
import com.sm.infratalk.presenter.board.form.CommentDeleteForm
import com.sm.infratalk.presenter.board.form.CommentInsertForm
import com.sm.infratalk.presenter.board.form.CommentInsertRequest
import com.sm.infratalk.presenter.board.form.CommentMetaListLoadForm
import com.sm.infratalk.presenter.mypage.form.MyCommentListLoadForm
import java.util.Date
import javax.inject.Inject


class CommentDataRepositoryImpl @Inject constructor(
    private val commentDataSource: CommentDataSource,
    private val userDataRepository: UserDataRepository
) : CommentDataRepository {
    override suspend fun insertComment(commentInsertForm: CommentInsertForm): CommentMetaEntity =
        with(commentInsertForm) {
            return commentDataSource.insertComment(
                CommentInsertRequest(
                    authorEmail = userDataRepository.getUserMe().email,
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