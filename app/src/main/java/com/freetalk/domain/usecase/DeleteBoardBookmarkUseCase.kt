package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.presenter.form.BoardBookmarkDeleteForm
import javax.inject.Inject


class DeleteBoardBookmarkUseCase @Inject constructor(private val repository: BookmarkDataRepository) {
    suspend operator fun invoke(
        boardBookmarkDeleteForm: BoardBookmarkDeleteForm,
        boardListEntity: BoardListEntity
    ): BoardListEntity {

        val bookmarkEntity = repository.deleteBoardBookmark(boardBookmarkDeleteForm)

        return BoardListEntity(
            boardList = boardListEntity.boardList.map {
                if (it.boardMetaEntity.author.email == boardBookmarkDeleteForm.boardAuthorEmail &&
                    it.boardMetaEntity.createTime == boardBookmarkDeleteForm.boardCreateTime
                ) {
                    BoardEntity(
                        boardMetaEntity = it.boardMetaEntity,
                        bookmarkEntity = bookmarkEntity,
                        likeEntity = it.likeEntity,
                        likeCountEntity = it.likeCountEntity
                    )
                } else it
            }
        )
    }
}