package com.sm.infratalk.di.component

import android.content.Context
import com.sm.infratalk.di.module.Modules
import com.sm.infratalk.presenter.main.fragment.MainFragment
import com.sm.infratalk.presenter.service.ForegroundService
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        Modules.ServiceViewModelModule::class
    ]
)


interface ServiceComponent {
    fun inject(fragment: MainFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): ServiceComponent
    }
} 