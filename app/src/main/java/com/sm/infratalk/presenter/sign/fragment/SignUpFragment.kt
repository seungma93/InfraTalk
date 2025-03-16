package com.sm.infratalk.presenter.sign.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sm.infratalk.data.model.request.image.ImagesRequest
import com.sm.infratalk.databinding.FragmentSignUpBinding
import com.sm.infratalk.di.component.DaggerSignFragmentComponent
import com.sm.infratalk.presenter.main.activity.EndPoint
import com.sm.infratalk.presenter.main.activity.Navigable
import com.sm.infratalk.presenter.sign.form.SignUpForm
import com.sm.infratalk.presenter.sign.viewmodel.SignViewModel
import com.sm.infratalk.presenter.sign.viewmodel.ViewEvent
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

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "선택한 이미지 URI: $uri")
            binding.profileImage.setImageURI(uri)
            val requestOptions = RequestOptions.circleCropTransform().autoClone()
            Glide.with(this)
                .load(uri)
                .apply(requestOptions)
                .into(binding.profileImage)
            binding.profileImage.tag = uri
        } else {
            Log.d("PhotoPicker", "이미지가 선택되지 않음")
        }
    }

    private val pickImageLegacy = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                binding.profileImage.setImageURI(uri)
                val requestOptions = RequestOptions.circleCropTransform().autoClone()
                Glide.with(this)
                    .load(uri)
                    .apply(requestOptions)
                    .into(binding.profileImage)
                binding.profileImage.tag = uri
            }
        }
    }

    
    override fun onAttach(context: Context) {
        DaggerSignFragmentComponent.factory().create(context).inject(this)
        super.onAttach(context)


        activityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    it.data?.let { intent ->
                        binding.profileImage.setImageURI(intent.data)
                        val requestOptions = RequestOptions.circleCropTransform().autoClone()
                            Glide.with(this)
                                .load(intent.data)
                                .apply(requestOptions)
                                .into(binding.profileImage)
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // API 33 이상: Photo Picker 사용
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else {
                    // API 32 이하: 기존 방식 사용
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    pickImageLegacy.launch(intent)
                }
            }
        }

        subscribe()
    }


    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
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
                        when (it.throwable) {
                            is com.sm.infratalk.data.InvalidPasswordException ->
                                Toast.makeText(
                                    requireActivity(), "비밀번호는 6자리 이상이어야 합니다.",
                                    Toast.LENGTH_SHORT
                                ).show()

                            is com.sm.infratalk.data.InvalidEmailException -> Toast.makeText(
                                requireActivity(), "이메일 형식을 확인 하세요",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.sm.infratalk.data.ExistEmailException -> Toast.makeText(
                                requireActivity(), "존재하는 이메일 입니다",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.sm.infratalk.data.BlockedRequestException -> Toast.makeText(
                                requireActivity(), "너무 많은 요청이 있었습니다 잠시 후 시도해 주세요",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.sm.infratalk.data.FailInsertException -> Toast.makeText(
                                requireActivity(), "인서트에 실패 했습니다",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.sm.infratalk.data.FailUpdateException -> Toast.makeText(
                                requireActivity(), "업데이트에 실패 했습니다",
                                Toast.LENGTH_SHORT
                            ).show()

                            is com.sm.infratalk.data.FailVerifiedEmailException -> Toast.makeText(
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