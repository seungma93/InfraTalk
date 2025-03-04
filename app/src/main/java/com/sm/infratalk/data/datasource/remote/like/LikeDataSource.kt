package com.sm.infratalk.data.datasource.remote.like

import com.sm.infratalk.data.model.request.board.BoardLikeCountSelectRequest
import com.sm.infratalk.data.model.request.board.BoardLikeDeleteRequest
import com.sm.infratalk.data.model.request.board.BoardLikeInsertRequest
import com.sm.infratalk.data.model.request.board.BoardLikeSelectRequest
import com.sm.infratalk.data.model.request.board.BoardLikesDeleteRequest
import com.sm.infratalk.data.model.request.comment.CommentLikeCountSelectRequest
import com.sm.infratalk.data.model.request.comment.CommentLikeDeleteRequest
import com.sm.infratalk.data.model.request.comment.CommentLikeInsertRequest
import com.sm.infratalk.data.model.request.comment.CommentLikeSelectRequest
import com.sm.infratalk.data.model.request.comment.CommentRelatedLikesDeleteRequest
import com.sm.infratalk.data.model.response.board.BoardLikesDeleteResponse
import com.sm.infratalk.data.model.response.comment.CommentRelatedLikesResponse
import com.sm.infratalk.data.model.response.like.LikeCountResponse
import com.sm.infratalk.data.model.response.like.LikeResponse

interface LikeDataSource {
    suspend fun insertBoardLike(boardLikeInsertRequest: BoardLikeInsertRequest): LikeResponse
    suspend fun deleteBoardLike(boardLikeDeleteRequest: BoardLikeDeleteRequest): LikeResponse
    suspend fun selectBoardLike(boardLikeSelectRequest: BoardLikeSelectRequest): LikeResponse
    suspend fun selectBoardLikeCount(boardLikeCountSelectRequest: BoardLikeCountSelectRequest): LikeCountResponse

    suspend fun insertCommentLike(commentLikeInsertRequest: CommentLikeInsertRequest): LikeResponse
    suspend fun deleteCommentLike(commentLikeDeleteRequest: CommentLikeDeleteRequest): LikeResponse
    suspend fun selectCommentLike(commentLikeSelectRequest: CommentLikeSelectRequest): LikeResponse
    suspend fun selectCommentLikeCount(commentLikeCountSelectRequest: CommentLikeCountSelectRequest): LikeCountResponse
    suspend fun deleteCommentRelatedLikes(commentRelatedLikesDeleteRequest: CommentRelatedLikesDeleteRequest): CommentRelatedLikesResponse
    suspend fun deleteBoardLikes(boardLikesDeleteRequest: BoardLikesDeleteRequest): BoardLikesDeleteResponse
}