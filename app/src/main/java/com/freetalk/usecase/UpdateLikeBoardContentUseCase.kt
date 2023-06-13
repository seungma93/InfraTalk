package com.freetalk.usecase

import com.freetalk.data.UserSingleton
import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.*
import com.freetalk.repository.BookMarkDataRepository
import com.freetalk.repository.LikeDataRepository
import com.freetalk.repository.UserDataRepository
import javax.inject.Inject


class UpdateLikeBoardContentUseCase @Inject constructor(private val repository: LikeDataRepository) {
    suspend operator fun invoke(
        likeUpdateForm: LikeUpdateForm,
        likeSelectForm: LikeSelectForm,
        likeCountSelectForm: LikeCountSelectForm,
        wrapperBoardEntity: WrapperBoardEntity
    ): WrapperBoardEntity = with(wrapperBoardEntity){

        repository.updateLike(likeUpdateForm)

        return WrapperBoardEntity(
            boardEntity = boardEntity,
            isBookMark = isBookMark,
            isLike = repository.selectLike(likeSelectForm).boardAuthorEmail.isNotEmpty(),
            likeCount = repository.selectLikeCount(likeCountSelectForm).likeCount
        )
    }

}