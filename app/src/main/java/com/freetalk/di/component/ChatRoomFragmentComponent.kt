package com.freetalk.di.component

import android.content.Context
import com.freetalk.di.module.Modules
import com.freetalk.presenter.fragment.board.BoardContentFragment
import com.freetalk.presenter.fragment.board.BoardFragment
import com.freetalk.presenter.fragment.board.BoardWriteFragment
import com.freetalk.presenter.fragment.chat.ChatFragment
import com.freetalk.presenter.fragment.chat.ChatRoomFragment
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
        Modules.FirebaseChatDataSourceModule::class,
        // Repository
        Modules.ChatDataRepositoryModule::class,
        Modules.UserDataRepositoryModule::class,
        // UseCase
//        Modules.SendChatMessageUseCaseModule::class,
//        Modules.LoadChatMessageListUseCaseModule::class,
//        Modules.LoadRealTimeChatMessageUseCaseModule::class,
        //Modules.GetUserInfoUseCaseModule::class,
        // ViewModel
        //Modules.ChatViewModelModule::class,
        Modules.ChatRoomViewModelModule::class,
        Modules.ViewModelFactoryModule::class
    ]
)


interface ChatRoomFragmentComponent {
    fun inject(fragment: ChatRoomFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): ChatRoomFragmentComponent
    }
}

