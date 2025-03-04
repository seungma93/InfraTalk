package com.sm.infratalk.domain.comment.repository

import com.sm.infratalk.domain.comment.entity.CommentDeleteEntity
import com.sm.infratalk.domain.comment.entity.CommentMetaEntity
import com.sm.infratalk.domain.comment.entity.CommentMetaListEntity
import com.sm.infratalk.presenter.board.form.BoardRelatedAllCommentMetaListSelectForm
import com.sm.infratalk.presenter.board.form.CommentDeleteForm
import com.sm.infratalk.presenter.board.form.CommentInsertForm
import com.sm.infratalk.presenter.board.form.CommentMetaListLoadForm
import com.sm.infratalk.presenter.mypage.form.MyCommentListLoadForm

interface CommentDataRepository {
    suspend fun insertComment(commentInsertForm: CommentInsertForm): CommentMetaEntity
    suspend fun loadCommentMetaList(commentMetaListLoadForm: CommentMetaListLoadForm): CommentMetaListEntity

    suspend fun loadBoardRelatedAllCommentMetaList(boardRelatedAllCommentMetaListSelectForm: BoardRelatedAllCommentMetaListSelectForm): CommentMetaListEntity

    suspend fun deleteComment(commentDeleteForm: CommentDeleteForm): CommentDeleteEntity
    suspend fun loadMyCommentList(myCommentListLoadForm: MyCommentListLoadForm): CommentMetaListEntity
    suspend fun loadMyBookmarkCommentList(): CommentMetaListEntity
    suspend fun loadMyLikeCommentList(): CommentMetaListEntity

}