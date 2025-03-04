package com.sm.infratalk.domain.board.usecase

import com.sm.infratalk.domain.board.repository.LikeDataRepository
import com.sm.infratalk.domain.board.entity.BoardEntity
import com.sm.infratalk.domain.board.entity.BoardListEntity
import com.sm.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.sm.infratalk.presenter.board.form.BoardLikeDeleteForm
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