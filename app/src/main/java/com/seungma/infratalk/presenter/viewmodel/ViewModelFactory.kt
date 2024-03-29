package com.seungma.infratalk.presenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider


class ViewModelFactory @Inject constructor(
    val viewModelMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModelMap[modelClass]?.get() as T
    }
}

/*
class SignViewModelFactory(
    private val signUpUseCase: SignUpUseCase, private val sendEmailUseCase: SendEmailUseCase,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase,
    private val logInUseCase: LogInUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val deleteUserInfoUseCase: DeleteUserInfoUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignViewModel(
            signUpUseCase,
            sendEmailUseCase,
            updateProfileImageUseCase,
            logInUseCase,
            resetPasswordUseCase,
            deleteUserInfoUseCase
        ) as T
    }
}

class BoardViewModelFactory(
    private val writeContentUseCase: WriteContentUseCase,
    private val updateImageContentUseCase: UpdateImageContentUseCase,
    private val printBoardListUesCase: PrintBoardListUesCase,
    private val updateBookMarkedBoardListUseCase: UpdateBookMarkedBoardListUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return BoardViewModel(writeContentUseCase, updateImageContentUseCase, printBoardListUesCase, updateBookMarkedBoardListUseCase) as T
    }


}

 */


