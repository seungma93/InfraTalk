package com.freetalk.data.entity

import android.net.Uri
import com.freetalk.data.remote.ImagesResponse

data class ImagesEntity(
    val successUris: List<Uri>,
    val failUris: List<Uri>
)

fun ImagesResponse.toEntity(): ImagesEntity{
    return ImagesEntity(
        successUris = successUris,
        failUris = failUris
    )
}

