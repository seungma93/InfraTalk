package com.freetalk.presenter.fragment.Sign

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.*
import com.freetalk.databinding.FragmentDialogChangeAccountBinding
import com.freetalk.presenter.viewmodel.*
import com.freetalk.repository.FirebaseImageDataRepositoryImpl
import com.freetalk.repository.FirebaseUserDataRepositoryImpl
import com.freetalk.usecase.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import javax.inject.Inject

class ResetPasswordFragment: DialogFragment(), View.OnClickListener {
    private var _binding: FragmentDialogChangeAccountBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var signViewModelFactory: ViewModelProvider.Factory
    private val signViewModel: SignViewModel by viewModels { signViewModelFactory }
    /*
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

     */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogChangeAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.btnFindPassword.setOnClickListener {
            val inputId = binding.emailTextInput.editText!!.text.toString()
                viewLifecycleOwner.lifecycleScope.launch{
                    Log.v("ChangeAccountFragment", inputId)
                    signViewModel.resetPassword(ResetPasswordForm(inputId))
                }
        }
        subscribe()
    }

    override fun onClick(p0: View?) {
        dismiss()
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            signViewModel.viewEvent.collect {
                when(it) {
                    is ViewEvent.ResetPassword -> {
                        binding.emailTextInput.visibility = View.GONE
                        binding.btnFindPassword.visibility = View.GONE
                        binding.completeText.text = "이메일로 재설정 링크를 보냈습니다"
                        binding.completeText.visibility = View.VISIBLE
                    }
                    is ViewEvent.Error -> {
                        when(it.errorCode){
                            is FailSendEmailException -> {
                                binding.emailTextInput.visibility = View.GONE
                                binding.btnFindPassword.visibility = View.GONE
                                binding.completeText.text = "이메일이 틀렸습니다 확인해 주세요"
                                binding.completeText.visibility = View.VISIBLE
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }


}