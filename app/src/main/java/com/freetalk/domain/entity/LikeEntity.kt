package com.freetalk.domain.entity

data class LikeEntity(
    val isLike: Boolean = false
)

data class LikeCountEntity(
    val likeCount: Int = 0
)


data class CommentRelatedLikesEntity(
    val isLikes: Boolean = false
)

data class BoardLikesDeleteEntity(
    val isBoardLikes: Boolean = false
)