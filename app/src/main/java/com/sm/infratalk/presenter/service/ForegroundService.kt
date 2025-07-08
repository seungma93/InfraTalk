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
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.sm.infratalk.R
import com.sm.infratalk.di.module.Modules
import com.sm.infratalk.presenter.main.activity.MainActivity
import javax.inject.Inject
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import com.sm.infratalk.presenter.viewmodel.ViewModelFactory
import com.sm.infratalk.di.component.DaggerServiceComponent

class ForegroundService : Service() {
    companion object {
        private const val CHANNEL_ID = "InfraTalkMessageChannel"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_NAME = "InfraTalk Messages"
        private const val CHANNEL_DESCRIPTION = "InfraTalk message notification channel"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var serviceViewModel: ServiceViewModel
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // ServiceComponent로 Dagger 주입
        DaggerServiceComponent.factory().create(this).inject(this)

        serviceViewModel = ViewModelProvider(
            ViewModelStore(),
            viewModelFactory
        ).get(ServiceViewModel::class.java)

        // 실제 사용자 이메일로 변경 필요
        val userEmail = "user@email.com"
        serviceViewModel.observeChatNotification(userEmail)

        // 알림 구독: 서비스가 살아있는 동안만 collect
        serviceScope.launch {
            serviceViewModel.chatNotification.collect { notifyEntity ->
                notifyEntity?.let {
                    updateNotification(it.content, it.sender)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
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
                enableVibration(true)
                enableLights(true)
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

    fun updateNotification(message: String, sender: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(sender)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentIntent(createPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
} 