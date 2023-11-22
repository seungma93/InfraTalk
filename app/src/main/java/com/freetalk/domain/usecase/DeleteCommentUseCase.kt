package com.freetalk.domain.usecase

import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.CommentDataRepository
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.CommentDeleteForm
import com.freetalk.presenter.form.CommentRelatedBookmarksDeleteForm
import com.freetalk.presenter.form.CommentRelatedLikesDeleteForm
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