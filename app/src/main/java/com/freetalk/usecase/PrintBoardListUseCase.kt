package com.freetalk.usecase

import android.util.Log
import com.freetalk.data.entity.BoardListEntity
import com.freetalk.data.entity.LikeEntity
import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.BoardSelectForm
import com.freetalk.data.remote.BookMarkSelectForm
import com.freetalk.data.remote.LikeCountSelectForm
import com.freetalk.data.remote.LikeSelectForm
import com.freetalk.repository.BoardDataRepository
import com.freetalk.repository.BookMarkDataRepository
import com.freetalk.repository.LikeDataRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import kotlin.coroutines.coroutineContext


class PrintBoardListUseCase @Inject constructor(
    private val boardDataRepository: BoardDataRepository,
    private val bookMarkDataRepository: BookMarkDataRepository,
    private val likeDataRepository: LikeDataRepository,
) {
    data class WrapperBoardList(
        val wrapperBoardList: List<WrapperBoardEntity>
    )

    suspend operator fun invoke(
        boardSelectForm: BoardSelectForm,
    ): WrapperBoardList = coroutineScope {
        Log.d("SelectContentsUseCase", "유즈케이스")
        val boardEntityList = boardDataRepository.select(boardSelectForm)

        WrapperBoardList(
            boardEntityList.boardList.map {
                val bookMarkSelectForm = BookMarkSelectForm(
                    boardAuthorEmail = it.author.email,
                    boardCreateTime = it.createTime
                )

                val likeSelectForm = LikeSelectForm(
                    boardAuthorEmail = it.author.email,
                    boardCreateTime = it.createTime
                )

                val likeCountSelectForm = LikeCountSelectForm(
                    boardAuthorEmail = it.author.email,
                    boardCreateTime = it.createTime
                )

                val jobSelectBookMarkEntity =
                    async { bookMarkDataRepository.selectBookMark(bookMarkSelectForm) }
                val jobSelectLikeEntity = async { likeDataRepository.selectLike(likeSelectForm) }
                val jobSelectLikeCountEntity =
                    async { likeDataRepository.selectLikeCount(likeCountSelectForm) }

                val bookMarkEntity = jobSelectBookMarkEntity.await()
                val likeEntity = jobSelectLikeEntity.await()
                val likeCountEntity = jobSelectLikeCountEntity.await()

                WrapperBoardEntity(
                    boardEntity = it,
                    bookMarkEntity = when (bookMarkEntity.boardAuthorEmail.isNotEmpty()) {
                        true -> bookMarkEntity
                        false -> null
                    },
                    likeEntity = when (likeEntity.boardAuthorEmail.isNotEmpty()) {
                        true -> likeEntity
                        false -> null
                    },
                    likeCount = likeCountEntity.likeCount
                )
            })
    }

}