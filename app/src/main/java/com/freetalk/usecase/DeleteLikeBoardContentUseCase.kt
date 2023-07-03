package com.freetalk.usecase

import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.*
import com.freetalk.repository.LikeDataRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


class DeleteLikeBoardContentUseCase @Inject constructor(private val repository: LikeDataRepository) {

    suspend operator fun invoke(
        deleteLikeForm: DeleteLikeForm,
        likeCountSelectForm: LikeCountSelectForm,
        wrapperBoardEntity: WrapperBoardEntity
    ): WrapperBoardEntity = coroutineScope {

        val deleteLikeRequest = DeleteLikeRequest(
            deleteLikeForm.boardAuthorEmail,
            deleteLikeForm.boardCreateTime
        )
        repository.deleteLike(deleteLikeRequest)
        val likeCount = repository.selectLikeCount(likeCountSelectForm).likeCount

        WrapperBoardEntity(
            boardEntity = wrapperBoardEntity.boardEntity,
            isBookMark = wrapperBoardEntity.isBookMark,
            likeEntity = null,
            likeCount = likeCount
        )
    }

}

