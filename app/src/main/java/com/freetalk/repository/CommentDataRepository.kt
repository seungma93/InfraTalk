package com.freetalk.repository

import android.util.Log
import com.freetalk.data.entity.*
import com.freetalk.data.remote.*
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject

interface CommentDataRepository {
    suspend fun insertComment(commentInsertForm: CommentInsertForm): CommentEntity
    suspend fun selectComments(commentsSelectForm: CommentsSelectForm): CommentListEntity

    //suspend fun update(boardUpdateForm: BoardUpdateForm): BoardEntity
    suspend fun selectCommentContent(commentContentSelectForm: CommentContentSelectForm): CommentEntity

}

class CommentDataRepositoryImpl @Inject constructor(
    private val commentDataSource: CommentDataSource,
    private val userDataSource: UserDataSource
) : CommentDataRepository {
    override suspend fun insertComment(commentInsertForm: CommentInsertForm): CommentEntity {
        val wrapperCommentInsertForm = WrapperCommentInsertForm(
            commentInsertForm = commentInsertForm,
            userSingleton = userDataSource.getUserSingleton()
        )
        return commentDataSource.insertComment(wrapperCommentInsertForm).toEntity()
    }

    override suspend fun selectComments(commentsSelectForm: CommentsSelectForm): CommentListEntity {
        return commentDataSource.selectComments(commentsSelectForm).toEntity()
    }

    override suspend fun selectCommentContent(commentContentSelectForm: CommentContentSelectForm): CommentEntity {
        return commentDataSource.selectCommentContent(commentContentSelectForm).toEntity()
    }


}