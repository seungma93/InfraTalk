package com.seungma.infratalk.data.mapper

import android.net.Uri
import com.seungma.infratalk.data.model.response.board.BoardDeleteResponse
import com.seungma.infratalk.data.model.response.board.BoardInsertResponse
import com.seungma.infratalk.data.model.response.board.BoardMetaListResponse
import com.seungma.infratalk.data.model.response.board.BoardMetaResponse
import com.seungma.infratalk.domain.board.entity.BoardDeleteEntity
import com.seungma.infratalk.domain.board.entity.BoardInsertEntity
import com.seungma.infratalk.domain.board.entity.BoardMetaEntity
import com.seungma.infratalk.domain.board.entity.BoardMetaListEntity
import com.seungma.infratalk.domain.image.ImagesResultEntity
import com.seungma.infratalk.domain.user.UserEntity
import java.util.Date

fun BoardMetaResponse.toEntity(): BoardMetaEntity {
    return BoardMetaEntity(
        author = author ?: UserEntity("", "", Uri.parse("")),
        title = title.orEmpty(),
        content = content.orEmpty(),
        images = images ?: ImagesResultEntity(listOf(Uri.parse("")), listOf(Uri.parse(""))),
        createTime = createTime ?: Date(),
        editTime = editTime ?: Date()
    )
}

fun BoardMetaListResponse.toEntity(): BoardMetaListEntity {
    return BoardMetaListEntity(
        boardMetaList = boardMetaList?.let { list ->
            list.map { it.toEntity() }
        } ?: emptyList()
    )
}
/*
fun BoardResponse.toEntity(): BoardEntity {
    return BoardEntity(
        boardMetaEntity = boardMetaResponse?.toEntity() ?: BoardMetaEntity(),
        bookmarkEntity = bookmarkEntity ?: BookmarkEntity(),
        likeEntity = likeEntity ?: LikeEntity(),
        likeCountEntity = likeCountEntity ?: LikeCountEntity()
    )
}

 */

fun BoardInsertResponse.toEntity(): BoardInsertEntity {
    return BoardInsertEntity(
        boardAuthorEmail = boardAuthorEmail.orEmpty(),
        boardCreteTime = boardCreteTime ?: Date(),
        isSuccess = isSuccess ?: false
    )
}

fun BoardDeleteResponse.toEntity(): BoardDeleteEntity {
    return BoardDeleteEntity(
        boardAuthorEmail = boardAuthorEmail.orEmpty(),
        boardCreateTime = boardCreateTime ?: Date(),
        isSuccess = isSuccess ?: false
    )
}