package com.seungma.domain.repository

import com.seungma.infratalk.data.datasource.remote.image.ImageDataSource
import com.seungma.infratalk.data.mapper.toEntity
import com.seungma.infratalk.data.model.request.image.ImagesRequest
import com.seungma.infratalk.domain.image.repository.ImageDataRepository
import com.seungma.infratalk.domain.image.entity.ImagesResultEntity
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