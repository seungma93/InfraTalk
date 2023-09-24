package com.freetalk.presenter.form

import android.net.Uri
import com.freetalk.domain.entity.UserEntity
import java.util.Date

data class BoardUpdateForm(
    val authorEmail: String,
    val createTime: Date,
    val title: String?,
    val content: String?,
    val images: List<Uri>?
)