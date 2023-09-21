package com.freetalk.presenter.fragment.board

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.freetalk.databinding.FragmentBoardContentBinding
import com.freetalk.di.component.DaggerBoardFragmentComponent
import com.freetalk.domain.entity.BoardContentPrimaryKeyEntity
import com.freetalk.domain.entity.BoardEntity
import com.freetalk.presenter.adapter.BoardContentImageAdapter
import com.freetalk.presenter.adapter.CommentListAdapter
import com.freetalk.presenter.form.CommentBookmarkAddForm
import com.freetalk.presenter.form.CommentBookmarkDeleteForm
import com.freetalk.presenter.form.CommentLikeAddForm
import com.freetalk.presenter.form.CommentLikeCountLoadForm
import com.freetalk.presenter.form.CommentLikeDeleteForm
import com.freetalk.presenter.viewmodel.BoardContentViewModel
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
    private val boardEntity get() = requireArguments().getSerializable(BOARD_ITEM_KEY) as BoardEntity
    private lateinit var callback: OnBackPressedCallback
    private var _boardContentImageAdapter: BoardContentImageAdapter? = null
    private val boardContentImageAdapter get() = _boardContentImageAdapter!!
    private var _commentAdapter: CommentListAdapter? = null
    private val commentAdapter get() = _commentAdapter!!
    /*
    private val onScrollListener: OnScrollListener = OnScrollListener({ moreItems() }, {
        Toast.makeText(
            requireContext(),
            "마지막 페이지 입니다.",
            Toast.LENGTH_SHORT
        ).show()
    })

     */

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
        _boardContentImageAdapter = BoardContentImageAdapter { }
        _commentAdapter = CommentListAdapter(
            itemClick = {
                //val endPoint = EndPoint.BoardContent(boardEntity = it)
                //(requireActivity() as? Navigable)?.navigateFragment(endPoint)
            },
            bookmarkClick = { commentEntity ->
                commentEntity.apply {
                    when (bookmarkEntity.isBookmark) {
                        true -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                boardContentViewModel.deleteCommentBookmark(
                                    commentBookmarkDeleteForm = CommentBookmarkDeleteForm(
                                        commentAuthorEmail = commentMetaEntity.author.email,
                                        commentCreateTime = commentMetaEntity.createTime
                                    )
                                )
                            }
                        }

                        false -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                boardContentViewModel.addCommentBookmark(
                                    commentBookmarkAddForm = CommentBookmarkAddForm(
                                        commentAuthorEmail = commentMetaEntity.author.email,
                                        commentCreateTime = commentMetaEntity.createTime
                                    )
                                )
                            }
                        }
                    }
                }
            },
            likeClick = { commentEntity ->
                commentEntity.apply {
                    when (likeEntity.isLike) {
                        true -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                boardContentViewModel.deleteCommentLike(
                                    commentLikeDeleteForm = CommentLikeDeleteForm(
                                        commentAuthorEmail = commentMetaEntity.author.email,
                                        commentCreateTime = commentMetaEntity.createTime
                                    ), commentLikeCountLoadForm = CommentLikeCountLoadForm(
                                        commentAuthorEmail = commentMetaEntity.author.email,
                                        commentCreateTime = commentMetaEntity.createTime
                                    )
                                )
                            }
                        }

                        false -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                boardContentViewModel.addCommentLike(
                                    commentLikeAddForm = CommentLikeAddForm(
                                        commentAuthorEmail = commentMetaEntity.author.email,
                                        commentCreateTime = commentMetaEntity.createTime
                                    ), commentLikeCountLoadForm = CommentLikeCountLoadForm(
                                        commentAuthorEmail = commentMetaEntity.author.email,
                                        commentCreateTime = commentMetaEntity.createTime
                                    )
                                )
                            }
                        }
                    }
                }
            },
            deleteClick = { /*
                    wrapperCommentEntity ->
                wrapperCommentEntity.apply {
                    viewLifecycleOwner.lifecycleScope.launch {
                        boardContentViewModel.deleteComment(
                            CommentDeleteForm(
                                commentAuthorEmail = commentEntity.commentAuthor.email,
                                commentCreateTIme = commentEntity.createTime
                            )
                        )
                    }
                }
                */
            }

        )
    }
