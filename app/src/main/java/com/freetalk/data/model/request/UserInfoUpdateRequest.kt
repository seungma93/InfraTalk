package com.freetalk.data.model.request

import android.net.Uri

data class UserInfoUpdateRequest(
    val email: String,
    val nickname: String?,
    val image: Uri?
)