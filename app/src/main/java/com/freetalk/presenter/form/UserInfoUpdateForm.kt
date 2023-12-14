package com.freetalk.presenter.form

import android.net.Uri

data class UserInfoUpdateForm(
    val email: String,
    val nickname: String?,
    val image: Uri?
)