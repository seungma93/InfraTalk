package com.sm.infratalk.presenter.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.sm.infratalk.R
import com.sm.infratalk.di.component.DaggerServiceComponent
import com.sm.infratalk.di.module.Modules
import com.sm.infratalk.domain.chat.entity.ChatMessageNotifyEntity
import com.sm.infratalk.presenter.main.activity.MainActivity
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

        createNotificationChannel()

        // ViewModel 초기화
        serviceViewModel = ViewModelProvider(this, viewModelFactory)[ServiceViewModel::class.java]
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        serviceViewModel.observeChatNotification()

        subscribe()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("InfraTalk")
            .setContentText("메시지 알림이 활성화되어 있습니다")
            .setSmallIcon(R.drawable.ic_logo)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()
    }

    fun updateNotification(chatMessageNotifyEntity: ChatMessageNotifyEntity) {
        Log.d("ForegroundService", "updateNotification 호출: ${chatMessageNotifyEntity.content}")
        
        val groupKey = "message_notification_group"
        val notificationId = System.currentTimeMillis().toInt()
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("room_id", chatMessageNotifyEntity.roomId)
            putExtra("sender_id", chatMessageNotifyEntity.sender)
        }
        
        Log.d("ForegroundService", "노티 Intent 생성: room_id=${chatMessageNotifyEntity.roomId}, sender_id=${chatMessageNotifyEntity.sender}")
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // 개별 알림 생성
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(chatMessageNotifyEntity.sender)
            .setContentText(chatMessageNotifyEntity.content)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setGroup(groupKey)
            .setAutoCancel(true)
            .build()
        
        // 그룹 요약 알림 생성
        val summaryNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle("")
            .setContentText("")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setDefaults(0)
            .setSilent(true)
            .setGroup(groupKey)
            .setGroupSummary(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)  // 개별 알림
        notificationManager.notify(0, summaryNotification)       // 그룹 요약 알림
    }


    private fun subscribe() {
        Log.d("seungma", "subscribe 시작")
        CoroutineScope(Dispatchers.IO + Job()).launch {
            Log.d("seungma", "subscribe collect 시작")
            serviceViewModel.chatNotification.collect { chatMessage ->
                Log.d("seungma", "채팅 들어옴: $chatMessage")



                // 채팅 메시지가 null이 아니고, 채팅방 활성이 아닐때 노티 밣생
                chatMessage?.let { message ->
                    if(!ActiveChatTracker.isActiveChatWith(message.roomId)) updateNotification(message)
                }
            }
        }
    }


} 