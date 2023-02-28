package com.freetalk.data.remote


import android.net.Uri
import android.util.Log
import androidx.compose.runtime.DisposableEffect
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.UserEntity
import com.freetalk.presenter.viewmodel.ViewEvent
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.rpc.context.AttributeContext.Auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface UserDataSource {
    suspend fun signUp(userData: UserEntity): AuthData
    suspend fun logIn(userData: UserEntity): LoginData
    suspend fun resetPassword(userData: UserEntity): AuthData
}

data class AuthData(
    val result: AuthResult?,
    val response: AuthResponse
)

data class ImageData(
    val userData: UserEntity,
    val response: AuthResponse
)

data class LoginData(
    val userEntity: UserEntity?,
    val response: AuthResponse
)

sealed class AuthResponse {
    data class SuccessSignUp(val code: String) : AuthResponse()
    data class SuccessAuth(val code: String) : AuthResponse()
    data class SuccessLogIn(val code: String) : AuthResponse()
    data class SuccessSendMail(val code: String) : AuthResponse()
    data class SuccessUploadImage(val code: String) : AuthResponse()
    data class FailSelect(val code: String) : AuthResponse()
    data class FailInsert(val code: String) : AuthResponse()
    data class FailUploadImage(val code: String) : AuthResponse()
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

class FirebaseUserRemoteDataSourceImpl(
    private val auth: FirebaseAuth, private val database: FirebaseFirestore,
    private val storage: FirebaseStorage
) : UserDataSource {
    private val currentUser = auth.currentUser
    private val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

    override suspend fun signUp(userData: UserEntity): AuthData {


        val authResult = createAuth(userData)

        return when (authResult.response) {
            is AuthResponse.SuccessAuth -> {
                when (val sendEmailResult = sendEmail()) {
                    is AuthResponse.SuccessSendMail -> {
                        userData.image?.let {
                            val uploadImageResult = uploadImageStorage(userData)
                            when (uploadImageResult.response) {
                                is AuthResponse.SuccessUploadImage -> {
                                    when (val insertResult =
                                        insertData(uploadImageResult.userData)) {
                                        is AuthResponse.SuccessSignUp -> {
                                            AuthData(authResult.result, insertResult)
                                        }
                                        else -> {
                                            AuthData(null, insertResult)
                                        }
                                    }
                                }
                                else -> {
                                    AuthData(null, uploadImageResult.response)
                                }
                            }
                        } ?: run {
                            Log.v("FirebaseUserDataSource", "이미지 없음")
                            when (val insertResult = insertData(userData)) {
                                is AuthResponse.SuccessSignUp -> {
                                    AuthData(authResult.result, insertResult)
                                }
                                else -> {
                                    AuthData(null, insertResult)
                                }
                            }
                        }

                    }
                    else -> {
                        AuthData(null, sendEmailResult)
                    }
                }
            }
            else -> {
                authResult
            }
        }

    }

    private suspend fun insertData(userData: UserEntity): AuthResponse {
        return kotlin.runCatching {
            database.collection("User").add(userData).await()
            AuthResponse.SuccessSignUp("회원가입 성공")
        }.getOrElse {
            AuthResponse.FailInsert("인서트 실패")
        }

    }

    private suspend fun uploadImageStorage(userData: UserEntity): ImageData {

        return kotlin.runCatching {
            val newUri = uploadImage(userData.image!!)
            newUri?.let {
                val userEntity = UserEntity(
                    userData.email,
                    userData.password,
                    userData.nickname,
                    newUri
                )
                ImageData(userEntity, AuthResponse.SuccessUploadImage("이미지 업로드 성공"))
            } ?: run {
                ImageData(userData, AuthResponse.FailUploadImage("이미지 업로드 실패"))
            }
        }.getOrElse {
            ImageData(userData, AuthResponse.FailUploadImage("이미지 업로드 실패"))
        }
    }

    private suspend fun uploadImage(uri: Uri): Uri? {

        return kotlin.runCatching {

            val imgFileName = "IMAGE_" + "_" + timeStamp + "_.png"
            val storageRef = storage.reference.child("images").child(imgFileName)
            val res = storageRef.putFile(uri).await()
            val downloadUri = res.storage.downloadUrl.await()
            downloadUri

        }.getOrNull()
    }

    private suspend fun sendEmail(): AuthResponse {

        return kotlin.runCatching {
            currentUser?.let {
                it.sendEmailVerification().await()
                AuthResponse.SuccessSendMail("메일 발송 성공")
            } ?: AuthResponse.Error("예상외의 에러 발생")
        }.getOrElse {
            if (it is FirebaseAuthException) {
                AuthResponse.FailSendMail("메일 발송 실패")
            } else {
                AuthResponse.Error("예상외의 에러 발생")
            }
        }
    }

    private suspend fun createAuth(userData: UserEntity): AuthData {
        return kotlin.runCatching {

            val signUpResult = auth.createUserWithEmailAndPassword(
                userData.email,
                userData.password
            ).await()

            AuthData(signUpResult, AuthResponse.SuccessAuth("인증 성공"))
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

    suspend fun signInAuth(userData: UserEntity): AuthData {

        return kotlin.runCatching {
            val signInResult =
                auth.signInWithEmailAndPassword(userData.email, userData.password).await()
            currentUser?.let {
                val authData = when (it.isEmailVerified) {
                    true -> AuthData(signInResult, AuthResponse.SuccessAuth("인증 성공"))
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


    override suspend fun logIn(userData: UserEntity): LoginData {

        val signInAuthResult = signInAuth(userData)
        return when (signInAuthResult.response) {
            is AuthResponse.SuccessAuth -> {
                kotlin.runCatching {
                    val snapshot =
                        database.collection("User")
                            .whereEqualTo("email", signInAuthResult.result?.user?.email).get()
                            .await()
                        Log.v("signUp", signInAuthResult.result?.user?.email.toString())
                    val userEntity = UserEntity(
                        snapshot.documents[0].data?.get("email") as String,
                        snapshot.documents[0].data?.get("password") as String,
                        snapshot.documents[0].data?.get("nickname") as String,
                        Uri.parse(snapshot.documents[0].data?.get("image") as String)
                    )
                    Log.v("signUp", userEntity.email)

                    LoginData(userEntity, AuthResponse.SuccessLogIn("로그인 성공"))
                }.getOrElse {
                    LoginData(null, AuthResponse.FailSelect("셀렉트 실패"))
                }
            }
            else -> {
                LoginData(null, signInAuthResult.response)
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

