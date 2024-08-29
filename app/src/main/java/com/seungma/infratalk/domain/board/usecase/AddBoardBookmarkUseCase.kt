package com.seungma.infratalk.domain.board.usecase

import com.seungma.infratalk.domain.board.repository.BookmarkDataRepository
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.domain.board.entity.BoardListEntity
import com.seungma.infratalk.presenter.board.form.BoardBookmarkAddForm
import javax.inject.Inject


class AddBoardBookmarkUseCase @Inject constructor(private val repository: BookmarkDataRepository) {
    suspend operator fun invoke(
        boardBookmarkAddForm: BoardBookmarkAddForm,
        boardListEntity: BoardListEntity
    ): BoardListEntity {


        val bookmarkEntity = repository.addBoardBookmark(boardBookmarkAddForm)

        return BoardListEntity(
            boardList = boardListEntity.boardList.map {
                if (it.boardMetaEntity.author.email == boardBookmarkAddForm.boardAuthorEmail &&
                    it.boardMetaEntity.createTime == boardBookmarkAddForm.boardCreateTime
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