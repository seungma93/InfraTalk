package com.seungma.infratalk.presenter.board.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.seungma.infratalk.databinding.FragmentBoardContentBinding
import com.seungma.infratalk.di.component.DaggerBoardFragmentComponent
import com.seungma.infratalk.domain.board.entity.BoardContentPrimaryKeyEntity
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.presenter.board.adpater.BoardContentImageAdapter
import com.seungma.infratalk.presenter.board.adpater.CommentListAdapter
import com.seungma.infratalk.presenter.board.adpater.ListItem
import com.seungma.infratalk.presenter.board.form.BoardBookmarkAddForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarkDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarkLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLikeAddForm
import com.seungma.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLikeDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardLikeLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLoadForm
import com.seungma.infratalk.presenter.board.form.BoardRelatedAllCommentMetaListSelectForm
import com.seungma.infratalk.presenter.board.form.CommentBookmarkAddForm
import com.seungma.infratalk.presenter.board.form.CommentBookmarkDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentInsertForm
import com.seungma.infratalk.presenter.board.form.CommentLikeAddForm
import com.seungma.infratalk.presenter.board.form.CommentLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.CommentLikeDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentMetaListLoadForm
import com.seungma.infratalk.presenter.board.form.CommentRelatedBookmarksDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentRelatedLikesDeleteForm
import com.seungma.infratalk.presenter.board.listener.OnCommentScrollListener
import com.seungma.infratalk.presenter.board.viewmodel.BoardContentViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


class BoardContentFragment : Fragment() {
    companion object {
        const val BOARD_ITEM_KEY = "BOARD_ITEM_KEY"

        fun newInstance(
            boardContentPrimaryKeyEntity: BoardContentPrimaryKeyEntity
        ): BoardContentFragment {
            return BoardContentFragment().apply {
                arguments = bundleOf(
                    BOARD_ITEM_KEY to boardContentPrimaryKeyEntity
                )
            }
        }
    }

    private var _binding: FragmentBoardContentBinding? = null
    private val binding get() = _binding!!
    private val boardContentPrimaryKeyEntity
        get() = requireArguments().getSerializable(
            BOARD_ITEM_KEY
        ) as BoardContentPrimaryKeyEntity
    private lateinit var callback: OnBackPressedCallback
    private var _boardContentImageAdapter: BoardContentImageAdapter? = null
    private val boardContentImageAdapter get() = _boardContentImageAdapter!!
    private var _commentAdapter: CommentListAdapter? = null
    private val commentAdapter get() = _commentAdapter!!
    private val onCommentScrollListener: OnCommentScrollListener = OnCommentScrollListener({
        Log.d("seungma", "람다 전달")
        moreItems()
    }, {
        Toast.makeText(
            requireContext(),
            "마지막 페이지 입니다.",
            Toast.LENGTH_SHORT
        ).show()
    })
    private lateinit var userEntity: UserEntity


