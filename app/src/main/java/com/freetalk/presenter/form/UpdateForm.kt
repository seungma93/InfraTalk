package com.freetalk.presenter.form

import android.net.Uri

data class UpdateForm(
    val email: String,
    val nickname: String?,
    val image: Uri?
)