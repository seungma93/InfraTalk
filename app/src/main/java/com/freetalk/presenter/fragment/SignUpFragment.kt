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
import com.freetalk.databinding.FragmentLoginMainBinding
import com.freetalk.databinding.FragmentSignUpBinding
import com.freetalk.presenter.viewmodel.LoginViewModel
import com.freetalk.presenter.viewmodel.LoginViewModelFactory
import com.freetalk.repository.FirebaseUserDataRepositoryImpl
import com.freetalk.usecase.UserUseCaseImpl
import kotlinx.coroutines.launch

class SignUpFragment: Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by lazy {
        val firebaseRemoteDataSourceImpl = FirebaseRemoteDataSourceImpl()
        val firebaseUserDataRepositoryImpl = FirebaseUserDataRepositoryImpl(firebaseRemoteDataSourceImpl)
        val firebaseUseCaseImpl = UserUseCaseImpl(firebaseUserDataRepositoryImpl)
        val factory = LoginViewModelFactory(firebaseUseCaseImpl)
        ViewModelProvider(requireActivity(), factory).get(LoginViewModel::class.java)
    }
    private var inputId: String = ""
    private var inputPassword: String = ""

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

            it.btnCheckId.setOnClickListener {

            }
            it.btnSignUp.setOnClickListener {
                inputId = binding.idEditText.text.toString()
                inputPassword = binding.passwordEditText.text.toString()
                if(inputId.isNotEmpty() && inputId.isNotEmpty()){
                    val userData = UserEntity(inputId, inputPassword)
                    lifecycleScope.launch {
                            val signUpInfo = loginViewModel.signUp(userData)

                    }
                }
            }
        }
    }
}