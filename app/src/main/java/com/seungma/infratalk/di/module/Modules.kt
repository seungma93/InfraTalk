package com.seungma.infratalk.di.module

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.seungma.domain.repository.BoardDataRepositoryImpl
import com.seungma.domain.repository.BookmarkDataRepositoryImpl
import com.seungma.domain.repository.ChatDataRepositoryImpl
import com.seungma.domain.repository.ImageDataRepositoryImpl
import com.seungma.domain.repository.LikeDataRepositoryImpl
import com.seungma.domain.repository.UserDataRepositoryImpl
import com.seungma.infratalk.data.datasource.local.preference.PreferenceDataSource
import com.seungma.infratalk.data.datasource.local.preference.PreferenceLocalDataSourceImpl
import com.seungma.infratalk.data.datasource.remote.bookmark.BookmarkDataSource
import com.seungma.infratalk.data.datasource.remote.chat.ChatDataSource
import com.seungma.infratalk.data.datasource.remote.comment.CommentDataSource
import com.seungma.infratalk.data.datasource.remote.bookmark.FirebaseBookmarkRemoteDataSourceImpl
import com.seungma.infratalk.data.datasource.remote.chat.FirebaseChatRemoteDataSourceImpl
import com.seungma.infratalk.data.datasource.remote.comment.FirebaseCommentRemoteDataSourceImpl
import com.seungma.infratalk.data.datasource.remote.image.FirebaseImageRemoteDataSourceImpl
import com.seungma.infratalk.data.datasource.remote.like.FirebaseLikeRemoteDataSourceImpl
import com.seungma.infratalk.data.datasource.remote.user.FirebaseUserRemoteDataSourceImpl
import com.seungma.infratalk.data.datasource.remote.image.ImageDataSource
import com.seungma.infratalk.data.datasource.remote.like.LikeDataSource
import com.seungma.infratalk.data.datasource.remote.user.UserDataSource
import com.seungma.infratalk.data.datasource.remote.board.BoardDataSource
import com.seungma.infratalk.data.datasource.remote.board.FirebaseBoardRemoteDataSourceImpl
import com.seungma.infratalk.data.repository.CommentDataRepositoryImpl
import com.seungma.infratalk.domain.board.repository.BookmarkDataRepository
import com.seungma.infratalk.domain.board.repository.LikeDataRepository
import com.seungma.infratalk.domain.board.repository.BoardDataRepository
import com.seungma.infratalk.domain.board.usecase.AddBoardBookmarkUseCase
import com.seungma.infratalk.domain.board.usecase.AddBoardContentBookmarkUseCase
import com.seungma.infratalk.domain.board.usecase.AddBoardContentLikeUseCase
import com.seungma.infratalk.domain.board.usecase.AddBoardLikeUseCase
import com.seungma.infratalk.domain.board.usecase.DeleteBoardBookmarkUseCase
import com.seungma.infratalk.domain.board.usecase.DeleteBoardContentBookmarkUseCase
import com.seungma.infratalk.domain.board.usecase.DeleteBoardContentLikeUseCase
import com.seungma.infratalk.domain.board.usecase.DeleteBoardLikeUseCase
import com.seungma.infratalk.domain.board.usecase.DeleteBoardUseCase
import com.seungma.infratalk.domain.board.usecase.LoadBoardContentUseCase
import com.seungma.infratalk.domain.board.usecase.LoadBoardListUseCase
import com.seungma.infratalk.domain.board.usecase.UpdateBoardContentImagesUseCase
import com.seungma.infratalk.domain.board.usecase.UpdateBoardContentUseCase
import com.seungma.infratalk.domain.board.usecase.WriteBoardContentUseCase
import com.seungma.infratalk.domain.chat.repository.ChatDataRepository
import com.seungma.infratalk.domain.chat.usecase.CheckChatRoomUseCase
import com.seungma.infratalk.domain.chat.usecase.CreateChatRoomUseCase
import com.seungma.infratalk.domain.chat.usecase.LeaveChatRoomUseCase
import com.seungma.infratalk.domain.chat.usecase.LoadChatMessageListUseCase
import com.seungma.infratalk.domain.chat.usecase.LoadChatRoomListUseCase
import com.seungma.infratalk.domain.chat.usecase.LoadChatRoomUseCase
import com.seungma.infratalk.domain.chat.usecase.LoadRealTimeChatMessageUseCase
import com.seungma.infratalk.domain.chat.usecase.LoadRealTimeChatRoomListUseCase
import com.seungma.infratalk.domain.chat.usecase.LoadRealTimeChatRoomUseCase
import com.seungma.infratalk.domain.chat.usecase.SendChatMessageUseCase
import com.seungma.infratalk.domain.comment.repository.CommentDataRepository
import com.seungma.infratalk.domain.comment.usecase.AddCommentBookmarkUseCase
import com.seungma.infratalk.domain.comment.usecase.AddCommentLikeUseCase
import com.seungma.infratalk.domain.comment.usecase.DeleteCommentBookmarkUseCase
import com.seungma.infratalk.domain.comment.usecase.DeleteCommentLikeUseCase
import com.seungma.infratalk.domain.comment.usecase.DeleteCommentUseCase
import com.seungma.infratalk.domain.comment.usecase.LoadBoardRelatedAllCommentListUseCase
import com.seungma.infratalk.domain.comment.usecase.LoadCommentListUseCase
import com.seungma.infratalk.domain.comment.usecase.WriteCommentUseCase
import com.seungma.infratalk.domain.image.repository.ImageDataRepository
import com.seungma.infratalk.domain.image.usecase.UploadImagesUseCase
import com.seungma.infratalk.domain.image.usecase.UploadImagesUseCaseImpl
import com.seungma.infratalk.domain.login.usecase.LogInUseCase
import com.seungma.infratalk.domain.login.usecase.LogInUseCaseImpl
import com.seungma.infratalk.domain.login.usecase.ResetPasswordUseCase
import com.seungma.infratalk.domain.login.usecase.ResetPasswordUseCaseImpl
import com.seungma.infratalk.domain.mypage.usecase.LoadMyBoardListUseCase
import com.seungma.infratalk.domain.mypage.usecase.LoadMyBookmarkBoardListUseCase
import com.seungma.infratalk.domain.mypage.usecase.LoadMyBookmarkCommentListUseCase
import com.seungma.infratalk.domain.mypage.usecase.LoadMyCommentListUseCase
import com.seungma.infratalk.domain.mypage.usecase.LoadMyLikeBoardListUseCase
import com.seungma.infratalk.domain.mypage.usecase.LoadMyLikeCommentListUseCase
import com.seungma.infratalk.domain.mypage.usecase.UpdateProfileImageUseCase
import com.seungma.infratalk.domain.mypage.usecase.UpdateUserInfoUseCase
import com.seungma.infratalk.domain.signup.usecase.DeleteUserInfoUseCase
import com.seungma.infratalk.domain.signup.usecase.DeleteUserInfoUseCaseImpl
import com.seungma.infratalk.domain.signup.usecase.SendEmailUseCase
import com.seungma.infratalk.domain.signup.usecase.SendEmailUseCaseImpl
import com.seungma.infratalk.domain.signup.usecase.SignUpUseCase
import com.seungma.infratalk.domain.signup.usecase.SignUpUseCaseImpl
import com.seungma.infratalk.domain.user.usecase.GetUserInfoUseCase
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.presenter.board.viewmodel.BoardContentViewModel
import com.seungma.infratalk.presenter.board.viewmodel.BoardViewModel
import com.seungma.infratalk.presenter.chat.viewmodel.ChatRoomViewModel
import com.seungma.infratalk.presenter.chat.viewmodel.ChatViewModel
import com.seungma.infratalk.presenter.mypage.viewmodel.MyBoardViewModel
import com.seungma.infratalk.presenter.mypage.viewmodel.MyBookmarkBoardViewModel
import com.seungma.infratalk.presenter.mypage.viewmodel.MyBookmarkCommentViewModel
import com.seungma.infratalk.presenter.mypage.viewmodel.MyCommentViewModel
import com.seungma.infratalk.presenter.mypage.viewmodel.MyLikeBoardViewModel
import com.seungma.infratalk.presenter.mypage.viewmodel.MyLikeCommentViewModel
import com.seungma.infratalk.presenter.mypage.viewmodel.MyPageViewModel
import com.seungma.infratalk.presenter.sign.viewmodel.SignViewModel
import com.seungma.infratalk.presenter.viewmodel.ViewModelFactory
import com.seungma.infratalk.presenter.viewmodel.ViewModelKey
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
            preferenceDataSource: PreferenceDataSource
        ): UserDataSource {
            return FirebaseUserRemoteDataSourceImpl(auth, database, preferenceDataSource)
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
            return UpdateProfileImageUseCase(uploadImagesUseCase, updateUserInfoUseCase)
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
            getUserInfoUseCase: GetUserInfoUseCase,
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
                getUserInfoUseCase,
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
            deleteUserInfoUseCase: DeleteUserInfoUseCase,
            updateUserInfoUseCase: UpdateUserInfoUseCase
        ): ViewModel {
            return SignViewModel(
                signUpUseCase,
                sendEmailUseCase,
                updateProfileImageUseCase,
                logInUseCase,
                resetPasswordUseCase,
                deleteUserInfoUseCase,
                updateUserInfoUseCase
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
            getUserInfoUseCase: GetUserInfoUseCase,
            loadRealTimeChatRoomListUseCase: LoadRealTimeChatRoomListUseCase
        ): ViewModel {
            return ChatRoomViewModel(
                loadChatRoomListUseCase,
                getUserInfoUseCase,
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
            getUserInfoUseCase: GetUserInfoUseCase,
            updateUserInfoUseCase: UpdateUserInfoUseCase
        ): ViewModel {
            return MyPageViewModel(
                getUserInfoUseCase,
                updateUserInfoUseCase
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
            getUserInfoUseCase: GetUserInfoUseCase,
            deleteBoardUseCase: DeleteBoardUseCase
        ): ViewModel {
            return MyBoardViewModel(
                loadMyBoardListUseCase,
                addBoardBookmarkUseCase,
                deleteBoardBookmarkUseCase,
                addBoardLikeUseCase,
                deleteBoardLikeUseCase,
                getUserInfoUseCase,
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
            getUserInfoUseCase: GetUserInfoUseCase
        ): ViewModel {
            return MyCommentViewModel(
                loadMyCommentListUseCase,
                deleteCommentUseCase,
                addCommentBookmarkUseCase,
                deleteCommentBookmarkUseCase,
                addCommentLikeUseCase,
                deleteCommentLikeUseCase,
                getUserInfoUseCase
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
            getUserInfoUseCase: GetUserInfoUseCase,
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
                getUserInfoUseCase,
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
            getUserInfoUseCase: GetUserInfoUseCase,
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
                getUserInfoUseCase,
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
            getUserInfoUseCase: GetUserInfoUseCase
        ): ViewModel {
            return MyBookmarkCommentViewModel(
                loadMyBookmarkCommentListUseCase,
                deleteCommentUseCase,
                addCommentBookmarkUseCase,
                deleteCommentBookmarkUseCase,
                addCommentLikeUseCase,
                deleteCommentLikeUseCase,
                getUserInfoUseCase
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
            getUserInfoUseCase: GetUserInfoUseCase
        ): ViewModel {
            return MyLikeCommentViewModel(
                loadMyLikeCommentListUseCase,
                deleteCommentUseCase,
                addCommentBookmarkUseCase,
                deleteCommentBookmarkUseCase,
                addCommentLikeUseCase,
                deleteCommentLikeUseCase,
                getUserInfoUseCase
            )
        }
    }
}