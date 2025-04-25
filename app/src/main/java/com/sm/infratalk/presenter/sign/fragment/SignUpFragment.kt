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
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.sm.infratalk.data.BlockedRequestException
import com.sm.infratalk.data.ExistEmailException
import com.sm.infratalk.data.FailInsertException
import com.sm.infratalk.data.FailUpdateException
import com.sm.infratalk.data.FailVerifiedEmailException
import com.sm.infratalk.data.InvalidEmailException
import com.sm.infratalk.data.InvalidPasswordException
import com.sm.infratalk.data.model.request.image.ImagesRequest
import com.sm.infratalk.di.component.DaggerSignFragmentComponent
import com.sm.infratalk.presenter.common.CustomSnackbar
import com.sm.infratalk.presenter.main.activity.EndPoint
import com.sm.infratalk.presenter.main.activity.Navigable
import com.sm.infratalk.presenter.sign.form.SignUpForm
import com.sm.infratalk.presenter.sign.viewmodel.SignViewModel
import com.sm.infratalk.presenter.sign.viewmodel.ViewEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpFragment : Fragment() {
    @Inject
    lateinit var signViewModelFactory: ViewModelProvider.Factory
    private val signViewModel: SignViewModel by viewModels { signViewModelFactory }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "선택한 이미지 URI: $uri")
        } else {
            Log.d("PhotoPicker", "이미지가 선택되지 않음")
        }
    }

    private val pickImageLegacy = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                Log.d("PhotoPicker", "선택한 이미지 URI: $uri")
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
                        // 회원가입 로직
                    },
                    onProfileImageClick = {
                        // 이미지 선택 로직
                    },
                    profileImageUri = null
                )
            }
        }
    }
}