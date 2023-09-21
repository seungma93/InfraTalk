package com.freetalk.data.model.response

import android.net.Uri

data class ImagesResponse(
    val successUris: List<Uri>,
    val failUris: List<Uri>
)