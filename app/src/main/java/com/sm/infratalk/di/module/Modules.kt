package com.sm.infratalk.di.module

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sm.domain.repository.BoardDataRepositoryImpl
import com.sm.domain.repository.BookmarkDataRepositoryImpl
import com.sm.domain.repository.ChatDataRepositoryImpl
import com.sm.domain.repository.ImageDataRepositoryImpl
import com.sm.domain.repository.LikeDataRepositoryImpl
import com.sm.domain.repository.UserDataRepositoryImpl
import com.sm.infratalk.data.datasource.local.preference.PreferenceDataSource
import com.sm.infratalk.data.datasource.local.preference.PreferenceLocalDataSourceImpl
import com.sm.infratalk.data.datasource.remote.board.BoardDataSource
import com.sm.infratalk.data.datasource.remote.board.FirebaseBoardRemoteDataSourceImpl
import com.sm.infratalk.data.datasource.remote.bookmark.BookmarkDataSource
import com.sm.infratalk.data.datasource.remote.bookmark.FirebaseBookmarkRemoteDataSourceImpl
import com.sm.infratalk.data.datasource.remote.chat.ChatDataSource
import com.sm.infratalk.data.datasource.remote.chat.FirebaseChatRemoteDataSourceImpl
import com.sm.infratalk.data.datasource.remote.comment.CommentDataSource
import com.sm.infratalk.data.datasource.remote.comment.FirebaseCommentRemoteDataSourceImpl
import com.sm.infratalk.data.datasource.remote.image.FirebaseImageRemoteDataSourceImpl
import com.sm.infratalk.data.datasource.remote.image.ImageDataSource
import com.sm.infratalk.data.datasource.remote.like.FirebaseLikeRemoteDataSourceImpl
import com.sm.infratalk.data.datasource.remote.like.LikeDataSource
import com.sm.infratalk.data.datasource.remote.user.FirebaseUserRemoteDataSourceImpl
import com.sm.infratalk.data.datasource.remote.user.UserDataSource
import com.sm.infratalk.data.repository.CommentDataRepositoryImpl
import com.sm.infratalk.domain.board.repository.BoardDataRepository
import com.sm.infratalk.domain.board.repository.BookmarkDataRepository
import com.sm.infratalk.domain.board.repository.LikeDataRepository
import com.sm.infratalk.domain.board.usecase.AddBoardBookmarkUseCase
import com.sm.infratalk.domain.board.usecase.AddBoardContentBookmarkUseCase
import com.sm.infratalk.domain.board.usecase.AddBoardContentLikeUseCase
import com.sm.infratalk.domain.board.usecase.AddBoardLikeUseCase
import com.sm.infratalk.domain.board.usecase.DeleteBoardBookmarkUseCase
import com.sm.infratalk.domain.board.usecase.DeleteBoardContentBookmarkUseCase
import com.sm.infratalk.domain.board.usecase.DeleteBoardContentLikeUseCase
import com.sm.infratalk.domain.board.usecase.DeleteBoardLikeUseCase
import com.sm.infratalk.domain.board.usecase.DeleteBoardUseCase
import com.sm.infratalk.domain.board.usecase.LoadBoardContentUseCase
import com.sm.infratalk.domain.board.usecase.LoadBoardListUseCase
import com.sm.infratalk.domain.board.usecase.UpdateBoardContentImagesUseCase
import com.sm.infratalk.domain.board.usecase.UpdateBoardContentUseCase
import com.sm.infratalk.domain.board.usecase.WriteBoardContentUseCase
import com.sm.infratalk.domain.chat.repository.ChatDataRepository
import com.sm.infratalk.domain.chat.usecase.CheckChatRoomUseCase
import com.sm.infratalk.domain.chat.usecase.CreateChatRoomUseCase
import com.sm.infratalk.domain.chat.usecase.LeaveChatRoomUseCase
import com.sm.infratalk.domain.chat.usecase.LoadChatMessageListUseCase
import com.sm.infratalk.domain.chat.usecase.LoadChatRoomListUseCase
import com.sm.infratalk.domain.chat.usecase.LoadChatRoomUseCase
import com.sm.infratalk.domain.chat.usecase.LoadRealTimeChatMessageUseCase
import com.sm.infratalk.domain.chat.usecase.LoadRealTimeChatRoomListUseCase
import com.sm.infratalk.domain.chat.usecase.LoadRealTimeChatRoomUseCase
import com.sm.infratalk.domain.chat.usecase.SendChatMessageUseCase
import com.sm.infratalk.domain.comment.repository.CommentDataRepository
import com.sm.infratalk.domain.comment.usecase.AddCommentBookmarkUseCase
import com.sm.infratalk.domain.comment.usecase.AddCommentLikeUseCase
import com.sm.infratalk.domain.comment.usecase.DeleteCommentBookmarkUseCase
import com.sm.infratalk.domain.comment.usecase.DeleteCommentLikeUseCase
import com.sm.infratalk.domain.comment.usecase.DeleteCommentUseCase
import com.sm.infratalk.domain.comment.usecase.LoadBoardRelatedAllCommentListUseCase
import com.sm.infratalk.domain.comment.usecase.LoadCommentListUseCase
import com.sm.infratalk.domain.comment.usecase.WriteCommentUseCase
import com.sm.infratalk.domain.image.repository.ImageDataRepository
import com.sm.infratalk.domain.image.usecase.UploadImagesUseCase
import com.sm.infratalk.domain.image.usecase.UploadImagesUseCaseImpl
import com.sm.infratalk.domain.login.usecase.LoginUseCase
import com.sm.infratalk.domain.login.usecase.LogoutUseCase
import com.sm.infratalk.domain.login.usecase.ResetPasswordUseCase
import com.sm.infratalk.domain.mypage.usecase.LoadMyBoardListUseCase
import com.sm.infratalk.domain.mypage.usecase.LoadMyBookmarkBoardListUseCase
import com.sm.infratalk.domain.mypage.usecase.LoadMyBookmarkCommentListUseCase
import com.sm.infratalk.domain.mypage.usecase.LoadMyCommentListUseCase
import com.sm.infratalk.domain.mypage.usecase.LoadMyLikeBoardListUseCase
import com.sm.infratalk.domain.mypage.usecase.LoadMyLikeCommentListUseCase
import com.sm.infratalk.domain.mypage.usecase.UpdateUserInfoUseCase
import com.sm.infratalk.domain.signup.usecase.DeleteUserInfoUseCase
import com.sm.infratalk.domain.signup.usecase.SendEmailUseCase
import com.sm.infratalk.domain.signup.usecase.SignUpUseCase
import com.sm.infratalk.domain.user.repository.UserDataRepository
import com.sm.infratalk.domain.user.usecase.DeleteSavedEmailUseCase
import com.sm.infratalk.domain.user.usecase.GetSavedEmailUseCase
import com.sm.infratalk.domain.user.usecase.GetUserMeUseCase
import com.sm.infratalk.domain.user.usecase.SetSavedEmailUseCase
import com.sm.infratalk.presenter.board.viewmodel.BoardContentViewModel
import com.sm.infratalk.presenter.board.viewmodel.BoardViewModel
import com.sm.infratalk.presenter.chat.viewmodel.ChatRoomViewModel
import com.sm.infratalk.presenter.chat.viewmodel.ChatViewModel
import com.sm.infratalk.presenter.mypage.viewmodel.MyBoardViewModel
import com.sm.infratalk.presenter.mypage.viewmodel.MyBookmarkBoardViewModel
import com.sm.infratalk.presenter.mypage.viewmodel.MyBookmarkCommentViewModel
import com.sm.infratalk.presenter.mypage.viewmodel.MyCommentViewModel
import com.sm.infratalk.presenter.mypage.viewmodel.MyLikeBoardViewModel
import com.sm.infratalk.presenter.mypage.viewmodel.MyLikeCommentViewModel
import com.sm.infratalk.presenter.mypage.viewmodel.MyPageViewModel
import com.sm.infratalk.presenter.sign.viewmodel.SignViewModel
import com.sm.infratalk.presenter.sign.viewmodel.SplashViewModel
import com.sm.infratalk.presenter.viewmodel.ViewModelFactory
import com.sm.infratalk.presenter.viewmodel.ViewModelKey
import com.sm.infratalk.network.RetrofitClient
import com.sm.infratalk.presenter.service.ServiceViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

