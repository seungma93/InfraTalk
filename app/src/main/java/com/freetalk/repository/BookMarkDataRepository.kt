package com.freetalk.repository

import com.freetalk.data.entity.BookMarkEntity
import com.freetalk.data.entity.LikeCountEntity
import com.freetalk.data.entity.LikeEntity
import com.freetalk.data.entity.toEntity
import com.freetalk.data.remote.*
import javax.inject.Inject

interface BookMarkDataRepository {
    suspend fun updateBookMark(bookMarkUpdateForm: BookMarkUpdateForm): BookMarkEntity
    suspend fun selectBookMark(bookMarkSelectForm: BookMarkSelectForm): BookMarkEntity
}

class BookMarkDataRepositoryImpl @Inject constructor(private val dataSource: BookMarkDataSource): BookMarkDataRepository {
    override suspend fun updateBookMark(bookMarkUpdateForm: BookMarkUpdateForm): BookMarkEntity {
        return dataSource.updateBookMark(bookMarkUpdateForm).toEntity()
    }

    override suspend fun selectBookMark(bookMarkSelectForm: BookMarkSelectForm): BookMarkEntity {
        return dataSource.selectBookMark(bookMarkSelectForm).toEntity()
    }

}