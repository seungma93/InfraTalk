package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.BoardLikeCountLoadForm
import com.freetalk.presenter.form.BoardLikeDeleteForm
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