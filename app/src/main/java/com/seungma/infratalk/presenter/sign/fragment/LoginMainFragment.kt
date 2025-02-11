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
import com.google.android.material.snackbar.Snackbar
import com.seungma.infratalk.data.BlockedRequestException
import com.seungma.infratalk.data.FailFirebaseLoginException
import com.seungma.infratalk.data.FailSelectException
import com.seungma.infratalk.data.FailVerifiedEmailException
import com.seungma.infratalk.data.InvalidEmailException
import com.seungma.infratalk.data.NeedVerifiedEmailException
import com.seungma.infratalk.data.NotExistEmailException
import com.seungma.infratalk.data.NotExistFirebaseUserException
import com.seungma.infratalk.data.UnKnownException
import com.seungma.infratalk.data.WrongPasswordException
import com.seungma.infratalk.databinding.FragmentLoginMainBinding
import com.seungma.infratalk.di.component.DaggerSignFragmentComponent
import com.seungma.infratalk.presenter.common.CustomSnackbar
import com.seungma.infratalk.presenter.main.activity.EndPoint
import com.seungma.infratalk.presenter.main.activity.Navigable
import com.seungma.infratalk.presenter.sign.form.LoginForm
import com.seungma.infratalk.presenter.sign.form.SavedEmailSetForm
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
            // TODO 프리퍼런스 데이터 확인
            runCatching {
                val savedEmail = signViewModel.getSavedEmail().email
                if(savedEmail.isNotEmpty()) {
                    emailEditText.setText(savedEmail)
                    cbId.isChecked = true
                } else cbId.isChecked = false
            }.onFailure {
                // TODO 프리퍼런스 예외 처리
            }



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
                            runCatching {
                                signViewModel.logIn(LoginForm(inputId, inputPassword))
                            }.onFailure {
                                when(it) {
                                    is FailFirebaseLoginException -> {
                                        val message = "로그인에 실패 했습니다"
                                        val duration = Snackbar.LENGTH_SHORT

                                        val snackbar = CustomSnackbar.make(requireView(), message, duration)
                                        snackbar.setMargin(bottomDp = 66)
                                        snackbar.show()
                                    }
                                    else -> {
                                        val message = "로그인 중 알 수 없는 에러가 발생했습니다"
                                        val duration = Snackbar.LENGTH_SHORT

                                        val snackbar = CustomSnackbar.make(requireView(), message, duration)
                                        snackbar.setMargin(bottomDp = 66)
                                        snackbar.show()
                                    }
                                }
                            }

                        }
                    }
                }
            }
            btnFindAccount.setOnClickListener {
                val dialogFragment = ResetPasswordFragment()
                dialogFragment.show(childFragmentManager, "CustomDialog")
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
                            when(binding.cbId.isChecked) {
                                true -> {
                                    if(signViewModel.getSavedEmail().email != binding.emailEditText.text.toString()) {
                                        signViewModel.setSavedEmail(savedEmailSetForm = SavedEmailSetForm(email = it.userEntity.email))
                                    }
                                }
                                false -> {
                                    signViewModel.deleteSavedEmail()
                                }
                            }
                            hideProgressBar()
                            (requireActivity() as? Navigable)?.navigateFragment(EndPoint.Main)
                    }

                    is ViewEvent.Error -> {
                        Log.d("LogInMainF", " 에러 발생")
                        hideProgressBar()
                        when (it.throwable) {
                            is NotExistEmailException -> {
                                val message = "이메일이 존재하지 않습니다"
                                val duration = Snackbar.LENGTH_SHORT

                                val snackbar = CustomSnackbar.make(requireView(), message, duration)
                                snackbar.setMargin(bottomDp = 66)
                                snackbar.show()
                            }

                            is InvalidEmailException -> {
                                val message = "이메일을 확인하세요"
                                val duration = Snackbar.LENGTH_SHORT

                                val snackbar = CustomSnackbar.make(requireView(), message, duration)
                                snackbar.setMargin(bottomDp = 66)
                                snackbar.show()
                            }

                            is WrongPasswordException -> {
                                val message = "암호가 틀렸습니다"
                                val duration = Snackbar.LENGTH_SHORT

                                val snackbar = CustomSnackbar.make(requireView(), message, duration)
                                snackbar.setMargin(bottomDp = 66)
                                snackbar.show()
                            }

                            is NeedVerifiedEmailException -> {
                                val message = "이메일 인증이 필요합니다"
                                val duration = Snackbar.LENGTH_SHORT

                                val snackbar = CustomSnackbar.make(requireView(), message, duration)
                                snackbar.setMargin(bottomDp = 66)
                                snackbar.show()
                            }

                            is BlockedRequestException -> {
                                val message = "요청이 많아 잠시 기다려 주세요"
                                val duration = Snackbar.LENGTH_SHORT

                                val snackbar = CustomSnackbar.make(requireView(), message, duration)
                                snackbar.setMargin(bottomDp = 66)
                                snackbar.show()
                            }

                            is FailVerifiedEmailException -> {
                                val message = "이메일 전송을 실패 했습니다"
                                val duration = Snackbar.LENGTH_SHORT

                                val snackbar = CustomSnackbar.make(requireView(), message, duration)
                                snackbar.setMargin(bottomDp = 66)
                                snackbar.show()
                            }

                            is FailSelectException -> {
                                val message = "계정 정보 조회에 실패했습니다"
                                val duration = Snackbar.LENGTH_SHORT

                                val snackbar = CustomSnackbar.make(requireView(), message, duration)
                                snackbar.setMargin(bottomDp = 66)
                                snackbar.show()
                            }

                            is UnKnownException -> {
                                val message = "알 수 없는 에러 발생"
                                val duration = Snackbar.LENGTH_SHORT

                                val snackbar = CustomSnackbar.make(requireView(), message, duration)
                                snackbar.setMargin(bottomDp = 66)
                                snackbar.show()
                            }

                            is FailFirebaseLoginException -> {
                                val message = "파이어 베이스 로그인 실패"
                                val duration = Snackbar.LENGTH_SHORT

                                val snackbar = CustomSnackbar.make(requireView(), message, duration)
                                snackbar.setMargin(bottomDp = 66)
                                snackbar.show()
                            }

                            is NotExistFirebaseUserException -> {
                                val message = "데이터 베이스에 유저 정보가 없습니다."
                                val duration = Snackbar.LENGTH_SHORT

                                val snackbar = CustomSnackbar.make(requireView(), message, duration)
                                snackbar.setMargin(bottomDp = 66)
                                snackbar.show()
                            }

                            else -> {
                                Log.d("LogInMain", it.throwable.message.toString())
                                val message = "알 수 없는 에러 발생"
                                val duration = Snackbar.LENGTH_SHORT

                                val snackbar = CustomSnackbar.make(requireView(), message, duration)
                                snackbar.setMargin(bottomDp = 66)
                                snackbar.show()
                            }
                        }
                    }

                    else -> {
                        val message = "알 수 없는 에러 발생"
                        val duration = Snackbar.LENGTH_SHORT

                        val snackbar = CustomSnackbar.make(requireView(), message, duration)
                        snackbar.setMargin(bottomDp = 66)
                        snackbar.show()
                    }
                }
            }
        }
    }
}