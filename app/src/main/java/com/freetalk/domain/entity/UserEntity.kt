package com.freetalk.domain.entity

import android.net.Uri


data class UserEntity(
    val email: String,
    val nickname: String,
    val image: Uri?,
)


