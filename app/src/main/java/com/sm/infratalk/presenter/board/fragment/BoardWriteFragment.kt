package com.sm.infratalk.presenter.board.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.sm.infratalk.data.FailGetUserMeException
import com.sm.infratalk.databinding.FragmentBoardWriteBinding
import com.sm.infratalk.di.component.DaggerBoardFragmentComponent
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.presenter.board.adpater.BoardWriteAdapter
import com.sm.infratalk.presenter.board.form.BoardContentInsertForm
import com.sm.infratalk.presenter.board.viewmodel.BoardViewEvent
import com.sm.infratalk.presenter.board.viewmodel.BoardViewModel
import com.sm.infratalk.presenter.common.CustomSnackbar
import kotlinx.coroutines.launch
import javax.inject.Inject

class BoardWriteFragment : Fragment() {
    private var _binding: FragmentBoardWriteBinding? = null
    private val binding get() = _binding!!
    private var adapter: BoardWriteAdapter? = null
    private lateinit var callback: OnBackPressedCallback
    private lateinit var userEntity: UserEntity

    @Inject
    lateinit var boardViewModelFactory: ViewModelProvider.Factory
    private val boardViewModel: BoardViewModel by viewModels { boardViewModelFactory }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        val imgList = uris.toMutableList()

        if (imgList.size > 5) {
            val message = "사진은 5장까지만 가능합니다."
            val duration = Snackbar.LENGTH_SHORT

            val snackbar = CustomSnackbar.make(requireView(), message, duration)
            snackbar.setMargin(bottomDp = 66)
            snackbar.show()
        } else {
            adapter?.setItems(imgList)
        }
    }

    private val pickImageLegacy =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imgList = mutableListOf<Uri>()
                result.data?.let {
                    it.clipData?.let { clipData ->
                        val count = clipData.itemCount
                        if (count > 5) {
                            Toast.makeText(
                                requireActivity(),
                                "사진은 5장까지만 가능합니다.",
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerBoardFragmentComponent.factory().create(context).inject(this)


        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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
        adapter = BoardWriteAdapter {}

        binding.apply {

            viewLifecycleOwner.lifecycleScope.launch {
                runCatching {
                    userEntity = boardViewModel.getUserMe()



                    btnInsert.setOnClickListener {
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
                }.onFailure {
                    when (it) {
                        is FailGetUserMeException -> {
                            Log.d("seungma", "게시판 버튼 2번 선택 에러 " + it.message)
                            val message = "유저 정보를 못가져왔습니다."
                            val duration = Snackbar.LENGTH_SHORT

                            val snackbar = CustomSnackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                message,
                                duration
                            )
                            snackbar.setMargin(bottomDp = 66)
                            snackbar.show()
                        }

                        else -> {

                        }
                    }
                }


            }

            btnUploadImage.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // API 33 이상: Photo Picker 사용
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else {
                    // API 32 이하: 기존 방식 사용
                    val intent = Intent(Intent.ACTION_PICK).apply {
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // 다중 선택 허용
                        type = "image/*"
                    }
                    pickImageLegacy.launch(intent)
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
                            is com.sm.infratalk.data.FailInsertException -> {
                                val message = "글작성에 실패 했습니다."
                                val duration = Snackbar.LENGTH_SHORT

                                val snackbar = CustomSnackbar.make(requireView(), message, duration)
                                snackbar.setMargin(bottomDp = 66)
                                snackbar.show()
                            }
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

}