    @Inject
    lateinit var boardContentViewModelFactory: ViewModelProvider.Factory
    private val boardContentViewModel: BoardContentViewModel by viewModels { boardContentViewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerBoardFragmentComponent.factory().create(context).inject(this)

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("BoardWriteFragment", "백스택 실행")
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            userEntity =boardContentViewModel.getUserMe()

            _commentAdapter = CommentListAdapter(
                commentItemClick = {
                    //val endPoint = EndPoint.BoardContent(boardEntity = it)
                    //(requireActivity() as? Navigable)?.navigateFragment(endPoint)
                },
                commentBookmarkClick = { commentEntity ->
                    commentEntity.apply {
                        when (bookmarkEntity.isBookmark) {
                            true -> {
                                viewLifecycleOwner.lifecycleScope.launch {
                                    val viewState = boardContentViewModel.deleteCommentBookmark(
                                        commentBookmarkDeleteForm = CommentBookmarkDeleteForm(
                                            commentAuthorEmail = commentMetaEntity.author.email,
                                            commentCreateTime = commentMetaEntity.createTime
                                        )
                                    )
                                    commentAdapter.submitList(createListItem(viewState))
                                }

                            }

                            false -> {
                                viewLifecycleOwner.lifecycleScope.launch {
                                    val viewState = boardContentViewModel.addCommentBookmark(
                                        commentBookmarkAddForm = CommentBookmarkAddForm(
                                            commentAuthorEmail = commentMetaEntity.author.email,
                                            commentCreateTime = commentMetaEntity.createTime
                                        )
                                    )
                                    commentAdapter.submitList(createListItem(viewState))
                                }
                            }
                        }
                    }
                },
                commentLikeClick = { commentEntity ->
                    commentEntity.apply {
                        when (likeEntity.isLike) {
                            true -> {
                                viewLifecycleOwner.lifecycleScope.launch {
                                    val viewState = boardContentViewModel.deleteCommentLike(
                                        commentLikeDeleteForm = CommentLikeDeleteForm(
                                            commentAuthorEmail = commentMetaEntity.author.email,
                                            commentCreateTime = commentMetaEntity.createTime
                                        ), commentLikeCountLoadForm = CommentLikeCountLoadForm(
                                            commentAuthorEmail = commentMetaEntity.author.email,
                                            commentCreateTime = commentMetaEntity.createTime
                                        )
                                    )
                                    commentAdapter.submitList(createListItem(viewState))
                                }
                            }

                            false -> {
                                viewLifecycleOwner.lifecycleScope.launch {
                                    val viewState = boardContentViewModel.addCommentLike(
                                        commentLikeAddForm = CommentLikeAddForm(
                                            commentAuthorEmail = commentMetaEntity.author.email,
                                            commentCreateTime = commentMetaEntity.createTime
                                        ), commentLikeCountLoadForm = CommentLikeCountLoadForm(
                                            commentAuthorEmail = commentMetaEntity.author.email,
                                            commentCreateTime = commentMetaEntity.createTime
                                        )
                                    )
                                    commentAdapter.submitList(createListItem(viewState))
                                }
                            }
                        }
                    }
                },
                commentDeleteClick = { commentEntity ->
                    commentEntity.apply {
                        viewLifecycleOwner.lifecycleScope.launch {
                            showProgressBar()
                            val viewState = boardContentViewModel.deleteComment(
                                commentDeleteForm = CommentDeleteForm(
                                    commentAuthorEmail = commentMetaEntity.author.email,
                                    commentCreateTime = commentMetaEntity.createTime
                                ),
                                commentRelatedBookmarksDeleteForm = CommentRelatedBookmarksDeleteForm(
                                    commentAuthorEmail = commentMetaEntity.author.email,
                                    commentCreateTime = commentMetaEntity.createTime
                                ),
                                commentRelatedLikesDeleteForm = CommentRelatedLikesDeleteForm(
                                    commentAuthorEmail = commentMetaEntity.author.email,
                                    commentCreateTime = commentMetaEntity.createTime
                                )
                            )
                            commentAdapter.submitList(createListItem(viewState)) {
                                hideProgressBar()
                            }
                        }
                    }

                },
                boardBookmarkClick = {
                    when (it.bookmarkEntity.isBookmark) {
                        true -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                val viewState = boardContentViewModel.deleteBoardContentBookmark(
                                    boardBookmarkDeleteForm = BoardBookmarkDeleteForm(
                                        boardAuthorEmail = it.boardMetaEntity.author.email,
                                        boardCreateTime = it.boardMetaEntity.createTime
                                    )
                                )
                                commentAdapter.submitList(createListItem(viewState))
                            }
                        }

                        false -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                val viewState = boardContentViewModel.addBoardContentBookmark(
                                    boardBookmarkAddForm = BoardBookmarkAddForm(
                                        boardAuthorEmail = it.boardMetaEntity.author.email,
                                        boardCreateTime = it.boardMetaEntity.createTime
                                    )
                                )
                                commentAdapter.submitList(createListItem(viewState))
                            }
                        }
                    }
                },
                boardLikeClick = {
                    when (it.likeEntity.isLike) {
                        true -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                val viewState = boardContentViewModel.deleteBoardContentLike(
                                    boardLikeDeleteForm = BoardLikeDeleteForm(
                                        boardAuthorEmail = it.boardMetaEntity.author.email,
                                        boardCreateTime = it.boardMetaEntity.createTime
                                    ),
                                    boardLikeCountLoadForm = BoardLikeCountLoadForm(
                                        boardAuthorEmail = it.boardMetaEntity.author.email,
                                        boardCreateTime = it.boardMetaEntity.createTime
                                    )
                                )
                                commentAdapter.submitList(createListItem(viewState))
                            }
                        }

                        false -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                val viewState = boardContentViewModel.addBoardContentLike(
                                    boardLikeAddForm = BoardLikeAddForm(
                                        boardAuthorEmail = it.boardMetaEntity.author.email,
                                        boardCreateTime = it.boardMetaEntity.createTime
                                    ),
                                    boardLikeCountLoadForm = BoardLikeCountLoadForm(
                                        boardAuthorEmail = it.boardMetaEntity.author.email,
                                        boardCreateTime = it.boardMetaEntity.createTime
                                    )
                                )
                                commentAdapter.submitList(createListItem(viewState))
                            }
                        }
                    }
                },
                userEntity = userEntity
            )

            binding.rvComment.adapter = commentAdapter
        }

        _boardContentImageAdapter = BoardContentImageAdapter { }


        binding.apply {

            btnSubmitComment.setOnClickListener {
                val inputComment = binding.commentTextInput.editText!!.text.toString()
                when (inputComment.isEmpty()) {
                    true -> {
                        Toast.makeText(
                            requireActivity(), "내용을 입력하세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    false -> {
                        btnSubmitComment.isEnabled = false
                        viewLifecycleOwner.lifecycleScope.launch {
                            showProgressBar()

                            boardContentViewModel.writeComment(
                                commentInsertForm = CommentInsertForm(
                                    boardAuthorEmail = boardContentPrimaryKeyEntity.boardAuthorEmail,
                                    boardCreateTime = boardContentPrimaryKeyEntity.boardCreateTime,
                                    content = commentEditText.text.toString()
                                )
                            )

                            val viewState = boardContentViewModel.loadBoardRelatedAllCommentList(
                                boardRelatedAllCommentMetaListSelectForm = BoardRelatedAllCommentMetaListSelectForm(
                                    boardAuthorEmail = boardContentPrimaryKeyEntity.boardAuthorEmail,
                                    boardCreateTime = boardContentPrimaryKeyEntity.boardCreateTime
                                )
                            )
                            commentAdapter.submitList(createListItem(viewState = viewState)) {
                                binding.rvComment.scrollToPosition(commentAdapter.itemCount - 1)
                                hideProgressBar()
                                commentEditText.text = null
                                btnSubmitComment.isEnabled = true
                            }

                        }
                    }

                }
            }

            lyRefreshSwipe.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    kotlin.runCatching {
                        val viewState = reloadBoardContent()
                        commentAdapter.submitList(createListItem(viewState)) {
                            binding.rvComment.scrollToPosition(0)
                            hideProgressBar()
                        }
                    }
                    lyRefreshSwipe.isRefreshing = false
                }

            }

            //recyclerviewImage.adapter = boardContentImageAdapter


            commentEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // 텍스트 변경 전에 호출되는 메서드
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 텍스트가 변경될 때 호출되는 메서드
                    when (s.isNullOrBlank()) {
                        true -> btnSubmitComment.isEnabled = false
                        false -> btnSubmitComment.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    // 텍스트 변경 후에 호출되는 메서드
                }
            })
        }

        viewLifecycleOwner.lifecycleScope.launch {
            showProgressBar()
            val viewState = reloadBoardContent()
            commentAdapter.submitList(createListItem(viewState)) {
                binding.rvComment.scrollToPosition(0)
                hideProgressBar()
            }
        }

        initScrollListener()
    }

    private fun createListItem(
        viewState: BoardContentViewModel.BoardContentViewState
    ): List<ListItem> = with(viewState) {
        return mutableListOf<ListItem>().apply {
            add(ListItem.BoardItem(boardEntity = boardEntity ?: error("")))
            commentListEntity?.let {
                it.commentList.map { commentEntity ->
                    add(ListItem.CommentItem(commentEntity = commentEntity))
                }
            }
        }
    }

    private suspend fun reloadBoardContent(): BoardContentViewModel.BoardContentViewState {
        return boardContentPrimaryKeyEntity.let {
            boardContentViewModel.loadBoardAndComment(
                boardLoadForm = BoardLoadForm(
                    boardAuthorEmail = it.boardAuthorEmail,
                    boardCreateTime = it.boardCreateTime
                ),
                boardBookmarkLoadForm = BoardBookmarkLoadForm(
                    boardAuthorEmail = it.boardAuthorEmail,
                    boardCreateTime = it.boardCreateTime
                ),
                boardLikeLoadForm = BoardLikeLoadForm(
                    boardAuthorEmail = it.boardAuthorEmail,
                    boardCreateTime = it.boardCreateTime
                ),
                boardLikeCountLoadForm = BoardLikeCountLoadForm(
                    boardAuthorEmail = it.boardAuthorEmail,
                    boardCreateTime = it.boardCreateTime
                ),
                commentMetaListLoadForm = CommentMetaListLoadForm(
                    boardAuthorEmail = it.boardAuthorEmail,
                    boardCreateTime = it.boardCreateTime,
                    reload = true
                )
            )
        }
    }

    private fun initScrollListener() {
        binding.rvComment.addOnScrollListener(onCommentScrollListener)
    }

    private fun moreItems() {
        showProgressBar()
        viewLifecycleOwner.lifecycleScope.launch {
            val viewState = boardContentViewModel.loadCommentList(
                commentMetaListLoadForm = CommentMetaListLoadForm(
                    boardAuthorEmail = boardContentPrimaryKeyEntity.boardAuthorEmail,
                    boardCreateTime = boardContentPrimaryKeyEntity.boardCreateTime,
                    reload = false
                )
            )
            commentAdapter.submitList(createListItem(viewState)) {
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

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}
