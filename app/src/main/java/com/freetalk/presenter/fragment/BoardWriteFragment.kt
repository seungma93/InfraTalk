package com.freetalk.presenter.fragment

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.remote.BoardRespond
import com.freetalk.data.remote.FirebaseBoardRemoteDataSourceImpl
import com.freetalk.data.remote.FirebaseUserRemoteDataSourceImpl
import com.freetalk.databinding.FragmentBoardBinding
import com.freetalk.databinding.FragmentBoardWriteBinding
import com.freetalk.databinding.FragmentHomeBinding
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.adapter.BoardWriteAdapter
import com.freetalk.presenter.viewmodel.*
import com.freetalk.repository.FirebaseBoardDataRepositoryImpl
import com.freetalk.repository.FirebaseUserDataRepositoryImpl
import com.freetalk.usecase.BoardUseCaseImpl
import com.freetalk.usecase.UserUseCaseImpl
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

class BoardWriteFragment : Fragment() {
    private var _binding: FragmentBoardWriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>
    private lateinit var activityResult: ActivityResultLauncher<Intent>
    private var imgList = mutableListOf<Uri>()
    private var adapter: BoardWriteAdapter? = null
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
                when {
                    it.data?.clipData != null -> {
                        val count = it.data?.clipData?.itemCount ?: 0
                        if (count > 10) {
                            Toast.makeText(
                                requireActivity(),
                                "사진은 10장까지만 가능합니다.",
                                Toast.LENGTH_LONG
                            ).show();
                        } else {
                            for (i in 0 until count) {
                                val imgUri = it.data?.clipData?.getItemAt(i)!!.uri
                                //Log.v("BoardWriteFragment", imgUri.toString())
                                imgList.add(imgUri)
                            }
                        }
                    }
                    else -> {
                        imgList.add(it.data?.data!!)
                    }
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
            imgList.remove(it)
            adapter?.setItems(imgList)
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
                    binding.titleEditText.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            requireActivity(),
                            "제목을 입력하세요.",
                            Toast.LENGTH_LONG
                        ).show();
                    }
                    binding.contextEditText.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            requireActivity(),
                            "내용을 입력하세요.",
                            Toast.LENGTH_LONG
                        ).show();
                    }
                    else -> {
                        viewLifecycleOwner.lifecycleScope.launch {
                            val boardEntity = BoardEntity(author = "테스트 계정",
                                title = binding.titleEditText.text.toString(),
                                context = binding.contextEditText.text.toString(),
                                image = imgList,
                                createTime = Date(System.currentTimeMillis()),
                                editTime = null
                            )
                            boardViewModel.insert(boardEntity)
                        }
                    }
                }
            }
            recyclerviewImage.adapter = adapter
            subscribe()
        }
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    boardViewModel.viewEvent.collect {
                        when(it) {
                            is BoardViewEvent.Insert -> {
                                when(it.respond.respond) {
                                    is BoardRespond.InsertSuccess -> {
                                        (requireActivity() as? Navigable)?.navigateFragment(EndPoint.Board(1))
                                    }
                                }
                            }
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