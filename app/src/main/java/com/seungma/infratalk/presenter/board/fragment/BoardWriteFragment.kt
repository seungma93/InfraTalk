package com.seungma.infratalk.presenter.board.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.seungma.infratalk.databinding.FragmentBoardWriteBinding
import com.seungma.infratalk.di.component.DaggerBoardFragmentComponent
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.presenter.board.adpater.BoardWriteAdapter
import com.seungma.infratalk.presenter.board.form.BoardContentInsertForm
import com.seungma.infratalk.presenter.board.viewmodel.BoardViewEvent
import com.seungma.infratalk.presenter.board.viewmodel.BoardViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class BoardWriteFragment : Fragment() {
    private var _binding: FragmentBoardWriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResult: ActivityResultLauncher<Intent>
    private var adapter: BoardWriteAdapter? = null
    private lateinit var callback: OnBackPressedCallback
    private lateinit var userEntity: UserEntity

    @Inject
    lateinit var boardViewModelFactory: ViewModelProvider.Factory
    private val boardViewModel: BoardViewModel by viewModels { boardViewModelFactory }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerBoardFragmentComponent.factory().create(context).inject(this)
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                Log.v("BoardWriteFragment", "퍼미션 체크 실행")
                if (isGranted) {
                    // 권한이 필요한 작업 수행
                    navigateImage()
                } else {
                    Log.v("BoardWriteFragment", "퍼미션 허용 안됨 ")
                    handlePermissionDenied()
                }
            }

        activityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it.resultCode == RESULT_OK) {
                    val imgList = mutableListOf<Uri>()
                    it.data?.let {
                        it.clipData?.let { clipData ->
                            val count = clipData.itemCount
                            if (count > 10) {
                                Toast.makeText(
                                    requireActivity(),
                                    "사진은 10장까지만 가능합니다.",
                                    Toast.LENGTH_LONG
                                ).show();
                            } else {

                                (0 until count).forEach {
                                    val uri = clipData.getItemAt(it).uri
                                    imgList.add(uri)
                                }
                            }
                        } ?: it.data?.let { uri -> imgList.add(uri) }
                    }
                    adapter?.setItems(imgList)
                }
            }

        callback = object : OnBackPressedCallback(true) {
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
    ): View? {
        _binding = FragmentBoardWriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BoardWriteFragment", "갯수" + parentFragmentManager.backStackEntryCount)
        Log.d(
            "BoardWriteFragment",
            "갯수2" + requireParentFragment().childFragmentManager.backStackEntryCount
        )
        adapter = BoardWriteAdapter {}

        binding.apply {

            viewLifecycleOwner.lifecycleScope.launch {
                userEntity = boardViewModel.getUserMe()

                btnInsert.setOnClickListener {
                    Log.v("BoardWriteFragment", "등록 버튼 클릭")
                    when {
                        titleEditText.text.isNullOrEmpty() -> {
                            Toast.makeText(
                                requireActivity(),
                                "제목을 입력하세요.",
                                Toast.LENGTH_LONG
                            ).show();
                        }

                        contextEditText.text.isNullOrEmpty() -> {
                            Toast.makeText(
                                requireActivity(),
                                "내용을 입력하세요.",
                                Toast.LENGTH_LONG
                            ).show();
                        }

                        else -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                showProgressBar()
                                boardViewModel.writeBoardContent(
                                    boardContentInsertForm = BoardContentInsertForm(
                                        author = userEntity,
                                        title = binding.titleEditText.text.toString(),
                                        content = binding.contextEditText.text.toString(),
                                        images = when (adapter!!.getItems().isEmpty()) {
                                            true -> null
                                            false -> adapter!!.getItems()
                                        },
                                        editTime = null
                                    )
                                )
                            }
                        }
                    }
                }
            }

            btnUploadImage.setOnClickListener {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                        // Android 13 이상: READ_MEDIA_IMAGES 권한 요청
                        if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.READ_MEDIA_IMAGES
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            // 권한이 이미 허용됨
                            navigateImage()
                        } else {
                            // 권한 요청
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    }
                    else -> {
                        // Android 12 이하: READ_EXTERNAL_STORAGE 권한 요청
                        if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            // 권한이 이미 허용됨
                            navigateImage()
                        } else {
                            // 권한 요청
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                }
            }


            recyclerviewImage.adapter = adapter
            subscribe()
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

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            boardViewModel.viewEvent.collect {
                when (it) {
                    is BoardViewEvent.Register -> {
                        hideProgressBar()
                        parentFragmentManager.popBackStack()
                    }

                    is BoardViewEvent.Error -> {
                        hideProgressBar()
                        when (it.errorCode) {
                            is com.seungma.infratalk.data.FailInsertException -> Toast.makeText(
                                requireActivity(), "인서트에 실패 했습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }

    private fun navigateImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        activityResult.launch(intent)
    }

    // 권한 거부 시 처리 로직
    private fun handlePermissionDenied() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            !ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    Manifest.permission.READ_MEDIA_IMAGES
                else
                    Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            // "다시 묻지 않음" 선택됨
            AlertDialog.Builder(requireContext())
                .setTitle("권한 필요")
                .setMessage("이미지를 선택하려면 권한이 필요합니다. 설정에서 권한을 허용해주세요.")
                .setPositiveButton("설정으로 이동") { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", requireContext().packageName, null)
                    }
                    startActivity(intent)
                }
                .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
                .show()
        } else {
            // 권한 설명 및 요청
            AlertDialog.Builder(requireContext())
                .setTitle("권한 필요")
                .setMessage("이미지를 선택하려면 저장소 접근 권한이 필요합니다.")
                .setPositiveButton("권한 요청") { _, _ ->
                    permissionLauncher.launch(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            Manifest.permission.READ_MEDIA_IMAGES
                        else
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
                .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }


}