package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.repository.BoardDataRepository
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.CommentDataRepository
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.BoardBookmarksDeleteForm
import com.freetalk.presenter.form.BoardDeleteForm
import com.freetalk.presenter.form.BoardLikesDeleteForm
import com.freetalk.presenter.form.BoardRelatedAllCommentMetaListSelectForm
import com.freetalk.presenter.form.CommentDeleteForm
import com.freetalk.presenter.form.CommentRelatedBookmarksDeleteForm
import com.freetalk.presenter.form.CommentRelatedLikesDeleteForm
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DeleteBoardUseCase @Inject constructor(
    private val boardDataRepository: BoardDataRepository,
    private val bookmarkDataRepository: BookmarkDataRepository,
    private val likeDataRepository: LikeDataRepository,
    private val commentDataRepository: CommentDataRepository
) {
    suspend operator fun invoke(
        boardDeleteForm: BoardDeleteForm,
        boardBookmarksDeleteForm: BoardBookmarksDeleteForm,
        boardLikesDeleteForm: BoardLikesDeleteForm,
        boardListEntity: BoardListEntity
    ): BoardListEntity = coroutineScope {

        // TODO: 보드와 관련된 코멘트 다 가져옴 -> 코멘트 다 삭제 -> 코멘트와 연관된 라이크 삭제 -> 코멘트와 연관된 북마크 삭제

        val boardRelatedCommentList = commentDataRepository.loadBoardRelatedAllCommentMetaList(boardRelatedAllCommentMetaListSelectForm = BoardRelatedAllCommentMetaListSelectForm(
            boardAuthorEmail = boardDeleteForm.boardAuthorEmail,
            boardCreateTime = boardDeleteForm.boardCreateTime
        ))

        boardRelatedCommentList.commentMetaList.map {
            val commentDeleteAsync = async {
                commentDataRepository.deleteComment(
                    commentDeleteForm = CommentDeleteForm(
                        commentAuthorEmail = it.author.email,
                        commentCreateTime = it.createTime
                    )
                ) }
            val commentLikesDeleteAsync = async {
                likeDataRepository.deleteCommentRelatedLikes(
                    commentRelatedLikesDeleteForm = CommentRelatedLikesDeleteForm(
                        commentAuthorEmail = it.author.email,
                        commentCreateTime = it.createTime
                    )
                )
            }
            val commentBookmarksAsync = async {
                bookmarkDataRepository.deleteCommentRelatedBookmarks(
                    commentRelatedBookmarksDeleteForm = CommentRelatedBookmarksDeleteForm(
                        commentAuthorEmail = it.author.email,
                        commentCreateTime = it.createTime
                    )
                )
            }
            Triple(commentDeleteAsync, commentLikesDeleteAsync, commentBookmarksAsync)
        }.map { deferred ->
            deferred.first.await()
            deferred.second.await()
            deferred.third.await()

        }

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