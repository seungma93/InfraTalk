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
import com.freetalk.data.remote.AuthRespond
import com.freetalk.data.remote.FirebaseUserRemoteDataSourceImpl
import com.freetalk.databinding.FragmentSignUpBinding
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.viewmodel.LoginViewModel
import com.freetalk.presenter.viewmodel.LoginViewModelFactory
import com.freetalk.presenter.viewmodel.ViewEvent
import com.freetalk.repository.FirebaseUserDataRepositoryImpl
import com.freetalk.usecase.UserUseCaseImpl
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by lazy {
        val firebaseRemoteDataSourceImpl = FirebaseUserRemoteDataSourceImpl(Firebase.auth)
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
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.let {

            it.btnSignUp.setOnClickListener {
                val inputId = binding.emailTextInput.editText!!.text.toString()
                val inputPassword = binding.passwordTextInput.editText!!.text.toString()
                val inputPasswordCheck = binding.passwordCheckTextInput.editText!!.text.toString()

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
                        viewLifecycleOwner.lifecycleScope.launch {
                            val signUpInfo = loginViewModel.signUp(userData)
                        }
                    }
                }
            }
        }
        subsribe()
    }


    private fun subsribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            loginViewModel.viewEvent.collect {

                when(it)
                {
                    is ViewEvent.SignUp -> {
                        when(it.authData.respond) {
                            is AuthRespond.InvalidPassword -> Toast.makeText(
                                requireActivity(), "비밀번호는 6자리 이상이어야 합니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            is AuthRespond.InvalidEmail -> Toast.makeText(
                                requireActivity(), "이메일 형식을 확인 하세요",
                                Toast.LENGTH_SHORT
                            ).show()
                            is AuthRespond.ExistEmail -> Toast.makeText(
                                requireActivity(), "존재하는 이메일 입니다",
                                Toast.LENGTH_SHORT
                            ).show()
                            is AuthRespond.SuccessSignUp -> {
                                Toast.makeText(
                                    requireActivity(), "회원가입 성공 이메일을 확인해 주세요",
                                    Toast.LENGTH_SHORT
                                ).show()
                                (requireActivity() as? Navigable)?.navigateFragment(EndPoint.LoginMain(1))
                            }
                            else -> {
                                error("구독 에러")
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

}