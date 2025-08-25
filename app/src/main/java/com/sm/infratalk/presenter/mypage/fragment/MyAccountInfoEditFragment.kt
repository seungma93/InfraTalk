package com.sm.infratalk.presenter.mypage.fragment

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
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.sm.infratalk.R
import com.sm.infratalk.data.FailGetUserMeException
import com.sm.infratalk.databinding.FragmentMyAccountInfoEditBinding
import com.sm.infratalk.di.component.DaggerMyPageFragmentComponent
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.presenter.common.CustomSnackbar
import com.sm.infratalk.presenter.mypage.viewmodel.MyPageViewEvent
import com.sm.infratalk.presenter.mypage.viewmodel.MyPageViewModel
import com.sm.infratalk.presenter.sign.form.UserInfoUpdateForm
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyAccountInfoEditFragment : Fragment() {
    companion object {
        const val DEFAULT_PROFILE_IMAGE = "DEFAULT_PROFILE_IMAGE"
    }

    private var _binding: FragmentMyAccountInfoEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var userEntity: UserEntity

    @Inject
    lateinit var myPageViewModelFactory: ViewModelProvider.Factory
    private val myPageViewModel: MyPageViewModel by viewModels { myPageViewModelFactory }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "선택한 이미지 URI: $uri")
            val requestOptions = RequestOptions.circleCropTransform().autoClone()
            Glide.with(requireContext())
                .load(uri)
                .apply(requestOptions)
                .into(binding.ivProfileImage)

            binding.ivProfileImage.tag = uri
        } else {
            val message = "이미지 선택이 취소되었거나 실패했습니다."
            val duration = Snackbar.LENGTH_SHORT

            val snackbar = CustomSnackbar.make(requireActivity().findViewById(android.R.id.content), message, duration)
            snackbar.setMargin(bottomDp = 66)
            snackbar.show()
        }
    }

    private val pickImageLegacy = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                val requestOptions = RequestOptions.circleCropTransform().autoClone()
                Glide.with(requireContext())
                    .load(uri)
                    .apply(requestOptions)
                    .into(binding.ivProfileImage)

                binding.ivProfileImage.tag = uri
            } else {
                val message = "이미지 선택이 취소되었거나 실패했습니다."
                val duration = Snackbar.LENGTH_SHORT

                val snackbar = CustomSnackbar.make(requireActivity().findViewById(android.R.id.content), message, duration)
                snackbar.setMargin(bottomDp = 66)
                snackbar.show()
            }
        }
    }

    override fun onAttach(context: Context) {
        DaggerMyPageFragmentComponent.factory().create(context).inject(this)
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("BoardWriteFragment", "백스택 실행")
                parentFragmentManager.popBackStackImmediate()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAccountInfoEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                userEntity = myPageViewModel.getUserMe()

                val email = userEntity.email
                val nickname = userEntity.nickname
                val profileUri = userEntity.image

                binding.apply {
                    tvEmail.text =
                        "[infratalk@mypage] email \n[infratalk@mypage] $email"
                    nicknameEditText.setText(nickname)

                    val requestOptions = RequestOptions.circleCropTransform().autoClone()
                    profileUri?.let {
                        Glide.with(requireContext())
                            .load(it)
                            .apply(requestOptions)
                            .into(ivProfileImage)
                    }

                    btnDefaultProfile.setOnClickListener {
                        val resourceId = R.drawable.ic_avatar
                        Glide.with(requireContext())
                            .load(resourceId)
                            .apply(requestOptions)
                            .centerCrop()
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .into(ivProfileImage)
                        ivProfileImage.tag = Uri.parse(DEFAULT_PROFILE_IMAGE)
                    }

                    btnEditComplete.setOnClickListener { view ->
                        val inputNickname = nicknameTextInput.editText?.text?.toString()

                        when {
                            inputNickname.isNullOrEmpty() -> {
                                val message = "닉네임을 입력하세요."
                                val duration = Snackbar.LENGTH_SHORT

                                val snackbar = CustomSnackbar.make(requireActivity().findViewById(android.R.id.content), message, duration)
                                snackbar.setMargin(bottomDp = 66)
                                snackbar.show()
                            }


                            else -> {
                                when (inputNickname == nickname) {

                                    true -> {
                                        binding.ivProfileImage.tag?.let {
                                            viewLifecycleOwner.lifecycleScope.launch {
                                                showProgressBar()
                                                myPageViewModel.updateUserInfo(
                                                    userInfoUpdateForm = UserInfoUpdateForm(
                                                        email = email,
                                                        nickname = null,
                                                        image = binding.ivProfileImage.tag as Uri
                                                    )
                                                )
                                            }
                                        } ?: run {
                                            val message = "변경된 내용이 없습니다."
                                            val duration = Snackbar.LENGTH_SHORT

                                            val snackbar = CustomSnackbar.make(requireActivity().findViewById(android.R.id.content), message, duration)
                                            snackbar.setMargin(bottomDp = 66)
                                            snackbar.show()
                                        }
                                    }

                                    false -> {

                                        binding.ivProfileImage.tag?.let {
                                            viewLifecycleOwner.lifecycleScope.launch {
                                                showProgressBar()
                                                myPageViewModel.updateUserInfo(
                                                    userInfoUpdateForm = UserInfoUpdateForm(
                                                        email = email,
                                                        nickname = inputNickname,
                                                        image = binding.ivProfileImage.tag as Uri
                                                    )
                                                )
                                            }
                                        } ?: run {
                                            viewLifecycleOwner.lifecycleScope.launch {
                                                showProgressBar()
                                                myPageViewModel.updateUserInfo(
                                                    userInfoUpdateForm = UserInfoUpdateForm(
                                                        email = email,
                                                        nickname = inputNickname,
                                                        image = null
                                                    )
                                                )
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }


                    ivProfileImage.setOnClickListener {
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
            }.onFailure {
                when(it) {
                    is FailGetUserMeException -> {
                        Log.d("seungma", "게시판 버튼 2번 선택 에러 " + it.message)
                        val message = "유저 정보를 못가져왔습니다."
                        val duration = Snackbar.LENGTH_SHORT

                        val snackbar = CustomSnackbar.make(requireActivity().findViewById(android.R.id.content), message, duration)
                        snackbar.setMargin(bottomDp = 66)
                        snackbar.show()
                    }
                    else -> {

                    }
                }
            }


        }




        subscribe()
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            myPageViewModel.viewEvent.collect {
                hideProgressBar()
                when (it) {
                    is MyPageViewEvent.UpdateUserInfo -> {
                        parentFragmentManager.popBackStack()
                    }

                    else -> {

                    }
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