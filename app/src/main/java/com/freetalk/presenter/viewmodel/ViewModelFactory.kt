package com.freetalk.presenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.freetalk.usecase.*


class SignViewModelFactory(
    private val signUpUseCase: SignUpUseCase, private val sendEmailUseCase: SendEmailUseCase,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase,
    private val logInUseCase: LogInUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignViewModel(
            signUpUseCase,
            sendEmailUseCase,
            updateProfileImageUseCase,
            logInUseCase,
            resetPasswordUseCase
        ) as T
    }
}

class BoardViewModelFactory(private val useCase: BoardUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return BoardViewModel(useCase) as T
    }
}
