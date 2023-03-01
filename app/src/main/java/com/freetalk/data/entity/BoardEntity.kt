package com.freetalk.data.entity

import android.net.Uri
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date

data class BoardEntity (
    val author: String = "",
    val title: String = "",
    val content:String = "",
    val image: List<Uri> = emptyList(),
    val createTime: Date = Date(),
    val editTime: Date? = null,
    )