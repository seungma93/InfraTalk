package com.seungma.infratalk.presenter.sign.fragment

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.seungma.infratalk.data.model.request.image.ImagesRequest
import com.seungma.infratalk.databinding.FragmentSignUpBinding
import com.seungma.infratalk.di.component.DaggerSignFragmentComponent
import com.seungma.infratalk.presenter.main.activity.EndPoint
import com.seungma.infratalk.presenter.main.activity.Navigable
import com.seungma.infratalk.presenter.sign.form.SignUpForm
import com.seungma.infratalk.presenter.sign.viewmodel.SignViewModel
import com.seungma.infratalk.presenter.sign.viewmodel.ViewEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>
    private lateinit var activityResult: ActivityResultLauncher<Intent>

    @Inject
    lateinit var signViewModelFactory: ViewModelProvider.Factory
    private val signViewModel: SignViewModel by viewModels { signViewModelFactory }
    
    override fun onAttach(context: Context) {
        DaggerSignFragmentComponent.factory().create(context).inject(this)
        super.onAttach(context)

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                Log.d("BoardWriteFragment", "퍼미션 체크 실행")
                if (isGranted) {
                    // 권한이 필요한 작업 수행
                    navigateImage()
                } else {
                    Log.d("BoardWriteFragment", "퍼미션 허용 안됨 ")
                }
            }

        activityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    it.data?.let { intent ->
                        binding.profileImage.setImageURI(intent.data)
                        binding.profileImage.tag = intent.data
                    }
                }
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
                        viewLifecycleOwner.lifecycleScope.launch {
                            showProgressBar()
                            when (binding.profileImage.tag) {
                                null -> {
                                    Log.d("SignUpF", "사진 x")
                                    signViewModel.signUp(
                                        SignUpForm(inputId, inputPassword, inputNickname), null
                                    )
                                }

                                else -> {
                                    Log.d("SignUpF", "사진 o")
                                    signViewModel.signUp(
                                        SignUpForm(inputId, inputPassword, inputNickname),
                                        ImagesRequest(
                                            listOf(binding.profileImage.tag as Uri)
                                        )
                                    )
                                }
                            }

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
                        Log.d("BoardWriteFragment", "권한 있음")
                        // 권한이 존재하는 경우
                        navigateImage()
                    }

                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                        // 권한이 거부 되어 있는 경우
                        Log.d("BoardWriteFragment", "권한 없음")
                        showPermissionContextPopup()
                    }

                    else -> {
                        // 처음 권한을 시도했을 때 띄움
                        Log.d("BoardWriteFragment", "처음 시도")
                        activityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
        }

        subscribe()
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

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            signViewModel.viewEvent.collect {

                when (it) {
                    is ViewEvent.SignUp -> {
                        hideProgressBar()
                        Toast.makeText(
                            requireActivity(), "회원가입 성공 이메일을 확인해 주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                        (requireActivity() as? Navigable)?.navigateFragment(EndPoint.LoginMain)
                    }

                    is ViewEvent.Error -> {
                        hideProgressBar()
                        when (it.errorCode) {
                            is com.seungma.infratalk.data.InvalidPasswordException ->
                                Toast.makeText(
                                    requireActivity(), "비밀번호는 6자리 이상이어야 합니다.",
                                    Toast.LENGTH_SHORT
                                ).show()

                            is com.seungma.infratalk.data.InvalidEmailException -> Toast.makeText(
                                requireActivity(), "이메일 형식을 확인 하세요",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.seungma.infratalk.data.ExistEmailException -> Toast.makeText(
                                requireActivity(), "존재하는 이메일 입니다",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.seungma.infratalk.data.BlockedRequestException -> Toast.makeText(
                                requireActivity(), "너무 많은 요청이 있었습니다 잠시 후 시도해 주세요",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.seungma.infratalk.data.FailInsertException -> Toast.makeText(
                                requireActivity(), "인서트에 실패 했습니다",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.seungma.infratalk.data.NoImageException -> Toast.makeText(
                                requireActivity(), "업로드할 이미지가 없습니다",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.seungma.infratalk.data.FailUpdatetException -> Toast.makeText(
                                requireActivity(), "업데이트에 실패 했습니다",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.seungma.infratalk.data.FailSendEmailException -> Toast.makeText(
                                requireActivity(), "메일 전송에 실패 했습니다",
                                Toast.LENGTH_SHORT
                            ).show()
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

}