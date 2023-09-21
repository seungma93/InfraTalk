package com.freetalk.domain.repository

import com.freetalk.data.model.request.ImagesRequest
import com.freetalk.domain.entity.ImagesResultEntity


interface ImageDataRepository {
    suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResultEntity
    suspend fun deleteImage()
}
