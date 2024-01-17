package com.seungma.infratalk.domain.comment.usecase

import com.seungma.infratalk.domain.comment.entity.CommentMetaEntity
import com.seungma.infratalk.domain.comment.repository.CommentDataRepository
import com.seungma.infratalk.presenter.board.form.CommentInsertForm
import javax.inject.Inject

class WriteCommentUseCase @Inject constructor(private val repository: CommentDataRepository) {
    suspend operator fun invoke(commentInsertForm: CommentInsertForm): CommentMetaEntity {
        return repository.insertComment(commentInsertForm)
    }
}