package com.seungma.infratalk.domain.image.usecase

import android.util.Log
import com.seungma.infratalk.data.model.request.image.ImagesRequest
import com.seungma.infratalk.domain.image.entity.ImagesResultEntity
import com.seungma.infratalk.domain.image.repository.ImageDataRepository
import javax.inject.Inject

interface UploadImagesUseCase {
    suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResultEntity
}

class UploadImagesUseCaseImpl @Inject constructor(val repository: ImageDataRepository) :
    UploadImagesUseCase {
    override suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResultEntity {
        Log.d("seungma", "UploadImagesUseCaseImpl.uploadImages")
        return repository.uploadImages(imagesRequest)
    }

}