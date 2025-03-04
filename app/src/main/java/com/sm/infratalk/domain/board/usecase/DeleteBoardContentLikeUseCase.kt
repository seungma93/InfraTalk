package com.sm.infratalk.domain.board.usecase

import com.sm.infratalk.domain.board.repository.LikeDataRepository
import com.sm.infratalk.domain.board.entity.BoardEntity
import com.sm.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.sm.infratalk.presenter.board.form.BoardLikeDeleteForm
import javax.inject.Inject


class DeleteBoardContentLikeUseCase @Inject constructor(private val repository: LikeDataRepository) {

    suspend operator fun invoke(
        boardLikeDeleteForm: BoardLikeDeleteForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm,
        boardEntity: BoardEntity
    ): BoardEntity = with(boardEntity) {

        val likeEntity = repository.deleteBoardLike(
            boardLikeDeleteForm = boardLikeDeleteForm
        )
        val likeCountEntity = repository.loadBoardLikeCount(
            boardLikeCountLoadForm = boardLikeCountLoadForm
        )

        return BoardEntity(
            boardMetaEntity = boardMetaEntity,
            bookmarkEntity = bookmarkEntity,
            likeEntity = likeEntity,
            likeCountEntity = likeCountEntity
        )
    }

}

