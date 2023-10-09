package com.freetalk.domain.repository

import com.freetalk.data.datasource.remote.ImageDataSource
import com.freetalk.data.mapper.toEntity
import com.freetalk.data.model.request.ImagesRequest
import com.freetalk.domain.entity.ImagesResultEntity
import javax.inject.Inject

class ImageDataRepositoryImpl @Inject constructor(private val dataSource: ImageDataSource):
    ImageDataRepository {
    override suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResultEntity {
        return dataSource.uploadImages(imagesRequest).toEntity()
    }

    override suspend fun deleteImage() {
        TODO("Not yet implemented")
    }
}