package com.seungma.infratalk.presenter.chat.form

data class ChatMessageListLoadForm(
    val chatRoomId: String,
    val reload: Boolean
)