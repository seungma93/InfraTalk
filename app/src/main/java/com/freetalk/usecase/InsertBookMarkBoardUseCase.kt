package com.freetalk.usecase

import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.InsertBookMarkForm
import com.freetalk.data.remote.InsertBookMarkRequest
import com.freetalk.repository.BookMarkDataRepository
import javax.inject.Inject


class InsertBookMarkBoardUseCase @Inject constructor(private val repository: BookMarkDataRepository) {
    suspend operator fun invoke(
        insertBookMarkForm: InsertBookMarkForm,
        boardList: List<WrapperBoardEntity>
    ): List<WrapperBoardEntity> {

        val insertBookMarkRequest = InsertBookMarkRequest(
            insertBookMarkForm.boardAuthorEmail,
            insertBookMarkForm.boardCreateTime
        )

        val bookMarkEntity = repository.insertBookMark(insertBookMarkRequest)

        return boardList.map {
            if (it.boardEntity.author.email == insertBookMarkForm.boardAuthorEmail &&
                it.boardEntity.createTime == insertBookMarkForm.boardCreateTime
            ) {
                WrapperBoardEntity(
                    boardEntity = it.boardEntity,
                    bookMarkEntity = bookMarkEntity,
                    likeEntity = it.likeEntity,
                    likeCount = it.likeCount
                )
            } else it
        }
    }
}