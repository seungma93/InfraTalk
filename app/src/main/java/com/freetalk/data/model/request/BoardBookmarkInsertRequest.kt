package com.freetalk.data.model.request

import com.freetalk.data.UserSingleton
import java.util.Date

data class BoardBookmarkInsertRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val userEmail: String,
    val updateTime: Date
)