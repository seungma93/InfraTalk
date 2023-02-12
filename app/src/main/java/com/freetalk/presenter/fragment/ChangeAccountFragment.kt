package com.freetalk.presenter.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.AuthRespond
import com.freetalk.data.remote.FirebaseRemoteDataSourceImpl
import com.freetalk.databinding.FragmentDialogChangeAccountBinding
import com.freetalk.databinding.FragmentLoginMainBinding
import com.freetalk.presenter.viewmodel.LoginViewModel
import com.freetalk.presenter.viewmodel.LoginViewModelFactory
import com.freetalk.presenter.viewmodel.ViewEvent
import com.freetalk.repository.FirebaseUserDataRepositoryImpl
import com.freetalk.usecase.UserUseCaseImpl
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.log

class ChangeAccountFragment: DialogFragment(), View.OnClickListener {
    private var _binding: FragmentDialogChangeAccountBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by lazy {
        val firebaseRemoteDataSourceImpl = FirebaseRemoteDataSourceImpl(Firebase.auth)
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
                    val userData = UserEntity(inputId, "")
                    Log.v("ChangeAccountFragment", inputId)
                    loginViewModel.resetPassword(userData)
                }
        }
        subscribe()
    }

    override fun onClick(p0: View?) {
        dismiss()
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            loginViewModel.viewEvent.collect {
                when(it) {
                    is ViewEvent.ResetPassword -> {
                        when(it.authData.respond){
                            is AuthRespond.SuccessSendMail -> {
                                binding.emailTextInput.visibility = View.GONE
                                binding.btnFindPassword.visibility = View.GONE
                                binding.completeText.text = "이메일로 재설정 링크를 보냈습니다"
                                binding.completeText.visibility = View.VISIBLE
                            }
                            is AuthRespond.FailSendMail -> {
                                binding.emailTextInput.visibility = View.GONE
                                binding.btnFindPassword.visibility = View.GONE
                                binding.completeText.text = "이메일이 틀렸습니다 확인해 주세요"
                                binding.completeText.visibility = View.VISIBLE
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