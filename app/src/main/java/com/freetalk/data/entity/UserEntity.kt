package com.freetalk.data.entity

import android.net.Uri

data class UserEntity(
    val email: String,
    val password: String,
    val nickname: String,
    val image: Uri?
)