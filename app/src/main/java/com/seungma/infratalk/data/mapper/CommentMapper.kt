package com.seungma.infratalk.data.mapper

import com.seungma.infratalk.data.model.response.comment.CommentDeleteResponse
import com.seungma.infratalk.data.model.response.comment.CommentMetaListResponse
import com.seungma.infratalk.data.model.response.comment.CommentMetaResponse
import com.seungma.infratalk.domain.comment.entity.CommentDeleteEntity
import com.seungma.infratalk.domain.comment.entity.CommentMetaEntity
import com.seungma.infratalk.domain.comment.entity.CommentMetaListEntity
import com.seungma.infratalk.domain.user.UserEntity
import toEntity
import java.util.Date


fun CommentMetaResponse.toEntity(): CommentMetaEntity {
    return CommentMetaEntity(
        author = author?.toEntity() ?: UserEntity("", "", null),
        createTime = createTime ?: Date(),
        content = content.orEmpty(),
        boardAuthorEmail = boardAuthorEmail.orEmpty(),
        boardCreateTime = boardCreateTime ?: Date(),
        editTime = editTime ?: Date(),
        isLastPage = isLastPage ?: false
    )
}

fun CommentMetaListResponse.toEntity(): CommentMetaListEntity {
    return CommentMetaListEntity(
        commentMetaList = commentMetaList?.let { list ->
            list.map { it.toEntity() }
        } ?: emptyList()
    )
}

fun CommentDeleteResponse.toEntity(): CommentDeleteEntity {
    return CommentDeleteEntity(
        commentAuthorEmail = commentAuthorEmail.orEmpty(),
        commentCreateTime = commentCreateTime ?: Date(),
        isSuccess = isSuccess ?: false
    )
}

/*
fun WrapperCommentResponse.toEntity(): WrapperCommentEntity {
    return WrapperCommentEntity(
        commentEntity = commentResponse?.toEntity() ?: CommentEntity(),
        bookMarkEntity = BookMarkEntity(),
        likeEntity = LikeEntity(),
        likeCount = likeCount ?: 0
    )
}

 */