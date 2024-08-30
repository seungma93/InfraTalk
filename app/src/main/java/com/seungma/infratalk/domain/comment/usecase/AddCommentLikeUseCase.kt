package com.seungma.infratalk.domain.comment.usecase

import com.seungma.infratalk.domain.board.repository.LikeDataRepository
import com.seungma.infratalk.domain.comment.entity.CommentEntity
import com.seungma.infratalk.domain.comment.entity.CommentListEntity
import com.seungma.infratalk.presenter.board.form.CommentLikeAddForm
import com.seungma.infratalk.presenter.board.form.CommentLikeCountLoadForm
import javax.inject.Inject


class AddCommentLikeUseCase @Inject constructor(private val repository: LikeDataRepository) {
    suspend operator fun invoke(
        commentLikeAddForm: CommentLikeAddForm,
        commentLikeCountLoadForm: CommentLikeCountLoadForm,
        commentListEntity: CommentListEntity
    ): CommentListEntity {

        val likeEntity = repository.addCommentLike(commentLikeAddForm)
        val likeCountEntity = repository.loadCommentLikeCount(commentLikeCountLoadForm)

        return CommentListEntity(
            commentList = commentListEntity.commentList.map {
                if (it.commentMetaEntity.author.email == commentLikeAddForm.commentAuthorEmail &&
                    it.commentMetaEntity.createTime == commentLikeAddForm.commentCreateTime
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