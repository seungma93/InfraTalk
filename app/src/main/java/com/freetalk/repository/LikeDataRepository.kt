package com.freetalk.repository

import com.freetalk.data.entity.ImagesResultEntity
import com.freetalk.data.entity.LikeCountEntity
import com.freetalk.data.entity.LikeEntity
import com.freetalk.data.entity.toEntity
import com.freetalk.data.remote.*
import javax.inject.Inject

interface LikeDataRepository {
    suspend fun updateLike(likeUpdateForm: LikeUpdateForm): LikeEntity
    suspend fun selectLike(likeSelectForm: LikeSelectForm): LikeEntity
    suspend fun selectLikeCount(likeCountSelectForm: LikeCountSelectForm): LikeCountEntity
}

class LikeDataRepositoryImpl @Inject constructor(private val dataSource: LikeDataSource): LikeDataRepository {
    override suspend fun updateLike(likeUpdateForm: LikeUpdateForm): LikeEntity {
        return dataSource.updateLike(likeUpdateForm).toEntity()
    }

    override suspend fun selectLike(likeSelectForm: LikeSelectForm): LikeEntity {
        return dataSource.selectLike(likeSelectForm).toEntity()
    }

    override suspend fun selectLikeCount(likeCountSelectForm: LikeCountSelectForm): LikeCountEntity {
        return dataSource.selectLikeCount(likeCountSelectForm).toEntity()
    }

}