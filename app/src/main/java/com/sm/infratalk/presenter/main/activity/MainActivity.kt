package com.sm.infratalk.presenter.main.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sm.infratalk.R
import com.sm.infratalk.databinding.ActivityMainBinding
import com.sm.infratalk.domain.board.entity.BoardContentPrimaryKeyEntity
import com.sm.infratalk.domain.chat.entity.ChatPrimaryKeyEntity
import com.sm.infratalk.presenter.board.fragment.BoardContentFragment
import com.sm.infratalk.presenter.chat.fragment.ChatFragment
import com.sm.infratalk.presenter.main.fragment.MainFragment
import com.sm.infratalk.presenter.service.ServiceViewModel
import com.sm.infratalk.presenter.sign.fragment.LoginMainFragment
import com.sm.infratalk.presenter.sign.fragment.SignUpFragment

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
    private val serviceViewModel: ServiceViewModel by viewModels()

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
            startServiceIfNeeded()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginSuccessKey = intent.getBooleanExtra("loginSuccessKey", false)
        Log.d("MainActivity", "로그인 성공키 :" + loginSuccessKey)
        
        when(loginSuccessKey) {
            true -> {
                navigateFragment(EndPoint.Main)
                checkAndRequestPermissions()
            }
            false -> navigateFragment(EndPoint.LoginMain)
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isEmpty()) {
            startServiceIfNeeded()
        } else {
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    private fun startServiceIfNeeded() {
        if (loginSuccessKey) {
            serviceViewModel.startService(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (loginSuccessKey) {
            serviceViewModel.stopService(this)
        }
        _binding = null
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

}