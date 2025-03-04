package com.sm.infratalk.data.datasource.remote.comment

import com.sm.infratalk.data.model.request.board.BoardRelatedAllCommentMetaListSelectRequest
import com.sm.infratalk.data.model.request.comment.CommentDeleteRequest
import com.sm.infratalk.data.model.request.comment.CommentMetaListSelectRequest
import com.sm.infratalk.data.model.request.comment.MyCommentListLoadRequest
import com.sm.infratalk.data.model.response.comment.CommentDeleteResponse
import com.sm.infratalk.data.model.response.comment.CommentMetaListResponse
import com.sm.infratalk.data.model.response.comment.CommentMetaResponse
import com.sm.infratalk.presenter.board.form.CommentInsertRequest

interface CommentDataSource {
    suspend fun insertComment(commentInsertRequest: CommentInsertRequest): CommentMetaResponse
    suspend fun selectCommentMetaList(commentMetaListSelectRequest: CommentMetaListSelectRequest): CommentMetaListResponse
    suspend fun selectRelatedAllCommentMetaList(
        boardRelatedAllCommentMetaListSelectRequest: BoardRelatedAllCommentMetaListSelectRequest
    ): CommentMetaListResponse

    suspend fun deleteComment(commentDeleteRequest: CommentDeleteRequest): CommentDeleteResponse
    suspend fun loadMyCommentList(myCommentListLoadRequest: MyCommentListLoadRequest): CommentMetaListResponse
    suspend fun loadMyBookmarkCommentList(): CommentMetaListResponse
    suspend fun loadMyLikeCommentList(): CommentMetaListResponse
}