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
import com.sm.infratalk.di.module.Modules
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





    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // ViewModel 초기화
        serviceViewModel = ViewModelProvider(this, viewModelFactory)[ServiceViewModel::class.java]
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        serviceViewModel.observeChatNotification()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }



    override val viewModelStore: androidx.lifecycle.ViewModelStore
        get() = androidx.lifecycle.ViewModelStore()


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

    private fun subscribe() {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            serviceViewModel.chatNotification.collect { chatMessage ->
                Log.d("seungma", "채팅 들어옴 " + chatMessage)
            }
        }
    }


} 