package com.freetalk.repository

import com.freetalk.data.entity.ImagesResultEntity
import com.freetalk.data.entity.LikeCountEntity
import com.freetalk.data.entity.LikeEntity
import com.freetalk.data.entity.toEntity
import com.freetalk.data.remote.*
import javax.inject.Inject

interface LikeDataRepository {
    suspend fun insertLike(insertLikeRequest: InsertLikeRequest): LikeEntity
    suspend fun deleteLike(deleteLikeRequest: DeleteLikeRequest): LikeEntity
    suspend fun selectLike(likeSelectForm: LikeSelectForm): LikeEntity
    suspend fun selectLikeCount(likeCountSelectForm: LikeCountSelectForm): LikeCountEntity
}

class LikeDataRepositoryImpl @Inject constructor(private val dataSource: LikeDataSource): LikeDataRepository {

    override suspend fun insertLike(insertLikeRequest: InsertLikeRequest): LikeEntity {
        return dataSource.insertLike(insertLikeRequest).toEntity()
    }

    override suspend fun deleteLike(deleteLikeRequest: DeleteLikeRequest): LikeEntity {
        return dataSource.deleteLike(deleteLikeRequest).toEntity()
    }

    override suspend fun selectLike(likeSelectForm: LikeSelectForm): LikeEntity {
        return dataSource.selectLike(likeSelectForm).toEntity()
    }

    override suspend fun selectLikeCount(likeCountSelectForm: LikeCountSelectForm): LikeCountEntity {
        return dataSource.selectLikeCount(likeCountSelectForm).toEntity()
    }

}