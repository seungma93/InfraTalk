package com.freetalk.presenter.fragment.Sign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.freetalk.data.UserSingleton
import com.freetalk.data.remote.*
import com.freetalk.databinding.FragmentLoginMainBinding
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.viewmodel.*
import com.freetalk.repository.FirebaseImageDataRepositoryImpl
import com.freetalk.repository.FirebaseUserDataRepositoryImpl
import com.freetalk.usecase.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class LoginMainFragment : Fragment() {
    private var _binding: FragmentLoginMainBinding? = null
    private val binding get() = _binding!!
    private val signViewModel: SignViewModel by lazy {
        // dataSource
        val firebaseRemoteDataSourceImpl =
            FirebaseUserRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val firebaseImageDataSourceImpl =
            FirebaseImageRemoteDataSourceImpl(FirebaseStorage.getInstance())
        // repository
        val firebaseUserDataRepositoryImpl =
            FirebaseUserDataRepositoryImpl(firebaseRemoteDataSourceImpl)
        val firebaseImageDataRepositoryImpl =
            FirebaseImageDataRepositoryImpl(firebaseImageDataSourceImpl)
        // useCase
        val uploadImageUseCaseImpl = UploadImagesUseCaseImpl(firebaseImageDataRepositoryImpl)
        val updateUserInfoUseCaseImpl = UpdateUserInfoUseCaseImpl(firebaseUserDataRepositoryImpl)
        val signUpUseCaseImpl = SignUpUseCaseImpl(firebaseUserDataRepositoryImpl)
        val sendEmailUseCaseImpl = SendEmailUseCaseImpl(firebaseUserDataRepositoryImpl)
        val updateProfileImageUseCaseImpl =
            UpdateProfileImageUseCaseImpl(uploadImageUseCaseImpl, updateUserInfoUseCaseImpl)
        val logInUseCaseImpl = LogInUseCaseImpl(firebaseUserDataRepositoryImpl)
        val resetPasswordUseCaseImpl = ResetPasswordUseCaseImpl(firebaseUserDataRepositoryImpl)
        // factory
        val factory = SignViewModelFactory(
            signUpUseCaseImpl,
            sendEmailUseCaseImpl,
            updateProfileImageUseCaseImpl,
            logInUseCaseImpl,
            resetPasswordUseCaseImpl
        )
        ViewModelProvider(requireActivity(), factory).get(SignViewModel::class.java)
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
                        viewLifecycleOwner.lifecycleScope.launch {
                            signViewModel.logIn(LogInForm(inputId, inputPassword))
                        }
                    }
                }
            }
            btnFindAccount.setOnClickListener {
                val dialogFragment = ResetPasswordFragment()
                dialogFragment.show(requireActivity().supportFragmentManager, "CustomDialog")
            }
        }
        subsribe()
    }


    private fun subsribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            signViewModel.viewEvent.collect {
                when(it) {
                    is ViewEvent.LogIn -> {
                        it.userEntity?.let { userEntity ->
                            UserSingleton.userEntity = userEntity
                        }
                        (requireActivity() as? Navigable)?.navigateFragment(EndPoint.Main(1))
                    }
                    is ViewEvent.Error -> {
                        when(it.errorCode) {
                            is NotExistEmailException -> Toast.makeText(
                                requireActivity(), "등록된 이메일이 없습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                            is InvalidEmailException -> Toast.makeText(
                                requireActivity(), "이메일을 확인하세요",
                                Toast.LENGTH_SHORT
                            ).show()
                            is WrongPasswordException -> Toast.makeText(
                                requireActivity(), "암호가 틀렸습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                            is VerifiedEmailException -> Toast.makeText(
                                requireActivity(), "이메일 인증이 필요 합니다",
                                Toast.LENGTH_SHORT
                            ).show()
                            is BlockedRequestException -> Toast.makeText(
                                requireActivity(), "너무 많은 요청이 있었습니다 잠시 후 시도해 주세요",
                                Toast.LENGTH_SHORT
                            ).show()
                            is FailSendEmailException -> Toast.makeText(
                                requireActivity(), "셀렉트에 실패 했습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                            is UnKnownException -> Toast.makeText(
                                requireActivity(), "알 수 없는 에러가 발생했습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                else -> {}
                }
            }
        }
    }
}