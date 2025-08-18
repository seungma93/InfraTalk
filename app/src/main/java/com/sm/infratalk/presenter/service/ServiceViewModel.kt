package com.sm.infratalk.presenter.service

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.infratalk.domain.chat.entity.ChatNotifyEntity
import com.sm.infratalk.domain.chat.usecase.NotifyChatMessageUseCase
import com.sm.infratalk.domain.user.usecase.GetUserMeUseCase
import com.sm.infratalk.presenter.chat.form.ChatMessageNotifyForm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatNotifyViewEvent(
    val chatNotifyEntity: ChatNotifyEntity
)

class ServiceViewModel @Inject constructor(
    private val notifyChatMessageUseCase: NotifyChatMessageUseCase,
    private val getUserMeUseCase: GetUserMeUseCase
) : ViewModel() {

    private val _chatNotifyViewEvent = MutableSharedFlow<ChatNotifyViewEvent>()
    val chatNotifyViewEvent: SharedFlow<ChatNotifyViewEvent> = _chatNotifyViewEvent.asSharedFlow()

    fun observeChatNotification() {
        viewModelScope.launch {
            Log.d("seungma", "observeChatNotification 시작")
            val userEntity = getUserMeUseCase()
            Log.d("seungma", "getUserMeUseCase 결과: $userEntity")
            
            if (userEntity != null) {
                Log.d("seungma", "사용자 이메일: ${userEntity.email}")
                notifyChatMessageUseCase(ChatMessageNotifyForm(userEntity.email)).collect { entity ->
                    Log.d("seungma", "채팅 알림 수신: $entity")
                    _chatNotifyViewEvent.emit(ChatNotifyViewEvent(
                        chatNotifyEntity = entity
                    ))
                }
            } else {
                Log.e("seungma", "사용자 정보가 null입니다")
            }
        }
    }
} 