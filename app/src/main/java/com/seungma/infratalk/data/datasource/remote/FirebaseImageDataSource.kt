package com.seungma.infratalk.data.datasource.remote

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.seungma.infratalk.data.model.request.image.ImagesRequest
import com.seungma.infratalk.data.model.response.image.ImagesResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

interface ImageDataSource {
    suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResponse
}

class FirebaseImageRemoteDataSourceImpl @Inject constructor(
    private val storage: FirebaseStorage
) : ImageDataSource {
    private val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

    override suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResponse =
        coroutineScope {
            val successImages = mutableListOf<Uri>()
            val failImages = mutableListOf<Uri>()

            imagesRequest.imageUris.mapIndexed { i, uri ->
                val imgFileName = "Image" + "_" + (i + 1) + "_" + timeStamp + "_.png"
                async {
                    uploadImage(imgFileName, uri!!)
                        ?.let { successImages.add(it) }
                        ?: failImages.add(uri)
                }
            }.awaitAll()
            ImagesResponse(successImages, failImages)
        }

    private suspend fun uploadImage(fileName: String, uri: Uri): Uri? {
        return kotlin.runCatching {
            val storageRef = storage.reference.child("images").child(fileName)
            val res = storageRef.putFile(uri).await()
            val downloadUri = res.storage.downloadUrl.await()
            downloadUri
        }.getOrNull()
    }
}


