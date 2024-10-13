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
import com.seungma.infratalk.databinding.FragmentMyBookmarkCommentBinding
import com.seungma.infratalk.di.component.DaggerMyPageFragmentComponent
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.presenter.board.form.CommentBookmarkAddForm
import com.seungma.infratalk.presenter.board.form.CommentBookmarkDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentLikeAddForm
import com.seungma.infratalk.presenter.board.form.CommentLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.CommentLikeDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentRelatedBookmarksDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentRelatedLikesDeleteForm
import com.seungma.infratalk.presenter.mypage.adapter.MyBookmarkCommentListAdapter
import com.seungma.infratalk.presenter.mypage.viewmodel.MyBookmarkCommentViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyBookmarkCommentFragment : Fragment() {

    private var _binding: FragmentMyBookmarkCommentBinding? = null
    private val binding get() = _binding!!
    private var _adapter: MyBookmarkCommentListAdapter? = null
    private val adapter get() = _adapter!!
    private lateinit var callback: OnBackPressedCallback
    private lateinit var userEntity: UserEntity

    @Inject
    lateinit var myBookmarkCommentViewModelFactory: ViewModelProvider.Factory
    private val myBookmarkCommentViewModel: MyBookmarkCommentViewModel by viewModels { myBookmarkCommentViewModelFactory }

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
    ): View? {
        _binding = FragmentMyBookmarkCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            userEntity = myBookmarkCommentViewModel.getUserMe()

            _adapter = MyBookmarkCommentListAdapter(
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
                                    val myCommentViewState =
                                        myBookmarkCommentViewModel.deleteCommentBookmark(
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
                                    val myCommentViewState =
                                        myBookmarkCommentViewModel.addCommentBookmark(
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
                                    val myCommentViewState =
                                        myBookmarkCommentViewModel.deleteCommentLike(
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
                                    val myCommentViewState =
                                        myBookmarkCommentViewModel.addCommentLike(
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
                            val myCommentViewState = myBookmarkCommentViewModel.deleteComment(
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
                },
                userEntity = userEntity
            )

            binding.rvMyBookmarkCommentList.adapter = adapter
        }

        binding.apply {

            swipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    kotlin.runCatching {
                        myBookmarkCommentViewModel.loadMyBookmarkCommentList()
                    }
                    swipeRefreshLayout.isRefreshing = false
                }

            }
        }
        loadBoardList()
    }

    private fun loadBoardList() {
        viewLifecycleOwner.lifecycleScope.launch {
            showProgressBar()
            val myCommentViewState = myBookmarkCommentViewModel.loadMyBookmarkCommentList()
            adapter.submitList(myCommentViewState.commentListEntity?.commentList) {
                binding.rvMyBookmarkCommentList.scrollToPosition(0)
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