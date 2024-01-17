package com.seungma.infratalk.data.mapper

import com.seungma.infratalk.data.model.response.board.BoardBookmarksDeleteResponse
import com.seungma.infratalk.data.model.response.bookmark.BookmarkResponse
import com.seungma.infratalk.data.model.response.comment.CommentRelatedBookmarksResponse
import com.seungma.infratalk.domain.BoardBookmarksDeleteEntity
import com.seungma.infratalk.domain.BookmarkEntity
import com.seungma.infratalk.domain.CommentRelatedBookmarksEntity

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