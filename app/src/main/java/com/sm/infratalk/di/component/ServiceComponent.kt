package com.sm.infratalk.di.component

import android.content.Context
import com.sm.infratalk.di.module.Modules
import com.sm.infratalk.presenter.service.ForegroundService
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        // Firebase
        Modules.FirebaseAuthModule::class,
        Modules.FirebaseFirestoreModule::class,
        Modules.FirebaseStorageModule::class,

        // Preference & Network
        Modules.PreferenceModule::class,
        Modules.RetrofitClientModule::class,

        // DataSource
        Modules.FirebaseUserDataSourceModule::class,
        Modules.FirebaseChatDataSourceModule::class,
        
        // Repository
        Modules.ChatDataRepositoryModule::class,
        Modules.UserDataRepositoryModule::class,

        // ViewModel
        Modules.ServiceViewModelModule::class,
        Modules.ViewModelFactoryModule::class
    ]
)


interface ServiceComponent {
    fun inject(service: ForegroundService)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): ServiceComponent
    }
} 