package com.freetalk.data.entity

import android.net.Uri
import com.freetalk.data.remote.BoardListResponse
import com.freetalk.data.remote.BoardResponse
import com.freetalk.data.remote.WrapperBoardResponse
import java.io.Serializable
import java.util.*

data class BoardEntity(
    val author: UserEntity = UserEntity("", "", Uri.parse("")),
    val title: String = "",
    val content: String = "",
    val images: ImagesResultEntity? = null,
    val createTime: Date = Date(),
    val editTime: Date? = null
) : Serializable

fun BoardResponse.toEntity(): BoardEntity {
    return BoardEntity(
        author = author ?: UserEntity("", "", Uri.parse("")),
        title = title.orEmpty(),
        content = content.orEmpty(),
        images = images ?: ImagesResultEntity(listOf(Uri.parse("")), listOf(Uri.parse(""))),
        createTime = createTime ?: Date(),
        editTime = editTime ?: Date()
    )
}

data class BoardListEntity (
    val boardList: List<WrapperBoardEntity> = emptyList()
)

fun BoardListResponse.toEntity(): BoardListEntity {
    return BoardListEntity(
        boardList = boardList?.let { list ->
            list.map { it.toEntity() }
        } ?: emptyList()
    )
}

data class WrapperBoardEntity(
    val boardEntity: BoardEntity = BoardEntity(
        UserEntity("", "", Uri.parse("")),
        "",
        "",
        null,
        Date(),
        null
    ),
    val isBookMark: Boolean = false,
    val isLike: Boolean = false,
    val likeCount: Int = 0
): Serializable

fun WrapperBoardResponse.toEntity(): WrapperBoardEntity {
    return WrapperBoardEntity(
        boardEntity = boardResponse?.toEntity() ?: BoardEntity(),
        isBookMark = isBookMark ?: false,
        isLike = isLike ?: false,
        likeCount = likeCount ?: 0
    )
}


