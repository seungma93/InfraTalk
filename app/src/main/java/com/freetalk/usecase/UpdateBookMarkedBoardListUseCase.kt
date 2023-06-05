package com.freetalk.usecase

import com.freetalk.data.entity.BookMarkableBoardEntity
import com.freetalk.data.remote.BookMarkUpdateForm
import javax.inject.Inject


class UpdateBookMarkedBoardListUseCase @Inject constructor(private val updateBookMarkUseCase: UpdateBookMarkUseCase) {
    suspend operator fun invoke(
        bookMarkUpdateForm: BookMarkUpdateForm,
        boardList: List<BookMarkableBoardEntity>
    ): List<BookMarkableBoardEntity> {
        val bookMarkableBoardEntity = updateBookMarkUseCase.invoke(bookMarkUpdateForm)

        return boardList.map {
            if (it.boardEntity.author.email == bookMarkableBoardEntity.boardEntity.author.email && it.boardEntity.createTime == bookMarkableBoardEntity.boardEntity.createTime) {
                bookMarkableBoardEntity
            } else it
        }
    }
}