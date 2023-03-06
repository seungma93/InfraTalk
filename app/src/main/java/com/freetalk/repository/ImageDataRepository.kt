package com.freetalk.repository

import com.freetalk.data.entity.ImagesEntity
import com.freetalk.data.entity.toEntity
import com.freetalk.data.remote.ImageDataSource
import com.freetalk.data.remote.ImagesRequest
import com.freetalk.data.remote.ImagesResponse


interface ImageDataRepository {
    suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesEntity
    suspend fun deleteImage()
}

class FirebaseImageDataRepositoryImpl(private val dataSource: ImageDataSource): ImageDataRepository {
    override suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesEntity {
        return dataSource.uploadImages(imagesRequest).toEntity()
    }

    override suspend fun deleteImage() {
        TODO("Not yet implemented")
    }
}