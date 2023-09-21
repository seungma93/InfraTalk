package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.presenter.form.BoardBookmarkDeleteForm
import javax.inject.Inject


class DeleteBoardContentBookmarkUseCase @Inject constructor(private val repository: BookmarkDataRepository) {
    suspend operator fun invoke(
        boardBookmarkDeleteForm: BoardBookmarkDeleteForm,
        boardEntity: BoardEntity
    ): BoardEntity = with(boardEntity) {

        val bookmarkEntity = repository.deleteBoardBookmark(
            boardBookmarkDeleteForm = boardBookmarkDeleteForm
        )

        return BoardEntity(
            boardMetaEntity = boardMetaEntity,
            bookmarkEntity = bookmarkEntity,
            likeEntity = likeEntity,
            likeCountEntity = likeCountEntity
        )
    }
}