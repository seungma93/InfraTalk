package com.teamaejung.aejung.network.service

import com.seungma.infratalk.data.model.request.FirebaseIdTokenRequest
import com.seungma.infratalk.data.model.response.UserEmailResponse
import retrofit2.http.Body
import retrofit2.http.POST


interface FirebaseAuthService {
        @POST("accounts:lookup")
        suspend fun getUserInfo(
                @Body request: FirebaseIdTokenRequest
        ): UserEmailResponse
}



