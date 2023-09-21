package com.freetalk.domain.entity

import android.net.Uri
import java.io.Serializable
import java.util.*

data class CommentMetaEntity(
    val author: UserEntity = UserEntity("", "", Uri.parse("")),
    val createTime: Date = Date(),
    val content: String = "",
    val images: ImagesResultEntity? = null,
    val boardAuthorEmail: String = "",
    val boardCreateTime: Date = Date(),
    val editTime: Date? = null
) : Serializable

data class CommentListEntity (
    val commentList: List<CommentEntity> = emptyList()
)

data class CommentMetaListEntity (
    val commentMetaList: List<CommentMetaEntity> = emptyList()
)



data class CommentEntity(
    val commentMetaEntity: CommentMetaEntity = CommentMetaEntity(
        boardAuthorEmail = "",
        boardCreateTime = Date(),
        author = UserEntity("", "", Uri.parse("")),
        content = "",
        createTime = Date(),
        editTime = null,
    ),
    val bookmarkEntity: BookmarkEntity,
    val likeEntity: LikeEntity,
    val likeCountEntity: LikeCountEntity
): Serializable



