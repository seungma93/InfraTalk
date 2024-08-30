package com.seungma.infratalk.domain.comment.usecase

import com.seungma.infratalk.domain.board.repository.BookmarkDataRepository
import com.seungma.infratalk.domain.board.repository.LikeDataRepository
import com.seungma.infratalk.domain.comment.entity.CommentListEntity
import com.seungma.infratalk.domain.comment.repository.CommentDataRepository
import com.seungma.infratalk.presenter.board.form.CommentDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentRelatedBookmarksDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentRelatedLikesDeleteForm
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val commentDataRepository: CommentDataRepository,
    private val bookmarkDataRepository: BookmarkDataRepository,
    private val likeDataRepository: LikeDataRepository
) {
    suspend operator fun invoke(
        commentDeleteForm: CommentDeleteForm,
        commentRelatedBookmarksDeleteForm: CommentRelatedBookmarksDeleteForm,
        commentRelatedLikesDeleteForm: CommentRelatedLikesDeleteForm,
        commentListEntity: CommentListEntity
    ): CommentListEntity = coroutineScope {

        val asyncCommentDelete = async {
            commentDataRepository.deleteComment(
                commentDeleteForm = commentDeleteForm
            )
        }
        val asyncBookmarkDelete =
            async {
                bookmarkDataRepository.deleteCommentRelatedBookmarks(
                    commentRelatedBookmarksDeleteForm = commentRelatedBookmarksDeleteForm
                )
            }
        val asyncLikeDelete = async {
            likeDataRepository.deleteCommentRelatedLikes(
                commentRelatedLikesDeleteForm = commentRelatedLikesDeleteForm
            )
        }

        asyncCommentDelete.await()
        asyncBookmarkDelete.await()
        asyncLikeDelete.await()

        CommentListEntity(
            commentList = commentListEntity.commentList.filterNot { commentEntity ->
                commentEntity.commentMetaEntity.commentPrimaryKey == commentDeleteForm.let { it.commentAuthorEmail + it.commentCreateTime }
            }
        )
    }
}