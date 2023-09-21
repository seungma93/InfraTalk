package com.freetalk.data.model.response

import com.freetalk.domain.entity.BookmarkEntity
import com.freetalk.domain.entity.LikeCountEntity
import com.freetalk.domain.entity.LikeEntity

data class BoardResponse(
    val boardMetaResponse: BoardMetaResponse? = null,
    val bookmarkEntity: BookmarkEntity? = null,
    val likeEntity: LikeEntity? = null,
    val likeCountEntity: LikeCountEntity? = null
)