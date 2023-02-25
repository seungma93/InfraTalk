package com.freetalk.presenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.freetalk.usecase.BoardUseCase
import com.freetalk.usecase.UserUseCase


class LoginViewModelFactory(private val useCase: UserUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return LoginViewModel(useCase) as T
    }
}

class BoardViewModelFactory(private val useCase: BoardUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return BoardViewModel(useCase) as T
    }
}
