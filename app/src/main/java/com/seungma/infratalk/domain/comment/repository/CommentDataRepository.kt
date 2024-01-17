package com.seungma.infratalk.domain.comment.repository

import com.seungma.infratalk.domain.comment.entity.CommentDeleteEntity
import com.seungma.infratalk.domain.comment.entity.CommentMetaEntity
import com.seungma.infratalk.domain.comment.entity.CommentMetaListEntity
import com.seungma.infratalk.presenter.board.form.BoardRelatedAllCommentMetaListSelectForm
import com.seungma.infratalk.presenter.board.form.CommentDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentInsertForm
import com.seungma.infratalk.presenter.board.form.CommentMetaListLoadForm
import com.seungma.infratalk.presenter.mypage.form.MyCommentListLoadForm

interface CommentDataRepository {
    suspend fun insertComment(commentInsertForm: CommentInsertForm): CommentMetaEntity
    suspend fun loadCommentMetaList(commentMetaListLoadForm: CommentMetaListLoadForm): CommentMetaListEntity

    suspend fun loadBoardRelatedAllCommentMetaList(boardRelatedAllCommentMetaListSelectForm: BoardRelatedAllCommentMetaListSelectForm): CommentMetaListEntity

    suspend fun deleteComment(commentDeleteForm: CommentDeleteForm): CommentDeleteEntity
    suspend fun loadMyCommentList(myCommentListLoadForm: MyCommentListLoadForm): CommentMetaListEntity
    suspend fun loadMyBookmarkCommentList(): CommentMetaListEntity
    suspend fun loadMyLikeCommentList(): CommentMetaListEntity

}