class Modules {

    // Firebase
    @Module
    class FirebaseAuthModule {
        @Provides
        fun providesFirebaseAuth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }
    }

    @Module
    class FirebaseFirestoreModule {
        @Provides
        fun providesFirebaseFirestore(): FirebaseFirestore {
            return FirebaseFirestore.getInstance()
        }
    }

    @Module
    class FirebaseStorageModule {
        @Provides
        fun providesFirebaseStorage(): FirebaseStorage {
            return FirebaseStorage.getInstance()
        }
    }

    @Module
    class PreferenceModule {
        @Provides
        fun providesPreferenceModule(context: Context): PreferenceDataSource {
            return PreferenceLocalDataSourceImpl(context)
        }
    }

    @Module
    class RetrofitClientModule {
        @Provides
        fun providesRetrofitClient(): RetrofitClient {
            return RetrofitClient
        }
    }

    // DataSource
    @Module
    class FirebaseBoardDataSourceModule {
        @Provides
        fun providesFirebaseBoardRemoteDataSource(
            database: FirebaseFirestore,
            userDataSource: UserDataSource
        ): BoardDataSource {
            return FirebaseBoardRemoteDataSourceImpl(
                database,
                userDataSource
            )
        }
    }

    @Module
    class FirebaseImageDataSourceModule {
        @Provides
        fun providesFirebaseImageRemoteDataSource(storage: FirebaseStorage): ImageDataSource {
            return FirebaseImageRemoteDataSourceImpl(storage)
        }
    }

    @Module
    class FirebaseUserDataSourceModule {
        @Provides
        fun providesFirebaseUserRemoteDataSource(
            auth: FirebaseAuth,
            database: FirebaseFirestore,
            preferenceDataSource: PreferenceDataSource,
            retrofitClient: RetrofitClient
        ): UserDataSource {
            return FirebaseUserRemoteDataSourceImpl(
                auth,
                database,
                preferenceDataSource,
                retrofitClient
            )
        }
    }

    @Module
    class FirebaseLikeDataSourceModule {
        @Provides
        fun providesFirebaseLikeRemoteDataSource(
            database: FirebaseFirestore,
            userDataSource: UserDataSource
        ): LikeDataSource {
            return FirebaseLikeRemoteDataSourceImpl(database, userDataSource)
        }
    }

    @Module
    class FirebaseBookmarkDataSourceModule {
        @Provides
        fun providesFirebaseBookmarkRemoteDataSource(
            database: FirebaseFirestore,
            userDataSource: UserDataSource
        ): BookmarkDataSource {
            return FirebaseBookmarkRemoteDataSourceImpl(database, userDataSource)
        }
    }

    @Module
    class FirebaseCommentDataSourceModule {
        @Provides
        fun providesFirebaseCommentRemoteDataSource(
            database: FirebaseFirestore,
            userDataSource: UserDataSource
        ): CommentDataSource {
            return FirebaseCommentRemoteDataSourceImpl(database, userDataSource)
        }
    }

    @Module
    class FirebaseChatDataSourceModule {
        @Provides
        fun providesFirebaseChatRemoteDataSource(
            database: FirebaseFirestore,
            userDataSource: UserDataSource
        ): ChatDataSource {
            return FirebaseChatRemoteDataSourceImpl(database, userDataSource)
        }
    }

    // Repository
    @Module
    class BoardDataRepositoryModule {
        @Provides
        fun providesBoardDataRepository(
            dataSource: BoardDataSource,
            userDataRepository: UserDataRepository
        ): BoardDataRepository {
            return BoardDataRepositoryImpl(dataSource, userDataRepository)
        }
    }

    @Module
    class ImageDataRepositoryModule {
        @Provides
        fun providesImageDataRepository(dataSource: ImageDataSource): ImageDataRepository {
            return ImageDataRepositoryImpl(dataSource)
        }
    }

    @Module
    class UserDataRepositoryModule {
        @Provides
        fun providesUserDataRepository(dataSource: UserDataSource, preferenceDataSource: PreferenceDataSource): UserDataRepository {
            return UserDataRepositoryImpl(dataSource, preferenceDataSource)
        }
    }

    @Module
    class LikeDataRepositoryModule {
        @Provides
        fun providesLikeDataRepository(
            dataSource: LikeDataSource,
            userDataRepository: UserDataRepository
        ): LikeDataRepository {
            return LikeDataRepositoryImpl(dataSource, userDataRepository)
        }
    }

    @Module
    class BookmarkDataRepositoryModule {
        @Provides
        fun providesBookmarkDataRepository(
            dataSource: BookmarkDataSource,
            userDataRepository: UserDataRepository
        ): BookmarkDataRepository {
            return BookmarkDataRepositoryImpl(dataSource, userDataRepository)
        }
    }

    @Module
    class CommentDataRepositoryModule {
        @Provides
        fun providesCommentDataRepository(
            commentDataSource: CommentDataSource,
            userDataRepository: UserDataRepository
        ): CommentDataRepository {
            return CommentDataRepositoryImpl(commentDataSource, userDataRepository)
        }
    }

    @Module
    class ChatDataRepositoryModule {
        @Provides
        fun providesChatDataRepository(
            chatDataSource: ChatDataSource,
            userDataSource: UserDataSource
        ): ChatDataRepository {
            return ChatDataRepositoryImpl(chatDataSource, userDataSource)
        }
    }

    // UseCase
    @Module
    class LoginUseCaseModule {
        @Provides
        fun providesLogInUseCase(repository: UserDataRepository): LoginUseCase {
            return LoginUseCase(repository)
        }
    }

    @Module
    class ResetPasswordUseCaseModule {
        @Provides
        fun providesResetPasswordUseCase(userDataRepository: UserDataRepository): ResetPasswordUseCase {
            return ResetPasswordUseCase(userDataRepository)
        }
    }

    @Module
    class SendEmailUseCaseModule {
        @Provides
        fun providesSendEmailUseCase(userDataRepository: UserDataRepository): SendEmailUseCase {
            return SendEmailUseCase(userDataRepository)
        }
    }

    @Module
    class SignUpUseCaseModule {
        @Provides
        fun providesSignUpUseCase(userDataRepository: UserDataRepository): SignUpUseCase {
            return SignUpUseCase(userDataRepository)
        }
    }

    @Module
    class UpdateBoardContentUseCaseModule {
        @Provides
        fun providesUpdateBoardContentUseCase(boardDataRepository: BoardDataRepository): UpdateBoardContentUseCase {
            return UpdateBoardContentUseCase(boardDataRepository)
        }
    }

    @Module
    class UpdateBoardContentImagesUseCaseModule {
        @Provides
        fun providesUpdateBoardContentImagesUseCase(
            updateBoardContentUseCase: UpdateBoardContentUseCase,
            uploadImagesUseCase: UploadImagesUseCase
        ): UpdateBoardContentImagesUseCase {
            return UpdateBoardContentImagesUseCase(updateBoardContentUseCase, uploadImagesUseCase)
        }
    }

    /*
        @Module
        class UpdateUserInfoUseCaseModule {
            @Provides
            fun providesUpdateUserInfoUseCase(userDataRepository: UserDataRepository, uploadImageUseCase: UploadImagesUseCase): UpdateUserInfoUseCase {
                return UpdateUserInfoUseCase(userDataRepository, uploadImageUseCase)
            }
        }

     */

    @Module
    class UploadImagesUseCaseModule {
        @Provides
        fun providesUploadImagesUseCase(repository: ImageDataRepository): UploadImagesUseCase {
            return UploadImagesUseCaseImpl(repository)
        }
    }

    @Module
    class WriteContentUseCaseModule {
        @Provides
        fun providesWriteContentUseCase(repository: BoardDataRepository): WriteBoardContentUseCase {
            return WriteBoardContentUseCase(repository)
        }
    }

    @Module
    class DeleteUserInfoUseCaseModule {
        @Provides
        fun providesDeleteUserInfoUseCase(repository: UserDataRepository): DeleteUserInfoUseCase {
            return DeleteUserInfoUseCase(repository)
        }
    }

    @Module
    class LoadBoardListUseCaseModule {
        @Provides
        fun providesLoadBoardListUseCase(
            boardDataRepository: BoardDataRepository,
            bookmarkDataRepository: BookmarkDataRepository,
            likeDataRepository: LikeDataRepository
        ): LoadBoardListUseCase {
            return LoadBoardListUseCase(
                boardDataRepository,
                bookmarkDataRepository,
                likeDataRepository
            )
        }
    }

    @Module
    class WriteCommentUesCaseModule {
        @Provides
        fun providesWriteCommentUesCase(repository: CommentDataRepository): WriteCommentUseCase {
            return WriteCommentUseCase(repository)
        }
    }

    @Module
    class AddBoardBookmarkUseCaseModule {
        @Provides
        fun providesAddBoardBookmarkUseCase(repository: BookmarkDataRepository): AddBoardBookmarkUseCase {
            return AddBoardBookmarkUseCase(repository)
        }
    }

    @Module
    class DeleteBoardBookmarkUseCaseModule {
        @Provides
        fun providesDeleteBoardBookmarkUseCase(repository: BookmarkDataRepository): DeleteBoardBookmarkUseCase {
            return DeleteBoardBookmarkUseCase(repository)
        }
    }

    @Module
    class AddBoardLikeUseCaseModule {
        @Provides
        fun providesAddBoardLikeUseCase(repository: LikeDataRepository): AddBoardLikeUseCase {
            return AddBoardLikeUseCase(repository)
        }
    }

    @Module
    class DeleteBoardLikeUseCaseModule {
        @Provides
        fun providesDeleteBoardLikeUseCase(repository: LikeDataRepository): DeleteBoardLikeUseCase {
            return DeleteBoardLikeUseCase(repository)
        }
    }

    @Module
    class AddBoardContentLikeUseCaseModule {
        @Provides
        fun providesAddBoardContentLikeUseCase(repository: LikeDataRepository): AddBoardContentLikeUseCase {
            return AddBoardContentLikeUseCase(repository)
        }
    }


    @Module
    class DeleteBoardContentLikeUseCaseModule {
        @Provides
        fun providesDeleteBoardContentLikeUseCase(repository: LikeDataRepository): DeleteBoardContentLikeUseCase {
            return DeleteBoardContentLikeUseCase(repository)
        }
    }

    @Module
    class AddBoardContentBookmarkUseCaseModule {
        @Provides
        fun providesAddBoardContentBookmarkUseCase(repository: BookmarkDataRepository): AddBoardContentBookmarkUseCase {
            return AddBoardContentBookmarkUseCase(repository)
        }
    }

    @Module
    class DeleteBoardContentBookmarkUseCaseModule {
        @Provides
        fun providesDeleteBoardContentBookmarkUseCase(repository: BookmarkDataRepository): DeleteBoardContentBookmarkUseCase {
            return DeleteBoardContentBookmarkUseCase(repository)
        }
    }

    @Module
    class LoadCommentListUseCaseModule {
        @Provides
        fun providesLoadCommentListUseCase(
            commentDataRepository: CommentDataRepository,
            bookmarkDataRepository: BookmarkDataRepository,
            likeDataRepository: LikeDataRepository
        ): LoadCommentListUseCase {
            return LoadCommentListUseCase(
                commentDataRepository,
                bookmarkDataRepository,
                likeDataRepository
            )
        }
    }

    @Module
    class AddCommentBookmarkUseCaseModule {
        @Provides
        fun providesAddCommentBookmarkUseCase(repository: BookmarkDataRepository): AddCommentBookmarkUseCase {
            return AddCommentBookmarkUseCase(repository)
        }
    }

    @Module
    class DeleteCommentBookmarkUseCaseModule {
        @Provides
        fun providesDeleteCommentBookmarkUseCase(repository: BookmarkDataRepository): DeleteCommentBookmarkUseCase {
            return DeleteCommentBookmarkUseCase(repository)
        }
    }

    @Module
    class AddCommentLikeUseCaseModule {
        @Provides
        fun providesAddCommentLikeUseCase(repository: LikeDataRepository): AddCommentLikeUseCase {
            return AddCommentLikeUseCase(repository)
        }
    }

    @Module
    class DeleteLikeCommentUseCaseModule {
        @Provides
        fun providesDeleteCommentLikeUseCase(repository: LikeDataRepository): DeleteCommentLikeUseCase {
            return DeleteCommentLikeUseCase(repository)
        }
    }

    @Module
    class DeleteCommentUseCaseModule {
        @Provides
        fun providesDeleteCommentUseCase(
            commentDataRepository: CommentDataRepository,
            bookmarkDataRepository: BookmarkDataRepository,
            likeDataRepository: LikeDataRepository
        ): DeleteCommentUseCase {
            return DeleteCommentUseCase(
                commentDataRepository,
                bookmarkDataRepository,
                likeDataRepository
            )
        }
    }

    @Module
    class LoadBoardRelatedAllCommentListUseCaseModule {
        @Provides
        fun providesLoadBoardRelatedAllCommentListUseCase(
            commentDataRepository: CommentDataRepository,
            bookmarkDataRepository: BookmarkDataRepository,
            likeDataRepository: LikeDataRepository
        ): LoadBoardRelatedAllCommentListUseCase {
            return LoadBoardRelatedAllCommentListUseCase(
                commentDataRepository,
                bookmarkDataRepository,
                likeDataRepository
            )
        }
    }

    @Module
    class LoadBoardContentUseCaseModule {
        @Provides
        fun providesSelectBoardContentUseCase(
            boardDataRepository: BoardDataRepository,
            bookmarkDataRepository: BookmarkDataRepository,
            likeDataRepository: LikeDataRepository
        ): LoadBoardContentUseCase {
            return LoadBoardContentUseCase(
                boardDataRepository,
                bookmarkDataRepository,
                likeDataRepository
            )
        }
    }

    @Module
    class CreateChatRoomUseCaseModule {
        @Provides
        fun providesCreateChatRoomUseCase(
            chatDataRepository: ChatDataRepository
        ): CreateChatRoomUseCase {
            return CreateChatRoomUseCase(
                chatDataRepository
            )
        }
    }

    @Module
    class GetUserInfoUseCaseModule {
        @Provides
        fun providesGetUserInfoUseCase(
            userDataRepository: UserDataRepository
        ): GetUserMeUseCase {
            return GetUserMeUseCase(
                userDataRepository
            )
        }
    }

    @Module
    class CheckChatRoomUseCaseModule {
        @Provides
        fun providesCheckChatRoomUseCase(
            chatDataRepository: ChatDataRepository
        ): CheckChatRoomUseCase {
            return CheckChatRoomUseCase(
                chatDataRepository
            )
        }
    }

    @Module
    class SendChatMessageUseCaseModule {
        @Provides
        fun providesSendChatMessageUseCase(
            chatDataRepository: ChatDataRepository
        ): SendChatMessageUseCase {
            return SendChatMessageUseCase(
                chatDataRepository
            )
        }
    }

    @Module
    class LoadChatMessageListUseCaseModule {
        @Provides
        fun providesLoadChatMessageListUseCase(
            chatDataRepository: ChatDataRepository
        ): LoadChatMessageListUseCase {
            return LoadChatMessageListUseCase(
                chatDataRepository
            )
        }
    }

    @Module
    class LoadRealTimeChatMessageUseCaseModule {
        @Provides
        fun providesLoadRealTimeChatMessageUseCase(
            chatDataRepository: ChatDataRepository
        ): LoadRealTimeChatMessageUseCase {
            return LoadRealTimeChatMessageUseCase(
                chatDataRepository
            )
        }
    }

    @Module
    class LoadChatRoomListUseCaseModule {
        @Provides
        fun providesLoadChatRoomListUseCase(
            chatDataRepository: ChatDataRepository
        ): LoadChatRoomListUseCase {
            return LoadChatRoomListUseCase(
                chatDataRepository
            )
        }
    }

    @Module
    class LoadRealTimeChatRoomListUseCaseModule {
        @Provides
        fun providesLoadRealTimeChatRoomUseCase(
            chatDataRepository: ChatDataRepository
        ): LoadRealTimeChatRoomListUseCase {
            return LoadRealTimeChatRoomListUseCase(
                chatDataRepository
            )
        }
    }

    @Module
    class LoadChatRoomUseCaseModule {
        @Provides
        fun providesLoadChatRoomUseCase(
            chatDataRepository: ChatDataRepository
        ): LoadChatRoomUseCase {
            return LoadChatRoomUseCase(
                chatDataRepository
            )
        }
    }

    @Module
    class LeaveChatRoomUseCaseModule {
        @Provides
        fun providesLeaveChatRoomUseCase(
            chatDataRepository: ChatDataRepository
        ): LeaveChatRoomUseCase {
            return LeaveChatRoomUseCase(
                chatDataRepository
            )
        }
    }

    @Module
    class LoadRealTimeChatRoomUseCaseModule {
        @Provides
        fun providesLoadRealTimeChatRoomUseCase(
            chatDataRepository: ChatDataRepository
        ): LoadRealTimeChatRoomUseCase {
            return LoadRealTimeChatRoomUseCase(
                chatDataRepository
            )
        }
    }

    @Module
    class LoadMyBoardListUseCaseModule {
        @Provides
        fun providesLoadMyBoardListUseCase(
            boardDataRepository: BoardDataRepository,
            bookmarkDataRepository: BookmarkDataRepository,
            likeDataRepository: LikeDataRepository
        ): LoadMyBoardListUseCase {
            return LoadMyBoardListUseCase(
                boardDataRepository,
                bookmarkDataRepository,
                likeDataRepository
            )
        }
    }

    @Module
    class DeleteBoardUseCaseModule {
        @Provides
        fun providesDeleteBoardUseCase(
            boardDataRepository: BoardDataRepository,
            bookmarkDataRepository: BookmarkDataRepository,
            likeDataRepository: LikeDataRepository,
            commentDataRepository: CommentDataRepository
        ): DeleteBoardUseCase {
            return DeleteBoardUseCase(
                boardDataRepository,
                bookmarkDataRepository,
                likeDataRepository,
                commentDataRepository
            )
        }
    }

    @Module
    class UpdateUserInfoUseCaseModule {
        @Provides
        fun providesUpdateUserInfoUseCase(
            userDataRepository: UserDataRepository,
            uploadImagesUseCase: UploadImagesUseCase
        ): UpdateUserInfoUseCase {
            return UpdateUserInfoUseCase(
                userDataRepository,
                uploadImagesUseCase
            )
        }
    }

    // ViewModel
    @Module
    abstract class ViewModelFactoryModule {
        @Binds
        abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
    }

    @Module
    class BoardViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(BoardViewModel::class)
        fun providesBoardViewModel(
            writeBoardContentUseCase: WriteBoardContentUseCase,
            updateBoardContentImagesUseCase: UpdateBoardContentImagesUseCase,
            loadBoardListUseCase: LoadBoardListUseCase,
            addBoardBookmarkUseCase: AddBoardBookmarkUseCase,
            deleteBoardBookmarkUseCase: DeleteBoardBookmarkUseCase,
            addBoardLikeUseCase: AddBoardLikeUseCase,
            deleteBoardLikeUseCase: DeleteBoardLikeUseCase,
            createChatRoomUseCase: CreateChatRoomUseCase,
            getUserMeUseCase: GetUserMeUseCase,
            checkChatRoomUseCase: CheckChatRoomUseCase,
            deleteBoardUseCase: DeleteBoardUseCase
        ): ViewModel {
            return BoardViewModel(
                writeBoardContentUseCase,
                updateBoardContentImagesUseCase,
                loadBoardListUseCase,
                addBoardBookmarkUseCase,
                deleteBoardBookmarkUseCase,
                addBoardLikeUseCase,
                deleteBoardLikeUseCase,
                createChatRoomUseCase,
                getUserMeUseCase,
                checkChatRoomUseCase,
                deleteBoardUseCase
            )
        }
    }

    @Module
    class BoardContentViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(BoardContentViewModel::class)
        fun providesBoardContentViewModel(
            writeCommentUseCase: WriteCommentUseCase,
            loadBoardContentUseCase: LoadBoardContentUseCase,
            loadCommentListUseCase: LoadCommentListUseCase,
            loadBoardRelatedAllCommentListUseCase: LoadBoardRelatedAllCommentListUseCase,
            deleteCommentUseCase: DeleteCommentUseCase,
            addBoardContentBookmarkUseCase: AddBoardContentBookmarkUseCase,
            deleteBoardContentBookmarkUseCase: DeleteBoardContentBookmarkUseCase,
            addBoardContentLikeUseCase: AddBoardContentLikeUseCase,
            deleteBoardContentLikeUseCase: DeleteBoardContentLikeUseCase,
            addCommentBookmarkUseCase: AddCommentBookmarkUseCase,
            deleteCommentBookmarkUseCase: DeleteCommentBookmarkUseCase,
            addCommentLikeUseCase: AddCommentLikeUseCase,
            deleteCommentLikeUseCase: DeleteCommentLikeUseCase,
            getUserMeUseCase: GetUserMeUseCase
        ): ViewModel {
            return BoardContentViewModel(
                writeCommentUseCase,
                loadBoardContentUseCase,
                loadCommentListUseCase,
                loadBoardRelatedAllCommentListUseCase,
                deleteCommentUseCase,
                addBoardContentBookmarkUseCase,
                deleteBoardContentBookmarkUseCase,
                addBoardContentLikeUseCase,
                deleteBoardContentLikeUseCase,
                addCommentBookmarkUseCase,
                deleteCommentBookmarkUseCase,
                addCommentLikeUseCase,
                deleteCommentLikeUseCase,
                getUserMeUseCase
            )
        }
    }

    @Module
    class SignViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(SignViewModel::class)
        fun providesSignViewModel(
            signUpUseCase: SignUpUseCase,
            sendEmailUseCase: SendEmailUseCase,
            loginUseCase: LoginUseCase,
            resetPasswordUseCase: ResetPasswordUseCase,
            deleteUserInfoUseCase: DeleteUserInfoUseCase,
            updateUserInfoUseCase: UpdateUserInfoUseCase,
            setSavedEmailUseCase: SetSavedEmailUseCase,
            getSavedEmailUseCase: GetSavedEmailUseCase,
            deleteSavedEmailUseCase: DeleteSavedEmailUseCase
        ): ViewModel {
            return SignViewModel(
                signUpUseCase,
                sendEmailUseCase,
                loginUseCase,
                resetPasswordUseCase,
                deleteUserInfoUseCase,
                updateUserInfoUseCase,
                setSavedEmailUseCase,
                getSavedEmailUseCase,
                deleteSavedEmailUseCase
            )
        }
    }

    @Module
    class ChatViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(ChatViewModel::class)
        fun providesChatViewModel(
            savedStateHandle: SavedStateHandle,
            sendChatMessageUseCase: SendChatMessageUseCase,
            loadChatMessageListUseCase: LoadChatMessageListUseCase,
            loadRealTimeChatMessageUseCase: LoadRealTimeChatMessageUseCase,
            loadChatRoomUseCase: LoadChatRoomUseCase,
            leaveChatRoomUseCase: LeaveChatRoomUseCase,
            loadRealTimeChatRoomUseCase: LoadRealTimeChatRoomUseCase
        ): ViewModel {
            return ChatViewModel(
                savedStateHandle,
                sendChatMessageUseCase,
                loadChatMessageListUseCase,
                loadRealTimeChatMessageUseCase,
                loadChatRoomUseCase,
                leaveChatRoomUseCase,
                loadRealTimeChatRoomUseCase
            )
        }
    }

    @Module
    class ChatRoomViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(ChatRoomViewModel::class)
        fun providesChatRoomViewModel(
            loadChatRoomListUseCase: LoadChatRoomListUseCase,
            getUserMeUseCase: GetUserMeUseCase,
            loadRealTimeChatRoomListUseCase: LoadRealTimeChatRoomListUseCase
        ): ViewModel {
            return ChatRoomViewModel(
                loadChatRoomListUseCase,
                getUserMeUseCase,
                loadRealTimeChatRoomListUseCase
            )
        }
    }

    @Module
    class MyPageViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(MyPageViewModel::class)
        fun providesMyPageViewModel(
            getUserMeUseCase: GetUserMeUseCase,
            updateUserInfoUseCase: UpdateUserInfoUseCase,
            logoutUseCase: LogoutUseCase
        ): ViewModel {
            return MyPageViewModel(
                getUserMeUseCase,
                updateUserInfoUseCase,
                logoutUseCase
            )
        }
    }

    @Module
    class MyBoardViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(MyBoardViewModel::class)
        fun providesMyBoardViewModel(
            loadMyBoardListUseCase: LoadMyBoardListUseCase,
            addBoardBookmarkUseCase: AddBoardBookmarkUseCase,
            deleteBoardBookmarkUseCase: DeleteBoardBookmarkUseCase,
            addBoardLikeUseCase: AddBoardLikeUseCase,
            deleteBoardLikeUseCase: DeleteBoardLikeUseCase,
            getUserMeUseCase: GetUserMeUseCase,
            deleteBoardUseCase: DeleteBoardUseCase
        ): ViewModel {
            return MyBoardViewModel(
                loadMyBoardListUseCase,
                addBoardBookmarkUseCase,
                deleteBoardBookmarkUseCase,
                addBoardLikeUseCase,
                deleteBoardLikeUseCase,
                getUserMeUseCase,
                deleteBoardUseCase
            )
        }
    }

    @Module
    class MyCommentViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(MyCommentViewModel::class)
        fun providesMyCommentViewModel(
            loadMyCommentListUseCase: LoadMyCommentListUseCase,
            deleteCommentUseCase: DeleteCommentUseCase,
            addCommentBookmarkUseCase: AddCommentBookmarkUseCase,
            deleteCommentBookmarkUseCase: DeleteCommentBookmarkUseCase,
            addCommentLikeUseCase: AddCommentLikeUseCase,
            deleteCommentLikeUseCase: DeleteCommentLikeUseCase,
            getUserMeUseCase: GetUserMeUseCase
        ): ViewModel {
            return MyCommentViewModel(
                loadMyCommentListUseCase,
                deleteCommentUseCase,
                addCommentBookmarkUseCase,
                deleteCommentBookmarkUseCase,
                addCommentLikeUseCase,
                deleteCommentLikeUseCase,
                getUserMeUseCase
            )
        }
    }

    @Module
    class MyBookmarkBoardViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(MyBookmarkBoardViewModel::class)
        fun providesMyBookmarkBoardViewModel(
            loadMyBookmarkBoardListUseCase: LoadMyBookmarkBoardListUseCase,
            addBoardBookmarkUseCase: AddBoardBookmarkUseCase,
            deleteBoardBookmarkUseCase: DeleteBoardBookmarkUseCase,
            addBoardLikeUseCase: AddBoardLikeUseCase,
            deleteBoardLikeUseCase: DeleteBoardLikeUseCase,
            getUserMeUseCase: GetUserMeUseCase,
            deleteBoardUseCase: DeleteBoardUseCase,
            checkChatRoomUseCase: CheckChatRoomUseCase,
            createChatRoomUseCase: CreateChatRoomUseCase
        ): ViewModel {
            return MyBookmarkBoardViewModel(
                loadMyBookmarkBoardListUseCase,
                addBoardBookmarkUseCase,
                deleteBoardBookmarkUseCase,
                addBoardLikeUseCase,
                deleteBoardLikeUseCase,
                getUserMeUseCase,
                deleteBoardUseCase,
                checkChatRoomUseCase,
                createChatRoomUseCase
            )
        }
    }

    @Module
    class MyLikeBoardViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(MyLikeBoardViewModel::class)
        fun providesMyLikeBoardViewModel(
            loadMyLikeBoardListUseCase: LoadMyLikeBoardListUseCase,
            addBoardBookmarkUseCase: AddBoardBookmarkUseCase,
            deleteBoardBookmarkUseCase: DeleteBoardBookmarkUseCase,
            addBoardLikeUseCase: AddBoardLikeUseCase,
            deleteBoardLikeUseCase: DeleteBoardLikeUseCase,
            getUserMeUseCase: GetUserMeUseCase,
            deleteBoardUseCase: DeleteBoardUseCase,
            checkChatRoomUseCase: CheckChatRoomUseCase,
            createChatRoomUseCase: CreateChatRoomUseCase
        ): ViewModel {
            return MyLikeBoardViewModel(
                loadMyLikeBoardListUseCase,
                addBoardBookmarkUseCase,
                deleteBoardBookmarkUseCase,
                addBoardLikeUseCase,
                deleteBoardLikeUseCase,
                getUserMeUseCase,
                deleteBoardUseCase,
                checkChatRoomUseCase,
                createChatRoomUseCase
            )
        }
    }

    @Module
    class MyBookmarkCommentViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(MyBookmarkCommentViewModel::class)
        fun providesMyBookmarkCommentViewModel(
            loadMyBookmarkCommentListUseCase: LoadMyBookmarkCommentListUseCase,
            deleteCommentUseCase: DeleteCommentUseCase,
            addCommentBookmarkUseCase: AddCommentBookmarkUseCase,
            deleteCommentBookmarkUseCase: DeleteCommentBookmarkUseCase,
            addCommentLikeUseCase: AddCommentLikeUseCase,
            deleteCommentLikeUseCase: DeleteCommentLikeUseCase,
            getUserMeUseCase: GetUserMeUseCase
        ): ViewModel {
            return MyBookmarkCommentViewModel(
                loadMyBookmarkCommentListUseCase,
                deleteCommentUseCase,
                addCommentBookmarkUseCase,
                deleteCommentBookmarkUseCase,
                addCommentLikeUseCase,
                deleteCommentLikeUseCase,
                getUserMeUseCase
            )
        }
    }

    @Module
    class MyLikeCommentViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(MyLikeCommentViewModel::class)
        fun providesMyLikeCommentViewModel(
            loadMyLikeCommentListUseCase: LoadMyLikeCommentListUseCase,
            deleteCommentUseCase: DeleteCommentUseCase,
            addCommentBookmarkUseCase: AddCommentBookmarkUseCase,
            deleteCommentBookmarkUseCase: DeleteCommentBookmarkUseCase,
            addCommentLikeUseCase: AddCommentLikeUseCase,
            deleteCommentLikeUseCase: DeleteCommentLikeUseCase,
            getUserMeUseCase: GetUserMeUseCase
        ): ViewModel {
            return MyLikeCommentViewModel(
                loadMyLikeCommentListUseCase,
                deleteCommentUseCase,
                addCommentBookmarkUseCase,
                deleteCommentBookmarkUseCase,
                addCommentLikeUseCase,
                deleteCommentLikeUseCase,
                getUserMeUseCase
            )
        }
    }

    @Module
    class SplashViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(SplashViewModel::class)
        fun providesSplashViewModel(
            getUserMeUseCase: GetUserMeUseCase
        ): ViewModel {
            return SplashViewModel(
                getUserMeUseCase
            )
        }
    }

    @Module
    class ServiceViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(ServiceViewModel::class)
        fun providesServiceViewModel(
        ): ViewModel {
            return ServiceViewModel()
        }
    }
}