package com.freetalk.domain.entity


data class BookmarkEntity(
    val isBookmark: Boolean = false
)


data class CommentRelatedBookmarksEntity(
    val isBookmarks: Boolean = false
)

data class BoardBookmarksDeleteEntity(
    val isBoardBookmarks: Boolean = false
)