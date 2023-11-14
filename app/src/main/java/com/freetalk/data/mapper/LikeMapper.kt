package com.freetalk.data.mapper

import com.freetalk.data.model.response.BoardLikesDeleteResponse
import com.freetalk.data.model.response.CommentRelatedLikesResponse
import com.freetalk.data.model.response.LikeCountResponse
import com.freetalk.data.model.response.LikeResponse
import com.freetalk.domain.entity.BoardLikesDeleteEntity
import com.freetalk.domain.entity.CommentRelatedLikesEntity
import com.freetalk.domain.entity.LikeCountEntity
import com.freetalk.domain.entity.LikeEntity

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