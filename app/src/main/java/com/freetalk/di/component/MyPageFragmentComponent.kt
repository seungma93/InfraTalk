package com.freetalk.di.component

import android.content.Context
import com.freetalk.di.module.Modules
import com.freetalk.presenter.fragment.board.BoardContentFragment
import com.freetalk.presenter.fragment.board.BoardFragment
import com.freetalk.presenter.fragment.board.BoardWriteFragment
import com.freetalk.presenter.fragment.chat.ChatFragment
import com.freetalk.presenter.fragment.chat.ChatRoomFragment
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
        // Repository
        Modules.UserDataRepositoryModule::class,
        // UseCase
//        Modules.SendChatMessageUseCaseModule::class,
//        Modules.LoadChatMessageListUseCaseModule::class,
//        Modules.LoadRealTimeChatMessageUseCaseModule::class,
        // ViewModel
        Modules.MyPageViewModelModule::class,
        //Modules.ChatRoomViewModelModule::class,
        Modules.ViewModelFactoryModule::class
    ]
)


interface MyPageFragmentComponent {
    fun inject(fragment: MyPageFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): MyPageFragmentComponent
    }
}

