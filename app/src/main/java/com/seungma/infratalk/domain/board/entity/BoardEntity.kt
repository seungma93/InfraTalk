package com.seungma.infratalk.domain.board.entity

import android.net.Uri
import com.seungma.infratalk.domain.image.entity.ImagesResultEntity
import com.seungma.infratalk.domain.user.entity.UserEntity
import java.io.Serializable
import java.util.Date

data class BoardMetaEntity(
    val author: UserEntity = UserEntity("", "", Uri.parse("")),
    val title: String = "",
    val content: String = "",
    val images: ImagesResultEntity? = null,
    val createTime: Date = Date(),
    val editTime: Date? = null
) : Serializable {
    val boardPrimaryKey: String get() = author.email + createTime
}

data class BoardMetaListEntity(
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
) : Serializable {
    val createTime get() = boardMetaEntity.createTime
}

data class BoardContentPrimaryKeyEntity(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
) : Serializable

data class BoardInsertEntity(
    val boardAuthorEmail: String,
    val boardCreteTime: Date,
    val isSuccess: Boolean
)

data class BoardWriteEntity(
    val isSuccess: Boolean
)

data class BoardDeleteEntity(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val isSuccess: Boolean
)



