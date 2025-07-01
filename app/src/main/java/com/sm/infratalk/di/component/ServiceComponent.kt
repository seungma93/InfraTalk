package com.sm.infratalk.di.component

import com.sm.infratalk.di.module.Modules
import com.sm.infratalk.presenter.service.ForegroundService
import dagger.Component

@Component(
    modules = [
        Modules.ServiceViewModelModule::class
    ]
)


interface ServiceComponent {
    fun inject(service: ForegroundService)
} 