package com.freetalk.di.component

import android.content.Context
import com.freetalk.di.module.Modules
import com.freetalk.presenter.fragment.board.BoardContentFragment
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
        Modules.FirebaseBookMarkDataSourceModule::class,
        Modules.BoardDataRepositoryModule::class,
        Modules.UserDataRepositoryModule::class,
        Modules.ImageDataRepositoryModule::class,
        Modules.LikeDataRepositoryModule::class,
        Modules.BookMarkDataRepositoryModule::class,
        Modules.WriteContentUseCaseModule::class,
        Modules.UploadImagesUseCaseModule::class,
        Modules.UpdateContentUseCaseModule::class,
        Modules.UpdateImageContentUseCaseModule::class,
        Modules.PrintBoardListUesCaseModule::class,
        Modules.UpdateBookMarkBoardUseCaseModule::class,
        Modules.UpdateLikeBoardUseCaseModule::class,
        Modules.UpdateBookMarkBoardContentUseCaseModule::class,
        Modules.SelectBoardContentUseCaseModule::class,
        Modules.BoardContentViewModelModule::class,
        Modules.UpdateLikeBoardContentUseCaseModule::class,
        Modules.BoardViewModelModule::class,
        Modules.ViewModelFactoryModule::class
    ]
)


interface BoardFragmentComponent {
    fun inject(fragment: BoardFragment)
    fun inject(fragment: BoardWriteFragment)
    fun inject(fragment: BoardContentFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): BoardFragmentComponent
    }
}

