package com.freetalk.di.module

import android.service.autofill.UserData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.freetalk.data.remote.*
import com.freetalk.presenter.viewmodel.*
import com.freetalk.repository.*
import com.freetalk.usecase.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

class Modules {

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
    class FirebaseBoardDataSourceModule {
        @Provides
        fun providesFirebaseBoardRemoteDataSource(database: FirebaseFirestore): BoardDataSource {
            return FirebaseBoardRemoteDataSourceImpl(database)
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
    class FirebaseBookMarkDataSourceModule {
        @Provides
        fun providesFirebaseLikeRemoteDataSource(
            database: FirebaseFirestore
        ): BookMarkDataSource {
            return FirebaseBookMarkRemoteDataSourceImpl(database)
        }
    }

    @Module
    class FirebaseCommentDataSourceModule {
        @Provides
        fun providesFirebaseCommentRemoteDataSource(
            database: FirebaseFirestore
        ): CommentDataSource {
            return FirebaseCommentRemoteDataSourceImpl(database)
        }
    }

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
    class BookMarkDataRepositoryModule {
        @Provides
        fun providesBookMarkDataRepository(dataSource: BookMarkDataSource): BookMarkDataRepository {
            return BookMarkDataRepositoryImpl(dataSource)
        }
    }

    @Module
    class CommentDataRepositoryModule {
        @Provides
        fun providesCommentDataRepository(
            commentDataSource: CommentDataSource,
            userDataSource: UserDataSource
        ): CommentDataRepository {
            return CommentDataRepositoryImpl(commentDataSource, userDataSource)
        }
    }

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
    class UpdateContentUseCaseModule {
        @Provides
        fun providesUpdateContentUseCase(boardDataRepository: BoardDataRepository): UpdateContentUseCase {
            return UpdateContentUseCaseImpl(boardDataRepository)
        }
    }

    @Module
    class UpdateImageContentUseCaseModule {
        @Provides
        fun providesUpdateImageContentUseCase(
            updateContentUseCase: UpdateContentUseCase,
            uploadImagesUseCase: UploadImagesUseCase
        ): UpdateImageContentUseCase {
            return UpdateImageContentUseCaseImpl(updateContentUseCase, uploadImagesUseCase)
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
        fun providesWriteContentUseCase(repository: BoardDataRepository): WriteContentUseCase {
            return WriteContentUseCaseImpl(repository)
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
    class PrintBoardListUseCaseModule {
        @Provides
        fun providesPrintBoardListUseCase(
            boardDataRepository: BoardDataRepository,
            bookMarkDataRepository: BookMarkDataRepository,
            likeDataRepository: LikeDataRepository
        ): PrintBoardListUseCase {
            return PrintBoardListUseCase(boardDataRepository, bookMarkDataRepository, likeDataRepository)
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
    class InsertBookMarkBoardUseCaseModule {
        @Provides
        fun providesInsertBookMarkBoardUseCase(repository: BookMarkDataRepository): InsertBookMarkBoardUseCase {
            return InsertBookMarkBoardUseCase(repository)
        }
    }

    @Module
    class DeleteBookMarkBoardUseCaseModule {
        @Provides
        fun providesDeleteBookMarkBoardUseCase(repository: BookMarkDataRepository): DeleteBookMarkBoardUseCase {
            return DeleteBookMarkBoardUseCase(repository)
        }
    }

    @Module
    class InsertLikeBoardUseCaseModule {
        @Provides
        fun providesInsertLikeBoardUseCase(repository: LikeDataRepository): InsertLikeBoardUseCase {
            return InsertLikeBoardUseCase(repository)
        }
    }

    @Module
    class InsertLikeBoardContentUseCaseModule {
        @Provides
        fun providesInsertLikeBoardContentUseCase(repository: LikeDataRepository): InsertLikeBoardContentUseCase {
            return InsertLikeBoardContentUseCase(repository)
        }
    }

    @Module
    class DeleteLikeBoardUseCaseModule {
        @Provides
        fun providesDeleteLikeBoardUseCase(repository: LikeDataRepository): DeleteLikeBoardUseCase {
            return DeleteLikeBoardUseCase(repository)
        }
    }

    @Module
    class DeleteLikeBoardContentUseCaseModule {
        @Provides
        fun providesDeleteLikeBoardContentUseCase(repository: LikeDataRepository): DeleteLikeBoardContentUseCase {
            return DeleteLikeBoardContentUseCase(repository)
        }
    }

    @Module
    class InsertBookMarkBoardContentUseCaseModule {
        @Provides
        fun providesInsertBookMarkBoardContentUseCase(repository: BookMarkDataRepository): InsertBookMarkBoardContentUseCase {
            return InsertBookMarkBoardContentUseCase(repository)
        }
    }

    @Module
    class DeleteBookMarkBoardContentUseCaseModule {
        @Provides
        fun providesDeleteBookMarkBoardContentUseCase(repository: BookMarkDataRepository): DeleteBookMarkBoardContentUseCase {
            return DeleteBookMarkBoardContentUseCase(repository)
        }
    }

    @Module
    class SelectBoardContentUseCaseModule {
        @Provides
        fun providesSelectBoardContentUseCase(
            boardDataRepository: BoardDataRepository,
            bookMarkDataRepository: BookMarkDataRepository,
            likeDataRepository: LikeDataRepository
        ): SelectBoardContentUseCase {
            return SelectBoardContentUseCase(
                boardDataRepository,
                bookMarkDataRepository,
                likeDataRepository
            )
        }
    }

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
            writeContentUseCase: WriteContentUseCase,
            updateImageContentUseCase: UpdateImageContentUseCase,
            printBoardListUseCase: PrintBoardListUseCase,
            insertBookMarkBoardUseCase: InsertBookMarkBoardUseCase,
            deleteBookMarkBoardUseCase: DeleteBookMarkBoardUseCase,
            insertLikeBoardUseCase: InsertLikeBoardUseCase,
            deleteLikeBoardUseCase: DeleteLikeBoardUseCase
        ): ViewModel {
            return BoardViewModel(
                writeContentUseCase,
                updateImageContentUseCase,
                printBoardListUseCase,
                insertBookMarkBoardUseCase,
                deleteBookMarkBoardUseCase,
                insertLikeBoardUseCase,
                deleteLikeBoardUseCase
            )
        }
    }

    @Module
    class BoardContentViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(BoardContentViewModel::class)
        fun providesBoardContentViewModel(
            selectBoardContentUseCase: SelectBoardContentUseCase,
            insertBookMarkBoardContentUseCase: InsertBookMarkBoardContentUseCase,
            deleteBookMarkBoardContentUseCase: DeleteBookMarkBoardContentUseCase,
            insertLikeBoardContentUseCase: InsertLikeBoardContentUseCase,
            deleteLikeBoardContentUseCase: DeleteLikeBoardContentUseCase,
            writeCommentUseCase: WriteCommentUseCase
        ): ViewModel {
            return BoardContentViewModel(
                insertBookMarkBoardContentUseCase,
                deleteBookMarkBoardContentUseCase,
                selectBoardContentUseCase,
                insertLikeBoardContentUseCase,
                deleteLikeBoardContentUseCase,
                writeCommentUseCase
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
}