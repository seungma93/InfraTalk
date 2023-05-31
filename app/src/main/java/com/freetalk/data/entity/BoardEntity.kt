package com.freetalk.data.entity

import android.net.Uri
import com.freetalk.data.remote.BoardResponse
import java.io.Serializable
import java.util.*

data class BoardEntity(
    val author: UserEntity = UserEntity("", "", Uri.parse(""), emptyList()),
    val title: String = "",
    val content: String = "",
    val images: ImagesResultEntity? = null,
    val createTime: Date = Date(),
    val editTime: Date? = null
) : Serializable

fun BoardResponse.toEntity(): BoardEntity {
    return BoardEntity(
        author = author ?: UserEntity("", "", Uri.parse(""), emptyList()),
        title = title.orEmpty(),
        content = content.orEmpty(),
        images = images ?: ImagesResultEntity(listOf(Uri.parse("")), listOf(Uri.parse(""))),
        createTime = createTime ?: Date(),
        editTime = editTime ?: Date()
    )
}


