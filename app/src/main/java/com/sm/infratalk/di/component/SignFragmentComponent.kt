package com.sm.infratalk.di.component

import android.content.Context
import com.sm.infratalk.di.module.Modules
import com.sm.infratalk.presenter.sign.fragment.LoginMainFragment
import com.sm.infratalk.presenter.sign.fragment.ResetPasswordFragment
import com.sm.infratalk.presenter.sign.fragment.SignUpFragment
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        Modules.FirebaseAuthModule::class,
        Modules.FirebaseFirestoreModule::class,
        Modules.FirebaseStorageModule::class,
        Modules.PreferenceModule::class,
        // Retrofit
        Modules.RetrofitClientModule::class,
        Modules.FirebaseUserDataSourceModule::class,
        Modules.FirebaseImageDataSourceModule::class,
        Modules.UserDataRepositoryModule::class,
        Modules.ImageDataRepositoryModule::class,
        Modules.UploadImagesUseCaseModule::class,
        Modules.UpdateUserInfoUseCaseModule::class,
        Modules.SignUpUseCaseModule::class,
        Modules.SendEmailUseCaseModule::class,
        Modules.LoginUseCaseModule::class,
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

