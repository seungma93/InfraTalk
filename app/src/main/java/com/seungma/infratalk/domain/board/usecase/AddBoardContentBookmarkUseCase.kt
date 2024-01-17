package com.seungma.infratalk.domain.board.usecase

import com.seungma.infratalk.domain.BookmarkDataRepository
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.presenter.board.form.BoardBookmarkAddForm
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