package com.seungma.infratalk.data.model.request.user

import android.net.Uri

data class UserInfoUpdateRequest(
    val email: String,
    val nickname: String?,
    val image: Uri?
)