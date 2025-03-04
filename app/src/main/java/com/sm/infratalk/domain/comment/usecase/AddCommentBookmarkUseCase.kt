package com.sm.infratalk.domain.comment.usecase

import com.sm.infratalk.domain.board.repository.BookmarkDataRepository
import com.sm.infratalk.domain.comment.entity.CommentEntity
import com.sm.infratalk.domain.comment.entity.CommentListEntity
import com.sm.infratalk.presenter.board.form.CommentBookmarkAddForm
import javax.inject.Inject


class AddCommentBookmarkUseCase @Inject constructor(private val repository: BookmarkDataRepository) {
    suspend operator fun invoke(
        commentBookmarkAddForm: CommentBookmarkAddForm,
        commentListEntity: CommentListEntity
    ): CommentListEntity {

        val bookmarkEntity = repository.addCommentBookmark(commentBookmarkAddForm)

        return CommentListEntity(
            commentList = commentListEntity.commentList.map {
                if (it.commentMetaEntity.author.email == commentBookmarkAddForm.commentAuthorEmail &&
                    it.commentMetaEntity.createTime == commentBookmarkAddForm.commentCreateTime
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