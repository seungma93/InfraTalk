package com.freetalk.usecase

import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.*
import com.freetalk.repository.LikeDataRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


class InsertLikeBoardContentUseCase @Inject constructor(private val repository: LikeDataRepository) {

    suspend operator fun invoke(
        insertLikeForm: InsertLikeForm,
        likeCountSelectForm: LikeCountSelectForm,
        wrapperBoardEntity: WrapperBoardEntity
    ): WrapperBoardEntity = with(wrapperBoardEntity) {

        val insertLikeRequest = InsertLikeRequest(
            insertLikeForm.boardAuthorEmail,
            insertLikeForm.boardCreateTime
        )

        val likeEntity = repository.insertLike(insertLikeRequest)
        val likeCount = repository.selectLikeCount(likeCountSelectForm).likeCount

        WrapperBoardEntity(
            boardEntity = boardEntity,
            bookMarkEntity = bookMarkEntity,
            likeEntity = likeEntity,
            likeCount = likeCount
        )
    }

}

