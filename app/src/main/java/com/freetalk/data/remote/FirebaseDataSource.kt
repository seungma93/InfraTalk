package com.freetalk.data.remote


import android.util.Log
import com.freetalk.data.entity.UserEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.intrinsics.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface UserDataSource<T> {
    suspend fun signUp(userData: UserEntity): T
    suspend fun logIn(userData: UserEntity): T
    suspend fun resetPassword(userData: UserEntity): T
}

data class AuthData(
    val task: Task<AuthResult>?,
    val message: String
)

class FirebaseRemoteDataSourceImpl(private val auth: FirebaseAuth) : UserDataSource<AuthData> {
    private val currentUser = auth.currentUser
    override suspend fun signUp(userData: UserEntity) =
        suspendCoroutine<AuthData> { continuation ->
            auth.createUserWithEmailAndPassword(userData.email, userData.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                            if (it.isSuccessful) {
                                continuation.resume(AuthData(it, "회원가입 성공 이메일 확인 해주세요"))
                            } else {
                                continuation.resume(AuthData(it, task.exception.toString()))
                            }
                        }
                    } else {
                        Log.v("DataSource", it.exception.toString())
                        continuation.resume(AuthData(it, it.exception.toString()))
                    }
                }
        }

    override suspend fun logIn(userData: UserEntity) =
        suspendCoroutine<AuthData> { continuation ->
            auth.signInWithEmailAndPassword(userData.email, userData.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (currentUser!!.isEmailVerified) {
                            continuation.resume(AuthData(it, "로그인 성공"))
                        } else {
                            continuation.resume(AuthData(it, "이메일 인증이 필요합니다"))
                        }

                    } else {
                        Log.v("DataSource", it.exception!!.toString())
                        continuation.resume(AuthData(it, it.exception!!.toString()))
                    }
                }
        }

    override suspend fun resetPassword(userData: UserEntity) =
        suspendCoroutine<AuthData> { continuation ->
            auth.sendPasswordResetEmail(userData.email).addOnCompleteListener {
                if (it.isSuccessful) {
                    continuation.resume(AuthData(null, "메일발송 성공"))
                }else {
                    Log.v("DataSource",it.exception.toString())
                    continuation.resume(AuthData(null, "메일발송 실패"))
                }
            }

        }

}

