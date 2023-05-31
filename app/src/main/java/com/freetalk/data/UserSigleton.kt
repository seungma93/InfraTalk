package com.freetalk.data

import android.net.Uri
import com.freetalk.data.entity.UserEntity

object UserSingleton {
    var userEntity: UserEntity = UserEntity("", "", Uri.parse(""), emptyList())
}