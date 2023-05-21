package com.freetalk.data.entity

import android.net.Uri
import com.freetalk.data.remote.BoardListResponse
import com.freetalk.data.remote.BoardResponse
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

data class BoardListEntity (
    val boardList: List<BoardEntity> = emptyList()
)

fun BoardListResponse.toEntity(): BoardListEntity {
    return BoardListEntity(
        boardList = boardList?.let { list ->
            list.map { it.toEntity() }
        } ?: emptyList()

    )
}