package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.repository.BoardDataRepository
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.CommentDataRepository
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.BoardBookmarkDeleteForm
import com.freetalk.presenter.form.BoardBookmarksDeleteForm
import com.freetalk.presenter.form.BoardDeleteForm
import com.freetalk.presenter.form.BoardLikeDeleteForm
import com.freetalk.presenter.form.BoardLikesDeleteForm
import com.freetalk.presenter.form.CommentDeleteForm
import com.freetalk.presenter.form.CommentRelatedBookmarksDeleteFrom
import com.freetalk.presenter.form.CommentRelatedLikesDeleteForm
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DeleteBoardUseCase @Inject constructor(
    private val boardDataRepository: BoardDataRepository,
    private val bookmarkDataRepository: BookmarkDataRepository,
    private val likeDataRepository: LikeDataRepository
) {
    suspend operator fun invoke(
        boardDeleteForm: BoardDeleteForm,
        boardBookmarksDeleteForm: BoardBookmarksDeleteForm,
        boardLikesDeleteForm: BoardLikesDeleteForm,
        boardListEntity: BoardListEntity
    ): BoardListEntity = coroutineScope {

        val asyncBoardDelete = async {
            boardDataRepository.deleteBoard(
                boardDeleteForm = boardDeleteForm
            )
        }
        val asyncBookmarkDelete =
            async {
                bookmarkDataRepository.deleteBoardBookmarks(
                    boardBookmarksDeleteForm = boardBookmarksDeleteForm
                )
            }
        val asyncLikeDelete = async {
            likeDataRepository.deleteBoardLikes(
                boardLikesDeleteForm = boardLikesDeleteForm
            )
        }

        asyncBoardDelete.await()
        asyncBookmarkDelete.await()
        asyncLikeDelete.await()

        BoardListEntity(
            boardList = boardListEntity.boardList.filterNot { boardEntity ->
                boardEntity.boardMetaEntity.boardPrimaryKey == boardDeleteForm.let { it.boardAuthorEmail + it.boardCreateTime }
            }
        )
    }
}