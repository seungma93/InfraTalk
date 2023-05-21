package com.freetalk.usecase

import android.util.Log
import com.freetalk.data.entity.BoardListEntity
import com.freetalk.data.remote.BoardSelectForm
import com.freetalk.repository.BoardDataRepository
import javax.inject.Inject


class PrintBoardListUesCase @Inject constructor(private val repository: BoardDataRepository) {
    suspend operator fun invoke (boardSelectForm: BoardSelectForm): BoardListEntity {
        Log.d("SelectContentsUseCase", "유즈케이스")
        return repository.select(boardSelectForm)
    }

}