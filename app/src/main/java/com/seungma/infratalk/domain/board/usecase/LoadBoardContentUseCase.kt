package com.seungma.infratalk.domain.board.usecase

import com.seungma.infratalk.domain.BookmarkDataRepository
import com.seungma.infratalk.domain.LikeDataRepository
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.domain.board.repository.BoardDataRepository
import com.seungma.infratalk.presenter.board.form.BoardBookmarkLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLikeLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLoadForm
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LoadBoardContentUseCase @Inject constructor(
    private val boardDataRepository: BoardDataRepository,
    private val bookmarkDataRepository: BookmarkDataRepository,
    private val likeDataRepository: LikeDataRepository
) {
    suspend operator fun invoke(
        boardLoadForm: BoardLoadForm,
        boardBookmarkLoadForm: BoardBookmarkLoadForm,
        boardLikeLoadForm: BoardLikeLoadForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm
    ): BoardEntity = coroutineScope {

        val asyncBoardMeta = async { boardDataRepository.loadBoard(boardLoadForm = boardLoadForm) }
        val asyncBookmark =
            async { bookmarkDataRepository.loadBoardBookmark(boardBookmarkLoadForm = boardBookmarkLoadForm) }
        val asyncLike =
            async { likeDataRepository.loadBoardLike(boardLikeLoadForm = boardLikeLoadForm) }
        val asyncLikeCount =
            async { likeDataRepository.loadBoardLikeCount(boardLikeCountLoadForm = boardLikeCountLoadForm) }

        val boardMetaEntity = asyncBoardMeta.await()
        val bookmarkEntity = asyncBookmark.await()
        val likeEntity = asyncLike.await()
        val likeCountEntity = asyncLikeCount.await()

        BoardEntity(
            boardMetaEntity = boardMetaEntity,
            bookmarkEntity = bookmarkEntity,
            likeEntity = likeEntity,
            likeCountEntity = likeCountEntity
        )
    }
}