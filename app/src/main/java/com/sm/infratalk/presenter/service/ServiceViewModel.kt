package com.sm.infratalk.presenter.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.infratalk.domain.chat.entity.ChatMessageNotifyEntity
import com.sm.infratalk.domain.chat.usecase.NotifyChatMessageUseCase
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.domain.user.usecase.GetUserMeUseCase
import com.sm.infratalk.presenter.chat.form.ChatMessageNotifyForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ServiceViewModel @Inject constructor(
    private val notifyChatMessageUseCase: NotifyChatMessageUseCase,
    private val getUserMeUseCase: GetUserMeUseCase
) : ViewModel() {
    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

    private val _chatNotification = MutableStateFlow<ChatMessageNotifyEntity?>(null)
    val chatNotification: StateFlow<ChatMessageNotifyEntity?> = _chatNotification.asStateFlow()

    private fun observeChatNotification() {
        viewModelScope.launch {
            val userEntity = getUserMeUseCase()
            notifyChatMessageUseCase(ChatMessageNotifyForm(userEntity.email)).collect { entity ->
                _chatNotification.value = entity
            }
        }
    }
} 