package com.freetalk.data.mapper

import com.freetalk.data.model.response.BoardBookmarksDeleteResponse
import com.freetalk.data.model.response.BookmarkResponse
import com.freetalk.data.model.response.CommentRelatedBookmarksResponse
import com.freetalk.domain.entity.BoardBookmarksDeleteEntity
import com.freetalk.domain.entity.BookmarkEntity
import com.freetalk.domain.entity.CommentRelatedBookmarksEntity

fun BookmarkResponse.toEntity(): BookmarkEntity {
    return BookmarkEntity(
        isBookmark = isBookmark ?: false
    )
}

fun CommentRelatedBookmarksResponse.toEntity(): CommentRelatedBookmarksEntity {
    return CommentRelatedBookmarksEntity(
        isBookmarks = isBookmarks ?: false
    )
}

fun BoardBookmarksDeleteResponse.toEntity(): BoardBookmarksDeleteEntity {
    return BoardBookmarksDeleteEntity(
        isBoardBookmarks = isBoardBookmarks ?: false
    )
}

/*
fun BookMarkListResponse.toEntity(): BookMarkListEntity {
    return BookMarkListEntity(
        bookMarkList = bookMarkList?.let { list ->
            list.map { it.toEntity() }
        } ?: emptyList()
    )
}

 */