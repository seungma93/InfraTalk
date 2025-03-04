package com.sm.infratalk.domain.image.usecase

import android.util.Log
import com.sm.infratalk.data.model.request.image.ImagesRequest
import com.sm.infratalk.domain.image.entity.ImagesResultEntity
import com.sm.infratalk.domain.image.repository.ImageDataRepository
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