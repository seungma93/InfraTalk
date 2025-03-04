package com.sm.infratalk.data.mapper

import com.sm.infratalk.data.model.response.board.BoardBookmarksDeleteResponse
import com.sm.infratalk.data.model.response.bookmark.BookmarkResponse
import com.sm.infratalk.data.model.response.comment.CommentRelatedBookmarksResponse
import com.sm.infratalk.domain.board.entity.BoardBookmarksDeleteEntity
import com.sm.infratalk.domain.board.entity.BookmarkEntity
import com.sm.infratalk.domain.board.entity.CommentRelatedBookmarksEntity

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