package com.freetalk.data.model.request

import android.net.Uri
import com.freetalk.domain.entity.UserEntity
import java.util.Date

data class BoardUpdateRequest(
    val author: UserEntity,
    val title: String?,
    val content: String?,
    val images: List<Uri>?,
    val createTime: Date,
    val editTime: Date
)