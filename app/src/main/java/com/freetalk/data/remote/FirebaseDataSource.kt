package com.freetalk.data.remote


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
}

class FirebaseRemoteDataSourceImpl : UserDataSource<Task<AuthResult>> {
    private val auth = Firebase.auth

    override suspend fun signUp(userData: UserEntity) =
        suspendCoroutine<Task<AuthResult>> { continuation ->
            auth.createUserWithEmailAndPassword(userData.email, userData.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(it)
                    } else {
                        error("회원가입 실패")
                    }
                }
        }
}

