package com.freetalk.presenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.FirebaseRemoteDataSourceImpl
import com.freetalk.databinding.FragmentLoginMainBinding
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.MainActivityNavigation
import com.freetalk.presenter.viewmodel.LoginViewModel
import com.freetalk.presenter.viewmodel.LoginViewModelFactory
import com.freetalk.repository.FirebaseUserDataRepositoryImpl
import com.freetalk.usecase.UserUseCaseImpl
import kotlinx.coroutines.launch

class LoginMainFragment: Fragment() {
    private var _binding: FragmentLoginMainBinding? = null
    private val binding get() = _binding!!
    private var inputId: String = ""
    private var inputPassword: String = ""
    private val loginViewModel: LoginViewModel by lazy {
        val firebaseRemoteDataSourceImpl = FirebaseRemoteDataSourceImpl()
        val firebaseUserDataRepositoryImpl =
            FirebaseUserDataRepositoryImpl(firebaseRemoteDataSourceImpl)
        val firebaseUseCaseImpl = UserUseCaseImpl(firebaseUserDataRepositoryImpl)
        val factory = LoginViewModelFactory(firebaseUseCaseImpl)
        ViewModelProvider(requireActivity(), factory).get(LoginViewModel::class.java)
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
                val signUpEndPoint = EndPoint.SignUp(1)
                (requireActivity() as? MainActivityNavigation)?.navigateFragment(signUpEndPoint)
            }
            btnLogin.setOnClickListener {
                inputId = binding.emailTextInput.editText!!.text.toString()
                inputPassword = binding.passwordTextInput.editText!!.text.toString()

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
                        val userData = UserEntity(inputId, inputPassword)
                        lifecycleScope.launch {
                            val logInInfo = loginViewModel.logIn(userData)
                        }
                    }
                }
            }
            btnFindAccount.setOnClickListener {
                val dialogFragment = ChangeAccountFragment()
                dialogFragment.show(requireActivity().supportFragmentManager, "CustomDialog")
            }
        }
        subsribe()
    }

    fun subsribe() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.logInEvent.collect {
                when {
                    it.message.contains("There is no user record corresponding to this identifier. The user may have been deleted") -> Toast.makeText(
                        requireActivity(), "등록된 이메일이 없습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                    it.message.contains("The email address is badly formatted") -> Toast.makeText(
                        requireActivity(), "이메일을 확인하세요",
                        Toast.LENGTH_SHORT
                    ).show()
                    it.message.contains("The password is invalid or the user does not have a password") -> Toast.makeText(
                        requireActivity(), "이메일이나 패스워드가 틀렸습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                    it.message.contains("이메일 인증이 필요합니다") -> Toast.makeText(
                        requireActivity(), "이메일 인증이 필요합니다",
                        Toast.LENGTH_SHORT
                    ).show()
                    it.message.contains("We have blocked all requests from this device due to unusual activity") -> Toast.makeText(
                        requireActivity(), "여러번 요청으로 인해 잠시 후 시도해 주세요",
                        Toast.LENGTH_SHORT
                    ).show()
                    it.message.contains("로그인 성공") -> {
                        Toast.makeText(
                            requireActivity(), "로그인 성공",
                            Toast.LENGTH_SHORT
                        ).show()
                        val endPoint = EndPoint.Main(1)
                        (requireActivity() as? MainActivityNavigation)?.navigateFragment(endPoint)
                    }
                }
            }
        }
    }
}