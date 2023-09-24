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
import com.freetalk.presenter.adapter.ListItem
import com.freetalk.presenter.form.BoardBookmarkLoadForm
import com.freetalk.presenter.form.BoardLikeCountLoadForm
import com.freetalk.presenter.form.BoardLikeLoadForm
import com.freetalk.presenter.form.BoardLoadForm
import com.freetalk.presenter.form.CommentBookmarkAddForm
import com.freetalk.presenter.form.CommentBookmarkDeleteForm
import com.freetalk.presenter.form.CommentInsertForm
import com.freetalk.presenter.form.CommentLikeAddForm
import com.freetalk.presenter.form.CommentLikeCountLoadForm
import com.freetalk.presenter.form.CommentLikeDeleteForm
import com.freetalk.presenter.form.CommentMetaListLoadForm
import com.freetalk.presenter.viewmodel.BoardContentViewModel
import kotlinx.coroutines.launch
import java.util.Collections.list
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
            commentItemClick = {
                //val endPoint = EndPoint.BoardContent(boardEntity = it)
                //(requireActivity() as? Navigable)?.navigateFragment(endPoint)
            },
            commentBookmarkClick = { commentEntity ->
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
            commentLikeClick = { commentEntity ->
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
            commentDeleteClick = { /*
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
            },
            boardBookmarkClick = {},
            boardLikeClick = {}
        )

        Log.d(
            "comment",
            "boardContentPrimaryKeyEntity" + boardContentPrimaryKeyEntity.boardAuthorEmail
        )
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
                        viewLifecycleOwner.lifecycleScope.launch {

                            boardContentViewModel.writeComment(
                                commentInsertForm = CommentInsertForm(
                                    boardAuthorEmail = boardContentPrimaryKeyEntity.boardAuthorEmail,
                                    boardCreateTime = boardContentPrimaryKeyEntity.boardCreateTime,
                                    content = commentEditText.text.toString()
                                )
                            )

                            commentEditText.text = null
                            /*
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

                             */
                        }
                    }

                }
            }


            //recyclerviewImage.adapter = boardContentImageAdapter
            rvComment.adapter = commentAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            //showProgressBar()
            boardContentPrimaryKeyEntity.let {
                boardContentViewModel.loadBoardContent(
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
                    )
                )
            }
            val a = boardContentViewModel.loadCommentList(
                commentMetaListLoadForm = CommentMetaListLoadForm(
                    boardAuthorEmail = boardContentPrimaryKeyEntity.boardAuthorEmail,
                    boardCreateTime = boardContentPrimaryKeyEntity.boardCreateTime, reload = false
                )
            )
            val list: MutableList<ListItem> = mutableListOf()
            list.add(ListItem.BoardItem(a.boardEntity))
            a.commentListEntity.commentList.map {
                list.add(ListItem.CommentItem(commentEntity = it))
            }

            commentAdapter.submitList(list) {
                binding.rvComment.scrollToPosition(0)
            }
        }

        subscribe()
        //initScrollListener()

    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            boardContentViewModel.viewState.collect {
                //commentAdapter.submitList(it.commentList.wrapperCommentList)
            }
        }
    }
    /*
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
