package com.sm.infratalk.presenter.sign.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sm.infratalk.di.component.DaggerSignFragmentComponent
import com.sm.infratalk.presenter.sign.components.LoginScreen
import com.sm.infratalk.presenter.sign.viewmodel.SignViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModelProvider
import com.sm.infratalk.presenter.main.activity.EndPoint
import com.sm.infratalk.presenter.main.activity.Navigable

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
                LoginScreen(
                    viewModel = signViewModel,
                    onSignUpClick = {
                        (requireActivity() as? Navigable)?.navigateFragment(EndPoint.SignUp)
                    },
                    onLoginSuccess = {
                        (requireActivity() as? Navigable)?.navigateFragment(EndPoint.Main)
                    },
                    onResetPasswordClick = {
                        val dialogFragment = ResetPasswordFragment()
                        dialogFragment.show(childFragmentManager, "CustomDialog")
                    }
                )
            }
        }
    }
}
