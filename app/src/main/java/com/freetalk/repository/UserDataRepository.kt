package com.freetalk.repository

import android.util.Log
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.UserDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

interface UserDataRepository {
    suspend fun signUp(userData: UserEntity): SignUpInfo
}

data class SignUpInfo (
    val userEmail: String
    )

class FirebaseUserDataRepositoryImpl(private val dataSource: UserDataSource<Task<AuthResult>>): UserDataRepository{

    override suspend fun signUp(userData: UserEntity): SignUpInfo {
        val authResult = dataSource.signUp(userData).result
        Log.v("UserDataRepository", authResult.user!!.email!!)
        return SignUpInfo(authResult.user.toString())
    }
}