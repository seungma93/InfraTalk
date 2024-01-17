package com.seungma.infratalk.presenter.sign.form

import android.net.Uri

data class UserInfoUpdateForm(
    val email: String,
    val nickname: String?,
    val image: Uri?
)