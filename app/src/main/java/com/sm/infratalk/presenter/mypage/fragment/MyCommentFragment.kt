package com.sm.infratalk.presenter.mypage.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.sm.infratalk.databinding.FragmentMyCommentBinding
import com.sm.infratalk.di.component.DaggerMyPageFragmentComponent
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.presenter.board.form.CommentBookmarkAddForm
import com.sm.infratalk.presenter.board.form.CommentBookmarkDeleteForm
import com.sm.infratalk.presenter.board.form.CommentDeleteForm
import com.sm.infratalk.presenter.board.form.CommentLikeAddForm
import com.sm.infratalk.presenter.board.form.CommentLikeCountLoadForm
import com.sm.infratalk.presenter.board.form.CommentLikeDeleteForm
import com.sm.infratalk.presenter.board.form.CommentRelatedBookmarksDeleteForm
import com.sm.infratalk.presenter.board.form.CommentRelatedLikesDeleteForm
import com.sm.infratalk.presenter.board.listener.OnScrollListener
import com.sm.infratalk.presenter.common.CustomSnackbar
import com.sm.infratalk.presenter.mypage.adapter.MyCommentListAdapter
import com.sm.infratalk.presenter.mypage.form.MyCommentListLoadForm
import com.sm.infratalk.presenter.mypage.viewmodel.MyCommentViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyCommentFragment : Fragment() {

    companion object {
        const val MY_ACCOUNT_PRIMARY_KEY = "MY_ACCOUNT_PRIMARY_KEY"

        fun newInstance(
            userEntity: UserEntity
        ): MyCommentFragment {
            return MyCommentFragment().apply {
                arguments = bundleOf(
                    MY_ACCOUNT_PRIMARY_KEY to userEntity
                )
            }
        }
    }

    private var _binding: FragmentMyCommentBinding? = null
    private val binding get() = _binding!!
    private var _adapter: MyCommentListAdapter? = null
    private val adapter get() = _adapter!!
    private val onScrollListener: OnScrollListener = OnScrollListener({ moreItems() }, {
        val message = "마지막 페이지 입니다."
        val duration = Snackbar.LENGTH_SHORT

        val snackbar = CustomSnackbar.make(requireActivity().findViewById(android.R.id.content), message, duration)
        snackbar.setMargin(bottomDp = 66)
        snackbar.show()
    })

    private lateinit var callback: OnBackPressedCallback


    @Inject
    lateinit var myCommentViewModelFactory: ViewModelProvider.Factory
    private val myCommentViewModel: MyCommentViewModel by viewModels { myCommentViewModelFactory }

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
        _binding = FragmentMyCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _adapter = MyCommentListAdapter(
            itemClick = {
                /*
                Log.d("comment", "클릭시 넘어온 board값" + it.author.email)
                val endPoint = EndPoint.BoardContent(
                    boardContentPrimaryKeyEntity = BoardContentPrimaryKeyEntity(
                        boardAuthorEmail = it.author.email,
                        boardCreateTime = it.createTime
                    )
                )
                (requireActivity() as? Navigable)?.navigateFragment(endPoint)

                 */
            },
            bookmarkClick = { commentEntity ->
                commentEntity.apply {
                    when (bookmarkEntity.isBookmark) {
                        true -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                val myCommentViewState = myCommentViewModel.deleteCommentBookmark(
                                    commentBookmarkDeleteForm = CommentBookmarkDeleteForm(
                                        commentAuthorEmail = commentMetaEntity.author.email,
                                        commentCreateTime = commentMetaEntity.createTime
                                    )
                                )
                                adapter.submitList(myCommentViewState.commentListEntity?.commentList)
                            }
                        }

                        false -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                val myCommentViewState = myCommentViewModel.addCommentBookmark(
                                    commentBookmarkAddForm = CommentBookmarkAddForm(
                                        commentAuthorEmail = commentMetaEntity.author.email,
                                        commentCreateTime = commentMetaEntity.createTime
                                    )
                                )
                                adapter.submitList(myCommentViewState.commentListEntity?.commentList)
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
                                val myCommentViewState = myCommentViewModel.deleteCommentLike(
                                    commentLikeDeleteForm = CommentLikeDeleteForm(
                                        commentAuthorEmail = commentMetaEntity.author.email,
                                        commentCreateTime = commentMetaEntity.createTime
                                    ), commentLikeCountLoadForm = CommentLikeCountLoadForm(
                                        commentAuthorEmail = commentMetaEntity.author.email,
                                        commentCreateTime = commentMetaEntity.createTime
                                    )
                                )
                                adapter.submitList(myCommentViewState.commentListEntity?.commentList)
                            }
                        }

                        false -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                val myCommentViewState = myCommentViewModel.addCommentLike(
                                    commentLikeAddForm = CommentLikeAddForm(
                                        commentAuthorEmail = commentMetaEntity.author.email,
                                        commentCreateTime = commentMetaEntity.createTime
                                    ), commentLikeCountLoadForm = CommentLikeCountLoadForm(
                                        commentAuthorEmail = commentMetaEntity.author.email,
                                        commentCreateTime = commentMetaEntity.createTime
                                    )
                                )
                                adapter.submitList(myCommentViewState.commentListEntity?.commentList)
                            }
                        }
                    }
                }
            },
            deleteClick = { commentEntity ->
                commentEntity.apply {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val myCommentViewState = myCommentViewModel.deleteComment(
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
                        adapter.submitList(myCommentViewState.commentListEntity?.commentList)
                    }
                }
            }
        )
        binding.apply {

            swipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    kotlin.runCatching {
                        myCommentViewModel.loadMyCommentList(
                            myCommentListLoadForm = MyCommentListLoadForm(
                                reload = true
                            )
                        )
                    }
                    swipeRefreshLayout.isRefreshing = false
                }

            }

            rvMyCommentList.adapter = adapter
        }
        loadBoardList()
        initScrollListener()
    }

    private fun loadBoardList() {
        viewLifecycleOwner.lifecycleScope.launch {
            showProgressBar()
            val myCommentViewState = myCommentViewModel.loadMyCommentList(
                myCommentListLoadForm = MyCommentListLoadForm(reload = true)
            )
            adapter.submitList(myCommentViewState.commentListEntity?.commentList) {
                binding.rvMyCommentList.scrollToPosition(0)
                hideProgressBar()
            }

        }
    }

    private fun initScrollListener() {
        binding.rvMyCommentList.addOnScrollListener(onScrollListener)
    }

    private fun moreItems() {
        showProgressBar()
        viewLifecycleOwner.lifecycleScope.launch {
            val myCommentViewState = myCommentViewModel.loadMyCommentList(
                myCommentListLoadForm = MyCommentListLoadForm(reload = true)
            )
            adapter.submitList(myCommentViewState.commentListEntity?.commentList) {
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