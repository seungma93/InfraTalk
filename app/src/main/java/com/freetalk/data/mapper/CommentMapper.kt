package com.freetalk.data.mapper

import android.net.Uri
import com.freetalk.data.model.response.CommentDeleteResponse
import com.freetalk.data.model.response.CommentMetaListResponse
import com.freetalk.data.model.response.CommentMetaResponse
import com.freetalk.domain.entity.CommentDeleteEntity
import com.freetalk.domain.entity.CommentMetaEntity
import com.freetalk.domain.entity.CommentMetaListEntity
import com.freetalk.domain.entity.UserEntity
import toEntity
import java.util.Date


fun CommentMetaResponse.toEntity(): CommentMetaEntity {
    return CommentMetaEntity(
        author = author?.toEntity() ?: UserEntity("","", null),
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