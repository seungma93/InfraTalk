package com.freetalk.usecase

import android.util.Log
import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.*
import com.freetalk.repository.LikeDataRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


class DeleteLikeBoardUseCase @Inject constructor(private val repository: LikeDataRepository) {

    suspend operator fun invoke(
        deleteLikeForm: DeleteLikeForm,
        likeCountSelectForm: LikeCountSelectForm,
        boardList: List<WrapperBoardEntity>
    ): List<WrapperBoardEntity> = coroutineScope {

        val deleteLikeRequest = DeleteLikeRequest(
            deleteLikeForm.boardAuthorEmail,
            deleteLikeForm.boardCreateTime
        )

        repository.deleteLike(deleteLikeRequest)
        val likeCount = repository.selectLikeCount(likeCountSelectForm).likeCount

        boardList.map {
            if (it.boardEntity.author.email == deleteLikeForm.boardAuthorEmail && it.boardEntity.createTime == deleteLikeForm.boardCreateTime) {
                WrapperBoardEntity(
                    boardEntity = it.boardEntity,
                    isBookMark = it.isBookMark,
                    likeEntity = null,
                    likeCount = likeCount
                )
            } else it
        }

    }
}