package com.seungma.infratalk.data.mapper

import com.seungma.infratalk.data.model.response.preference.SavedEmailGetResponse
import com.seungma.infratalk.domain.user.entity.SavedEmailGetEntity

fun SavedEmailGetResponse.toEntity(): SavedEmailGetEntity {
    return SavedEmailGetEntity(
        email = email ?: ""
    )
}