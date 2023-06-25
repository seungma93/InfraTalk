package com.freetalk.usecase

import android.util.Log
import com.freetalk.data.entity.BoardListEntity
import com.freetalk.data.entity.CommentListEntity
import com.freetalk.data.remote.BoardSelectForm
import com.freetalk.data.remote.CommentListResponse
import com.freetalk.data.remote.CommentsSelectForm
import com.freetalk.repository.BoardDataRepository
import com.freetalk.repository.CommentDataRepository
import javax.inject.Inject


class PrintCommentListUseCase @Inject constructor(private val repository: CommentDataRepository) {
    suspend operator fun invoke (commentsSelectForm: CommentsSelectForm): CommentListEntity {
        Log.d("SelectContentsUseCase", "유즈케이스")
        return repository.selectComments(commentsSelectForm)
    }

}