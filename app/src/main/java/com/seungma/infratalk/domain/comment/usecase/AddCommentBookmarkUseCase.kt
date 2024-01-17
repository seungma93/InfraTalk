package com.seungma.infratalk.domain.comment.usecase

import com.seungma.infratalk.domain.BookmarkDataRepository
import com.seungma.infratalk.domain.comment.entity.CommentEntity
import com.seungma.infratalk.domain.comment.entity.CommentListEntity
import com.seungma.infratalk.presenter.board.form.CommentBookmarkAddForm
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