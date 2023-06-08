package com.freetalk.data.entity

import com.freetalk.data.remote.LikeCountResponse
import com.freetalk.data.remote.LikeResponse
import java.util.*

data class LikeEntity(
    val boardAuthorEmail: String = "",
    val boardCreateTime: Date = Date(),
    val userEmail: String = "",
    val updateTime: Date = Date()
)

fun LikeResponse.toEntity(): LikeEntity {
    return LikeEntity(
        boardAuthorEmail = boardAuthorEmail.orEmpty(),
        boardCreateTime = boardCreateTime ?: Date(),
        userEmail = userEmail.orEmpty(),
        updateTime = updateTime ?: Date()
    )
}

data class LikeCountEntity(
    val boardAuthorEmail: String = "",
    val boardCreateTime: Date = Date(),
    val likeCount: Int = 0
)

fun LikeCountResponse.toEntity(): LikeCountEntity {
    return LikeCountEntity(
        boardAuthorEmail = boardAuthorEmail.orEmpty(),
        boardCreateTime = boardCreateTime ?: Date(),
        likeCount = likeCount ?: 0
    )
}