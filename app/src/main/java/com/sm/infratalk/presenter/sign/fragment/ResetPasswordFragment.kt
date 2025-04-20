package com.sm.infratalk.presenter.sign.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.sm.infratalk.di.component.DaggerSignFragmentComponent
import com.sm.infratalk.presenter.sign.components.ResetPasswordDialog
import com.sm.infratalk.presenter.sign.viewmodel.SignViewModel
import javax.inject.Inject

class ResetPasswordFragment : DialogFragment() {

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
        // ComposeView를 반환하여 Compose를 사용합니다.
        return ComposeView(requireContext()).apply {
            setContent {
                ResetPasswordDialog(
                    onDismissRequest = { dismiss() },
                    signViewModel = signViewModel // 이 부분에서 ViewModel을 전달합니다.
                )
            }
        }
    }
}