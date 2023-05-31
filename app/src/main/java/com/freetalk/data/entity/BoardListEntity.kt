package com.freetalk.data.entity

import android.net.Uri
import com.freetalk.data.remote.BoardListResponse
import com.freetalk.data.remote.BoardResponse
import com.freetalk.data.remote.BookMarkableBoardResponse
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

data class BoardListEntity (
    val boardList: List<BookMarkableBoardEntity> = emptyList()
)

fun BoardListResponse.toEntity(): BoardListEntity {
    return BoardListEntity(
        boardList = boardList?.let { list ->
            list.map { it.toEntity() }
        } ?: emptyList()


    )
}

data class BookMarkableBoardEntity(
    val boardEntity: BoardEntity = BoardEntity(
        UserEntity("", "", Uri.parse(""), emptyList()),
        "",
        "",
        null,
        Date(),
        null
    ),
    val bookMarkToken: Boolean = false
)

fun BookMarkableBoardResponse.toEntity(): BookMarkableBoardEntity {
    return BookMarkableBoardEntity(
        boardEntity = boardResponse.toEntity(),
        bookMarkToken = bookMarkToken ?: false
    )
}