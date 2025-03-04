package com.sm.infratalk.data.mapper

import com.sm.infratalk.data.model.response.preference.SavedEmailGetResponse
import com.sm.infratalk.domain.user.entity.SavedEmailGetEntity

fun SavedEmailGetResponse.toEntity(): SavedEmailGetEntity {
    return SavedEmailGetEntity(
        email = email ?: ""
    )
}