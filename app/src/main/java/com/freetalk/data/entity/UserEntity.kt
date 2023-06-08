package com.freetalk.data.entity

import android.net.Uri
import com.freetalk.data.remote.ImagesResponse
import com.freetalk.data.remote.UserResponse

data class UserEntity(
    val email: String,
    val nickname: String,
    val image: Uri?,
    val bookMarkList: List<String>,
    val likeList: List<String>
)

fun UserResponse.toEntity(): UserEntity{
    return UserEntity(
        email = email.orEmpty(),
        nickname = nickname.orEmpty(),
        image = null,
        bookMarkList = bookMarkList ?: emptyList(),
        likeList = likeList ?: emptyList()
    )
}

