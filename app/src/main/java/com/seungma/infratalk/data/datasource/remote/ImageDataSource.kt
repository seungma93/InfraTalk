package com.seungma.infratalk.data.datasource.remote

import com.seungma.infratalk.data.model.request.image.ImagesRequest
import com.seungma.infratalk.data.model.response.image.ImagesResponse

interface ImageDataSource {
    suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResponse
}