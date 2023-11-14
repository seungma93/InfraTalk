package com.freetalk.domain.entity

import android.net.Uri
import java.io.Serializable


data class UserEntity(
    val email: String,
    val nickname: String,
    val image: Uri?,
): Serializable


