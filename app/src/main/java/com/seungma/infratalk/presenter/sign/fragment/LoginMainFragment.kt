package com.seungma.infratalk.presenter.sign.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.seungma.infratalk.databinding.FragmentLoginMainBinding
import com.seungma.infratalk.di.component.DaggerSignFragmentComponent
import com.seungma.infratalk.domain.user.UserEntity
import com.seungma.infratalk.presenter.main.activity.EndPoint
import com.seungma.infratalk.presenter.main.activity.Navigable
import com.seungma.infratalk.presenter.sign.form.LogInForm
import com.seungma.infratalk.presenter.sign.viewmodel.SignViewModel
import com.seungma.infratalk.presenter.sign.viewmodel.ViewEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginMainFragment : Fragment() {
    private var _binding: FragmentLoginMainBinding? = null
    private val binding get() = _binding!!

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
    ): View? {
        _binding = FragmentLoginMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnSignUp.setOnClickListener {
                val signUpEndPoint = EndPoint.SignUp
                (requireActivity() as? Navigable)?.navigateFragment(signUpEndPoint)
            }
            btnLogin.setOnClickListener {
                val inputId = binding.emailTextInput.editText!!.text.toString()
                val inputPassword = binding.passwordTextInput.editText!!.text.toString()

                when {
                    inputId.isNullOrEmpty() -> Toast.makeText(
                        requireActivity(), "이메일을 입력하세요",
                        Toast.LENGTH_SHORT
                    ).show()

                    inputPassword.isNullOrEmpty() -> Toast.makeText(
                        requireActivity(), "비밀번호를 입력하세요",
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> {
                        showProgressBar()
                        viewLifecycleOwner.lifecycleScope.launch {
                            signViewModel.logIn(LogInForm(inputId, inputPassword))
                        }
                    }
                }
            }
            btnFindAccount.setOnClickListener {
                val dialogFragment = ResetPasswordFragment()
                dialogFragment.show(childFragmentManager, "CustomDialog")
            }
            btnTest.setOnClickListener {
                com.seungma.infratalk.data.UserSingleton.userEntity =
                    UserEntity(email = "test@naver.com", image = null, nickname = "상실이")
                (requireActivity() as? Navigable)?.navigateFragment(EndPoint.Main)
            }
        }
        subsribe()
    }

    private fun showProgressBar() {
        blockLayoutTouch()
        binding.progressBar.isVisible = true
    }

    private fun blockLayoutTouch() {
        requireActivity().window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideProgressBar() {
        clearBlockLayoutTouch()
        binding.progressBar.isVisible = false
    }

    private fun clearBlockLayoutTouch() {
        requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun subsribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            signViewModel.viewEvent.collect {
                when (it) {
                    is ViewEvent.LogIn -> {
                        hideProgressBar()
                        it.userEntity?.let { userEntity ->
                            com.seungma.infratalk.data.UserSingleton.userEntity = userEntity
                        }
                        Log.d("LogInMainF", " 로그인 프레그먼트")
                        (requireActivity() as? Navigable)?.navigateFragment(EndPoint.Main)
                    }

                    is ViewEvent.Error -> {
                        Log.d("LogInMainF", " 에러 발생")
                        hideProgressBar()
                        when (it.errorCode) {
                            is com.seungma.infratalk.data.NotExistEmailException -> Toast.makeText(
                                requireActivity(), "등록된 이메일이 없습니다",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.seungma.infratalk.data.InvalidEmailException -> Toast.makeText(
                                requireActivity(), "이메일을 확인하세요",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.seungma.infratalk.data.WrongPasswordException -> Toast.makeText(
                                requireActivity(), "암호가 틀렸습니다",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.seungma.infratalk.data.VerifiedEmailException -> Toast.makeText(
                                requireActivity(), "이메일 인증이 필요 합니다",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.seungma.infratalk.data.BlockedRequestException -> Toast.makeText(
                                requireActivity(), "너무 많은 요청이 있었습니다 잠시 후 시도해 주세요",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.seungma.infratalk.data.FailSendEmailException -> Toast.makeText(
                                requireActivity(), "이메일 전송에 실패 했습니다",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.seungma.infratalk.data.FailSelectException -> Toast.makeText(
                                requireActivity(), "계정 정보조회에 실패 했습니다",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.seungma.infratalk.data.UnKnownException -> Toast.makeText(
                                requireActivity(), "알 수 없는 에러가 발생했습니다",
                                Toast.LENGTH_SHORT
                            ).show()

                            else -> Log.d("LogInMain", it.errorCode.message.toString())
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}