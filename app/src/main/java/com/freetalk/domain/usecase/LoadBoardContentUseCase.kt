package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.repository.BoardDataRepository
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.BoardBookmarkLoadForm
import com.freetalk.presenter.form.BoardLikeCountLoadForm
import com.freetalk.presenter.form.BoardLikeLoadForm
import com.freetalk.presenter.form.BoardLoadForm
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
        val asyncBookmark = async { bookmarkDataRepository.loadBoardBookmark(boardBookmarkLoadForm = boardBookmarkLoadForm) }
        val asyncLike = async { likeDataRepository.loadBoardLike(boardLikeLoadForm = boardLikeLoadForm) }
        val asyncLikeCount = async { likeDataRepository.loadBoardLikeCount(boardLikeCountLoadForm = boardLikeCountLoadForm) }

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