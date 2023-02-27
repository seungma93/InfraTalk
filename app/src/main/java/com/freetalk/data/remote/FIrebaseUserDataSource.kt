package com.freetalk.data.remote


import android.util.Log
import com.freetalk.data.entity.UserEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.rpc.context.AttributeContext.Auth
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface UserDataSource<T> {
    suspend fun signUp(userData: UserEntity): T
    suspend fun logIn(userData: UserEntity): T
    suspend fun resetPassword(userData: UserEntity): T
}

data class AuthData(
    val result: AuthResult?,
    val respond: AuthResponse
)

sealed class AuthResponse {
    data class SuccessSignUp(val code: String) : AuthResponse()
    data class SuccessLogIn(val code: String) : AuthResponse()
    data class SuccessSendMail(val code: String) : AuthResponse()
    data class FailSendMail(val code: String) : AuthResponse()
    data class RequireEmail(val code: String) : AuthResponse()
    data class InvalidEmail(val code: String) : AuthResponse()
    data class InvalidPassword(val code: String) : AuthResponse()
    data class WrongPassword(val code: String) : AuthResponse()
    data class BlockedRequest(val code: String) : AuthResponse()
    data class NotExistEmail(val code: String) : AuthResponse()
    data class ExistEmail(val code: String) : AuthResponse()
    data class Error(val code: String) : AuthResponse()
}

class FirebaseUserRemoteDataSourceImpl(private val auth: FirebaseAuth) : UserDataSource<AuthData> {
    private val currentUser = auth.currentUser

    override suspend fun signUp(userData: UserEntity): AuthData {

        return kotlin.runCatching {
            currentUser?.let {
                val signUpResult = auth.createUserWithEmailAndPassword(
                    userData.email,
                    userData.password
                ).await()
                val sendEmailResult = it.sendEmailVerification().await()

                AuthData(signUpResult, AuthResponse.SuccessSignUp("회원가입 성공"))
            } ?: AuthData(null, AuthResponse.Error("예상외의 에러 발생"))

        }.getOrElse {
            if (it is FirebaseAuthException) {
                AuthData(null, separatedErrorCode(it.errorCode))
            } else {
                AuthData(null, AuthResponse.Error("예상외의 에러 발생"))
            }
        }

    }

    private fun separatedErrorCode(errorCode: String): AuthResponse {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> AuthResponse.InvalidEmail("ERROR_INVALID_EMAIL")
            "ERROR_WRONG_PASSWORD" -> AuthResponse.WrongPassword("ERROR_WRONG_PASSWORD")
            "ERROR_USER_NOT_FOUND" -> AuthResponse.NotExistEmail("ERROR_USER_NOT_FOUND")
            "ERROR_EMAIL_ALREADY_IN_USE" -> AuthResponse.ExistEmail("ERROR_EMAIL_ALREADY_IN_USE")
            "ERROR_WEAK_PASSWORD" -> AuthResponse.InvalidPassword("ERROR_WEAK_PASSWORD")
            "ERROR_TOO_MANY_REQUESTS" -> AuthResponse.BlockedRequest("ERROR_TOO_MANY_REQUESTS")
            else -> AuthResponse.Error("파이어베이스 코드 에러")
        }
    }

    override suspend fun logIn(userData: UserEntity): AuthData {

        return kotlin.runCatching {
            val signInResult =
                auth.signInWithEmailAndPassword(userData.email, userData.password).await()
            currentUser?.let {
                val authData = when (it.isEmailVerified) {
                    true -> AuthData(signInResult, AuthResponse.SuccessLogIn("로그인 성공"))
                    false -> AuthData(null, AuthResponse.RequireEmail("이메일 인증이 필요 합니다"))
                }
                authData
            } ?: AuthData(null, AuthResponse.Error("예상외의 에러 발생"))
        }.getOrElse {
            when (it) {
                is FirebaseAuthException -> AuthData(null, separatedErrorCode(it.errorCode))
                else -> AuthData(null, AuthResponse.Error("예상외의 에러 발생"))
            }
        }

    }

    override suspend fun resetPassword(userData: UserEntity): AuthData {

        return kotlin.runCatching {
            val sendEmailResult = auth.sendPasswordResetEmail(userData.email).await()
            AuthData(null, AuthResponse.SuccessSendMail("메일 발송 성공"))
        }.getOrElse {
            AuthData(null, AuthResponse.FailSendMail("메일발송 실패"))
        }
    }

}

