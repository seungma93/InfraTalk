package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.BoardLikeCountLoadForm
import com.freetalk.presenter.form.BoardLikeDeleteForm
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

