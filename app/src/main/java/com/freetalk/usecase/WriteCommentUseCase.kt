package com.freetalk.usecase

import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.CommentEntity
import com.freetalk.data.remote.*
import com.freetalk.repository.BoardDataRepository
import com.freetalk.repository.CommentDataRepository
import javax.inject.Inject

class WriteCommentUseCase @Inject constructor(private val repository: CommentDataRepository) {
    suspend operator fun invoke(commentInsertForm: CommentInsertForm): CommentEntity {
        return repository.insertComment(commentInsertForm)
    }
}