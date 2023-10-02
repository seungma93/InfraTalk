package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.repository.BoardDataRepository
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.BoardBookmarkLoadForm
import com.freetalk.presenter.form.BoardLikeCountLoadForm
import com.freetalk.presenter.form.BoardLikeLoadForm
import com.freetalk.presenter.form.BoardListLoadForm
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LoadBoardListUseCase @Inject constructor(
    private val boardDataRepository: BoardDataRepository,
    private val bookMarkDataRepository: BookmarkDataRepository,
    private val likeDataRepository: LikeDataRepository,
) {
    suspend operator fun invoke(
        boardListLoadForm: BoardListLoadForm
    ): BoardListEntity = coroutineScope {
        val boardMetaListEntity = boardDataRepository.loadBoardMetaList(boardListLoadForm)

        boardMetaListEntity.boardMetaList.map {
            val asyncBookmark =
                async {
                    bookMarkDataRepository.loadBoardBookmark(
                        BoardBookmarkLoadForm(
                            boardAuthorEmail = it.author.email,
                            boardCreateTime = it.createTime
                        )
                    )
                }
            val asyncLike = async {
                likeDataRepository.loadBoardLike(
                    BoardLikeLoadForm(
                        boardAuthorEmail = it.author.email,
                        boardCreateTime = it.createTime
                    )
                )
            }
            val asyncLikeCount =
                async {
                    likeDataRepository.loadBoardLikeCount(
                        BoardLikeCountLoadForm(
                            boardAuthorEmail = it.author.email,
                            boardCreateTime = it.createTime
                        )
                    )
                }
            it to Triple(asyncBookmark, asyncLike, asyncLikeCount)
        }.map { (board, deferred) ->
            val asyncBookmark = deferred.first
            val asyncLike = deferred.second
            val asyncLikeCount = deferred.third
            BoardEntity(
                boardMetaEntity = board,
                bookmarkEntity = asyncBookmark.await(),
                likeEntity = asyncLike.await(),
                likeCountEntity = asyncLikeCount.await()
            )
        }.let {
            BoardListEntity(boardList = it)
        }
    }
}

