package com.freetalk.di.component

import android.content.Context
import com.freetalk.di.module.Modules
import dagger.BindsInstance
import com.freetalk.presenter.fragment.Sign.LoginMainFragment;
import com.freetalk.presenter.fragment.Sign.ResetPasswordFragment;
import com.freetalk.presenter.fragment.Sign.SignUpFragment;
import dagger.Component;

@Component(
    modules = [
        Modules.FirebaseAuthModule::class,
        Modules.FirebaseFirestoreModule::class,
        Modules.FirebaseStorageModule::class,
        Modules.FirebaseUserDataSourceModule::class,
        Modules.FirebaseImageDataSourceModule::class,
        Modules.UserDataRepositoryModule::class,
        Modules.ImageDataRepositoryModule::class,
        Modules.UploadImagesUseCaseModule::class,
        Modules.UpdateUserInfoUseCaseModule::class,
        Modules.SignUpUseCaseModule::class,
        Modules.SendEmailUseCaseModule::class,
        Modules.UpdateProfileImageUseCaseModule::class,
        Modules.LogInUseCaseModule::class,
        Modules.ResetPasswordUseCaseModule::class,
        Modules.DeleteUserInfoUseCaseModule::class,
        Modules.SignViewModelModule::class,
        Modules.ViewModelFactoryModule::class
    ]
)


interface SignFragmentComponent {
    fun inject(fragment: LoginMainFragment)
    fun inject(fragment: ResetPasswordFragment)
    fun inject(fragment: SignUpFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): SignFragmentComponent
    }
}

