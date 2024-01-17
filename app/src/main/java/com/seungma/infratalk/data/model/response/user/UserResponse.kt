package com.seungma.infratalk.data.model.response.user

import android.net.Uri

data class UserResponse(
    val email: String? = null,
    val nickname: String? = null,
    val image: Uri? = null
)