/*
        binding.apply {
            btnBookmark.setOnClickListener {
                btnBookmark.isEnabled = false
                viewLifecycleOwner.lifecycleScope.launch {

                    when (binding.btnBookmark.isSelected) {
                        true -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                boardContentViewModel.deleteBoardContentBookmark(
                                    boardBookmarkDeleteForm = BoardBookmarkDeleteForm(
                                        boardAuthorEmail = boardMetaEntity.a.email,
                                        boardCreateTime = boardEntity.createTime
                                    )
                                )
                            }
                        }

                        false -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                boardContentViewModel.insertBookMarkContent(
                                    BoardBookmarkInsertFrom(
                                        boardAuthorEmail = boardEntity.author.email,
                                        boardCreateTime = boardEntity.createTime
                                    )
                                )
                            }
                        }
                    }
                }
            }
            btnLike.setOnClickListener {
                btnBookmark.isEnabled = false
                boardContentViewModel.viewState.value.wrapperBoardEntity.likeEntity?.let {
                    viewLifecycleOwner.lifecycleScope.launch {
                        boardContentViewModel.deleteLikeContent(
                            BoardLikeDeleteForm(
                                boardAuthorEmail = boardEntity.author.email,
                                boardCreateTime = boardEntity.createTime
                            ), BoardLikeCountSelectForm(
                                boardEntity.author.email,
                                boardEntity.createTime
                            )
                        )
                    }
                } ?: run {
                    viewLifecycleOwner.lifecycleScope.launch {
                        boardContentViewModel.insertLikeContent(
                            BoardLikeInsertForm(
                                boardAuthorEmail = boardEntity.author.email,
                                boardCreateTime = boardEntity.createTime
                            ), BoardLikeCountSelectForm(
                                boardAuthorEmail = boardEntity.author.email,
                                boardCreateTime = boardEntity.createTime
                            )
                        )
                    }
                }
            }
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
                        viewLifecycleOwner.lifecycleScope.launch {
                            boardContentViewModel.viewState.value.wrapperBoardEntity.boardEntity.let {
                                val commentInsertForm = CommentInsertForm(
                                    it.author.email,
                                    it.createTime,
                                    commentEditText.text.toString()
                                )
                                boardContentViewModel.insertComment(commentInsertForm = commentInsertForm)
                            }
                            commentEditText.text = null

                            val result = boardContentViewModel.selectAllComments(
                                CommentsSelectForm(
                                    boardAuthorEmail = boardEntity.author.email,
                                    boardCreateTime = boardEntity.createTime,
                                    reload = true
                                )
                            )
                            commentAdapter.submitList(result.commentList.wrapperCommentList) {
                                binding.rvComment.scrollToPosition(result.commentList.wrapperCommentList.size - 1)
                            }
                        }
                    }

                }
            }
            //recyclerviewImage.adapter = boardContentImageAdapter
            rvComment.adapter = commentAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("BoardCommentFragment", "코멘트 셀렉트")
            //showProgressBar()
            val result = boardContentViewModel.selectComments(
                CommentsSelectForm(
                    boardAuthorEmail = boardEntity.author.email,
                    boardCreateTime = boardEntity.createTime,
                    reload = true
                )
            )
            commentAdapter.submitList(result.commentList.wrapperCommentList) {
                binding.rvComment.scrollToPosition(0)
            }
        }

        subscribe()
        printContent()
        initScrollListener()

    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            boardContentViewModel.viewState.collect {
                Log.d("BoardCommentFragment", "콜렉트 시작")
                it.wrapperBoardEntity.let {
                    binding.apply {
                        title.text = it.boardEntity.title
                        context.text = it.boardEntity.content
                        date.text = it.boardEntity.createTime.toString()
                        author.text = it.boardEntity.author.nickname
                        btnBookmark.isSelected = it.bookMarkEntity != null
                        btnLike.isSelected = it.likeEntity != null
                        likeCount.text = it.likeCount.toString()
                        it.boardEntity.images?.let {
                            boardContentImageAdapter.setItems(it.successUris)
                        }
                        btnBookmark.isEnabled = true
                        btnLike.isEnabled = true
                    }
                }
                //commentAdapter.submitList(it.commentList.wrapperCommentList)
            }
        }
    }

    private fun printContent() = with(boardEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            boardContentViewModel.select(
                BoardContentSelectForm(
                    boardAuthorEmail = author.email,
                    boardCreateTime = createTime
                ),
                BoardContentBookmarkSelectForm(
                    boardAuthorEmail = author.email,
                    boardCreateTime = createTime
                ),
                BoardContentLikeSelectForm(
                    boardAuthorEmail = author.email,
                    boardCreateTime = createTime
                ),
                BoardContentLikeCountSelectForm(
                    boardAuthorEmail = author.email,
                    boardCreateTime = createTime
                )
            )
        }
    }

    private fun initScrollListener() {
        binding.rvComment.addOnScrollListener(onScrollListener)
    }

    private fun moreItems() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            boardContentViewModel.selectComments(
                CommentsSelectForm(
                    boardAuthorEmail = boardEntity.author.email,
                    boardCreateTime = boardEntity.createTime,
                    reload = false
                )
            )
        }
    }

 */

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}
