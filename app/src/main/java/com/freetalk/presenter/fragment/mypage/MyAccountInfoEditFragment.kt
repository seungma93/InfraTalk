package com.freetalk.presenter.fragment.mypage

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
import com.freetalk.di.component.DaggerSignFragmentComponent
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.form.SignUpForm
import com.freetalk.presenter.viewmodel.SignViewModel
import com.freetalk.presenter.viewmodel.ViewEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyAccountInfoEditFragment : Fragment() {
    private var _binding: FragmentMyAccountInfoEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>
    private lateinit var activityResult: ActivityResultLauncher<Intent>

    @Inject
    lateinit var signViewModelFactory: ViewModelProvider.Factory
    private val signViewModel: SignViewModel by viewModels { signViewModelFactory }

    override fun onAttach(context: Context) {
        //DaggerSignFragmentComponent.factory().create(context).inject(this)
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
        _binding = FragmentMyAccountInfoEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
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
                                            listOf(it.profileImage.tag as Uri)
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

         */

        binding.apply {
            //tvEmail.text =
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