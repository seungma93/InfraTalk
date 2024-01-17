package com.seungma.infratalk.data.model.response.board

import com.seungma.infratalk.domain.BookmarkEntity
import com.seungma.infratalk.domain.LikeCountEntity
import com.seungma.infratalk.domain.LikeEntity

data class BoardResponse(
    val boardMetaResponse: BoardMetaResponse? = null,
    val bookmarkEntity: BookmarkEntity? = null,
    val likeEntity: LikeEntity? = null,
    val likeCountEntity: LikeCountEntity? = null
)