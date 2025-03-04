package com.sm.infratalk.data.datasource.remote.image

import com.sm.infratalk.data.model.request.image.ImagesRequest
import com.sm.infratalk.data.model.response.image.ImagesResponse

interface ImageDataSource {
    suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResponse
}