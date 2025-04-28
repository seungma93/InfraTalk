package com.sm.infratalk.presenter.sign.fragment

import SignUpScreen
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
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.sm.infratalk.data.model.request.image.ImagesRequest
import com.sm.infratalk.di.component.DaggerSignFragmentComponent
import com.sm.infratalk.presenter.sign.form.SignUpForm
import com.sm.infratalk.presenter.sign.viewmodel.SignViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpFragment : Fragment() {
    @Inject
    lateinit var signViewModelFactory: ViewModelProvider.Factory
    private val signViewModel: SignViewModel by viewModels { signViewModelFactory }
    
    // 선택된 이미지 URI를 저장할 변수 추가
    private var selectedImageUri by mutableStateOf<Uri?>(null)

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "선택한 이미지 URI: $uri")
            selectedImageUri = uri
        } else {
            Log.d("PhotoPicker", "이미지가 선택되지 않음")
        }
    }

    private val pickImageLegacy = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                Log.d("PhotoPicker", "선택한 이미지 URI: $uri")
                selectedImageUri = uri
            }
        }
    }

    override fun onAttach(context: Context) {
        DaggerSignFragmentComponent.factory().create(context).inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SignUpScreen(
                    isLoading = false,
                    onSignUpClick = { email, password, passwordCheck, nickname ->
                        if (password == passwordCheck) {
                            viewLifecycleOwner.lifecycleScope.launch {
                                selectedImageUri?.let {
                                    signViewModel.signUp(
                                        signUpForm = SignUpForm(
                                            email = email,
                                            password = password,
                                            nickname = nickname
                                        ),
                                        imagesRequest = ImagesRequest(
                                            imageUris = listOf(it)
                                        )
                                    )
                                } ?: run {
                                    signViewModel.signUp(
                                        signUpForm = SignUpForm(
                                            email = email,
                                            password = password,
                                            nickname = nickname
                                        ),
                                        imagesRequest = null
                                    )
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onProfileImageClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        } else {
                            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            pickImageLegacy.launch(intent)
                        }
                    },
                    profileImageUri = selectedImageUri
                )
            }
        }
    }
}