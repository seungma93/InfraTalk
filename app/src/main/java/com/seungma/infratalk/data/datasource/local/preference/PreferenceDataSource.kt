package com.seungma.infratalk.data.datasource.local.preference

import com.seungma.infratalk.data.model.request.preference.SavedEmailSetRequest
import com.seungma.infratalk.data.model.request.preference.UserTokenSetRequest
import com.seungma.infratalk.data.model.response.preference.SavedEmailGetResponse
import com.seungma.infratalk.data.model.response.preference.UserTokenResponse


interface PreferenceDataSource {
    fun getUserToken(): UserTokenResponse
    fun setUserToken(userTokenSetRequest: UserTokenSetRequest)
    fun deleteUserToken()
    fun getSavedEmail(): SavedEmailGetResponse
    fun setSavedEmail(savedEmailSetRequest: SavedEmailSetRequest)
    fun deleteSavedEmail()
}

