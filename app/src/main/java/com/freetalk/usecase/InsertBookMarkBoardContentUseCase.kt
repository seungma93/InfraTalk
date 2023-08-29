package com.freetalk.usecase

import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.InsertBookMarkForm
import com.freetalk.data.remote.InsertBookMarkRequest
import com.freetalk.repository.BookMarkDataRepository
import javax.inject.Inject


class InsertBookMarkBoardContentUseCase @Inject constructor(private val repository: BookMarkDataRepository) {
    suspend operator fun invoke(
        insertBookMarkForm: InsertBookMarkForm,
        wrapperBoardEntity: WrapperBoardEntity
    ): WrapperBoardEntity = with(wrapperBoardEntity) {

        val insertBookMarkRequest = InsertBookMarkRequest(
            insertBookMarkForm.boardAuthorEmail,
            insertBookMarkForm.boardCreateTime
        )

        val bookMarkEntity = repository.insertBookMark(insertBookMarkRequest)

        return WrapperBoardEntity(
            boardEntity = boardEntity,
            bookMarkEntity = bookMarkEntity,
            likeEntity = likeEntity,
            likeCount = likeCount
        )
    }
}