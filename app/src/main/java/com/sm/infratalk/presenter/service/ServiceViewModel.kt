package com.sm.infratalk.presenter.service

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.sm.infratalk.domain.chat.entity.ChatMessageNotifyEntity
import com.sm.infratalk.domain.chat.usecase.NotifyChatMessageUseCase
import com.sm.infratalk.presenter.chat.form.ChatMessageNotifyForm
import kotlinx.coroutines.flow.update
import androidx.lifecycle.ViewModelProvider

class ServiceViewModel(
    private val notifyChatMessageUseCase: NotifyChatMessageUseCase
) : ViewModel() {
    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

    private val _chatNotification = MutableStateFlow<ChatMessageNotifyEntity?>(null)
    val chatNotification: StateFlow<ChatMessageNotifyEntity?> = _chatNotification.asStateFlow()

    fun observeChatNotification(email: String) {
        viewModelScope.launch {
            notifyChatMessageUseCase(ChatMessageNotifyForm(email)).collect { entity ->
                _chatNotification.value = entity
            }
        }
    }

    fun startService(context: Context) {
        viewModelScope.launch {
            val serviceIntent = Intent(context, ForegroundService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            _isServiceRunning.value = true
        }
    }

    fun stopService(context: Context) {
        viewModelScope.launch {
            val serviceIntent = Intent(context, ForegroundService::class.java)
            context.stopService(serviceIntent)
            _isServiceRunning.value = false
        }
    }

    class Factory(
        private val notifyChatMessageUseCase: NotifyChatMessageUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ServiceViewModel(notifyChatMessageUseCase) as T
        }
    }
} 