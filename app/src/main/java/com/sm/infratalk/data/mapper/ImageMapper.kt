package com.sm.infratalk.data.mapper

import com.sm.infratalk.data.model.response.image.ImagesResponse
import com.sm.infratalk.domain.image.entity.ImagesResultEntity

fun ImagesResponse.toEntity(): ImagesResultEntity {
    return ImagesResultEntity(
        successUris = successUris,
        failUris = failUris
    )
}