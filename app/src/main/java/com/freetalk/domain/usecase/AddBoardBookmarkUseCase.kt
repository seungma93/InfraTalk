package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.presenter.form.BoardBookmarkAddForm
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