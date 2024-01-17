package com.seungma.infratalk.domain.mypage.usecase

import com.seungma.infratalk.domain.BookmarkDataRepository
import com.seungma.infratalk.domain.LikeDataRepository
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.domain.board.entity.BoardListEntity
import com.seungma.infratalk.domain.board.repository.BoardDataRepository
import com.seungma.infratalk.presenter.board.form.BoardBookmarkLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLikeLoadForm
import com.seungma.infratalk.presenter.mypage.form.MyBoardListLoadForm
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LoadMyBoardListUseCase @Inject constructor(
    private val boardDataRepository: BoardDataRepository,
    private val bookMarkDataRepository: BookmarkDataRepository,
    private val likeDataRepository: LikeDataRepository,
) {
    suspend operator fun invoke(
        myBoardListLoadForm: MyBoardListLoadForm
    ): BoardListEntity = coroutineScope {
        val boardMetaListEntity =
            boardDataRepository.loadMyBoardList(myBoardListLoadForm = myBoardListLoadForm)

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

