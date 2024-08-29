package com.seungma.infratalk.data.model.response.board

import com.seungma.infratalk.domain.board.entity.BookmarkEntity
import com.seungma.infratalk.domain.board.entity.LikeCountEntity
import com.seungma.infratalk.domain.board.entity.LikeEntity

data class BoardResponse(
    val boardMetaResponse: BoardMetaResponse? = null,
    val bookmarkEntity: BookmarkEntity? = null,
    val likeEntity: LikeEntity? = null,
    val likeCountEntity: LikeCountEntity? = null
)