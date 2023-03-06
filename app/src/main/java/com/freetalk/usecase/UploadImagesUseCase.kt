package com.freetalk.usecase

import com.freetalk.data.entity.ImagesEntity
import com.freetalk.data.remote.ImagesRequest
import com.freetalk.repository.ImageDataRepository
import java.security.PrivilegedAction

interface UploadImagesUseCase {
    suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesEntity
}

class UploadImagesUseCaseImpl(val repository: ImageDataRepository): UploadImagesUseCase{
    override suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesEntity {
        return repository.uploadImages(imagesRequest)
    }

}