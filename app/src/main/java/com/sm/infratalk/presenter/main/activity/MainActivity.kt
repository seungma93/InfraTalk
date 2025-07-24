package com.sm.infratalk.presenter.main.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sm.infratalk.R
import com.sm.infratalk.databinding.ActivityMainBinding
import com.sm.infratalk.domain.board.entity.BoardContentPrimaryKeyEntity
import com.sm.infratalk.domain.chat.entity.ChatPrimaryKeyEntity
import com.sm.infratalk.presenter.board.fragment.BoardContentFragment
import com.sm.infratalk.presenter.chat.fragment.ChatFragment
import com.sm.infratalk.presenter.main.fragment.MainFragment
import com.sm.infratalk.presenter.service.ForegroundService
import com.sm.infratalk.presenter.service.ServiceViewModel
import com.sm.infratalk.presenter.sign.fragment.LoginMainFragment
import com.sm.infratalk.presenter.sign.fragment.SignUpFragment
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EndPoint {
    object LoginMain : EndPoint()
    object SignUp : EndPoint()
    object Main : EndPoint()
    data class BoardContent(val boardContentPrimaryKeyEntity: BoardContentPrimaryKeyEntity) :
        EndPoint()

    data class Chat(val chatPrimaryKeyEntity: ChatPrimaryKeyEntity) : EndPoint()
    object Error : EndPoint()
}

interface Navigable {
    fun navigateFragment(endPoint: EndPoint)
}

class MainActivity() : AppCompatActivity(), Navigable {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var loginSuccessKey: Boolean = false

    private val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.FOREGROUND_SERVICE_REMOTE_MESSAGING
        )
    } else {
        arrayOf(
            Manifest.permission.FOREGROUND_SERVICE
        )
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            //startService(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginSuccessKey = intent.getBooleanExtra("loginSuccessKey", false)
        Log.d("MainActivity", "로그인 성공키 :" + loginSuccessKey)

        // 노티 클릭으로 들어온 경우 처리
        handleNotificationIntent()

        checkAndRequestPermissions()

        when (loginSuccessKey) {
            true -> {
                startService(this)
                navigateFragment(EndPoint.Main)
            }

            false -> navigateFragment(EndPoint.LoginMain)
        }
    }

    private fun checkAndRequestPermissions() {
        Log.d("seungma", "checkAndRequestPermissions")
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isEmpty() || permissionsToRequest.first() == "android.permission.FOREGROUND_SERVICE_REMOTE_MESSAGING") {
            Log.d("seungma", "checkAndRequestPermissions True")
            //startService(this)
        } else {
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    private fun handleNotificationIntent() {
        val roomId = intent.getStringExtra("room_id")
        val senderId = intent.getStringExtra("sender_id")


        if(roomId != null && senderId != null){
            Log.d("MainActivity", "노티 클릭으로 채팅방 이동: $roomId")
            // 채팅방으로 이동하는 로직
            // ChatPrimaryKeyEntity 생성 후 ChatFragment로 이동
            //TODO Key에 들어갈 partnerEmail 작업 필요
            val chatPrimaryKey = ChatPrimaryKeyEntity(roomId, senderId)
            navigateFragment(EndPoint.Chat(chatPrimaryKey))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 모든 서비스 종료
        stopAllServices()
        _binding = null
    }

    private fun stopAllServices() {
        Log.d("MainActivity", "모든 서비스 종료")
        // ForegroundService 종료
        stopService(this)
        
        // 다른 서비스들도 있다면 여기에 추가
        // stopService(Intent(this, OtherService::class.java))
    }

    private fun setFragment(fragment: Fragment, viewId: Int, backStackToken: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        when (backStackToken) {
            true -> {
                transaction.replace(viewId, fragment)
                    .addToBackStack(null)
                    .commit()
            }

            false -> {
                transaction.replace(viewId, fragment)
                    .commit()
            }
        }

    }

    override fun navigateFragment(endPoint: EndPoint) {
        when (endPoint) {
            is EndPoint.LoginMain -> {
                val fragment = LoginMainFragment()
                setFragment(fragment, R.id.activity_frame_layout, false)
            }

            is EndPoint.SignUp -> {
                val fragment = SignUpFragment()
                setFragment(fragment, R.id.activity_frame_layout, true)
            }

            is EndPoint.Main -> {
                val fragment = MainFragment()
                setFragment(fragment, R.id.activity_frame_layout, false)
            }

            is EndPoint.BoardContent -> {
                val fragment =
                    BoardContentFragment.newInstance(endPoint.boardContentPrimaryKeyEntity)
                setFragment(fragment, R.id.activity_frame_layout, true)
            }

            is EndPoint.Chat -> {
                val fragment = ChatFragment.newInstance(endPoint.chatPrimaryKeyEntity)
                setFragment(fragment, R.id.activity_frame_layout, true)
            }

            is EndPoint.Error -> {
            }
        }
    }

    fun startService(context: Context) {
        Log.d("seungma", "서비스 시작 뷰모델")
            val serviceIntent = Intent(context, ForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }

    }

    fun stopService(context: Context) {
            val serviceIntent = Intent(context, ForegroundService::class.java)
            context.stopService(serviceIntent)

    }

}