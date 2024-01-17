package com.seungma.infratalk.domain.comment.usecase

import com.seungma.infratalk.domain.BookmarkDataRepository
import com.seungma.infratalk.domain.comment.entity.CommentEntity
import com.seungma.infratalk.domain.comment.entity.CommentListEntity
import com.seungma.infratalk.presenter.board.form.CommentBookmarkDeleteForm
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