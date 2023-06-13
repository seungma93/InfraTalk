package com.freetalk.usecase

import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.*
import com.freetalk.repository.BoardDataRepository
import com.freetalk.repository.BookMarkDataRepository
import com.freetalk.repository.LikeDataRepository
import com.freetalk.repository.LikeDataRepositoryImpl
import javax.inject.Inject

class SelectBoardContentUseCase @Inject constructor(
    private val boardDataRepository: BoardDataRepository,
    private val bookMarkDataRepository: BookMarkDataRepository,
    private val likeDataRepository: LikeDataRepository
) {
    suspend operator fun invoke(
        boardContentSelectForm: BoardContentSelectForm,
        bookMarkSelectForm: BookMarkSelectForm,
        likeSelectForm: LikeSelectForm,
        likeCountSelectForm: LikeCountSelectForm
    ): WrapperBoardEntity {

        return WrapperBoardEntity(
            boardEntity = boardDataRepository.selectBoardContent(boardContentSelectForm),
            isBookMark = bookMarkDataRepository.selectBookMark(bookMarkSelectForm).boardAuthorEmail.isNotEmpty(),
            isLike = likeDataRepository.selectLike(likeSelectForm).boardAuthorEmail.isNotEmpty(),
            likeCount = likeDataRepository.selectLikeCount(likeCountSelectForm).likeCount
        )
    }

}