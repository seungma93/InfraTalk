package com.freetalk.usecase

import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.*
import com.freetalk.repository.BoardDataRepository
import com.freetalk.repository.BookMarkDataRepository
import com.freetalk.repository.LikeDataRepository
import com.freetalk.repository.LikeDataRepositoryImpl
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    ): WrapperBoardEntity = coroutineScope {

        val jobSelectBoardEntity = boardDataRepository.selectBoardContent(boardContentSelectForm)
        val jobSelectBookMark = bookMarkDataRepository.selectBookMark(bookMarkSelectForm)
        val jobSelectLikeEntity = likeDataRepository.selectLike(likeSelectForm)
        val jobSelectLikeCountEntity = likeDataRepository.selectLikeCount(likeCountSelectForm)

        val boardEntity = async { jobSelectBoardEntity }.await()
        val bookMarkEntity = async { jobSelectBookMark }.await()
        val likeEntity = async { jobSelectLikeEntity }.await()
        val likeCount = async { jobSelectLikeCountEntity }.await()

        WrapperBoardEntity(
            boardEntity = boardEntity,
            isBookMark = bookMarkEntity.boardAuthorEmail.isNotEmpty(),
            likeEntity = when (likeEntity.boardAuthorEmail.isNotEmpty()) {
                true -> likeEntity
                false -> null
            },
            likeCount = likeCount.likeCount
        )
    }

}