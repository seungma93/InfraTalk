package com.sm.infratalk.data.mapper

import com.sm.infratalk.data.model.response.comment.CommentDeleteResponse
import com.sm.infratalk.data.model.response.comment.CommentMetaListResponse
import com.sm.infratalk.data.model.response.comment.CommentMetaResponse
import com.sm.infratalk.domain.comment.entity.CommentDeleteEntity
import com.sm.infratalk.domain.comment.entity.CommentMetaEntity
import com.sm.infratalk.domain.comment.entity.CommentMetaListEntity
import com.sm.infratalk.domain.user.entity.UserEntity
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