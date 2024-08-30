package com.seungma.infratalk.domain.image.entity

import android.net.Uri

data class ImagesResultEntity(
    val successUris: List<Uri>,
    val failUris: List<Uri>
)


