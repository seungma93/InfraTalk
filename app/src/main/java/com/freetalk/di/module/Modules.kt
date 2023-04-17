package com.freetalk.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.freetalk.data.remote.*
import com.freetalk.presenter.viewmodel.BoardViewModel
import com.freetalk.presenter.viewmodel.SignViewModel
import com.freetalk.presenter.viewmodel.ViewModelFactory
import com.freetalk.presenter.viewmodel.ViewModelKey
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
    class FirebaseBoardDataRepositoryModule {
        @Provides
        fun providesFirebaseBoardDataRepository(dataSource: BoardDataSource): BoardDataRepository {
            return FirebaseBoardDataRepositoryImpl(dataSource)
        }
    }

    @Module
    class FirebaseImageDataRepositoryModule {
        @Provides
        fun providesFirebaseImageDataRepository(dataSource: ImageDataSource): ImageDataRepository {
            return FirebaseImageDataRepositoryImpl(dataSource)
        }
    }

    @Module
    class FirebaseUserDataRepositoryModule {
        @Provides
        fun providesFirebaseUserDataRepository(dataSource: UserDataSource): UserDataRepository {
            return FirebaseUserDataRepositoryImpl(dataSource)
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
    abstract class ViewModelFactoryModule {
        @Binds
        abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory) : ViewModelProvider.Factory
    }

    @Module
    class BoardViewModelModule {
        @Provides
        @IntoMap
        @ViewModelKey(BoardViewModel::class)
        fun providesBoardViewModel(
            writeContentUseCase: WriteContentUseCase,
            updateImageContentUseCase: UpdateImageContentUseCase
        ): ViewModel {
            return BoardViewModel(writeContentUseCase, updateImageContentUseCase)
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
            resetPasswordUseCase: ResetPasswordUseCase
        ): ViewModel {
            return SignViewModel(signUpUseCase, sendEmailUseCase, updateProfileImageUseCase, logInUseCase, resetPasswordUseCase)
        }
    }
}