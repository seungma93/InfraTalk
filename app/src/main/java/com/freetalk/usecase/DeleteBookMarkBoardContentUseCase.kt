package com.freetalk.usecase

import android.util.Log
import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.*
import com.freetalk.repository.BookMarkDataRepository
import com.freetalk.repository.LikeDataRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


class DeleteBookMarkBoardContentUseCase @Inject constructor(private val repository: BookMarkDataRepository) {
    suspend operator fun invoke(
        deleteBookMarkForm: DeleteBookMarkForm,
        wrapperBoardEntity: WrapperBoardEntity
    ): WrapperBoardEntity = with(wrapperBoardEntity) {

        val deleteBookMarkRequest = DeleteBookMarkRequest(
            deleteBookMarkForm.boardAuthorEmail,
            deleteBookMarkForm.boardCreateTime
        )

        repository.deleteBookMark(deleteBookMarkRequest)

        return WrapperBoardEntity(
            boardEntity = boardEntity,
            bookMarkEntity = null,
            likeEntity = likeEntity,
            likeCount = likeCount
        )
    }
}