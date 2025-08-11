package com.sm.infratalk.presenter.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.sm.infratalk.di.component.DaggerServiceComponent
import com.sm.infratalk.presenter.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ForegroundService : Service(), ViewModelStoreOwner {
    companion object {
        private const val CHANNEL_ID = "InfraTalkMessageChannel"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_NAME = "InfraTalk Messages"
        private const val CHANNEL_DESCRIPTION = "InfraTalk message notification channel"
    }
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var serviceViewModel: ServiceViewModel

    override val viewModelStore: androidx.lifecycle.ViewModelStore
        get() = androidx.lifecycle.ViewModelStore()

    override fun onCreate() {
        super.onCreate()
        DaggerServiceComponent.factory().create(this).inject(this)

        createNotificationChannel(
            channelId = CHANNEL_ID,
            channelName = CHANNEL_NAME,
            channelDescription = CHANNEL_DESCRIPTION,
            context = this
        )

        // ViewModel 초기화
        serviceViewModel = ViewModelProvider(this, viewModelFactory)[ServiceViewModel::class.java]
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification(context = this, channelId = CHANNEL_ID)
        startForeground(NOTIFICATION_ID, notification)

        serviceViewModel.observeChatNotification()

        subscribe()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun subscribe() {
        Log.d("seungma", "subscribe 시작")
        CoroutineScope(Dispatchers.IO + Job()).launch {
            Log.d("seungma", "subscribe collect 시작")
            serviceViewModel.chatNotification.collect { chatMessage ->
                Log.d("seungma", "채팅 들어옴: $chatMessage")



                // 채팅 메시지가 null이 아니고, 채팅방 활성이 아닐때 노티 밣생
                chatMessage?.let { message ->
                    if(!ActiveChatTracker.isActiveChatWith(message.roomId)) updateNotification(
                        chatMessageNotifyEntity = message, context = this@ForegroundService, channelId = CHANNEL_ID
                    )
                }
            }
        }
    }


} 