package com.freetalk.presenter.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.FirebaseRemoteDataSourceImpl
import com.freetalk.databinding.FragmentSignUpBinding
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.MainActivityNavigation
import com.freetalk.presenter.viewmodel.LoginViewModel
import com.freetalk.presenter.viewmodel.LoginViewModelFactory
import com.freetalk.repository.FirebaseUserDataRepositoryImpl
import com.freetalk.usecase.UserUseCaseImpl
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by lazy {
        val firebaseRemoteDataSourceImpl = FirebaseRemoteDataSourceImpl()
        val firebaseUserDataRepositoryImpl =
            FirebaseUserDataRepositoryImpl(firebaseRemoteDataSourceImpl)
        val firebaseUseCaseImpl = UserUseCaseImpl(firebaseUserDataRepositoryImpl)
        val factory = LoginViewModelFactory(firebaseUseCaseImpl)
        ViewModelProvider(requireActivity(), factory).get(LoginViewModel::class.java)
    }
    private var inputId: String = ""
    private var inputPassword: String = ""
    private var inputPasswordCheck: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.let {

            it.btnSignUp.setOnClickListener {
                inputId = binding.idEditText.text.toString()
                inputPassword = binding.passwordEditText.text.toString()
                inputPasswordCheck = binding.passwordCheckEditText.text.toString()

                when {
                    inputId.isNullOrEmpty() -> Toast.makeText(
                        requireActivity(), "이메일을 입력하세요",
                        Toast.LENGTH_SHORT
                    ).show()
                    inputPassword.isNullOrEmpty() -> Toast.makeText(
                        requireActivity(), "비밀번호를 입력하세요",
                        Toast.LENGTH_SHORT
                    ).show()
                    inputPasswordCheck.isNullOrEmpty() -> Toast.makeText(
                        requireActivity(), "비밀번호 확인을 입력하세요",
                        Toast.LENGTH_SHORT
                    ).show()
                    inputPassword != inputPasswordCheck -> Toast.makeText(
                        requireActivity(), "비밀번호 확인이 일치하지 않습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> {
                        val userData = UserEntity(inputId, inputPassword)
                        lifecycleScope.launch {
                            val signUpInfo = loginViewModel.signUp(userData)
                        }
                    }
                }
            }
        }
        subsribe()
    }

    fun subsribe() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.signUpEvent.collect {
                when {
                    it.message.contains("FirebaseAuthWeakPasswordException") -> Toast.makeText(
                        requireActivity(), "비밀번호는 6자리 이상이어야 합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    it.message.contains("The email address is badly formatted") -> Toast.makeText(
                        requireActivity(), "이메일 형식을 확인 하세요",
                        Toast.LENGTH_SHORT
                    ).show()
                    it.message.contains("The email address is already in use by another account") -> Toast.makeText(
                        requireActivity(), "존재하는 이메일 입니다",
                        Toast.LENGTH_SHORT
                    ).show()
                    it.message.contains("회원가입 성공") -> {
                        Toast.makeText(
                            requireActivity(), "회원가입 성공 이메일을 확인해 주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                        val endPoint = EndPoint.LoginMain(1)
                        (requireActivity() as? MainActivityNavigation)?.navigateFragment(endPoint)
                    }

                }
            }
        }
    }
}