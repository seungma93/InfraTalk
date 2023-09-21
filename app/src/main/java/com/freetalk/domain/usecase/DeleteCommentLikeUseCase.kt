package com.freetalk.domain.usecase

import com.freetalk.domain.entity.CommentEntity
import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.CommentLikeCountLoadForm
import com.freetalk.presenter.form.CommentLikeDeleteForm
import javax.inject.Inject


class DeleteCommentLikeUseCase @Inject constructor(private val repository: LikeDataRepository) {

    suspend operator fun invoke(
        commentLikeDeleteForm: CommentLikeDeleteForm,
        commentLikeCountLoadForm: CommentLikeCountLoadForm,
        commentListEntity: CommentListEntity
    ): CommentListEntity {

        val likeEntity = repository.deleteCommentLike(commentLikeDeleteForm = commentLikeDeleteForm)
        val likeCountEntity =
            repository.loadCommentLikeCount(commentLikeCountLoadForm = commentLikeCountLoadForm)

        return CommentListEntity(
            commentList = commentListEntity.commentList.map {
                if (it.commentMetaEntity.author.email == commentLikeDeleteForm.commentAuthorEmail &&
                    it.commentMetaEntity.createTime == commentLikeDeleteForm.commentCreateTime
                ) {
                    CommentEntity(
                        commentMetaEntity = it.commentMetaEntity,
                        bookmarkEntity = it.bookmarkEntity,
                        likeEntity = likeEntity,
                        likeCountEntity = likeCountEntity
                    )
                } else it
            }
        )
    }
}