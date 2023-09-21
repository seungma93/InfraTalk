package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.BoardLikeAddForm
import com.freetalk.presenter.form.BoardLikeCountLoadForm
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