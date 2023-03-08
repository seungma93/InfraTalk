package com.freetalk.data.entity

import android.net.Uri
import com.freetalk.data.UserSingleton
import com.freetalk.data.remote.BoardResponse
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date

data class BoardEntity (
    val author: UserEntity = UserEntity("", "", Uri.parse("")),
    val title: String = "",
    val content: String = "",
    val images: ImagesEntity? = null,
    val createTime: Date = Date(),
    val editTime: Date? = null,
    )

fun BoardResponse.toEntity(): BoardEntity {
    return BoardEntity(
        author = author ?: UserEntity("", "", Uri.parse("")),
        title = title.orEmpty(),
        content = content.orEmpty(),
        images = images ?: ImagesEntity(listOf(Uri.parse("")), listOf(Uri.parse(""))),
        createTime = createTime ?: Date(),
        editTime = editTime ?: Date()

    )
}