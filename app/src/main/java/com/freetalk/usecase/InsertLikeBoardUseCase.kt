package com.freetalk.usecase

import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.InsertLikeForm
import com.freetalk.data.remote.InsertLikeRequest
import com.freetalk.data.remote.LikeCountSelectForm
import com.freetalk.repository.LikeDataRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


class InsertLikeBoardUseCase @Inject constructor(private val repository: LikeDataRepository) {

    suspend operator fun invoke(
        insertLikeForm: InsertLikeForm,
        likeCountSelectForm: LikeCountSelectForm,
        boardList: List<WrapperBoardEntity>
    ): List<WrapperBoardEntity> = coroutineScope {

        val insertLikeRequest = InsertLikeRequest(
            insertLikeForm.boardAuthorEmail,
            insertLikeForm.boardCreateTime
        )

        val likeEntity = repository.insertLike(insertLikeRequest)
        val likeCount = repository.selectLikeCount(likeCountSelectForm).likeCount

        boardList.map {
            if (it.boardEntity.author.email == insertLikeForm.boardAuthorEmail && it.boardEntity.createTime == insertLikeForm.boardCreateTime) {
                WrapperBoardEntity(
                    boardEntity = it.boardEntity,
                    isBookMark = it.isBookMark,
                    likeEntity = likeEntity,
                    likeCount = likeCount
                )
            } else it
        }

    }
}