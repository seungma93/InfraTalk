package com.freetalk.usecase

import androidx.compose.runtime.withRunningRecomposer
import com.freetalk.data.entity.LikeCountEntity
import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.LikeCountSelectForm
import com.freetalk.data.remote.LikeSelectForm
import com.freetalk.data.remote.LikeUpdateForm
import com.freetalk.repository.LikeDataRepository
import javax.inject.Inject


class UpdateLikeBoardUseCase @Inject constructor(private val repository: LikeDataRepository) {
    suspend operator fun invoke(
        likeUpdateForm: LikeUpdateForm,
        likeSelectForm: LikeSelectForm,
        likeCountSelectForm: LikeCountSelectForm,
        boardList: List<WrapperBoardEntity>
    ): List<WrapperBoardEntity> {

        repository.updateLike(likeUpdateForm)

        return boardList.map {
            if (it.boardEntity.author.email == likeUpdateForm.boardAuthorEmail && it.boardEntity.createTime == likeUpdateForm.boardCreateTime) {
                WrapperBoardEntity(
                    boardEntity = it.boardEntity,
                    isBookMark = it.isBookMark,
                    isLike = repository.selectLike(likeSelectForm).boardAuthorEmail.isNotEmpty(),
                    likeCount = repository.selectLikeCount(likeCountSelectForm).likeCount
                )
            } else it
        }

    }
}