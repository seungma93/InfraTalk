package com.seungma.infratalk.domain.image.repository

import com.seungma.infratalk.data.model.request.image.ImagesRequest
import com.seungma.infratalk.domain.image.entity.ImagesResultEntity


interface ImageDataRepository {
    suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResultEntity
    suspend fun deleteImage()
}
