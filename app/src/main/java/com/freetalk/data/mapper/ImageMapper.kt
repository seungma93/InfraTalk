package com.freetalk.data.mapper

import com.freetalk.data.model.response.ImagesResponse
import com.freetalk.domain.entity.ImagesResultEntity

fun ImagesResponse.toEntity(): ImagesResultEntity {
    return ImagesResultEntity(
        successUris = successUris,
        failUris = failUris
    )
}