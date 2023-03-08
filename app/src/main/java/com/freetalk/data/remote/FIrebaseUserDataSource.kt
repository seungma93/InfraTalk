package com.freetalk.data.remote


import android.net.Uri
import android.util.Log
import com.freetalk.data.entity.UserEntity
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

interface UserDataSource {
    suspend fun signUp(signUpForm: SignUpForm): UserResponse
    suspend fun logIn(logInForm: LogInForm): UserResponse
    suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserResponse
    suspend fun updateUserInfo(updateForm: UpdateForm): UserResponse
    suspend fun sendVerifiedEmail(): UserResponse
}

class InvalidEmailException(
    val _message: String
) : Exception(_message)

class VerifiedEmailException(
    val _message: String
) : Exception(_message)

class InvalidPasswordException(
    val _message: String
) : Exception(_message)

class WrongPasswordException(
    val _message: String
) : Exception(_message)

class BlockedRequestException(
    val _message: String
) : Exception(_message)

class NotExistEmailException(
    val _message: String
) : Exception(_message)

class ExistEmailException(
    val _message: String
) : Exception(_message)

class UnKnownException(
    val _message: String
) : Exception(_message)

class FailSendEmailException(
    val _message: String
) : Exception(_message)

class FailInsertException(
    val _message: String
) : Exception(_message)

class FailUpdatetException(
    val _message: String
) : Exception(_message)

class NoImageException(
    val _message: String
) : Exception(_message)

class FailSelectException(
    val _message: String
) : Exception(_message)




private fun separatedFirebaseErrorCode(errorCode: String): Exception {
    return when (errorCode) {
        "ERROR_INVALID_EMAIL" -> InvalidEmailException("유효하지 않은 이메일")
        "ERROR_WRONG_PASSWORD" -> WrongPasswordException("잘못된 비밀번호")
        "ERROR_USER_NOT_FOUND" -> NotExistEmailException("존재하지 않는 이메일")
        "ERROR_EMAIL_ALREADY_IN_USE" -> ExistEmailException("존재하는 이메일")
        "ERROR_WEAK_PASSWORD" -> InvalidPasswordException("잘못된 형식의 비밀번호")
        "ERROR_TOO_MANY_REQUESTS" -> BlockedRequestException("블락된 요청")
        else -> UnKnownException("알 수 없는 에러")
    }
}

data class UserResponse(
    val email: String? = null,
    val nickname: String? = null,
    val image: Uri? = null
)

data class SignUpForm(
    val email: String,
    val password: String,
    val nickname: String
)

data class LogInForm(
    val email: String,
    val password: String
)

data class UpdateForm(
    val email: String,
    val nickname: String?,
    val image: Uri?
)

data class ResetPasswordForm(
    val email: String
)

class FirebaseUserRemoteDataSourceImpl(
    private val auth: FirebaseAuth, private val database: FirebaseFirestore
) : UserDataSource {
    private val currentUser = auth.currentUser

    override suspend fun signUp(signUpForm: SignUpForm): UserResponse {
        insertData(UserEntity(signUpForm.email, signUpForm.nickname, null))
        val createAuthResult = createAuth(signUpForm)
        return UserResponse(createAuthResult.user?.email.toString(), signUpForm.nickname)
    }

    override suspend fun updateUserInfo(updateForm: UpdateForm): UserResponse {
        return kotlin.runCatching {
            FirebaseFirestore.getInstance().collection("User")
                .whereEqualTo("email", updateForm.email).get().await().let {
                it.documents[0].reference.set(updateForm).await()
            }
            UserResponse(updateForm.email, updateForm.nickname, updateForm.image)
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
            } ?: run {Log.d("UserDataSource" ,"알 수 없는1")
                throw UnKnownException("알 수 없는 에러")
            }
        }.onFailure {
            when (it) {
                is FirebaseAuthException -> throw FailSendEmailException("메일 발송 실패")
                else -> {Log.d("UserDataSource" ,"알 수 없는2")
                    throw UnKnownException("알 수 없는 에러")
                }
            }
        }.getOrThrow()
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
            UserResponse(
                snapshot.documents[0].data?.get("email") as String,
                snapshot.documents[0].data?.get("nickname") as String,
                Uri.parse(snapshot.documents[0].data?.get("image") as String)
            )
        }.onFailure {
            throw FailSelectException("셀렉트 실패")
        }.getOrThrow()
    }


    private suspend fun logInAuth(logInForm: LogInForm): String {

         return kotlin.runCatching {
            auth.signInWithEmailAndPassword(logInForm.email, logInForm.password).await()
             Log.d("UserDataSource" ,currentUser?.email.toString())
            currentUser?.let {
                when (it.isEmailVerified) {
                    true -> it.email
                    false -> {
                        Log.d("UserDataSource" ,"로그인 1")
                        throw VerifiedEmailException("이메일 인증이 필요 합니다")
                    }
                }
            } ?: run {Log.d("UserDataSource" ,"로그인 2")
                throw UnKnownException("알 수 없는 에러")
            }
        }.onFailure {
            when (it) {
                is VerifiedEmailException -> throw VerifiedEmailException("이메일 인증이 필요 합니다")
                else -> {
                    Log.d("UserDataSource" ,"로그인 3")
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



}

