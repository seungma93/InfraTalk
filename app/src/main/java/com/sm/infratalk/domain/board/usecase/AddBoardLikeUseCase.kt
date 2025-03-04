package com.sm.infratalk.domain.board.usecase

import com.sm.infratalk.domain.board.repository.LikeDataRepository
import com.sm.infratalk.domain.board.entity.BoardEntity
import com.sm.infratalk.domain.board.entity.BoardListEntity
import com.sm.infratalk.presenter.board.form.BoardLikeAddForm
import com.sm.infratalk.presenter.board.form.BoardLikeCountLoadForm
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