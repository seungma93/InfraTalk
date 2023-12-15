package com.freetalk.data.datasource.remote


import android.net.Uri
import android.util.Log
import com.freetalk.data.*
import com.freetalk.data.model.request.UserInfoUpdateRequest
import com.freetalk.data.model.request.UserSelectRequest
import com.freetalk.data.model.response.UserResponse
import com.freetalk.domain.entity.UserEntity
import com.freetalk.presenter.form.LogInForm
import com.freetalk.presenter.form.ResetPasswordForm
import com.freetalk.presenter.form.SignUpForm
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface UserDataSource {
    suspend fun signUp(signUpForm: SignUpForm): UserResponse
    suspend fun logIn(logInForm: LogInForm): UserResponse
    suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserResponse
    suspend fun updateUserInfo(userInfoUpdateRequest: UserInfoUpdateRequest): UserResponse
    suspend fun sendVerifiedEmail(): UserResponse
    suspend fun deleteUserInfo(signUpForm: SignUpForm): UserResponse
    fun getUserInfo(): UserEntity
    suspend fun selectUserInfo(userSelectRequest: UserSelectRequest): UserResponse

    fun obtainUser(): UserResponse
}

class FirebaseUserRemoteDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : UserDataSource {
    private val currentUser = auth.currentUser

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

    override suspend fun signUp(signUpForm: SignUpForm): UserResponse {
        Log.d("FirebaseUserData", "시작")
        insertData(UserEntity(signUpForm.email, signUpForm.nickname, null))
        val createAuthResult = createAuth(signUpForm)
        return UserResponse(createAuthResult.user?.email.toString(), signUpForm.nickname)
    }

    override suspend fun updateUserInfo(userInfoUpdateRequest: UserInfoUpdateRequest): UserResponse =
        with(userInfoUpdateRequest) {
            return kotlin.runCatching {

                val updateMap = nickname?.let {
                    image?.let {
                        mapOf("nickname" to nickname, "image" to image)
                    } ?: mapOf("nickname" to nickname)
                } ?: run {
                    image?.let {
                        mapOf("image" to image)
                    } ?: error("")
                }

                database.collection("User")
                    .whereEqualTo("email", email).get().await().let {
                        it.documents.firstOrNull()?.reference?.update(updateMap)?.await()
                    }

                selectUserInfo(userSelectRequest = UserSelectRequest(userEmail = email))
            }.onFailure {
                throw FailUpdatetException("업데이트 실패")
            }.getOrThrow()
        }

    override suspend fun sendVerifiedEmail(): UserResponse {
        return kotlin.runCatching {
            currentUser?.let {
                Log.d("SendEmail", "데이터소스")
                it.sendEmailVerification().await()
                UserResponse(currentUser.email, null, null)
            } ?: run {
                Log.d("UserDataSource", "알 수 없는1")
                throw UnKnownException("알 수 없는 에러")
            }
        }.onFailure {
            when (it) {
                is FirebaseAuthException -> throw FailSendEmailException("메일 발송 실패")
                else -> {
                    Log.d("UserDataSource", "알 수 없는2")
                    throw UnKnownException("알 수 없는 에러")
                }
            }
        }.getOrThrow()
    }

    override suspend fun deleteUserInfo(signUpForm: SignUpForm): UserResponse {
        return kotlin.runCatching {
            database.collection("User")
                .whereEqualTo("email", signUpForm.email).get().await().let {
                    it.documents.firstOrNull()?.reference?.delete()?.await()
                }
            UserResponse(email = signUpForm.email, nickname = null, image = null)
        }.onFailure {
            throw FailDeleteException("딜리트에 실패 했습니다")
        }.getOrThrow()
    }

    override fun getUserInfo(): UserEntity {
        return UserSingleton.userEntity
    }

    private suspend fun createAuth(signUpForm: SignUpForm): AuthResult {
        return kotlin.runCatching {
            auth.createUserWithEmailAndPassword(
                signUpForm.email,
                signUpForm.password
            ).await()
        }.onFailure {
            when (it) {
                is FirebaseAuthException -> throw separatedFirebaseErrorCode(it.errorCode)
                else -> throw UnKnownException("알 수 없는 에러")
            }
        }.getOrThrow()
    }

    private suspend fun insertData(userEntity: UserEntity): DocumentReference {
        return kotlin.runCatching {
            Log.d("insertData", "시작")
            database.collection("User").add(userEntity).await()
        }.onFailure {
            throw FailInsertException("인서트에 실패 했습니다")
        }.getOrThrow()
    }

    override suspend fun logIn(logInForm: LogInForm): UserResponse {

        val logInAuthResult = logInAuth(logInForm)
        return kotlin.runCatching {
            val snapshot =
                database.collection("User")
                    .whereEqualTo("email", logInAuthResult).get()
                    .await()
            snapshot.documents.firstOrNull()?.let {
                UserResponse(
                    email = it.data?.get("email") as? String,
                    nickname = it.data?.get("nickname") as? String,
                    image = (it.data?.get("image") as? String)?.let { Uri.parse(it) }
                )
            } ?: run {
                throw FailSelectLogInInfoException("로그인 정보 가져오기 실패")
            }
        }.onFailure {
            Log.d("UserDataSource", it.message.toString())
            throw FailSelectException("셀렉트 실패", it)
        }.getOrThrow()
    }

    private suspend fun logInAuth(logInForm: LogInForm): String {

        return kotlin.runCatching {
            auth.signInWithEmailAndPassword(logInForm.email, logInForm.password).await()
            Log.d("UserDataSource", currentUser?.email.toString())
            currentUser?.let {
                when (it.isEmailVerified) {
                    true -> it.email
                    false -> {
                        Log.d("UserDataSource", "로그인 1")
                        throw VerifiedEmailException("이메일 인증이 필요 합니다")
                    }
                }
            } ?: run {
                Log.d("UserDataSource", "로그인 2")
                throw UnKnownException("알 수 없는 에러")
            }
        }.onFailure {
            when (it) {
                is VerifiedEmailException -> throw VerifiedEmailException("이메일 인증이 필요 합니다")
                else -> {
                    Log.d("UserDataSource", "로그인 3")
                    Log.d("UserDataSource", it.message.toString())
                    throw UnKnownException("알 수 없는 에러")
                }
            }
        }.getOrThrow()
    }

    override suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserResponse {

        return kotlin.runCatching {
            auth.sendPasswordResetEmail(resetPasswordForm.email).await()
            UserResponse(resetPasswordForm.email, null, null)
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

