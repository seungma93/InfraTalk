package com.freetalk.usecase

import com.freetalk.data.UserSingleton
import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.BookMarkSelectForm
import com.freetalk.data.remote.BookMarkUpdateForm
import com.freetalk.repository.BookMarkDataRepository
import com.freetalk.repository.UserDataRepository
import javax.inject.Inject


class UpdateBookMarkBoardUseCase @Inject constructor(private val repository: BookMarkDataRepository) {
    suspend operator fun invoke(
        bookMarkUpdateForm: BookMarkUpdateForm,
        bookMarkSelectForm: BookMarkSelectForm,
        boardList: List<WrapperBoardEntity>
    ): List<WrapperBoardEntity> {

        repository.updateBookMark(bookMarkUpdateForm)

        return boardList.map {
            if (it.boardEntity.author.email == bookMarkUpdateForm.boardAuthorEmail && it.boardEntity.createTime == bookMarkUpdateForm.boardCreateTime) {
                WrapperBoardEntity(
                    boardEntity = it.boardEntity,
                    isBookMark = repository.selectBookMark(bookMarkSelectForm).boardAuthorEmail.isNotEmpty(),
                    likeEntity = it.likeEntity,
                    likeCount = it.likeCount
                )
            } else it
        }
    }
}