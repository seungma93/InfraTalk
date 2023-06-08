package com.freetalk.di.component

import android.content.Context
import com.freetalk.di.module.Modules
import com.freetalk.presenter.fragment.board.BoardFragment
import com.freetalk.presenter.fragment.board.BoardWriteFragment
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        Modules.FirebaseFirestoreModule::class,
        Modules.FirebaseStorageModule::class,
        Modules.FirebaseAuthModule::class,
        Modules.FirebaseBoardDataSourceModule::class,
        Modules.FirebaseImageDataSourceModule::class,
        Modules.FirebaseUserDataSourceModule::class,
        Modules.FirebaseLikeDataSourceModule::class,
        Modules.BoardDataRepositoryModule::class,
        Modules.UserDataRepositoryModule::class,
        Modules.ImageDataRepositoryModule::class,
        Modules.LikeDataRepositoryModule::class,
        Modules.WriteContentUseCaseModule::class,
        Modules.UploadImagesUseCaseModule::class,
        Modules.UpdateContentUseCaseModule::class,
        Modules.UpdateImageContentUseCaseModule::class,
        Modules.PrintBoardListUesCaseModule::class,
        Modules.UpdateBookMarkBoardUseCaseModule::class,
        Modules.UpdateLikeBoardUseCaseModule::class,
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

