package com.freetalk.data.entity

import com.freetalk.data.remote.BookMarkResponse
import com.freetalk.data.remote.LikeCountResponse
import com.freetalk.data.remote.LikeResponse
import java.util.*

data class BookMarkEntity(
    val boardAuthorEmail: String = "",
    val boardCreateTime: Date = Date(),
    val userEmail: String = "",
    val updateTime: Date = Date()
)

fun BookMarkResponse.toEntity(): BookMarkEntity {
    return BookMarkEntity(
        boardAuthorEmail = boardAuthorEmail.orEmpty(),
        boardCreateTime = boardCreateTime ?: Date(),
        userEmail = userEmail.orEmpty(),
        updateTime = updateTime ?: Date()
    )
}
