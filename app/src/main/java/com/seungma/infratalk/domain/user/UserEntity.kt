package com.seungma.infratalk.domain.user

import android.net.Uri
import java.io.Serializable


data class UserEntity(
    val email: String,
    val nickname: String,
    val image: Uri?,
) : Serializable


