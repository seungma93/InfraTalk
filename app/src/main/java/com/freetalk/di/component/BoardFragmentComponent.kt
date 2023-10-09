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
        // Firebase
        Modules.FirebaseFirestoreModule::class,
        Modules.FirebaseStorageModule::class,
        Modules.FirebaseAuthModule::class,
        // DataSource
        Modules.FirebaseBoardDataSourceModule::class,
        Modules.FirebaseImageDataSourceModule::class,
        Modules.FirebaseUserDataSourceModule::class,
        Modules.FirebaseLikeDataSourceModule::class,
        Modules.FirebaseBookmarkDataSourceModule::class,
        Modules.FirebaseCommentDataSourceModule::class,
        Modules.FirebaseChatDataSourceModule::class,
        // Repository
        Modules.BoardDataRepositoryModule::class,
        Modules.UserDataRepositoryModule::class,
        Modules.ImageDataRepositoryModule::class,
        Modules.LikeDataRepositoryModule::class,
        Modules.BookmarkDataRepositoryModule::class,
        Modules.CommentDataRepositoryModule::class,
        Modules.ChatDataRepositoryModule::class,
        // UseCase
        Modules.WriteContentUseCaseModule::class,
        Modules.UploadImagesUseCaseModule::class,
        Modules.UpdateBoardContentUseCaseModule::class,
        Modules.UpdateBoardContentImagesUseCaseModule::class,
        Modules.LoadBoardListUseCaseModule::class,
        Modules.AddBoardBookmarkUseCaseModule::class,
        Modules.DeleteBoardBookmarkUseCaseModule::class,
        Modules.AddBoardLikeUseCaseModule::class,
        Modules.DeleteBoardLikeUseCaseModule::class,
        Modules.AddBoardContentBookmarkUseCaseModule::class,
        Modules.DeleteBoardContentBookmarkUseCaseModule::class,
        Modules.AddBoardContentLikeUseCaseModule::class,
        Modules.DeleteBoardContentLikeUseCaseModule::class,
        Modules.LoadBoardContentUseCaseModule::class,
        Modules.BoardContentViewModelModule::class,
        Modules.WriteCommentUesCaseModule::class,
        Modules.DeleteCommentUseCaseModule::class,
        Modules.CreateChatRoomUseCaseModule::class,
        Modules.GetUserInfoUseCaseModule::class,
        // ViewModel
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

