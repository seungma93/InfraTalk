package com.seungma.infratalk.data.model.response.image

import android.net.Uri

data class ImagesResponse(
    val successUris: List<Uri>,
    val failUris: List<Uri>
)