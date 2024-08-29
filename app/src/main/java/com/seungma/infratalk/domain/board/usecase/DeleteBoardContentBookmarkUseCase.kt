package com.seungma.infratalk.domain.board.usecase

import com.seungma.infratalk.domain.board.repository.BookmarkDataRepository
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.presenter.board.form.BoardBookmarkDeleteForm
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