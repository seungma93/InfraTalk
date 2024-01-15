package com.freetalk.presenter.fragment.mypage

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.freetalk.R
import com.freetalk.data.BlockedRequestException
import com.freetalk.data.ExistEmailException
import com.freetalk.data.FailInsertException
import com.freetalk.data.FailSendEmailException
import com.freetalk.data.FailUpdatetException
import com.freetalk.data.InvalidEmailException
import com.freetalk.data.InvalidPasswordException
import com.freetalk.data.NoImageException
import com.freetalk.data.model.request.ImagesRequest
import com.freetalk.databinding.FragmentMyAccountInfoEditBinding
import com.freetalk.databinding.FragmentSignUpBinding
import com.freetalk.di.component.DaggerMyPageFragmentComponent
import com.freetalk.di.component.DaggerSignFragmentComponent
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.form.SignUpForm
import com.freetalk.presenter.form.UserInfoUpdateForm
import com.freetalk.presenter.viewmodel.MyPageViewEvent
import com.freetalk.presenter.viewmodel.MyPageViewModel
import com.freetalk.presenter.viewmodel.SignViewModel
import com.freetalk.presenter.viewmodel.ViewEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyAccountInfoEditFragment : Fragment() {
    companion object {
        const val DEFAULT_PROFILE_IMAGE = "DEFAULT_PROFILE_IMAGE"
    }

    private var _binding: FragmentMyAccountInfoEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>
    private lateinit var activityResult: ActivityResultLauncher<Intent>

    @Inject
    lateinit var myPageViewModelFactory: ViewModelProvider.Factory
    private val myPageViewModel: MyPageViewModel by viewModels { myPageViewModelFactory }

    override fun onAttach(context: Context) {
        DaggerMyPageFragmentComponent.factory().create(context).inject(this)
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
                        val requestOptions = RequestOptions.circleCropTransform().autoClone()
                        Glide.with(requireContext())
                            .load(intent.data)
                            .apply(requestOptions)
                            .into(binding.ivProfileImage)

                        binding.ivProfileImage.tag = intent.data
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyAccountInfoEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userEntity = myPageViewModel.getUserInfo()
        val email = userEntity.email
        val nickname = userEntity.nickname
        val profileUri = userEntity.image


        binding.apply {

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
                    inputNickname.isNullOrEmpty() -> Toast.makeText(
                        requireActivity(), "닉네임을 입력하세요",
                        Toast.LENGTH_SHORT
                    ).show()

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
                                    Toast.makeText(
                                        requireActivity(), "변경된 내용이 없습니다",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                /*
                                                                when (binding.profileImage.tag == profileUri) {

                                                                    true -> {
                                                                        Toast.makeText(
                                                                            requireActivity(), "변경된 내용이 없습니다",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }

                                                                    false -> {
                                                                        viewLifecycleOwner.lifecycleScope.launch {
                                                                            showProgressBar()
                                                                            myPageViewModel.updateUserInfo(
                                                                                userInfoUpdateForm = UserInfoUpdateForm(
                                                                                    email = email,
                                                                                    nickname = null,
                                                                                    image = binding.profileImage.tag as Uri
                                                                                )
                                                                            )
                                                                        }
                                                                    }
                                                                }

                                 */

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
                                /*
                                when (binding.profileImage == profileUri) {

                                    true -> {
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

                                    false -> {
                                        viewLifecycleOwner.lifecycleScope.launch {
                                            showProgressBar()
                                            myPageViewModel.updateUserInfo(
                                                userInfoUpdateForm = UserInfoUpdateForm(
                                                    email = email,
                                                    nickname = inputNickname,
                                                    image = binding.profileImage.tag as Uri
                                                )
                                            )
                                        }
                                    }
                                }
                                */
                            }

                        }
                    }
                }
            }


            ivProfileImage.setOnClickListener {
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