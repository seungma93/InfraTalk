package com.freetalk.domain.entity

import android.net.Uri
import java.io.Serializable
import java.util.Date

data class BoardMetaEntity(
    val author: UserEntity = UserEntity("", "", Uri.parse("")),
    val title: String = "",
    val content: String = "",
    val images: ImagesResultEntity? = null,
    val createTime: Date = Date(),
    val editTime: Date? = null
) : Serializable

data class BoardMetaListEntity (
    val boardMetaList: List<BoardMetaEntity> = emptyList()
)

data class BoardListEntity(
    val boardList: List<BoardEntity> = emptyList()
)

data class BoardEntity(
    val boardMetaEntity: BoardMetaEntity = BoardMetaEntity(
        UserEntity("", "", Uri.parse("")),
        "",
        "",
        null,
        Date(),
        null
    ),
    val bookmarkEntity: BookmarkEntity,
    val likeEntity: LikeEntity,
    val likeCountEntity: LikeCountEntity
): Serializable

data class BoardContentPrimaryKeyEntity(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
): Serializable

data class BoardInsertEntity(
    val boardAuthorEmail: String,
    val boardCreteTime: Date,
    val isSuccess: Boolean
)

data class BoardWriteEntity(
    val isSuccess: Boolean
)



