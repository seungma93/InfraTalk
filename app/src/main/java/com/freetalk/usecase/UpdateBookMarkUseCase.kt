package com.freetalk.usecase

import android.util.Log
import com.freetalk.data.UserSingleton
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.BookMarkableBoardEntity
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.BookMarkUpdateForm
import com.freetalk.repository.BoardDataRepository
import com.freetalk.repository.UserDataRepository
import javax.inject.Inject


class UpdateBookMarkUseCase @Inject constructor(private val repository: UserDataRepository) {
    suspend operator fun invoke(bookMarkUpdateForm: BookMarkUpdateForm): BookMarkableBoardEntity {
        Log.d("UpdateBookMarkUseCase", "유즈케이스")
        val userEntity = repository.updateBookMark(bookMarkUpdateForm)
        UserSingleton.userEntity = userEntity
        val boardId = bookMarkUpdateForm.boardEntity.author.email + bookMarkUpdateForm.boardEntity.createTime
        Log.d("UpdateBookMarkUseCase", userEntity.bookMarkList.contains(boardId).toString())
        return BookMarkableBoardEntity(
            boardEntity = bookMarkUpdateForm.boardEntity,
            isBookMark = userEntity.bookMarkList.contains(boardId)
        )
    }
}