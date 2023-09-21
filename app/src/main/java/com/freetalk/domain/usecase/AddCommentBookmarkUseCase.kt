package com.freetalk.domain.usecase

import com.freetalk.domain.entity.CommentEntity
import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.presenter.form.CommentBookmarkAddForm
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