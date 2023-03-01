package com.freetalk.data.entity

import android.net.Uri
import com.freetalk.data.UserSingleton
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date

data class BoardEntity (
    val author: UserSingleton = UserSingleton,
    val title: String = "",
    val content:String = "",
    val image: List<Uri> = emptyList(),
    val createTime: Date = Date(),
    val editTime: Date? = null,
    )