package com.seungma.infratalk.domain.image

import com.seungma.infratalk.data.model.request.image.ImagesRequest


interface ImageDataRepository {
    suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResultEntity
    suspend fun deleteImage()
}
