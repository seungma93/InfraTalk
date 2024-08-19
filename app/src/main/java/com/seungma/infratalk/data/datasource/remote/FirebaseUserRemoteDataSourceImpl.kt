package com.seungma.infratalk.data.datasource.remote


import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.seungma.infratalk.data.BlockedRequestException
import com.seungma.infratalk.data.ExistEmailException
import com.seungma.infratalk.data.FailDeleteException
import com.seungma.infratalk.data.FailInsertException
import com.seungma.infratalk.data.FailSelectLogInInfoException
import com.seungma.infratalk.data.FailSendEmailException
import com.seungma.infratalk.data.FailUpdatetException
import com.seungma.infratalk.data.InvalidEmailException
import com.seungma.infratalk.data.InvalidPasswordException
import com.seungma.infratalk.data.NotExistEmailException
import com.seungma.infratalk.data.UnKnownException
import com.seungma.infratalk.data.UserSingleton
import com.seungma.infratalk.data.WrongPasswordException
import com.seungma.infratalk.data.model.request.SignupRequest
import com.seungma.infratalk.data.model.request.user.DeleteUserRequest
import com.seungma.infratalk.data.model.request.user.LoginRequest
import com.seungma.infratalk.data.model.request.user.ResetPasswordRequest
import com.seungma.infratalk.data.model.request.user.UserInfoUpdateRequest
import com.seungma.infratalk.data.model.request.user.UserSelectRequest
import com.seungma.infratalk.data.model.response.user.UserResponse
import com.seungma.infratalk.domain.user.UserEntity
import com.seungma.infratalk.presenter.mypage.fragment.MyAccountInfoEditFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.security.auth.login.LoginException

class FirebaseUserRemoteDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : UserDataSource {

    companion object {
        const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"
        const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
        const val ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
        const val ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
        const val ERROR_WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"
        const val ERROR_TOO_MANY_REQUESTS = "ERROR_TOO_MANY_REQUESTS"
    }

    private fun separatedFirebaseErrorCode(errorCode: String): Exception {
        return when (errorCode) {
            ERROR_INVALID_EMAIL -> InvalidEmailException("유효하지 않은 이메일")
            ERROR_WRONG_PASSWORD -> WrongPasswordException("잘못된 비밀번호")
            ERROR_USER_NOT_FOUND -> NotExistEmailException("존재하지 않는 이메일")
            ERROR_EMAIL_ALREADY_IN_USE -> ExistEmailException("존재하는 이메일")
            ERROR_WEAK_PASSWORD -> InvalidPasswordException("잘못된 형식의 비밀번호")
            ERROR_TOO_MANY_REQUESTS -> BlockedRequestException("블락된 요청")
            else -> UnKnownException("알 수 없는 에러")
        }
    }

    override suspend fun signUp(signupRequest: SignupRequest): UserResponse = coroutineScope {
        runCatching {
            val insertAsync = async {
                database.collection("User").add(
                    UserEntity(
                        email = signupRequest.email,
                        nickname = signupRequest.nickname,
                        image = Uri.parse(signupRequest.imageUri)
                    )
                )
            }
            val authAsync = async {
                auth.createUserWithEmailAndPassword(
                    signupRequest.email,
                    signupRequest.password
                )
            }

            val resultInsert = insertAsync.await()
            val resultAuth = authAsync.await()

            if (resultAuth.isSuccessful && resultInsert.isSuccessful) {
                UserResponse(
                    email = signupRequest.email,
                    nickname = signupRequest.nickname,
                    image = Uri.parse(signupRequest.imageUri)
                )
            } else {
                throw LoginException("파이어베이스 로그인 로직 실패")
            }

        }.onFailure {
            when (it) {
                is FirebaseAuthException -> throw separatedFirebaseErrorCode(it.errorCode)
                is FirebaseException -> throw FailInsertException("인서트에 실패 했습니다")
                else -> throw UnKnownException("알 수 없는 에러")
            }
        }.getOrThrow()

    }

