package com.seungma.infratalk.domain.comment.usecase

import com.seungma.infratalk.domain.LikeDataRepository
import com.seungma.infratalk.domain.comment.entity.CommentEntity
import com.seungma.infratalk.domain.comment.entity.CommentListEntity
import com.seungma.infratalk.presenter.board.form.CommentLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.CommentLikeDeleteForm
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