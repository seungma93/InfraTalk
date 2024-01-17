package com.seungma.infratalk.data.model.request.image

import android.net.Uri


data class ImagesRequest(
    val imageUris: List<Uri>
)