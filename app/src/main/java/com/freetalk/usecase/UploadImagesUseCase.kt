package com.freetalk.usecase

import com.freetalk.data.entity.ImagesResultEntity
import com.freetalk.data.remote.ImagesRequest
import com.freetalk.repository.ImageDataRepository
import javax.inject.Inject

interface UploadImagesUseCase {
    suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResultEntity
}

class UploadImagesUseCaseImpl @Inject constructor(val repository: ImageDataRepository): UploadImagesUseCase{
    override suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResultEntity {
        return repository.uploadImages(imagesRequest)
    }

}