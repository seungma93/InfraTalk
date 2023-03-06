package com.freetalk.data.entity

import android.net.Uri
import com.freetalk.data.remote.ImagesResponse
import com.freetalk.data.remote.UserResponse

data class UserEntity(
    val email: String,
    val nickname: String,
    val image: Uri?
)

fun UserResponse.toEntity(): UserEntity{
    return UserEntity(
        email = email.orEmpty(),
        nickname = nickname.orEmpty(),
        image = null
    )
}

