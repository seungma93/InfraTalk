package com.freetalk.data.entity

import android.net.Uri
import com.freetalk.data.remote.ImagesResponse

data class ImagesResultEntity(
    val successUris: List<Uri>,
    val failUris: List<Uri>
)

fun ImagesResponse.toEntity(): ImagesResultEntity{
    return ImagesResultEntity(
        successUris = successUris,
        failUris = failUris
    )
}

