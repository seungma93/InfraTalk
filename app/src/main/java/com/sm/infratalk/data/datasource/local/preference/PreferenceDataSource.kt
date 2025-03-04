package com.sm.infratalk.data.datasource.local.preference

import com.sm.infratalk.data.model.request.preference.SavedEmailSetRequest
import com.sm.infratalk.data.model.request.preference.UserTokenSetRequest
import com.sm.infratalk.data.model.response.preference.SavedEmailGetResponse
import com.sm.infratalk.data.model.response.preference.UserTokenResponse


interface PreferenceDataSource {
    fun getUserToken(): UserTokenResponse
    fun setUserToken(userTokenSetRequest: UserTokenSetRequest)
    fun deleteUserToken()
    fun getSavedEmail(): SavedEmailGetResponse
    fun setSavedEmail(savedEmailSetRequest: SavedEmailSetRequest)
    fun deleteSavedEmail()
}

