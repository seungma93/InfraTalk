package com.freetalk.di.module

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.freetalk.data.datasource.remote.BoardDataSource
import com.freetalk.data.datasource.remote.BookmarkDataSource
import com.freetalk.data.datasource.remote.ChatDataSource
import com.freetalk.data.datasource.remote.CommentDataSource
import com.freetalk.data.datasource.remote.FirebaseBoardRemoteDataSourceImpl
import com.freetalk.data.datasource.remote.FirebaseBookmarkRemoteDataSourceImpl
import com.freetalk.data.datasource.remote.FirebaseChatRemoteDataSourceImpl
import com.freetalk.data.datasource.remote.FirebaseCommentRemoteDataSourceImpl
import com.freetalk.data.datasource.remote.FirebaseImageRemoteDataSourceImpl
import com.freetalk.data.datasource.remote.FirebaseLikeRemoteDataSourceImpl
import com.freetalk.data.datasource.remote.FirebaseUserRemoteDataSourceImpl
import com.freetalk.data.datasource.remote.ImageDataSource
import com.freetalk.data.datasource.remote.LikeDataSource
import com.freetalk.data.datasource.remote.UserDataSource
import com.freetalk.data.repository.CommentDataRepositoryImpl
import com.freetalk.domain.repository.BoardDataRepository
import com.freetalk.domain.repository.BoardDataRepositoryImpl
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.BookmarkDataRepositoryImpl
import com.freetalk.domain.repository.ChatDataRepository
import com.freetalk.domain.repository.ChatDataRepositoryImpl
import com.freetalk.domain.repository.CommentDataRepository
import com.freetalk.domain.repository.ImageDataRepository
import com.freetalk.domain.repository.ImageDataRepositoryImpl
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.domain.repository.LikeDataRepositoryImpl
import com.freetalk.domain.repository.UserDataRepository
import com.freetalk.domain.repository.UserDataRepositoryImpl
import com.freetalk.domain.usecase.AddBoardBookmarkUseCase
import com.freetalk.domain.usecase.AddBoardContentBookmarkUseCase
import com.freetalk.domain.usecase.AddBoardContentLikeUseCase
import com.freetalk.domain.usecase.AddBoardLikeUseCase
import com.freetalk.domain.usecase.AddCommentBookmarkUseCase
import com.freetalk.domain.usecase.AddCommentLikeUseCase
import com.freetalk.domain.usecase.CheckChatRoomUseCase
import com.freetalk.domain.usecase.CreateChatRoomUseCase
import com.freetalk.domain.usecase.DeleteBoardBookmarkUseCase
import com.freetalk.domain.usecase.DeleteBoardContentBookmarkUseCase
import com.freetalk.domain.usecase.DeleteBoardContentLikeUseCase
import com.freetalk.domain.usecase.DeleteBoardLikeUseCase
import com.freetalk.domain.usecase.DeleteCommentBookmarkUseCase
import com.freetalk.domain.usecase.DeleteCommentLikeUseCase
import com.freetalk.domain.usecase.DeleteCommentUseCase
import com.freetalk.domain.usecase.DeleteUserInfoUseCase
import com.freetalk.domain.usecase.DeleteUserInfoUseCaseImpl
import com.freetalk.domain.usecase.GetUserInfoUseCase
import com.freetalk.domain.usecase.LeaveChatRoomUseCase
import com.freetalk.domain.usecase.LoadBoardContentUseCase
import com.freetalk.domain.usecase.LoadBoardListUseCase
import com.freetalk.domain.usecase.LoadBoardRelatedAllCommentListUseCase
import com.freetalk.domain.usecase.LoadChatMessageListUseCase
import com.freetalk.domain.usecase.LoadChatRoomListUseCase
import com.freetalk.domain.usecase.LoadChatRoomUseCase
import com.freetalk.domain.usecase.LoadCommentListUseCase
import com.freetalk.domain.usecase.LoadRealTimeChatMessageUseCase
import com.freetalk.domain.usecase.LoadRealTimeChatRoomListUseCase
import com.freetalk.domain.usecase.LogInUseCase
import com.freetalk.domain.usecase.LogInUseCaseImpl
import com.freetalk.domain.usecase.ResetPasswordUseCase
import com.freetalk.domain.usecase.ResetPasswordUseCaseImpl
import com.freetalk.domain.usecase.SendChatMessageUseCase
import com.freetalk.domain.usecase.SendEmailUseCase
import com.freetalk.domain.usecase.SendEmailUseCaseImpl
import com.freetalk.domain.usecase.SignUpUseCase
import com.freetalk.domain.usecase.SignUpUseCaseImpl
import com.freetalk.domain.usecase.UpdateBoardContentImagesUseCase
import com.freetalk.domain.usecase.UpdateBoardContentUseCase
import com.freetalk.domain.usecase.UpdateProfileImageUseCase
import com.freetalk.domain.usecase.UpdateProfileImageUseCaseImpl
import com.freetalk.domain.usecase.UpdateUserInfoUseCase
import com.freetalk.domain.usecase.UpdateUserInfoUseCaseImpl
import com.freetalk.domain.usecase.UploadImagesUseCase
import com.freetalk.domain.usecase.UploadImagesUseCaseImpl
import com.freetalk.domain.usecase.WriteBoardContentUseCase
import com.freetalk.domain.usecase.WriteCommentUseCase
import com.freetalk.presenter.viewmodel.BoardContentViewModel
import com.freetalk.presenter.viewmodel.BoardViewModel
import com.freetalk.presenter.viewmodel.ChatRoomViewModel
import com.freetalk.presenter.viewmodel.ChatViewModel
import com.freetalk.presenter.viewmodel.SignViewModel
import com.freetalk.presenter.viewmodel.ViewModelFactory
import com.freetalk.presenter.viewmodel.ViewModelKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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

    // DataSource
    @Module
    class FirebaseBoardDataSourceModule {
        @Provides
        fun providesFirebaseBoardRemoteDataSource(
            database: FirebaseFirestore,
            userDataSource: UserDataSource
        ): BoardDataSource {
            return FirebaseBoardRemoteDataSourceImpl(database, userDataSource)
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
            database: FirebaseFirestore
        ): UserDataSource {
            return FirebaseUserRemoteDataSourceImpl(auth, database)
        }
    }

    @Module
    class FirebaseLikeDataSourceModule {
        @Provides
        fun providesFirebaseLikeRemoteDataSource(
            database: FirebaseFirestore
        ): LikeDataSource {
            return FirebaseLikeRemoteDataSourceImpl(database)
        }
    }

    @Module
    class FirebaseBookmarkDataSourceModule {
        @Provides
        fun providesFirebaseBookmarkRemoteDataSource(
            database: FirebaseFirestore
        ): BookmarkDataSource {
            return FirebaseBookmarkRemoteDataSourceImpl(database)
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
        fun providesBoardDataRepository(dataSource: BoardDataSource): BoardDataRepository {
            return BoardDataRepositoryImpl(dataSource)
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
        fun providesUserDataRepository(dataSource: UserDataSource): UserDataRepository {
            return UserDataRepositoryImpl(dataSource)
        }
    }

    @Module
    class LikeDataRepositoryModule {
        @Provides
        fun providesLikeDataRepository(dataSource: LikeDataSource): LikeDataRepository {
            return LikeDataRepositoryImpl(dataSource)
        }
    }

    @Module
    class BookmarkDataRepositoryModule {
        @Provides
        fun providesBookmarkDataRepository(dataSource: BookmarkDataSource): BookmarkDataRepository {
            return BookmarkDataRepositoryImpl(dataSource)
        }
    }

    @Module
    class CommentDataRepositoryModule {
        @Provides
        fun providesCommentDataRepository(
            commentDataSource: CommentDataSource
        ): CommentDataRepository {
            return CommentDataRepositoryImpl(commentDataSource)
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
    class LogInUseCaseModule {
        @Provides
        fun providesLogInUseCase(repository: UserDataRepository): LogInUseCase {
            return LogInUseCaseImpl(repository)
        }
    }

    @Module
    class ResetPasswordUseCaseModule {
        @Provides
        fun providesResetPasswordUseCase(userDataRepository: UserDataRepository): ResetPasswordUseCase {
            return ResetPasswordUseCaseImpl(userDataRepository)
        }
    }

    @Module
    class SendEmailUseCaseModule {
        @Provides
        fun providesSendEmailUseCase(userDataRepository: UserDataRepository): SendEmailUseCase {
            return SendEmailUseCaseImpl(userDataRepository)
        }
    }

    @Module
    class SignUpUseCaseModule {
        @Provides
        fun providesSignUpUseCase(userDataRepository: UserDataRepository): SignUpUseCase {
            return SignUpUseCaseImpl(userDataRepository)
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

    @Module
    class UpdateProfileImageUseCaseModule {
        @Provides
        fun providesUpdateImageContentUseCase(
            uploadImagesUseCase: UploadImagesUseCase,
            updateUserInfoUseCase: UpdateUserInfoUseCase
        ): UpdateProfileImageUseCase {
            return UpdateProfileImageUseCaseImpl(uploadImagesUseCase, updateUserInfoUseCase)
        }
    }

    @Module
    class UpdateUserInfoUseCaseModule {
        @Provides
        fun providesUpdateUserInfoUseCase(repository: UserDataRepository): UpdateUserInfoUseCase {
            return UpdateUserInfoUseCaseImpl(repository)
        }
    }

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
            return DeleteUserInfoUseCaseImpl(repository)
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
        ): GetUserInfoUseCase {
            return GetUserInfoUseCase(
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
            getUserInfoUseCase: GetUserInfoUseCase,
            checkChatRoomUseCase: CheckChatRoomUseCase
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
                getUserInfoUseCase,
                checkChatRoomUseCase
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
            getUserInfoUseCase: GetUserInfoUseCase
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
                getUserInfoUseCase
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
            updateProfileImageUseCase: UpdateProfileImageUseCase,
            logInUseCase: LogInUseCase,
            resetPasswordUseCase: ResetPasswordUseCase,
            deleteUserInfoUseCase: DeleteUserInfoUseCase
        ): ViewModel {
            return SignViewModel(
                signUpUseCase,
                sendEmailUseCase,
                updateProfileImageUseCase,
                logInUseCase,
                resetPasswordUseCase,
                deleteUserInfoUseCase
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
            leaveChatRoomUseCase: LeaveChatRoomUseCase
        ): ViewModel {
            return ChatViewModel(
                savedStateHandle,
                sendChatMessageUseCase,
                loadChatMessageListUseCase,
                loadRealTimeChatMessageUseCase,
                loadChatRoomUseCase,
                leaveChatRoomUseCase
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
            checkChatRoomUseCase: CheckChatRoomUseCase,
            getUserInfoUseCase: GetUserInfoUseCase,
            loadRealTimeChatRoomListUseCase: LoadRealTimeChatRoomListUseCase
        ): ViewModel {
            return ChatRoomViewModel(
                loadChatRoomListUseCase,
                checkChatRoomUseCase,
                getUserInfoUseCase,
                loadRealTimeChatRoomListUseCase
            )
        }
    }
}