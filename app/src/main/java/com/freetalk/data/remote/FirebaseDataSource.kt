package com.freetalk.data.remote


import android.util.Log
import com.freetalk.data.entity.UserEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface UserDataSource<T> {
    suspend fun signUp(userData: UserEntity): T
    suspend fun logIn(userData: UserEntity): T
    suspend fun resetPassword(userData: UserEntity): T
}

data class AuthData(
    val task: Task<AuthResult>?,
    val respond: AuthRespond
)

sealed class AuthRespond {
    data class SuccessSignUp(val code: String) : AuthRespond()
    data class SuccessLogIn(val code: String) : AuthRespond()
    data class SuccessSendMail(val code: String) : AuthRespond()
    data class FailSendMail(val code: String) : AuthRespond()
    data class RequireEmail(val code: String) : AuthRespond()
    data class InvalidEmail(val code: String) : AuthRespond()
    data class InvalidPassword(val code: String) : AuthRespond()
    data class WrongPassword(val code: String) : AuthRespond()
    data class BlockedRequest(val code: String) : AuthRespond()
    data class NotExistEmail(val code: String) : AuthRespond()
    data class ExistEmail(val code: String) : AuthRespond()
    data class Error(val code: String) : AuthRespond()
}

class FirebaseRemoteDataSourceImpl(private val auth: FirebaseAuth) : UserDataSource<AuthData> {
    private val currentUser = auth.currentUser
    override suspend fun signUp(userData: UserEntity) =
        suspendCoroutine<AuthData> { continuation ->
            auth.createUserWithEmailAndPassword(userData.email, userData.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                            if (it.isSuccessful) {
                                continuation.resume(AuthData(it, AuthRespond.SuccessSignUp("회원가입 성공")))
                            } else {
                                continuation.resume(
                                    AuthData(
                                        it,
                                        separatedErrorCode((it.exception as FirebaseAuthException).errorCode)
                                    )
                                )
                            }
                        }
                    } else {
                        it.exception
                        Log.v("DataSource", (it.exception as FirebaseAuthException).errorCode)
                        continuation.resume(AuthData(it, separatedErrorCode((it.exception as FirebaseAuthException).errorCode)))
                    }
                }
        }

    private fun separatedErrorCode(errorCode: String): AuthRespond {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" ->  AuthRespond.InvalidEmail("ERROR_INVALID_EMAIL")
            "ERROR_WRONG_PASSWORD" -> AuthRespond.WrongPassword("ERROR_WRONG_PASSWORD")
            "ERROR_USER_NOT_FOUND" -> AuthRespond.NotExistEmail("ERROR_USER_NOT_FOUND")
            "ERROR_EMAIL_ALREADY_IN_USE" -> AuthRespond.ExistEmail("ERROR_EMAIL_ALREADY_IN_USE")
            "ERROR_WEAK_PASSWORD" -> AuthRespond.InvalidPassword("ERROR_WEAK_PASSWORD")
            "ERROR_TOO_MANY_REQUESTS" -> AuthRespond.BlockedRequest("ERROR_TOO_MANY_REQUESTS")
            else -> AuthRespond.Error("파이어베이스 코드 에러")
        }
    }

    override suspend fun logIn(userData: UserEntity) =
        suspendCoroutine<AuthData> { continuation ->
            auth.signInWithEmailAndPassword(userData.email, userData.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (currentUser != null) {
                            if (currentUser.isEmailVerified) {
                                continuation.resume(AuthData(it, AuthRespond.SuccessLogIn("로그인 성공")))
                            } else {
                                continuation.resume(AuthData(it, AuthRespond.RequireEmail("이메일이 필요합니다")))
                            }
                        } else {
                            error("커렌트 유저 에러")
                        }
                    } else {
                        Log.v("DataSource", (it.exception as FirebaseAuthException).errorCode)
                        continuation.resume(AuthData(it, separatedErrorCode((it.exception as FirebaseAuthException).errorCode)))
                    }
                }
        }

    override suspend fun resetPassword(userData: UserEntity) =
        suspendCoroutine<AuthData> { continuation ->
            auth.sendPasswordResetEmail(userData.email).addOnCompleteListener {
                if (it.isSuccessful) {
                    continuation.resume(AuthData(null, AuthRespond.SuccessSendMail("메일발송 성공")))
                } else {
                    Log.v("DataSource", (it.exception as FirebaseAuthException).errorCode)
                    continuation.resume(AuthData(null, AuthRespond.FailSendMail("메일발송 실패")))
                }
            }

        }

}

