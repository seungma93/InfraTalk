package com.freetalk.data.remote

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

interface ImageDataSource {
    suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResponse
}

data class ImagesRequest(
    val imageUris: List<Uri>
)

data class ImagesResponse(
    val successUris: List<Uri>,
    val failUris: List<Uri>
)

class FirebaseImageRemoteDataSourceImpl @Inject constructor(
    private val storage: FirebaseStorage
) : ImageDataSource {
    private val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

    override suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResponse = coroutineScope {
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


