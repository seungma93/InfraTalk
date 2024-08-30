package com.seungma.infratalk.data.mapper

import com.seungma.infratalk.data.model.response.board.BoardLikesDeleteResponse
import com.seungma.infratalk.data.model.response.comment.CommentRelatedLikesResponse
import com.seungma.infratalk.data.model.response.like.LikeCountResponse
import com.seungma.infratalk.data.model.response.like.LikeResponse
import com.seungma.infratalk.domain.board.entity.BoardLikesDeleteEntity
import com.seungma.infratalk.domain.board.entity.CommentRelatedLikesEntity
import com.seungma.infratalk.domain.board.entity.LikeCountEntity
import com.seungma.infratalk.domain.board.entity.LikeEntity

fun LikeResponse.toEntity(): LikeEntity {
    return LikeEntity(
        isLike = isLike ?: false
    )
}

fun LikeCountResponse.toEntity(): LikeCountEntity {
    return LikeCountEntity(
        likeCount = likeCount ?: 0
    )
}

fun CommentRelatedLikesResponse.toEntity(): CommentRelatedLikesEntity {
    return CommentRelatedLikesEntity(
        isLikes = isLikes ?: false
    )

}

fun BoardLikesDeleteResponse.toEntity(): BoardLikesDeleteEntity {
    return BoardLikesDeleteEntity(
        isBoardLikes = isBoardLikes ?: false
    )
}
/*
fun LikeListResponse.toEntity(): LikeListEntity {
    return LikeListEntity(
        likeList = likeList?.let { list ->
            list.map { it.toEntity() }
        } ?: emptyList()
    )
}

 */