package com.freetalk.di.component

import android.content.Context
import com.freetalk.di.module.Modules
import com.freetalk.presenter.fragment.board.BoardContentFragment
import com.freetalk.presenter.fragment.board.BoardFragment
import com.freetalk.presenter.fragment.board.BoardWriteFragment
import com.freetalk.presenter.fragment.chat.ChatFragment
import com.freetalk.presenter.fragment.chat.ChatRoomFragment
import com.freetalk.presenter.fragment.mypage.MyAccountInfoEditFragment
import com.freetalk.presenter.fragment.mypage.MyBoardFragment
import com.freetalk.presenter.fragment.mypage.MyBookmarkBoardFragment
import com.freetalk.presenter.fragment.mypage.MyBookmarkCommentFragment
import com.freetalk.presenter.fragment.mypage.MyCommentFragment
import com.freetalk.presenter.fragment.mypage.MyLikeBoardFragment
import com.freetalk.presenter.fragment.mypage.MyLikeCommentFragment
import com.freetalk.presenter.fragment.mypage.MyPageFragment
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        // Firebase
        Modules.FirebaseFirestoreModule::class,
        Modules.FirebaseStorageModule::class,
        Modules.FirebaseAuthModule::class,
        // DataSource
        Modules.FirebaseUserDataSourceModule::class,
        Modules.FirebaseBoardDataSourceModule::class,
        Modules.FirebaseBookmarkDataSourceModule::class,
        Modules.FirebaseLikeDataSourceModule::class,
        Modules.FirebaseCommentDataSourceModule::class,
        Modules.FirebaseImageDataSourceModule::class,
        // Repository
        Modules.UserDataRepositoryModule::class,
        Modules.BoardDataRepositoryModule::class,
        Modules.BookmarkDataRepositoryModule::class,
        Modules.LikeDataRepositoryModule::class,
        Modules.CommentDataRepositoryModule::class,
        Modules.ImageDataRepositoryModule::class,
        // UseCase
//        Modules.SendChatMessageUseCaseModule::class,
//        Modules.LoadChatMessageListUseCaseModule::class,
//        Modules.LoadRealTimeChatMessageUseCaseModule::class,
        Modules.UploadImagesUseCaseModule::class,
        // ViewModel
        Modules.MyPageViewModelModule::class,
        Modules.MyBoardViewModelModule::class,
        Modules.MyCommentViewModelModule::class,
        Modules.MyBookmarkBoardViewModelModule::class,
        Modules.MyLikeBoardViewModelModule::class,
        Modules.MyBookmarkCommentViewModelModule::class,
        Modules.MyLikeCommentViewModelModule::class,
        Modules.ViewModelFactoryModule::class
    ]
)


interface MyPageFragmentComponent {
    fun inject(fragment: MyPageFragment)
    fun inject(fragment: MyBoardFragment)
    fun inject(fragment: MyCommentFragment)
    fun inject(fragment: MyBookmarkBoardFragment)
    fun inject(fragment: MyLikeBoardFragment)
    fun inject(fragment: MyBookmarkCommentFragment)
    fun inject(fragment: MyLikeCommentFragment)
    fun inject(fragment: MyAccountInfoEditFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): MyPageFragmentComponent
    }
}

