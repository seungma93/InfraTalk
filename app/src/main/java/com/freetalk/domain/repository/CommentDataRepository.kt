package com.freetalk.domain.repository

import com.freetalk.data.model.request.CommentDeleteRequest
import com.freetalk.domain.entity.CommentDeleteEntity
import com.freetalk.domain.entity.CommentMetaEntity
import com.freetalk.domain.entity.CommentMetaListEntity
import com.freetalk.presenter.form.BoardRelatedAllCommentMetaListSelectForm
import com.freetalk.presenter.form.CommentDeleteForm
import com.freetalk.presenter.form.CommentInsertForm
import com.freetalk.presenter.form.CommentMetaListLoadForm

interface CommentDataRepository {
    suspend fun insertComment(commentInsertForm: CommentInsertForm): CommentMetaEntity
    suspend fun loadCommentMetaList(commentMetaListLoadForm: CommentMetaListLoadForm): CommentMetaListEntity

    suspend fun loadBoardRelatedAllCommentMetaList(boardRelatedAllCommentMetaListSelectForm: BoardRelatedAllCommentMetaListSelectForm): CommentMetaListEntity

    suspend fun deleteComment(commentDeleteForm: CommentDeleteForm): CommentDeleteEntity

    //suspend fun update(boardUpdateForm: BoardUpdateForm): BoardEntity
    //suspend fun selectCommentContent(commentContentSelectForm: CommentContentSelectForm): CommentEntity

}