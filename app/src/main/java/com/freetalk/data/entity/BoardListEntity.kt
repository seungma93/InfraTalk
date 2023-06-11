package com.freetalk.data.entity

import android.net.Uri
import com.freetalk.data.remote.BoardListResponse
import com.freetalk.data.remote.WrapperBoardResponse
import java.io.Serializable
import java.util.*

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
        UserEntity("", "", Uri.parse(""), emptyList(), emptyList()),
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
        boardEntity = boardResponse.toEntity(),
        isBookMark = isBookMark ?: false,
        isLike = isLike ?: false,
        likeCount = likeCount ?: 0
    )
}