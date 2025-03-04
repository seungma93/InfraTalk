package com.sm.infratalk.data.model.request.board

import android.net.Uri
import com.sm.infratalk.domain.user.entity.UserEntity
import java.util.Date

data class BoardUpdateRequest(
    val author: UserEntity,
    val title: String?,
    val content: String?,
    val images: List<Uri>?,
    val createTime: Date,
    val editTime: Date?
)