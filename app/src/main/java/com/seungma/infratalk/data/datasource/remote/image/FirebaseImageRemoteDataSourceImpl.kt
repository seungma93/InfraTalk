package com.seungma.infratalk.data.datasource.remote.image

import android.net.Uri
import android.util.Log
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

class FirebaseImageRemoteDataSourceImpl @Inject constructor(
    private val storage: FirebaseStorage
) : ImageDataSource {
    private val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

    override suspend fun uploadImages(imagesRequest: ImagesRequest): ImagesResponse =
        coroutineScope {
            Log.d("seungma", "ImageDataSource.uploadImages")
            val successImages = mutableListOf<Uri>()
            val failImages = mutableListOf<Uri>()
            Log.d("seungma","이미지데이터소스 이미지요청 갯수" + imagesRequest.imageUris.size)
            kotlin.runCatching {
                imagesRequest.imageUris.mapIndexed { i, uri ->
                    val imgFileName = "Image" + "_" + (i + 1) + "_" + timeStamp + "_.png"
                    async {
                        uploadImage(imgFileName, uri!!)?.let { successImages.add(it) }
                            ?: run { failImages.add(uri) }
                    }
                }.awaitAll()
                Log.d("seungma", " 업로드이미지 데이터 소스 끝")
                ImagesResponse(successImages, failImages)
            }.onFailure {
                Log.d("seungma", "이미지 데이터소스" + it.message)
            }.getOrThrow()
        }

    private suspend fun uploadImage(fileName: String, uri: Uri): Uri? {
        return kotlin.runCatching {
            val storageRef = storage.reference.child("images").child(fileName)
            storageRef.putFile(uri).await()
            val downloadUri = storageRef.downloadUrl.await()
            Log.d("seungma", "이미지 데이터소스 스토리지" + downloadUri.toString())
            downloadUri
        }.onFailure {
            Log.d("seungma", "이미지 데이터소스 스토리지" + it.message)
        }.getOrNull()
    }
}


