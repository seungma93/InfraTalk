package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freetalk.data.entity.UserEntity
import com.freetalk.repository.SignUpInfo
import com.freetalk.repository.UserDataRepository
import com.freetalk.usecase.UserUseCase
import kotlinx.coroutines.launch

class LoginViewModel(private val useCase: UserUseCase): ViewModel() {

    suspend fun signUp(userData: UserEntity) {
        kotlin.runCatching {
            useCase.signUp(userData)
        }.onFailure {
            when(it) {
                is IllegalStateException -> Log.v("SignUpFragment", "회원가입 실패")
            }
        }
    }
}