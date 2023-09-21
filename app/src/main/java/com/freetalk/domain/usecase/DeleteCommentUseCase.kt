package com.freetalk.domain.usecase

import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.CommentDataRepository
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.CommentDeleteForm
import com.freetalk.presenter.form.CommentRelatedBookmarksDeleteFrom
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
        commentRelatedBookmarksDeleteForm: CommentRelatedBookmarksDeleteFrom,
        commentRelatedLikesDeleteForm: CommentRelatedLikesDeleteForm,
        commentListEntity: CommentListEntity
    ): CommentListEntity = coroutineScope {


        val asyncComment = async {
            commentDataRepository.deleteComment(
                commentDeleteForm = commentDeleteForm
            )
        }
        val asyncBookmark =
            async {
                bookmarkDataRepository.deleteCommentRelatedBookmarks(
                    commentRelatedBookmarksDeleteForm = commentRelatedBookmarksDeleteForm
                )
            }
        val asyncLike = async {
            likeDataRepository.deleteCommentRelatedLikes(
                commentRelatedLikesDeleteForm = commentRelatedLikesDeleteForm
            )
        }

        asyncComment.await()
        asyncBookmark.await()
        asyncLike.await()

        CommentListEntity(
            commentList = commentListEntity.commentList.filterNot {
                it.commentMetaEntity.author.email == commentDeleteForm.commentAuthorEmail &&
                        it.commentMetaEntity.createTime == commentDeleteForm.commentCreateTIme
            }
        )
    }
}