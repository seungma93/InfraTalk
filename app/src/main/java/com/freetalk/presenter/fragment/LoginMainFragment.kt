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
import com.freetalk.data.remote.AuthData
import com.freetalk.data.remote.AuthResponse
import com.freetalk.data.remote.FirebaseUserRemoteDataSourceImpl
import com.freetalk.databinding.FragmentLoginMainBinding
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.viewmodel.LoginViewModel
import com.freetalk.presenter.viewmodel.LoginViewModelFactory
import com.freetalk.presenter.viewmodel.ViewEvent
import com.freetalk.repository.FirebaseUserDataRepositoryImpl
import com.freetalk.usecase.UserUseCaseImpl
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class LoginMainFragment : Fragment() {
    private var _binding: FragmentLoginMainBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by lazy {
        val firebaseUserRemoteDataSourceImpl = FirebaseUserRemoteDataSourceImpl(Firebase.auth, Firebase.firestore, FirebaseStorage.getInstance())
        val firebaseUserDataRepositoryImpl =
            FirebaseUserDataRepositoryImpl(firebaseUserRemoteDataSourceImpl)
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
                        val userData = UserEntity(inputId, inputPassword, "", null)
                        viewLifecycleOwner.lifecycleScope.launch {
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


    private fun subsribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            loginViewModel.viewEvent.collect {
                when(it){
                    is ViewEvent.LogIn -> {
                        when (it.authData.response) {
                            is AuthResponse.NotExistEmail -> Toast.makeText(
                                requireActivity(), "등록된 이메일이 없습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                            is AuthResponse.InvalidEmail -> Toast.makeText(
                                requireActivity(), "이메일을 확인하세요",
                                Toast.LENGTH_SHORT
                            ).show()
                            is AuthResponse.WrongPassword -> Toast.makeText(
                                requireActivity(), "이메일이나 패스워드가 틀렸습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                            is AuthResponse.RequireEmail -> Toast.makeText(
                                requireActivity(), "이메일 인증이 필요합니다",
                                Toast.LENGTH_SHORT
                            ).show()
                            is AuthResponse.BlockedRequest -> Toast.makeText(
                                requireActivity(), "여러번 요청으로 인해 잠시 후 시도해 주세요",
                                Toast.LENGTH_SHORT
                            ).show()
                            is AuthResponse.SuccessLogIn -> {
                                Toast.makeText(
                                    requireActivity(), "로그인 성공",
                                    Toast.LENGTH_SHORT
                                ).show()

                                (requireActivity() as? Navigable)?.navigateFragment(EndPoint.Main(1))
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