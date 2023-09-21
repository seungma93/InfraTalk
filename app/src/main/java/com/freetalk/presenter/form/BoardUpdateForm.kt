package com.freetalk.presenter.form

import android.net.Uri
import com.freetalk.domain.entity.UserEntity
import java.util.Date

data class BoardUpdateForm(
    val author: UserEntity,
    val title: String?,
    val content: String?,
    val images: List<Uri>?,
    val createTime: Date
)