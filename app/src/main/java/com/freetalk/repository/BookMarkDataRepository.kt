package com.freetalk.repository

import com.freetalk.data.entity.BookMarkEntity
import com.freetalk.data.entity.LikeCountEntity
import com.freetalk.data.entity.LikeEntity
import com.freetalk.data.entity.toEntity
import com.freetalk.data.remote.*
import javax.inject.Inject

interface BookMarkDataRepository {
    suspend fun insertBookMark(insertBookMarkRequest: InsertBookMarkRequest): BookMarkEntity
    suspend fun deleteBookMark(deleteBookMarkRequest: DeleteBookMarkRequest): BookMarkEntity
    suspend fun selectBookMark(bookMarkSelectForm: BookMarkSelectForm): BookMarkEntity
}

class BookMarkDataRepositoryImpl @Inject constructor(private val dataSource: BookMarkDataSource): BookMarkDataRepository {
    override suspend fun insertBookMark(insertBookMarkRequest: InsertBookMarkRequest): BookMarkEntity {
        return dataSource.insertBookMark(insertBookMarkRequest).toEntity()
    }

    override suspend fun deleteBookMark(deleteBookMarkRequest: DeleteBookMarkRequest): BookMarkEntity {
        return dataSource.deleteBookMark(deleteBookMarkRequest).toEntity()
    }


    override suspend fun selectBookMark(bookMarkSelectForm: BookMarkSelectForm): BookMarkEntity {
        return dataSource.selectBookMark(bookMarkSelectForm).toEntity()
    }

}