package com.freetalk.usecase

import android.util.Log
import com.freetalk.data.entity.BoardListEntity
import com.freetalk.data.entity.UserEntity
import com.freetalk.repository.BoardDataRepository
import com.freetalk.repository.UserDataRepository
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject


interface SelectContentsUseCase {
    suspend fun selectContents(lastDocumentSnapshot: DocumentSnapshot?): BoardListEntity
}

class SelectContentsUseCaseImpl @Inject constructor(private val repository: BoardDataRepository): SelectContentsUseCase {
    override suspend fun selectContents(lastDocumentSnapshot: DocumentSnapshot?): BoardListEntity {
        Log.d("SelectContentsUseCase", "유즈케이스")
        return repository.select(lastDocumentSnapshot)
    }

}