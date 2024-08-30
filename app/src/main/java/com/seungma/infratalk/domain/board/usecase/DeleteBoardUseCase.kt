package com.seungma.infratalk.domain.board.usecase

import com.seungma.infratalk.domain.board.repository.BookmarkDataRepository
import com.seungma.infratalk.domain.board.repository.LikeDataRepository
import com.seungma.infratalk.domain.board.entity.BoardListEntity
import com.seungma.infratalk.domain.board.repository.BoardDataRepository
import com.seungma.infratalk.domain.comment.repository.CommentDataRepository
import com.seungma.infratalk.presenter.board.form.BoardBookmarksDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardLikesDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardRelatedAllCommentMetaListSelectForm
import com.seungma.infratalk.presenter.board.form.CommentDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentRelatedBookmarksDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentRelatedLikesDeleteForm
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

        val boardRelatedCommentList = commentDataRepository.loadBoardRelatedAllCommentMetaList(
            boardRelatedAllCommentMetaListSelectForm = BoardRelatedAllCommentMetaListSelectForm(
                boardAuthorEmail = boardDeleteForm.boardAuthorEmail,
                boardCreateTime = boardDeleteForm.boardCreateTime
            )
        )

        boardRelatedCommentList.commentMetaList.map {
            val commentDeleteAsync = async {
                commentDataRepository.deleteComment(
                    commentDeleteForm = CommentDeleteForm(
                        commentAuthorEmail = it.author.email,
                        commentCreateTime = it.createTime
                    )
                )
            }
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
            listOf(commentDeleteAsync, commentLikesDeleteAsync, commentBookmarksAsync)
        }.map { deferred ->
            deferred.awaitAll()
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