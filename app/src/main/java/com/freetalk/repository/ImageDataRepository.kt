package com.freetalk.repository

import com.freetalk.data.entity.ImagesResultEntity
import com.freetalk.data.entity.toEntity
import com.freetalk.data.remote.ImageDataSource
import com.freetalk.data.remote.ImagesRequest
import com.freetalk.data.remote.ImagesResponse
import javax.inject.Inject


interface ImageDataRepository {
    suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResultEntity
    suspend fun deleteImage()
}

class ImageDataRepositoryImpl @Inject constructor(private val dataSource: ImageDataSource): ImageDataRepository {
    override suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResultEntity {
        return dataSource.uploadImages(imagesRequest).toEntity()
    }

    override suspend fun deleteImage() {
        TODO("Not yet implemented")
    }
}