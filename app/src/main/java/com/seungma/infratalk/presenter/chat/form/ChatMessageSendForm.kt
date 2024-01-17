package com.seungma.infratalk.presenter.chat.form

data class ChatMessageSendForm(
    val chatRoomId: String,
    val content: String
)