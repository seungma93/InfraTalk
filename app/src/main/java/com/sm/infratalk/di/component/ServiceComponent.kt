package com.sm.infratalk.di.component

import com.sm.infratalk.di.module.ServiceViewModelModule
import com.sm.infratalk.presenter.service.ForegroundService
import dagger.Component

@Component(
    modules = [
        ServiceViewModelModule::class
    ]
)


interface ServiceComponent {
    fun inject(service: ForegroundService)
} 