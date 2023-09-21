package com.freetalk.domain.usecase

import com.freetalk.domain.entity.CommentEntity
import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.presenter.form.CommentBookmarkDeleteForm
import javax.inject.Inject


class DeleteCommentBookmarkUseCase @Inject constructor(private val repository: BookmarkDataRepository) {
    suspend operator fun invoke(
        commentBookmarkDeleteForm: CommentBookmarkDeleteForm,
        commentListEntity: CommentListEntity
    ): CommentListEntity {

        val bookmarkEntity = repository.deleteCommentBookmark(
            commentBookmarkDeleteForm = commentBookmarkDeleteForm
        )

        return CommentListEntity(
            commentList = commentListEntity.commentList.map {
                if (it.commentMetaEntity.author.email == commentBookmarkDeleteForm.commentAuthorEmail &&
                    it.commentMetaEntity.createTime == commentBookmarkDeleteForm.commentCreateTime
                ) {
                    CommentEntity(
                        commentMetaEntity = it.commentMetaEntity,
                        bookmarkEntity = bookmarkEntity,
                        likeEntity = it.likeEntity,
                        likeCountEntity = it.likeCountEntity
                    )
                } else it
            }
        )
    }
}