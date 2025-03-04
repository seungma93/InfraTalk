package com.sm.infratalk.domain.board.repository

import com.sm.infratalk.domain.board.entity.BoardLikesDeleteEntity
import com.sm.infratalk.domain.board.entity.CommentRelatedLikesEntity
import com.sm.infratalk.domain.board.entity.LikeCountEntity
import com.sm.infratalk.domain.board.entity.LikeEntity
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

    suspend fun deleteBoardLikes(
        boardLikesDeleteForm: BoardLikesDeleteForm
    ): BoardLikesDeleteEntity
}
