package com.seungma.infratalk.domain.image

import android.util.Log
import com.seungma.infratalk.data.model.request.image.ImagesRequest
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