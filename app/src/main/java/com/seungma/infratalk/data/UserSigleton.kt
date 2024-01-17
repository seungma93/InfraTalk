package com.seungma.infratalk.data

import android.net.Uri
import com.seungma.infratalk.domain.user.UserEntity

object UserSingleton {
    var userEntity: UserEntity = UserEntity("", "", Uri.parse(""))
}
