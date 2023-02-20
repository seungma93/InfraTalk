package com.freetalk.data.entity

import android.net.Uri
import java.util.Date

data class BoardEntity (
    val author: String,
    val title: String,
    val context:String,
    val image: List<Uri>,
    val createTime: Date,
    val editTime: Date?,
    )