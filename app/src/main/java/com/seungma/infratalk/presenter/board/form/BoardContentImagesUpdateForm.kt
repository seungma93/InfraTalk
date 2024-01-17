package com.seungma.infratalk.presenter.board.form

import android.net.Uri
import java.util.Date

data class BoardContentImagesUpdateForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val images: List<Uri>
)