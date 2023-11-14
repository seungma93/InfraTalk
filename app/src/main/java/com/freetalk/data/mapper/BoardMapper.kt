package com.freetalk.data.mapper

import android.net.Uri
import com.freetalk.data.model.response.BoardDeleteResponse
import com.freetalk.data.model.response.BoardInsertResponse
import com.freetalk.data.model.response.BoardMetaListResponse
import com.freetalk.data.model.response.BoardMetaResponse
import com.freetalk.data.model.response.BoardResponse
import com.freetalk.domain.entity.BoardDeleteEntity
import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.BoardInsertEntity
import com.freetalk.domain.entity.BoardMetaEntity
import com.freetalk.domain.entity.BoardMetaListEntity
import com.freetalk.domain.entity.BookmarkEntity
import com.freetalk.domain.entity.ImagesResultEntity
import com.freetalk.domain.entity.LikeCountEntity
import com.freetalk.domain.entity.LikeEntity
import com.freetalk.domain.entity.UserEntity
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