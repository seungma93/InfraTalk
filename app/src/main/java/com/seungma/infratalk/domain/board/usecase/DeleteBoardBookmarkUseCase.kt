package com.seungma.infratalk.domain.board.usecase

import com.seungma.infratalk.domain.BookmarkDataRepository
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.domain.board.entity.BoardListEntity
import com.seungma.infratalk.presenter.board.form.BoardBookmarkDeleteForm
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