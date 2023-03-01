package com.freetalk.presenter.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.AuthResponse
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>
    private lateinit var activityResult: ActivityResultLauncher<Intent>
    private var inputProfileImage: Uri? = null
    private val loginViewModel: LoginViewModel by lazy {
        val firebaseRemoteDataSourceImpl = FirebaseUserRemoteDataSourceImpl(Firebase.auth, Firebase.firestore, FirebaseStorage.getInstance())
        val firebaseUserDataRepositoryImpl =
            FirebaseUserDataRepositoryImpl(firebaseRemoteDataSourceImpl)
        val firebaseUseCaseImpl = UserUseCaseImpl(firebaseUserDataRepositoryImpl)
        val factory = LoginViewModelFactory(firebaseUseCaseImpl)
        ViewModelProvider(requireActivity(), factory).get(LoginViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                Log.v("BoardWriteFragment", "퍼미션 체크 실행")
                if (isGranted) {
                    // 권한이 필요한 작업 수행
                    navigateImage()
                } else {
                    Log.v("BoardWriteFragment", "퍼미션 허용 안됨 ")
                }
            }

        activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == Activity.RESULT_OK) {

                it.data?.let { intent -> inputProfileImage =  intent.data  }
            }
            binding.profileImage.setImageURI(inputProfileImage)

        }

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

            it.btnSignUp.setOnClickListener { view ->
                val inputId = it.emailTextInput.editText!!.text.toString()
                val inputPassword = it.passwordTextInput.editText!!.text.toString()
                val inputPasswordCheck = it.passwordCheckTextInput.editText!!.text.toString()
                val inputNickname = it.nicknameTextInput.editText!!.text.toString()

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
                    inputNickname.isNullOrEmpty() -> Toast.makeText(
                        requireActivity(), "닉네임을 입력하세요",
                        Toast.LENGTH_SHORT
                    ).show()
                    inputPassword != inputPasswordCheck -> Toast.makeText(
                        requireActivity(), "비밀번호 확인이 일치하지 않습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> {
                        val userData = UserEntity(inputId, inputPassword, inputNickname, inputProfileImage )
                        viewLifecycleOwner.lifecycleScope.launch {
                            showProgressBar()
                            val signUpInfo = loginViewModel.signUp(userData)
                        }
                    }
                }
            }
            it.profileImage.setOnClickListener {
                when {
                    ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                    -> {
                        Log.v("BoardWriteFragment", "권한 있음")
                        // 권한이 존재하는 경우
                        navigateImage()
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                        // 권한이 거부 되어 있는 경우
                        Log.v("BoardWriteFragment", "권한 없음")
                        showPermissionContextPopup()
                    }
                    else -> {
                        // 처음 권한을 시도했을 때 띄움
                        Log.v("BoardWriteFragment", "처음 시도")
                        activityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
        }
        subsribe()
    }

    private fun navigateImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activityResult.launch(intent)
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(requireActivity())
            .setTitle("권한이 필요합니다")
            .setMessage("전자액자에서 사진을 선택하려면 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                activityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

    private fun subsribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            loginViewModel.viewEvent.collect {

                when(it)
                {
                    is ViewEvent.SignUp -> {
                        hideProgressBar()
                        when(it.authData.response) {
                            is AuthResponse.InvalidPassword -> {
                                Toast.makeText(
                                    requireActivity(), "비밀번호는 6자리 이상이어야 합니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is AuthResponse.InvalidEmail -> Toast.makeText(
                                requireActivity(), "이메일 형식을 확인 하세요",
                                Toast.LENGTH_SHORT
                            ).show()
                            is AuthResponse.ExistEmail -> Toast.makeText(
                                requireActivity(), "존재하는 이메일 입니다",
                                Toast.LENGTH_SHORT
                            ).show()
                            is AuthResponse.SuccessSignUp -> {
                                Toast.makeText(
                                    requireActivity(), "회원가입 성공 이메일을 확인해 주세요",
                                    Toast.LENGTH_SHORT
                                ).show()
                                (requireActivity() as? Navigable)?.navigateFragment(EndPoint.LoginMain(1))
                            }
                            is AuthResponse.FailUploadImage -> {
                                Toast.makeText(
                                    requireActivity(), "이미지 업로드 실패",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is AuthResponse.FailSendMail -> {
                                Toast.makeText(
                                    requireActivity(), "인증 이메일 전송 실패",
                                    Toast.LENGTH_SHORT
                                ).show()
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

    private fun showProgressBar() {
        blockLayoutTouch()
        binding.progressBar.isVisible = true
    }
    private fun blockLayoutTouch() {
        requireActivity().window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun hideProgressBar() {
        clearBlockLayoutTouch()
        binding.progressBar.isVisible = false
    }

    private fun clearBlockLayoutTouch() {
        requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

}