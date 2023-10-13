package com.freetalk.presenter.form

data class ChatMessageSendForm(
    val chatRoomId: String,
    val senderEmail: String,
    val content: String
)