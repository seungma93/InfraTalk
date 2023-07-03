package com.freetalk.usecase

import com.freetalk.data.UserSingleton
import com.freetalk.data.entity.LikeEntity
import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.BookMarkSelectForm
import com.freetalk.data.remote.BookMarkUpdateForm
import com.freetalk.repository.BookMarkDataRepository
import com.freetalk.repository.UserDataRepository
import javax.inject.Inject


class UpdateBookMarkBoardContentUseCase @Inject constructor(private val repository: BookMarkDataRepository) {
    suspend operator fun invoke(
        bookMarkUpdateForm: BookMarkUpdateForm,
        bookMarkSelectForm: BookMarkSelectForm,
        wrapperBoardEntity: WrapperBoardEntity
    ): WrapperBoardEntity = with(wrapperBoardEntity){

        repository.updateBookMark(bookMarkUpdateForm)

        return WrapperBoardEntity(
            boardEntity = boardEntity,
            isBookMark = repository.selectBookMark(bookMarkSelectForm).boardAuthorEmail.isNotEmpty(),
            likeEntity = likeEntity,
            likeCount = likeCount
        )
    }

}