package com.seungma.infratalk.domain.comment.entity

import android.net.Uri
import com.seungma.infratalk.domain.BookmarkEntity
import com.seungma.infratalk.domain.LikeCountEntity
import com.seungma.infratalk.domain.LikeEntity
import com.seungma.infratalk.domain.image.ImagesResultEntity
import com.seungma.infratalk.domain.user.UserEntity
import java.io.Serializable
import java.util.Date

data class CommentMetaEntity(
    val author: UserEntity = UserEntity("", "", Uri.parse("")),
    val createTime: Date = Date(),
    val content: String = "",
    val images: ImagesResultEntity? = null,
    val boardAuthorEmail: String = "",
    val boardCreateTime: Date = Date(),
    val editTime: Date? = null,
    val isLastPage: Boolean = false
) : Serializable {
    val commentPrimaryKey: String get() = author.email + createTime
}

data class CommentListEntity(
    val commentList: List<CommentEntity> = emptyList()
)

data class CommentMetaListEntity(
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
) : Serializable

data class CommentDeleteEntity(
    val commentAuthorEmail: String,
    val commentCreateTime: Date,
    val isSuccess: Boolean
)

interface BookMarkable {
    val bookMarkEntity: BookmarkEntity
}

interface Likeable {
    val likeEntity: LikeEntity
}

data class Feed(
    override val bookMarkEntity: BookmarkEntity,
    override val likeEntity: LikeEntity
) : BookMarkable, Likeable {

}
