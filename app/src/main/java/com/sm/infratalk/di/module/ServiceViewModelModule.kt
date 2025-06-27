package com.sm.infratalk.di.module

import com.sm.infratalk.domain.chat.usecase.NotifyChatMessageUseCase
import com.sm.infratalk.presenter.service.ServiceViewModel
import dagger.Module
import dagger.Provides

@Module
class ServiceViewModelModule {
    @Provides
    fun provideServiceViewModelFactory(
        notifyChatMessageUseCase: NotifyChatMessageUseCase
    ): ServiceViewModel.Factory {
        return ServiceViewModel.Factory(notifyChatMessageUseCase)
    }
} 