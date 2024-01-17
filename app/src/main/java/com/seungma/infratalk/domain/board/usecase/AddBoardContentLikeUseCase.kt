package com.seungma.infratalk.domain.board.usecase

import com.seungma.infratalk.domain.LikeDataRepository
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.presenter.board.form.BoardLikeAddForm
import com.seungma.infratalk.presenter.board.form.BoardLikeCountLoadForm
import javax.inject.Inject


class AddBoardContentLikeUseCase @Inject constructor(private val repository: LikeDataRepository) {

    suspend operator fun invoke(
        boardLikeAddForm: BoardLikeAddForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm,
        boardEntity: BoardEntity
    ): BoardEntity = with(boardEntity) {

        val likeEntity = repository.addBoardLike(boardLikeAddForm = boardLikeAddForm)
        val likeCountEntity =
            repository.loadBoardLikeCount(boardLikeCountLoadForm = boardLikeCountLoadForm)

        BoardEntity(
            boardMetaEntity = boardMetaEntity,
            bookmarkEntity = bookmarkEntity,
            likeEntity = likeEntity,
            likeCountEntity = likeCountEntity
        )
    }

}

