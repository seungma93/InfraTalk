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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ServiceViewModel @Inject constructor(
    private val notifyChatMessageUseCase: NotifyChatMessageUseCase,
    private val getUserMeUseCase: GetUserMeUseCase
) : ViewModel() {
    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

    private val _chatNotification = MutableSharedFlow<ChatMessageNotifyEntity?>()
    val chatNotification: SharedFlow<ChatMessageNotifyEntity?> = _chatNotification.asSharedFlow()

    fun observeChatNotification() {
        viewModelScope.launch {
            Log.d("seungma", "observeChatNotification 시작")
            val userEntity = getUserMeUseCase()
            Log.d("seungma", "getUserMeUseCase 결과: $userEntity")
            
            if (userEntity != null) {
                Log.d("seungma", "사용자 이메일: ${userEntity.email}")
                notifyChatMessageUseCase(ChatMessageNotifyForm(userEntity.email)).collect { entity ->
                    Log.d("seungma", "채팅 알림 수신: $entity")
                    _chatNotification.emit(entity)
                }
            } else {
                Log.e("seungma", "사용자 정보가 null입니다")
            }
        }
    }
} 