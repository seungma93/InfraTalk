package com.sm.infratalk.domain.comment.usecase

import com.sm.infratalk.domain.comment.entity.CommentMetaEntity
import com.sm.infratalk.domain.comment.repository.CommentDataRepository
import com.sm.infratalk.presenter.board.form.CommentInsertForm
import javax.inject.Inject

class WriteCommentUseCase @Inject constructor(private val repository: CommentDataRepository) {
    suspend operator fun invoke(commentInsertForm: CommentInsertForm): CommentMetaEntity {
        return repository.insertComment(commentInsertForm)
    }
}