    override suspend fun updateUserInfo(userInfoUpdateRequest: UserInfoUpdateRequest): UserResponse =
        coroutineScope {
            runCatching {

                val updateData = userInfoUpdateRequest.nickname?.let {
                    userInfoUpdateRequest.image?.let {
                            if (userInfoUpdateRequest.image != Uri.parse(MyAccountInfoEditFragment.DEFAULT_PROFILE_IMAGE)) {
                                mapOf("nickname" to userInfoUpdateRequest.nickname, "image" to userInfoUpdateRequest.image)
                            } else mapOf("nickname" to userInfoUpdateRequest.nickname, "image" to null)
                        } ?: mapOf("nickname" to userInfoUpdateRequest.nickname)
                    } ?: run {
                    userInfoUpdateRequest.image?.let {
                            if (userInfoUpdateRequest.image != Uri.parse(MyAccountInfoEditFragment.DEFAULT_PROFILE_IMAGE)) {
                                mapOf("image" to userInfoUpdateRequest.image)
                            } else mapOf("image" to null)
                        } ?: error("")
                    }


                val snapshotAsync =
                    async { database.collection("User").whereEqualTo("email", userInfoUpdateRequest.email)
                        .get()
                    }
                val userResponseAsync = async {
                    selectUserInfo(userSelectRequest = UserSelectRequest(userEmail = userInfoUpdateRequest.email))
                }
                val userResponse = userResponseAsync.await()
                val snapshot = snapshotAsync.await()

                val documentId = snapshot.result.documents.firstOrNull()?.id
                documentId?.let {
                    database.collection("User").document(documentId).update(updateData).await()
                }

                userResponse
            }.onFailure {
                Log.d("seungma", "유저데이터소스 업데이트 유저인포 터짐")
                throw FailUpdatetException("업데이트 실패")
            }.getOrThrow()
        }

    override suspend fun sendVerifiedEmail(): UserResponse {
        val currentUser = auth.currentUser
        return kotlin.runCatching {
            currentUser?.let {
                Log.d("SendEmail", "데이터소스")
                it.sendEmailVerification().await()
                UserResponse(it.email, null, null)
            } ?: run {
                Log.d("UserDataSource", "알 수 없는1")
                throw UnKnownException("알 수 없는 에러")
            }
        }.onFailure {
            when (it) {
                is FirebaseAuthException -> throw FailSendEmailException(
                    "메일 발송 실패"
                )

                else -> {
                    Log.d("UserDataSource", "알 수 없는2")
                    throw UnKnownException("알 수 없는 에러")
                }
            }
        }.getOrThrow()
    }

    override suspend fun deleteUserInfo(deleteUserRequest: DeleteUserRequest): UserResponse {
        return kotlin.runCatching {
            database.collection("User")
                .whereEqualTo("email", deleteUserRequest.email).get().await().let {
                    it.documents.firstOrNull()?.reference?.delete()?.await()
                }
            UserResponse(email = deleteUserRequest.email, nickname = null, image = null)
        }.onFailure {
            throw FailDeleteException("딜리트에 실패 했습니다")
        }.getOrThrow()
    }

    override fun getUserInfo(): UserEntity {
        return UserSingleton.userEntity
    }

    override suspend fun login(loginRequest: LoginRequest): UserResponse = with(loginRequest) {
        runCatching {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            user?.let {
                if (!it.isEmailVerified) {
                    Log.d("FirebaseUserDataSource", "이메일 인증 필요")
                }
            } ?: run {
                Log.d("FirebaseUserDataSource", "파이어 베이스 로그인 실패")
            }
        }.onFailure {
            Log.d("FirebaseUserDataSource", "파이어 베이스 로그인 에러")
        }
        val snapshot =
            database.collection("User")
                .whereEqualTo("email", email).get()
                .await()
        runCatching {
            snapshot.documents.firstOrNull()?.let {
                val data = it.data
                data?.let {
                    UserResponse(
                        email = data["email"] as? String,
                        nickname = data["nickname"] as? String,
                        image = (data["image"] as? String)?.let { image ->
                            Uri.parse(image)
                        }
                    )
                }
            } ?: run {
                Log.d("FirebaseUserDataSource", "로그인 정보 DB에 없음")
                throw error("")
            }
        }.onFailure {
            Log.d("FirebaseUserDataSource", "로그인 정보 DB에 없음")
        }.getOrThrow()
    }

    override suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): UserResponse {
        return kotlin.runCatching {
            auth.sendPasswordResetEmail(resetPasswordRequest.email).await()
            UserResponse(resetPasswordRequest.email, null, null)
        }.onFailure {
            throw FailSendEmailException("메일 발송 실패")
        }.getOrThrow()
    }

    override suspend fun selectUserInfo(userSelectRequest: UserSelectRequest): UserResponse {
        return kotlin.runCatching {
            val query = database.collection("User")
                .whereEqualTo("email", userSelectRequest.userEmail)

            val snapshot = query.get().await()

            snapshot.documents.firstOrNull()?.let {
                Log.d("comment", "유저데이터 소스 데이터" + it.data?.get("email") as? String)
                UserResponse(
                    email = it.data?.get("email") as? String,
                    nickname = it.data?.get("nickname") as? String,
                    image = (it.data?.get("image") as? String)?.let { Uri.parse(it) }
                )
            } ?: run {
                throw FailSelectLogInInfoException("로그인 정보 가져오기 실패")
            }
        }.onFailure {

        }.getOrThrow()
    }

    override fun obtainUser(): UserResponse {
        val userEntity = UserSingleton.userEntity
        return UserResponse(
            email = userEntity.email,
            nickname = userEntity.nickname,
            image = userEntity.image
        )
    }
}

