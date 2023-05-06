package com.freetalk.di.component

import android.content.Context
import com.freetalk.di.module.Modules
import com.freetalk.presenter.fragment.board.BoardFragment
import com.freetalk.presenter.fragment.board.BoardWriteFragment
import com.freetalk.usecase.SelectContentsUseCase
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        Modules.FirebaseFirestoreModule::class,
        Modules.FirebaseStorageModule::class,
        Modules.FirebaseBoardDataSourceModule::class,
        Modules.FirebaseImageDataSourceModule::class,
        Modules.BoardDataRepositoryModule::class,
        Modules.ImageDataRepositoryModule::class,
        Modules.WriteContentUseCaseModule::class,
        Modules.UploadImagesUseCaseModule::class,
        Modules.UpdateContentUseCaseModule::class,
        Modules.UpdateImageContentUseCaseModule::class,
        Modules.SelectContentsUseCaseModule::class,
        Modules.BoardViewModelModule::class,
        Modules.ViewModelFactoryModule::class
    ]
)


interface BoardFragmentComponent {
    fun inject(fragment: BoardFragment)
    fun inject(fragment: BoardWriteFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): BoardFragmentComponent
    }
}

