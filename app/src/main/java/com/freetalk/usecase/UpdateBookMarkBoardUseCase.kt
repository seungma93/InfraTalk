package com.freetalk.usecase

import com.freetalk.data.UserSingleton
import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.BookMarkUpdateForm
import com.freetalk.repository.UserDataRepository
import javax.inject.Inject


class UpdateBookMarkBoardUseCase @Inject constructor(private val repository: UserDataRepository) {
    suspend operator fun invoke(
        bookMarkUpdateForm: BookMarkUpdateForm,
        boardList: List<WrapperBoardEntity>
    ): List<WrapperBoardEntity> {
        val userEntity = repository.updateBookMark(bookMarkUpdateForm)
        UserSingleton.userEntity = userEntity

        val boardId = bookMarkUpdateForm.boardEntity.author.email + bookMarkUpdateForm.boardEntity.createTime

        return boardList.map {
            if (it.boardEntity.author.email == bookMarkUpdateForm.boardEntity.author.email && it.boardEntity.createTime == bookMarkUpdateForm.boardEntity.createTime) {
                WrapperBoardEntity(
                    boardEntity = bookMarkUpdateForm.boardEntity,
                    isBookMark = userEntity.bookMarkList.contains(boardId),
                    isLike = it.isLike,
                    likeCount = it.likeCount
                    )
            } else it
        }
    }
}