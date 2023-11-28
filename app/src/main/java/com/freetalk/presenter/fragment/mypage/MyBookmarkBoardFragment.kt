package com.freetalk.presenter.fragment.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.freetalk.databinding.FragmentMyBookmarkBoardBinding
import com.freetalk.di.component.DaggerMyPageFragmentComponent
import com.freetalk.domain.entity.BoardContentPrimaryKeyEntity
import com.freetalk.domain.entity.UserEntity
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.adapter.MyBookmarkBoardListAdapter
import com.freetalk.presenter.form.BoardBookmarkAddForm
import com.freetalk.presenter.form.BoardBookmarkDeleteForm
import com.freetalk.presenter.form.BoardBookmarksDeleteForm
import com.freetalk.presenter.form.BoardDeleteForm
import com.freetalk.presenter.form.BoardLikeAddForm
import com.freetalk.presenter.form.BoardLikeCountLoadForm
import com.freetalk.presenter.form.BoardLikeDeleteForm
import com.freetalk.presenter.form.BoardLikesDeleteForm
import com.freetalk.presenter.viewmodel.MyBookmarkBoardViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyBookmarkBoardFragment: Fragment() {

    private var _binding: FragmentMyBookmarkBoardBinding? = null
    private val binding get() = _binding!!
    private var _adapter: MyBookmarkBoardListAdapter? = null
    private val adapter get() = _adapter!!

    @Inject
    lateinit var myBookmarkBoardViewModelFactory: ViewModelProvider.Factory
    private val myBookmarkBoardViewModel: MyBookmarkBoardViewModel by viewModels { myBookmarkBoardViewModelFactory }

    override fun onAttach(context: Context) {
        DaggerMyPageFragmentComponent.factory().create(context).inject(this)
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyBookmarkBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                        adapter.submitList(myBoardViewState.boardListEntity.boardList){
                            hideProgressBar()
                        }
                    }
                }
            }
        )
        binding.apply {

            swipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    kotlin.runCatching {
                        myBookmarkBoardViewModel.loadMyBookmarkBoardList()
                    }
                    swipeRefreshLayout.isRefreshing = false
                }

            }

            rvMyBookmarkBoardList.adapter = adapter
        }
        loadBoardList()
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