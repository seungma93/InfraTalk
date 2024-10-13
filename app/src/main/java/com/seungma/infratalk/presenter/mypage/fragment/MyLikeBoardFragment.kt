package com.seungma.infratalk.presenter.mypage.fragment

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
import com.seungma.infratalk.databinding.FragmentMyLikeBoardBinding
import com.seungma.infratalk.di.component.DaggerMyPageFragmentComponent
import com.seungma.infratalk.domain.board.entity.BoardContentPrimaryKeyEntity
import com.seungma.infratalk.domain.chat.entity.ChatPrimaryKeyEntity
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.presenter.board.form.BoardBookmarkAddForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarkDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarksDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardLikeAddForm
import com.seungma.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLikeDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardLikesDeleteForm
import com.seungma.infratalk.presenter.chat.form.ChatRoomCheckForm
import com.seungma.infratalk.presenter.chat.form.ChatRoomCreateForm
import com.seungma.infratalk.presenter.main.activity.EndPoint
import com.seungma.infratalk.presenter.main.activity.Navigable
import com.seungma.infratalk.presenter.mypage.adapter.MyLikeBoardListAdapter
import com.seungma.infratalk.presenter.mypage.viewmodel.MyLikeBoardViewEvent
import com.seungma.infratalk.presenter.mypage.viewmodel.MyLikeBoardViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyLikeBoardFragment : Fragment() {

    private var _binding: FragmentMyLikeBoardBinding? = null
    private val binding get() = _binding!!
    private var _adapter: MyLikeBoardListAdapter? = null
    private val adapter get() = _adapter!!
    private lateinit var callback: OnBackPressedCallback
    private lateinit var userEntity: UserEntity

    @Inject
    lateinit var myLikeBoardViewModelFactory: ViewModelProvider.Factory
    private val myLikeBoardViewModel: MyLikeBoardViewModel by viewModels { myLikeBoardViewModelFactory }

    override fun onAttach(context: Context) {
        DaggerMyPageFragmentComponent.factory().create(context).inject(this)
        callback = object : OnBackPressedCallback(true) {
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
        _binding = FragmentMyLikeBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            userEntity = myLikeBoardViewModel.getUserMe()

            _adapter = MyLikeBoardListAdapter(
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
                                    val boardViewState = myLikeBoardViewModel.deleteBookMark(
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
                                    val boardViewState = myLikeBoardViewModel.addBookMark(
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
                                    val boardViewState = myLikeBoardViewModel.deleteLike(
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
                                    val boardViewState = myLikeBoardViewModel.addLike(
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
                            val myBoardViewState = myLikeBoardViewModel.deleteBoard(
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
                        myLikeBoardViewModel.startChat(
                            chatRoomCreateForm = ChatRoomCreateForm(member = member),
                            chatRoomCheckForm = ChatRoomCheckForm(member = member)
                        )
                    }
                },
                userEntity = userEntity
            )

            binding.rvMyLikeBoardList.adapter = adapter
        }

        binding.apply {

            swipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    kotlin.runCatching {
                        myLikeBoardViewModel.loadMyLikeBoardList()
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
            myLikeBoardViewModel.viewEvent.collect {
                when (it) {
                    is MyLikeBoardViewEvent.ChatStart -> {
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
            val boardViewState = myLikeBoardViewModel.loadMyLikeBoardList()
            adapter.submitList(boardViewState.boardListEntity.boardList) {
                binding.rvMyLikeBoardList.scrollToPosition(0)
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