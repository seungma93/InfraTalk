package com.seungma.infratalk.domain.board.usecase

import com.seungma.infratalk.domain.LikeDataRepository
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.domain.board.entity.BoardListEntity
import com.seungma.infratalk.presenter.board.form.BoardLikeAddForm
import com.seungma.infratalk.presenter.board.form.BoardLikeCountLoadForm
import javax.inject.Inject


class AddBoardLikeUseCase @Inject constructor(private val repository: LikeDataRepository) {

    suspend operator fun invoke(
        boardLikeAddForm: BoardLikeAddForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm,
        boardListEntity: BoardListEntity
    ): BoardListEntity {

        val likeEntity = repository.addBoardLike(boardLikeAddForm)
        val likeCountEntity = repository.loadBoardLikeCount(boardLikeCountLoadForm)

        return BoardListEntity(
            boardListEntity.boardList.map {
                if (it.boardMetaEntity.author.email == boardLikeAddForm.boardAuthorEmail &&
                    it.boardMetaEntity.createTime == boardLikeAddForm.boardCreateTime
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