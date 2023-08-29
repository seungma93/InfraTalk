package com.freetalk.usecase

import android.util.Log
import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.*
import com.freetalk.repository.BookMarkDataRepository
import com.freetalk.repository.LikeDataRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


class DeleteBookMarkBoardUseCase @Inject constructor(private val repository: BookMarkDataRepository) {
    suspend operator fun invoke(
        deleteBookMarkForm: DeleteBookMarkForm,
        boardList: List<WrapperBoardEntity>
    ): List<WrapperBoardEntity> {

        val deleteBookMarkRequest = DeleteBookMarkRequest(
            deleteBookMarkForm.boardAuthorEmail,
            deleteBookMarkForm.boardCreateTime
        )

        repository.deleteBookMark(deleteBookMarkRequest)

        return boardList.map {
            if (it.boardEntity.author.email == deleteBookMarkForm.boardAuthorEmail &&
                it.boardEntity.createTime == deleteBookMarkForm.boardCreateTime
            ) {
                WrapperBoardEntity(
                    boardEntity = it.boardEntity,
                    bookMarkEntity = null,
                    likeEntity = it.likeEntity,
                    likeCount = it.likeCount
                )
            } else it
        }
    }
}