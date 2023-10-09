package com.freetalk.domain.usecase

import com.freetalk.domain.entity.CommentEntity
import com.freetalk.domain.entity.CommentMetaEntity
import com.freetalk.domain.repository.CommentDataRepository
import com.freetalk.presenter.form.CommentInsertForm
import javax.inject.Inject

class WriteCommentUseCase @Inject constructor(private val repository: CommentDataRepository) {
    suspend operator fun invoke(commentInsertForm: CommentInsertForm): CommentMetaEntity {
        return repository.insertComment(commentInsertForm)
    }
}