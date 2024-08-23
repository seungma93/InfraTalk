package com.seungma.infratalk.presenter.board.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.seungma.infratalk.databinding.FragmentBoardBinding
import com.seungma.infratalk.di.component.DaggerBoardFragmentComponent
import com.seungma.infratalk.domain.board.entity.BoardContentPrimaryKeyEntity
import com.seungma.infratalk.domain.chat.entity.ChatPrimaryKeyEntity
import com.seungma.infratalk.presenter.board.adpater.BoardListAdapter
import com.seungma.infratalk.presenter.board.form.BoardBookmarkAddForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarkDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarksDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardLikeAddForm
import com.seungma.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLikeDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardLikesDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardListLoadForm
import com.seungma.infratalk.presenter.board.listener.OnScrollListener
import com.seungma.infratalk.presenter.board.viewmodel.BoardViewEvent
import com.seungma.infratalk.presenter.board.viewmodel.BoardViewModel
import com.seungma.infratalk.presenter.chat.form.ChatRoomCheckForm
import com.seungma.infratalk.presenter.chat.form.ChatRoomCreateForm
import com.seungma.infratalk.presenter.main.activity.EndPoint
import com.seungma.infratalk.presenter.main.activity.Navigable
import com.seungma.infratalk.presenter.main.fragment.ChildFragmentNavigable
import com.seungma.infratalk.presenter.main.fragment.MainChildFragmentEndPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


class BoardFragment : Fragment() {
    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!
    private var _adapter: BoardListAdapter? = null
    private val adapter get() = _adapter!!
    private val onScrollListener: OnScrollListener = OnScrollListener({ moreItems() }, {
    })

    @Inject
    lateinit var boardViewModelFactory: ViewModelProvider.Factory
    private val boardViewModel: BoardViewModel by viewModels { boardViewModelFactory }


    override fun onAttach(context: Context) {
        DaggerBoardFragmentComponent.factory().create(context).inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var isFabOpen = false
        _adapter = BoardListAdapter(
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
                                val boardViewState = boardViewModel.deleteBookMark(
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
                                val boardViewState = boardViewModel.addBookMark(
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
                                val boardViewState = boardViewModel.deleteLike(
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
                                val boardViewState = boardViewModel.addLike(
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
            chatClick = { boardMetaEntity ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val member =
                        listOf(boardViewModel.getUserInfo().email, boardMetaEntity.author.email)
                    boardViewModel.startChat(
                        chatRoomCreateForm = ChatRoomCreateForm(member = member),
                        chatRoomCheckForm = ChatRoomCheckForm(member = member)
                    )
                }
            },
            userEntity = boardViewModel.getUserInfo(),
            deleteClick = { boardMetaEntity ->
                showProgressBar()
                viewLifecycleOwner.lifecycleScope.launch {
                    val boardViewState = boardViewModel.deleteBoard(
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
                    adapter.submitList(boardViewState.boardListEntity.boardList) {
                        hideProgressBar()
                    }
                }
            }
        )

        binding.apply {
            // fab 메뉴
            btnFabMenu.setOnClickListener {
                isFabOpen = toggleFab(isFabOpen)
            }
            // 글쓰기 버튼
            btnFabWrite.setOnClickListener {
                (parentFragment as? ChildFragmentNavigable)?.navigateFragment(
                    MainChildFragmentEndPoint.BoardWrite
                )
                toggleFab(isFabOpen = true)
            }
            // 나의 게시글 버튼
            btnFabMyList.setOnClickListener {
                val endPoint =
                    MainChildFragmentEndPoint.MyBoard(userEntity = boardViewModel.getUserInfo())
                (parentFragment as? ChildFragmentNavigable)?.navigateFragment(endPoint)
                toggleFab(isFabOpen = true)
            }
            // 북마크 버튼
            btnFabBookmark.setOnClickListener {
                val endPoint =
                    MainChildFragmentEndPoint.MyBookmarkBoard
                (parentFragment as? ChildFragmentNavigable)?.navigateFragment(endPoint)
                toggleFab(isFabOpen = true)
            }
            // 좋아요 버튼
            btnFabLike.setOnClickListener {
                val endPoint = MainChildFragmentEndPoint.MyLikeBoard
                (parentFragment as? ChildFragmentNavigable)?.navigateFragment(endPoint)
                toggleFab(isFabOpen = true)
            }

            // 스와이프
            swipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    kotlin.runCatching {
                        boardViewModel.loadBoardList(BoardListLoadForm(reload = true))
                    }
                    swipeRefreshLayout.isRefreshing = false
                }

            }
            recyclerviewBoardList.adapter = adapter
            recyclerviewBoardList.itemAnimator = null
        }
        subscribe()
        loadBoardList()
        initScrollListener()
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            boardViewModel.viewEvent.collect {
                when (it) {
                    is BoardViewEvent.ChatStart -> {
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
            val boardViewState = boardViewModel.loadBoardList(BoardListLoadForm(reload = true))
            adapter.submitList(boardViewState.boardListEntity.boardList) {
                binding.recyclerviewBoardList.scrollToPosition(0)
                hideProgressBar()
            }

        }
    }

    private fun initScrollListener() {
        binding.recyclerviewBoardList.addOnScrollListener(onScrollListener)
    }

    private fun moreItems() {
        showProgressBar()
        viewLifecycleOwner.lifecycleScope.launch {
            val boardViewState = boardViewModel.loadBoardList(BoardListLoadForm(reload = false))
            adapter.submitList(boardViewState.boardListEntity.boardList) {
                hideProgressBar()
            }
        }
    }

    private fun toggleFab(isFabOpen: Boolean): Boolean {
        return if (isFabOpen) {
            AnimatorSet().apply {
                this.playTogether(
                    ObjectAnimator.ofFloat(binding.btnFabLike, "translationY", 0f),
                    ObjectAnimator.ofFloat(binding.btnFabBookmark, "translationY", 0f),
                    ObjectAnimator.ofFloat(binding.btnFabMyList, "translationY", 0f),
                    ObjectAnimator.ofFloat(binding.btnFabWrite, "translationY", 0f)
                )
            }.start()
            false
        } else {
            AnimatorSet().apply {
                this.playTogether(
                    ObjectAnimator.ofFloat(binding.btnFabLike, "translationY", -800f),
                    ObjectAnimator.ofFloat(binding.btnFabBookmark, "translationY", -600f),
                    ObjectAnimator.ofFloat(binding.btnFabMyList, "translationY", -400f),
                    ObjectAnimator.ofFloat(binding.btnFabWrite, "translationY", -200f)
                )
            }.start()
            true
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