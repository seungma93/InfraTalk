package com.seungma.infratalk.data.mapper

import com.seungma.infratalk.data.model.response.image.ImagesResponse
import com.seungma.infratalk.domain.image.ImagesResultEntity

fun ImagesResponse.toEntity(): ImagesResultEntity {
    return ImagesResultEntity(
        successUris = successUris,
        failUris = failUris
    )
}