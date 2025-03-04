package com.sm.infratalk.domain.image.repository

import com.sm.infratalk.data.model.request.image.ImagesRequest
import com.sm.infratalk.domain.image.entity.ImagesResultEntity


interface ImageDataRepository {
    suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResultEntity
    suspend fun deleteImage()
}
