package com.sm.infratalk.presenter.mypage.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.sm.infratalk.data.FailGetUserMeException
import com.sm.infratalk.databinding.FragmentMyBookmarkBoardBinding
import com.sm.infratalk.di.component.DaggerMyPageFragmentComponent
import com.sm.infratalk.domain.board.entity.BoardContentPrimaryKeyEntity
import com.sm.infratalk.domain.chat.entity.ChatPrimaryKeyEntity
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.presenter.board.form.BoardBookmarkAddForm
import com.sm.infratalk.presenter.board.form.BoardBookmarkDeleteForm
import com.sm.infratalk.presenter.board.form.BoardBookmarksDeleteForm
import com.sm.infratalk.presenter.board.form.BoardDeleteForm
import com.sm.infratalk.presenter.board.form.BoardLikeAddForm
import com.sm.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.sm.infratalk.presenter.board.form.BoardLikeDeleteForm
import com.sm.infratalk.presenter.board.form.BoardLikesDeleteForm
import com.sm.infratalk.presenter.chat.form.ChatRoomCheckForm
import com.sm.infratalk.presenter.chat.form.ChatRoomCreateForm
import com.sm.infratalk.presenter.common.CustomSnackbar
import com.sm.infratalk.presenter.main.activity.EndPoint
import com.sm.infratalk.presenter.main.activity.Navigable
import com.sm.infratalk.presenter.mypage.adapter.MyBookmarkBoardListAdapter
import com.sm.infratalk.presenter.mypage.viewmodel.MyBookmarkBoardViewEvent
import com.sm.infratalk.presenter.mypage.viewmodel.MyBookmarkBoardViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyBookmarkBoardFragment : Fragment() {

    private var _binding: FragmentMyBookmarkBoardBinding? = null
    private val binding get() = _binding!!
    private var _adapter: MyBookmarkBoardListAdapter? = null
    private val adapter get() = _adapter!!
    private lateinit var userEntity: UserEntity

    @Inject
    lateinit var myBookmarkBoardViewModelFactory: ViewModelProvider.Factory
    private val myBookmarkBoardViewModel: MyBookmarkBoardViewModel by viewModels { myBookmarkBoardViewModelFactory }

    override fun onAttach(context: Context) {
        DaggerMyPageFragmentComponent.factory().create(context).inject(this)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("BoardWriteFragment", "백스택 실행")
                parentFragmentManager.popBackStackImmediate()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBookmarkBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                userEntity = myBookmarkBoardViewModel.getUserMe()

                _adapter = MyBookmarkBoardListAdapter(
                    itemClick = {
                        Log.d("comment", "클릭시 넘어온 board값" + it.author.email)
                        val endPoint = EndPoint.BoardContent(
                            boardContentPrimaryKeyEntity = BoardContentPrimaryKeyEntity(
                                boardAuthorEmail = it.author.email,
                                boardCreateTime = it.createTime
                            )
                        )
                        (requireActivity() as? Navigable)?.navigateFragment(endPoint)
                    },
                    bookmarkClick = { boardEntity ->
                        boardEntity.apply {
                            when (bookmarkEntity.isBookmark) {
                                true -> {
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        val boardViewState = myBookmarkBoardViewModel.deleteBookMark(
                                            BoardBookmarkDeleteForm(
                                                boardAuthorEmail = boardMetaEntity.author.email,
                                                boardCreateTime = boardMetaEntity.createTime
                                            )
                                        )
                                        adapter.submitList(boardViewState.boardListEntity.boardList)
                                    }
                                }

                                false -> {
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        val boardViewState = myBookmarkBoardViewModel.addBookMark(
                                            BoardBookmarkAddForm(
                                                boardAuthorEmail = boardMetaEntity.author.email,
                                                boardCreateTime = boardMetaEntity.createTime
                                            )
                                        )
                                        adapter.submitList(boardViewState.boardListEntity.boardList)
                                    }
                                }
                            }
                        }
                    },
                    likeClick = { boardEntity ->
                        boardEntity.apply {
                            when (likeEntity.isLike) {
                                true -> {
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        val boardViewState = myBookmarkBoardViewModel.deleteLike(
                                            BoardLikeDeleteForm(
                                                boardAuthorEmail = boardMetaEntity.author.email,
                                                boardCreateTime = boardMetaEntity.createTime
                                            ), BoardLikeCountLoadForm(
                                                boardAuthorEmail = boardMetaEntity.author.email,
                                                boardCreateTime = boardMetaEntity.createTime
                                            )
                                        )
                                        adapter.submitList(boardViewState.boardListEntity.boardList)
                                    }
                                }

                                false -> {
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        val boardViewState = myBookmarkBoardViewModel.addLike(
                                            BoardLikeAddForm(
                                                boardAuthorEmail = boardMetaEntity.author.email,
                                                boardCreateTime = boardMetaEntity.createTime
                                            ), BoardLikeCountLoadForm(
                                                boardAuthorEmail = boardMetaEntity.author.email,
                                                boardCreateTime = boardMetaEntity.createTime
                                            )
                                        )
                                        adapter.submitList(boardViewState.boardListEntity.boardList)
                                    }
                                }
                            }
                        }
                    },
                    deleteClick = { boardEntity ->
                        showProgressBar()
                        boardEntity.apply {
                            viewLifecycleOwner.lifecycleScope.launch {
                                val myBoardViewState = myBookmarkBoardViewModel.deleteBoard(
                                    boardDeleteForm = BoardDeleteForm(
                                        boardAuthorEmail = boardMetaEntity.author.email,
                                        boardCreateTime = boardMetaEntity.createTime
                                    ),
                                    boardBookmarksDeleteForm = BoardBookmarksDeleteForm(
                                        boardAuthorEmail = boardMetaEntity.author.email,
                                        boardCreateTime = boardMetaEntity.createTime
                                    ),
                                    boardLikesDeleteForm = BoardLikesDeleteForm(
                                        boardAuthorEmail = boardMetaEntity.author.email,
                                        boardCreateTime = boardMetaEntity.createTime
                                    )
                                )
                                adapter.submitList(myBoardViewState.boardListEntity.boardList) {
                                    hideProgressBar()
                                }
                            }
                        }
                    },
                    chatClick = { boardMetaEntity ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            val member =
                                listOf(userEntity.email, boardMetaEntity.author.email)
                            myBookmarkBoardViewModel.startChat(
                                chatRoomCreateForm = ChatRoomCreateForm(member = member),
                                chatRoomCheckForm = ChatRoomCheckForm(member = member)
                            )
                        }
                    },
                    userEntity = userEntity
                )
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

            binding.rvMyBookmarkBoardList.adapter = adapter
        }

        binding.apply {

            swipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    kotlin.runCatching {
                        myBookmarkBoardViewModel.loadMyBookmarkBoardList()
                    }
                    swipeRefreshLayout.isRefreshing = false
                }

            }
        }

        loadBoardList()
        subscribe()
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            myBookmarkBoardViewModel.viewEvent.collect {
                when (it) {
                    is MyBookmarkBoardViewEvent.ChatStart -> {
                        when (it.chatStartEntity.isSuccess) {
                            true -> {
                                Log.d("seungma", "채팅 시작 성공")
                                val endPoint = EndPoint.Chat(
                                    chatPrimaryKeyEntity = ChatPrimaryKeyEntity(
                                        partnerEmail = it.chatStartEntity.chatPartner,
                                        chatRoomId = it.chatStartEntity.chatRoomId ?: error("")
                                    )
                                )
                                (requireActivity() as? Navigable)?.navigateFragment(endPoint)
                            }

                            false -> Log.d("seungma", "채팅방 시작 실패")
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun loadBoardList() {
        viewLifecycleOwner.lifecycleScope.launch {
            showProgressBar()
            val boardViewState = myBookmarkBoardViewModel.loadMyBookmarkBoardList()
            adapter.submitList(boardViewState.boardListEntity.boardList) {
                binding.rvMyBookmarkBoardList.scrollToPosition(0)
                hideProgressBar()
            }

        }
    }

    private fun showProgressBar() {
        Log.d("BoardFragment", "프로그레스바 시작")
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
        Log.d("BoardFragment", "프로그레스바 종료")
        clearBlockLayoutTouch()
        binding.progressBar.isVisible = false
    }

    private fun clearBlockLayoutTouch() {
        requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}