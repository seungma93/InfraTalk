package com.freetalk.domain.usecase

import com.freetalk.domain.entity.CommentEntity
import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.CommentLikeAddForm
import com.freetalk.presenter.form.CommentLikeCountLoadForm
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