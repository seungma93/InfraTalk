package com.freetalk.presenter.form

import com.freetalk.data.UserSingleton

data class WrapperCommentInsertForm(
    val commentInsertForm: CommentInsertForm,
    val userSingleton: UserSingleton
)