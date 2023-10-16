package com.freetalk.presenter.form

data class ChatMessageListLoadForm(
    val chatRoomId: String,
    val reload: Boolean
)