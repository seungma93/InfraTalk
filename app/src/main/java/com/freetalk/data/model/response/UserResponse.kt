package com.freetalk.data.model.response

import android.net.Uri

data class UserResponse(
    val email: String? = null,
    val nickname: String? = null,
    val image: Uri? = null
)