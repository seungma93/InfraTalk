package com.sm.infratalk.presenter.service

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServiceViewModel : ViewModel() {
    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

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
} 