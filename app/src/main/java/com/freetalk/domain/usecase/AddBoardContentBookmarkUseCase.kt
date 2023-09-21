package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.presenter.form.BoardBookmarkAddForm
import javax.inject.Inject


class AddBoardContentBookmarkUseCase @Inject constructor(private val repository: BookmarkDataRepository) {
    suspend operator fun invoke(
        boardBookmarkAddForm: BoardBookmarkAddForm,
        boardEntity: BoardEntity
    ): BoardEntity = with(boardEntity) {

        val bookmarkEntity = repository.addBoardBookmark(boardBookmarkAddForm)

        return BoardEntity(
            boardMetaEntity = boardMetaEntity,
            bookmarkEntity = bookmarkEntity,
            likeEntity = likeEntity,
            likeCountEntity = likeCountEntity
        )
    }
}