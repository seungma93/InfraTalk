package com.seungma.infratalk.data.datasource.remote.user


import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonObject
import com.seungma.infratalk.data.BlockedRequestException
import com.seungma.infratalk.data.ExistEmailException
import com.seungma.infratalk.data.FailDeleteUserException
import com.seungma.infratalk.data.FailFirebaseLoginException
import com.seungma.infratalk.data.FailFirebaseSelectUserException
import com.seungma.infratalk.data.FailFirebaseSignupException
import com.seungma.infratalk.data.FailGetUserException
import com.seungma.infratalk.data.FailLogoutException
import com.seungma.infratalk.data.FailResetPasswordException
import com.seungma.infratalk.data.FailUpdateException
import com.seungma.infratalk.data.FailUserDBInsertException
import com.seungma.infratalk.data.FailVerifiedEmailException
import com.seungma.infratalk.data.InvalidEmailException
import com.seungma.infratalk.data.InvalidPasswordException
import com.seungma.infratalk.data.NeedVerifiedEmailException
import com.seungma.infratalk.data.NotExistDBUserInfoException
import com.seungma.infratalk.data.NotExistEmailException
import com.seungma.infratalk.data.NotExistFirebaseCurrentUserException
import com.seungma.infratalk.data.NotExistFirebaseUserException
import com.seungma.infratalk.data.NotExistUpdateInfoException
import com.seungma.infratalk.data.UnKnownException
import com.seungma.infratalk.data.WrongPasswordException
import com.seungma.infratalk.data.datasource.local.preference.PreferenceDataSource
import com.seungma.infratalk.data.model.request.preference.UserTokenSetRequest
import com.seungma.infratalk.data.model.request.user.DeleteUserRequest
import com.seungma.infratalk.data.model.request.user.LoginRequest
import com.seungma.infratalk.data.model.request.user.ResetPasswordRequest
import com.seungma.infratalk.data.model.request.user.SignupRequest
import com.seungma.infratalk.data.model.request.user.UserInfoUpdateRequest
import com.seungma.infratalk.data.model.request.user.UserSelectRequest
import com.seungma.infratalk.data.model.response.user.UserResponse
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.presenter.mypage.fragment.MyAccountInfoEditFragment
import com.teamaejung.aejung.network.RetrofitClient
import com.teamaejung.aejung.network.service.FirebaseAuthService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserRemoteDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val preferenceDataSource: PreferenceDataSource,
    private val retrofitClient: RetrofitClient
) : UserDataSource {

    companion object {
        const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"
        const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
        const val ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
        const val ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
        const val ERROR_WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"
        const val ERROR_TOO_MANY_REQUESTS = "ERROR_TOO_MANY_REQUESTS"
    }

    private fun separatedFirebaseErrorCode(throwable: Throwable): Exception {
        return when (throwable.message) {
            ERROR_INVALID_EMAIL -> InvalidEmailException(
                _message = "유요하지 않은 이메일 입니다",
                throwable = throwable
            )

            ERROR_WRONG_PASSWORD -> WrongPasswordException(
                _message = "잘못된 비밀번호",
                throwable = throwable
            )

            ERROR_USER_NOT_FOUND -> NotExistEmailException(
                _message = "존재하지 않는 이메일",
                throwable = throwable
            )

            ERROR_EMAIL_ALREADY_IN_USE -> ExistEmailException(
                _message = "존재하는 이메일",
                throwable = throwable
            )

            ERROR_WEAK_PASSWORD -> InvalidPasswordException(
                _message = "잘못된 형식의 비밀번호",
                throwable = throwable
            )

            ERROR_TOO_MANY_REQUESTS -> BlockedRequestException(
                _message = "블락된 요청",
                throwable = throwable
            )

            else -> UnKnownException(_message = "알 수 없는 에러")
        }
    }

    override suspend fun signUp(signupRequest: SignupRequest): UserResponse = coroutineScope {
        runCatching {
            val authAsync = async {
                auth.createUserWithEmailAndPassword(
                    signupRequest.email,
                    signupRequest.password
                )
            }
            val insertAsync = async {
                database.collection("User").add(
                    UserEntity(
                        email = signupRequest.email,
                        nickname = signupRequest.nickname,
                        image = Uri.parse(signupRequest.imageUri)
                    )
                )
            }

            val resultAuth = authAsync.await()
            val resultInsert = insertAsync.await()

            // 예외를 명확히 던짐
            if (!resultAuth.isSuccessful) {
                throw FailFirebaseSignupException(
                    _message = "파이어 베이스 가입 어스 실패",
                    throwable = resultAuth.exception
                )
            }
            if (!resultInsert.isSuccessful) {
                throw FailUserDBInsertException(
                    _message = "유저 디비 인서트 실패",
                    throwable = resultInsert.exception
                )
            }

            UserResponse(
                email = signupRequest.email,
                nickname = signupRequest.nickname,
                image = Uri.parse(signupRequest.imageUri)
            )

        }.onFailure {
            when (it) {
                is FirebaseAuthException -> throw separatedFirebaseErrorCode(it)
                is FailFirebaseSignupException, is FailUserDBInsertException -> throw it
                else -> throw UnKnownException(_message = "알 수 없는 에러")
            }
        }.getOrThrow()

    }

    override suspend fun updateUserInfo(userInfoUpdateRequest: UserInfoUpdateRequest): UserResponse =
        coroutineScope {
            runCatching {

                val updateData = userInfoUpdateRequest.nickname?.let {
                    userInfoUpdateRequest.image?.let {
                        if (userInfoUpdateRequest.image != Uri.parse(MyAccountInfoEditFragment.DEFAULT_PROFILE_IMAGE)) {
                            // 닉네임, 프로필
                            mapOf(
                                "nickname" to userInfoUpdateRequest.nickname,
                                "image" to userInfoUpdateRequest.image
                            )
                        } else mapOf(
                            "nickname" to userInfoUpdateRequest.nickname,
                            "image" to null
                        ) // 닉네임, 기본 프로필
                    } ?: mapOf("nickname" to userInfoUpdateRequest.nickname)    // 닉네임
                } ?: run {
                    userInfoUpdateRequest.image?.let {
                        if (userInfoUpdateRequest.image != Uri.parse(MyAccountInfoEditFragment.DEFAULT_PROFILE_IMAGE)) {
                            mapOf("image" to userInfoUpdateRequest.image)   // 프로필
                        } else mapOf("image" to null) // 기본 프로필
                    } ?: throw NotExistUpdateInfoException(_message = "업데이트 할 내용이 없습니다")
                }


                val snapshotAsync =
                    async {
                        database.collection("User")
                            .whereEqualTo("email", userInfoUpdateRequest.email)
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
                throw FailUpdateException(_message = "정보 업데이트 실패", throwable = it)
            }.getOrThrow()
        }

    override suspend fun sendVerifiedEmail(): UserResponse {
        val currentUser = auth.currentUser
        return kotlin.runCatching {
            currentUser?.let {
                it.sendEmailVerification().await()
                UserResponse(it.email, null, null)
            } ?: run {
                throw NotExistFirebaseCurrentUserException("파이어베이스의 커렌트유저가 없습니다")
            }
        }.onFailure {
            when (it) {
                is FirebaseAuthException -> throw FailVerifiedEmailException(
                    _message = "이메일 인증 실패",
                    throwable = it
                )

                is NotExistFirebaseCurrentUserException -> throw it
                else -> throw UnKnownException("알 수 없는 에러")
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
            throw FailDeleteUserException(_message = "유저정보 딜리트에 실패 했습니다", throwable = it)
        }.getOrThrow()
    }

    override suspend fun login(loginRequest: LoginRequest): UserResponse = coroutineScope {
        runCatching {
            val result =
                auth.signInWithEmailAndPassword(loginRequest.email, loginRequest.password).await()
            val user = result.user

            user?.let {
                if (!it.isEmailVerified) {
                    throw NeedVerifiedEmailException(_message = "이메일 인증이 필요")
                }
            } ?: run {
                throw NotExistFirebaseUserException(_message = "파이어베이스에 유저 정보 없음")
            }
        }.onFailure {
            throw FailFirebaseLoginException(_message = "파이어 베이스 로그인 실패", throwable = it)
        }

        val snapshotAsync = async {
            database.collection("User")
                .whereEqualTo("email", loginRequest.email).get().await()
        }
        val tokenAsync = async {
            auth.currentUser?.getIdToken(false)
        }
        val snapshot = snapshotAsync.await()
        val token = tokenAsync.await()
        token?.let {
            it.result.token?.let {
                preferenceDataSource.setUserToken(userTokenSetRequest = UserTokenSetRequest(token = it))
            }
        }
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
            throw NotExistDBUserInfoException(_message = "로그인 정보 DB에 없음")
        }

    }

    override suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): UserResponse {
        return runCatching {
            auth.sendPasswordResetEmail(resetPasswordRequest.email).await()
            UserResponse(resetPasswordRequest.email, null, null)
        }.onFailure {
            throw FailResetPasswordException(_message = "패스워드 초기화 실패", throwable = it)
        }.getOrThrow()
    }

    override suspend fun selectUserInfo(userSelectRequest: UserSelectRequest): UserResponse {
        return kotlin.runCatching {
            val query = database.collection("User")
                .whereEqualTo("email", userSelectRequest.userEmail)

            val snapshot = query.get().await()

            snapshot.documents.firstOrNull()?.let {
                UserResponse(
                    email = it.data?.get("email") as? String,
                    nickname = it.data?.get("nickname") as? String,
                    image = (it.data?.get("image") as? String)?.let { Uri.parse(it) }
                )
            } ?: run {
                throw NotExistDBUserInfoException(_message = "로그인 정보 DB에 없음")
            }
        }.onFailure {
            throw FailFirebaseSelectUserException(_message = "유저 정보 가져오기 실패", throwable = it)
        }.getOrThrow()
    }


    override suspend fun getUserMe(): UserResponse {
        val token = preferenceDataSource.getUserToken().token
        return runCatching {
            val apiKey = "AIzaSyDwVSV8A6EE15B-Vscpfxg-eovbSzRyocE"
            token?.let {
                val getUserMeResponse =
                    retrofitClient.retrofit.create(FirebaseAuthService::class.java).getUserInfo(
                        apiKey = apiKey,
                        request = JsonObject().apply {
                            addProperty("idToken", token)
                        }

                    )
                val email = getUserMeResponse.users?.firstOrNull()?.email
                email?.let {
                    val snapshot = database.collection("User")
                        .whereEqualTo("email", it).get().await()
                    snapshot.documents.firstOrNull()?.let { document ->
                        val data = document.data
                        data?.let {
                            UserResponse(
                                email = data["email"] as? String,
                                nickname = data["nickname"] as? String,
                                image = (data["image"] as? String)?.let { image ->
                                    Uri.parse(image)
                                }
                            )
                        }
                    }
                }
            } ?: run {
                UserResponse(
                    email = null,
                    nickname = null,
                    image = null
                )
            }
        }.onFailure {
            throw FailGetUserException(_message = "유저 정보 가져오기", throwable = it)
        }.getOrThrow()

    }

    override fun logout() {
        kotlin.runCatching {
            preferenceDataSource.deleteUserToken()
            auth.signOut()
        }.onFailure {
            throw FailLogoutException(_message = "로그아웃 실패", throwable = it)
        }

    }
}

