package com.sm.infratalk.presenter.sign.fragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.sm.infratalk.di.component.DaggerSignFragmentComponent
import com.sm.infratalk.presenter.main.activity.EndPoint
import com.sm.infratalk.presenter.main.activity.Navigable
import com.sm.infratalk.presenter.sign.components.LoginScreen
import com.sm.infratalk.presenter.service.ForegroundService
import com.sm.infratalk.presenter.sign.form.LoginForm
import com.sm.infratalk.presenter.sign.form.SavedEmailSetForm
import com.sm.infratalk.presenter.sign.viewmodel.SignViewModel
import javax.inject.Inject

class LoginMainFragment : Fragment() {
    @Inject
    lateinit var signViewModelFactory: ViewModelProvider.Factory
    private val signViewModel: SignViewModel by viewModels { signViewModelFactory }

    override fun onAttach(context: Context) {
        DaggerSignFragmentComponent.factory().create(context).inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                // 스낵바 상태 관리
                var showSnackbar by remember { mutableStateOf(false) }
                var snackbarMessage by remember { mutableStateOf("") }

                // ViewModel의 이벤트를 구독
                val viewEvent by signViewModel.viewEvent.collectAsState(initial = null)

                LoginScreen(
                    viewModel = signViewModel,
                    onSignUpClick = {
                        (requireActivity() as? Navigable)?.navigateFragment(EndPoint.SignUp)
                    },
                    onLoginSuccess = {
                        startService(context = requireContext())
                        (requireActivity() as? Navigable)?.navigateFragment(EndPoint.Main)
                    },
                    onResetPasswordClick = {
                        val dialogFragment = ResetPasswordFragment()
                        dialogFragment.show(childFragmentManager, "CustomDialog")
                    },
                    // 에러 처리 콜백 추가
                    onError = { errorMessage ->
                        snackbarMessage = errorMessage
                        showSnackbar = true
                    },
                    // 스낵바 상태 전달
                    showSnackbar = showSnackbar,
                    snackbarMessage = snackbarMessage,
                    onDismissSnackbar = { showSnackbar = false }
                )
            }
        }
    }

    private fun startService(context: Context) {
        Log.d("seungma", "서비스 시작 뷰모델")
        val serviceIntent = Intent(context, ForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

    }

}