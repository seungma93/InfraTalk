package com.seungma.infratalk.domain.board.usecase

import com.seungma.infratalk.domain.LikeDataRepository
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.domain.board.entity.BoardListEntity
import com.seungma.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLikeDeleteForm
import javax.inject.Inject


class DeleteBoardLikeUseCase @Inject constructor(private val repository: LikeDataRepository) {
    suspend operator fun invoke(
        boardLikeDeleteForm: BoardLikeDeleteForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm,
        boardListEntity: BoardListEntity
    ): BoardListEntity {

        val likeEntity = repository.deleteBoardLike(boardLikeDeleteForm = boardLikeDeleteForm)
        val likeCountEntity =
            repository.loadBoardLikeCount(boardLikeCountLoadForm = boardLikeCountLoadForm)

        return BoardListEntity(
            boardList = boardListEntity.boardList.map {
                if (it.boardMetaEntity.author.email == boardLikeDeleteForm.boardAuthorEmail &&
                    it.boardMetaEntity.createTime == boardLikeDeleteForm.boardCreateTime
                ) {
                    BoardEntity(
                        boardMetaEntity = it.boardMetaEntity,
                        bookmarkEntity = it.bookmarkEntity,
                        likeEntity = likeEntity,
                        likeCountEntity = likeCountEntity
                    )
                } else it
            }
        )
    }
}