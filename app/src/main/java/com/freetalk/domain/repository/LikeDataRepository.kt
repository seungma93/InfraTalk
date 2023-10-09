package com.freetalk.domain.repository

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

interface LikeDataRepository {
    suspend fun addBoardLike(boardLikeAddForm: BoardLikeAddForm): LikeEntity
    suspend fun deleteBoardLike(boardLikeDeleteForm: BoardLikeDeleteForm): LikeEntity
    suspend fun loadBoardLike(boardLikeLoadForm: BoardLikeLoadForm): LikeEntity
    suspend fun loadBoardLikeCount(boardLikeCountLoadForm: BoardLikeCountLoadForm): LikeCountEntity

    suspend fun addCommentLike(commentLikeAddForm: CommentLikeAddForm): LikeEntity
    suspend fun deleteCommentLike(commentLikeDeleteForm: CommentLikeDeleteForm): LikeEntity
    suspend fun loadCommentLike(commentLikeLoadForm: CommentLikeLoadForm): LikeEntity
    suspend fun loadCommentLikeCount(commentLikeCountLoadForm: CommentLikeCountLoadForm): LikeCountEntity
    suspend fun deleteCommentRelatedLikes(
        commentRelatedLikesDeleteForm: CommentRelatedLikesDeleteForm
    ): CommentRelatedLikesEntity
}
