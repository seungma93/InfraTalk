package com.seungma.infratalk.presenter.board.form

import android.net.Uri
import com.seungma.infratalk.domain.user.entity.UserEntity
import java.util.Date

data class BoardContentInsertForm(
    val author: UserEntity,
    val title: String,
    val content: String,
    val images: List<Uri>?,
    val editTime: Date?
)