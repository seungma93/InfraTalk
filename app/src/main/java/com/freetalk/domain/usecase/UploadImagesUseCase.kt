package com.freetalk.domain.usecase

import com.freetalk.data.model.request.ImagesRequest
import com.freetalk.domain.entity.ImagesResultEntity
import com.freetalk.domain.repository.ImageDataRepository
import javax.inject.Inject

interface UploadImagesUseCase {
    suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResultEntity
}

class UploadImagesUseCaseImpl @Inject constructor(val repository: ImageDataRepository): UploadImagesUseCase{
    override suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResultEntity {
        return repository.uploadImages(imagesRequest)
    }

}