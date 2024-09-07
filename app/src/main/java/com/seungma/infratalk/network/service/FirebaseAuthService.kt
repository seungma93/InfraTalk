package com.teamaejung.aejung.network.service

import com.google.gson.JsonObject
import com.seungma.infratalk.data.model.response.UserEmailResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query


interface FirebaseAuthService {
    @POST("/v1/accounts:lookup")
    suspend fun getUserInfo(
        @Query("key") apiKey: String,
        @Body request: JsonObject
    ): UserEmailResponse
}



