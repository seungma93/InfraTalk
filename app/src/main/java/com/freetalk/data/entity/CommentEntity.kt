package com.freetalk.data.entity

import android.net.Uri
import com.freetalk.data.remote.*
import java.io.Serializable
import java.util.*

data class CommentEntity(
    val boardAuthorEmail: String = "",
    val boardCreateTime: Date = Date(),
    val author: UserEntity = UserEntity("", "", Uri.parse("")),
    val content: String = "",
    val createTime: Date = Date(),
    val editTime: Date? = null
) : Serializable

fun CommentResponse.toEntity(): CommentEntity {
    return CommentEntity(
        boardAuthorEmail = boardAuthorEmail.orEmpty(),
        boardCreateTime = boardCreateTime ?: Date(),
        author = author ?: UserEntity("", "", Uri.parse("")),
        content = content.orEmpty(),
        createTime = createTime ?: Date(),
        editTime = editTime ?: Date()
    )
}

data class CommentListEntity (
    val commentList: List<WrapperCommentEntity> = emptyList()
)

fun CommentListResponse.toEntity(): CommentListEntity {
    return CommentListEntity(
        commentList = commentList?.let { list ->
            list.map { it.toEntity() }
        } ?: emptyList()
    )
}

data class WrapperCommentEntity(
    val commentEntity: CommentEntity = CommentEntity(
        boardAuthorEmail = "",
        boardCreateTime = Date(),
        author = UserEntity("", "", Uri.parse("")),
        content = "",
        createTime = Date(),
        editTime = null,
    ),
    val isLike: Boolean = false,
    val likeCount: Int = 0
): Serializable

fun WrapperCommentResponse.toEntity(): WrapperCommentEntity {
    return WrapperCommentEntity(
        commentEntity = commentResponse?.toEntity() ?: CommentEntity(),
        isLike = isLike ?: false,
        likeCount = likeCount ?: 0
    )
}


