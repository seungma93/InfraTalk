package com.freetalk.data.remote


import android.util.Log
import com.freetalk.data.entity.UserEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
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
}

data class AuthData(
    val task: Task<AuthResult>,
    val message: String
)

class FirebaseRemoteDataSourceImpl : UserDataSource<AuthData> {
    private val auth = Firebase.auth

    override suspend fun signUp(userData: UserEntity) =
        suspendCoroutine<AuthData> { continuation ->
            auth.createUserWithEmailAndPassword(userData.email, userData.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(AuthData(it, "회원가입 성공"))
                    } else  {
                        Log.v("DataSource", it.exception!!.toString())
                        continuation.resume(AuthData(it, it.exception!!.toString()))
                    }
                }
        }

    override suspend fun logIn(userData: UserEntity)=
        suspendCoroutine<AuthData> { continuation ->
            auth.signInWithEmailAndPassword(userData.email, userData.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(AuthData(it, "로그인 성공"))
                    } else  {
                        Log.v("DataSource", it.exception!!.toString())
                        continuation.resume(AuthData(it, it.exception!!.toString()))
                    }
                }
        }

}

