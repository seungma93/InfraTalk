package com.freetalk.domain.entity

import android.net.Uri

data class ImagesResultEntity(
    val successUris: List<Uri>,
    val failUris: List<Uri>
)


