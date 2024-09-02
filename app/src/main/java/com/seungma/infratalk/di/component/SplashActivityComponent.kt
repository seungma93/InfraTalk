package com.seungma.infratalk.di.component

import android.content.Context
import com.seungma.infratalk.di.module.Modules
import com.seungma.infratalk.presenter.main.activity.SplashActivity
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
        Modules.SplashViewModelModule::class,
        Modules.ViewModelFactoryModule::class
    ]
)


interface SplashActivityComponent {
    fun inject(activity: SplashActivity)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): SplashActivityComponent
    }
}

