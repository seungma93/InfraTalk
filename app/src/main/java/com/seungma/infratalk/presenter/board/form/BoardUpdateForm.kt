package com.seungma.infratalk.presenter.board.form

import android.net.Uri
import java.util.Date

data class BoardUpdateForm(
    val authorEmail: String,
    val createTime: Date,
    val title: String?,
    val content: String?,
    val images: List<Uri>?
)