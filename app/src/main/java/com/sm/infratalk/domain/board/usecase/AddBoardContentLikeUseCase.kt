package com.sm.infratalk.domain.board.usecase

import com.sm.infratalk.domain.board.repository.LikeDataRepository
import com.sm.infratalk.domain.board.entity.BoardEntity
import com.sm.infratalk.presenter.board.form.BoardLikeAddForm
import com.sm.infratalk.presenter.board.form.BoardLikeCountLoadForm
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

