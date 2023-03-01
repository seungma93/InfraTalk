package com.freetalk.presenter.fragment.board

import android.Manifest
import android.app.Activity.RESULT_OK
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
import com.freetalk.data.UserSingleton
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.remote.BoardResponse
import com.freetalk.data.remote.FirebaseBoardRemoteDataSourceImpl
import com.freetalk.databinding.FragmentBoardWriteBinding
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.adapter.BoardWriteAdapter
import com.freetalk.presenter.viewmodel.*
import com.freetalk.repository.FirebaseBoardDataRepositoryImpl
import com.freetalk.usecase.BoardUseCaseImpl
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.*

class BoardWriteFragment : Fragment() {
    private var _binding: FragmentBoardWriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>
    private lateinit var activityResult: ActivityResultLauncher<Intent>
    private var adapter: BoardWriteAdapter? = null
    private val imgList = mutableListOf<Uri>()
    private val boardViewModel: BoardViewModel by lazy {
        val firebaseBoardRemoteDataSourceImpl = FirebaseBoardRemoteDataSourceImpl(Firebase.firestore, FirebaseStorage.getInstance())
        val firebaseBoardDataRepositoryImpl =
            FirebaseBoardDataRepositoryImpl(firebaseBoardRemoteDataSourceImpl)
        val firebaseBoardCaseImpl = BoardUseCaseImpl(firebaseBoardDataRepositoryImpl)
        val factory = BoardViewModelFactory(firebaseBoardCaseImpl)
        ViewModelProvider(requireActivity(), factory).get(BoardViewModel::class.java)
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

            if (it.resultCode == RESULT_OK) {
                imgList.clear()
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

        adapter = BoardWriteAdapter {
        }
        binding.apply {
            btnUploadImage.setOnClickListener {
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
                            val boardEntity = BoardEntity(author = UserSingleton,
                                title = binding.titleEditText.text.toString(),
                                content = binding.contextEditText.text.toString(),
                                image = imgList,
                                createTime = Date(System.currentTimeMillis()),
                                editTime = null
                            )
                            showProgressBar()
                            boardViewModel.insert(boardEntity)
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
        requireActivity().window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
                        when(it) {
                            is BoardViewEvent.Insert -> {
                                when(it.boardInsertData.respond) {
                                    is BoardResponse.InsertSuccess -> {
                                        (requireActivity() as? Navigable)?.navigateFragment(EndPoint.Board(1))
                                        hideProgressBar()
                                    }
                                    else -> {}
                                }
                            }
                            else -> {}
                        }
                    }
        }
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

}