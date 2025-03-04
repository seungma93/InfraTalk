package com.sm.domain.repository

import com.sm.infratalk.data.datasource.remote.image.ImageDataSource
import com.sm.infratalk.data.mapper.toEntity
import com.sm.infratalk.data.model.request.image.ImagesRequest
import com.sm.infratalk.domain.image.repository.ImageDataRepository
import com.sm.infratalk.domain.image.entity.ImagesResultEntity
import javax.inject.Inject

class ImageDataRepositoryImpl @Inject constructor(private val dataSource: ImageDataSource) :
    ImageDataRepository {
    override suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResultEntity {
        return dataSource.uploadImages(imagesRequest).toEntity()
    }

    override suspend fun deleteImage() {
        TODO("Not yet implemented")
    }
}