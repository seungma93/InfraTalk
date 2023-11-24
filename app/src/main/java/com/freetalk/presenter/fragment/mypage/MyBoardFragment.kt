package com.freetalk.presenter.fragment.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.freetalk.databinding.FragmentHomeBinding
import com.freetalk.databinding.FragmentMyBoardBinding
import com.freetalk.databinding.FragmentMyPageBinding
import com.freetalk.di.component.DaggerBoardFragmentComponent
import com.freetalk.di.component.DaggerMyPageFragmentComponent
import com.freetalk.domain.entity.BoardContentPrimaryKeyEntity
import com.freetalk.domain.entity.ChatPrimaryKeyEntity
import com.freetalk.domain.entity.UserEntity
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.adapter.MyBoardListAdapter
import com.freetalk.presenter.form.BoardBookmarkAddForm
import com.freetalk.presenter.form.BoardBookmarkDeleteForm
import com.freetalk.presenter.form.BoardBookmarksDeleteForm
import com.freetalk.presenter.form.BoardDeleteForm
import com.freetalk.presenter.form.BoardLikeAddForm
import com.freetalk.presenter.form.BoardLikeCountLoadForm
import com.freetalk.presenter.form.BoardLikeDeleteForm
import com.freetalk.presenter.form.BoardLikesDeleteForm
import com.freetalk.presenter.form.BoardListLoadForm
import com.freetalk.presenter.form.MyBoardListLoadForm
import com.freetalk.presenter.fragment.board.OnScrollListener
import com.freetalk.presenter.fragment.chat.ChatFragment
import com.freetalk.presenter.viewmodel.BoardViewModel
import com.freetalk.presenter.viewmodel.MyBoardViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyBoardFragment: Fragment() {

    companion object {
        const val MY_ACCOUNT_PRIMARY_KEY = "MY_ACCOUNT_PRIMARY_KEY"

        fun newInstance(
            userEntity: UserEntity
        ): MyBoardFragment {
            return MyBoardFragment().apply {
                arguments = bundleOf(
                    MY_ACCOUNT_PRIMARY_KEY to userEntity
                )
            }
        }
    }

    private var _binding: FragmentMyBoardBinding? = null
    private val binding get() = _binding!!
    private var _adapter: MyBoardListAdapter? = null
    private val adapter get() = _adapter!!
    private val onScrollListener: OnScrollListener = OnScrollListener({ moreItems() }, {
        Toast.makeText(
            requireContext(),
            "마지막 페이지 입니다.",
            Toast.LENGTH_SHORT
        ).show()
    })

    private val myAccountUserEntity
        get() = requireArguments().getSerializable(
            MyBoardFragment.MY_ACCOUNT_PRIMARY_KEY
        ) as UserEntity

    @Inject
    lateinit var myBoardViewModelFactory: ViewModelProvider.Factory
    private val myBoardViewModel: MyBoardViewModel by viewModels { myBoardViewModelFactory }

    override fun onAttach(context: Context) {
        DaggerMyPageFragmentComponent.factory().create(context).inject(this)
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _adapter = MyBoardListAdapter(
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
                                val boardViewState = myBoardViewModel.deleteBookMark(
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
                                val boardViewState = myBoardViewModel.addBookMark(
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
                                val boardViewState = myBoardViewModel.deleteLike(
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
                                val boardViewState = myBoardViewModel.addLike(
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
                        val myBoardViewState = myBoardViewModel.deleteBoard(
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
                        myBoardViewModel.loadMyBoardList(myBoardListLoadForm = MyBoardListLoadForm(reload = true))
                    }
                    swipeRefreshLayout.isRefreshing = false
                }

            }

            rvMyBoardList.adapter = adapter
        }
        loadBoardList()
        initScrollListener()
    }

    private fun loadBoardList() {
        viewLifecycleOwner.lifecycleScope.launch {
            showProgressBar()
            val boardViewState = myBoardViewModel.loadMyBoardList(myBoardListLoadForm = MyBoardListLoadForm(reload = true))
            adapter.submitList(boardViewState.boardListEntity.boardList) {
                binding.rvMyBoardList.scrollToPosition(0)
                hideProgressBar()
            }

        }
    }

    private fun initScrollListener() {
        binding.rvMyBoardList.addOnScrollListener(onScrollListener)
    }

    private fun moreItems() {
        showProgressBar()
        viewLifecycleOwner.lifecycleScope.launch{
            val boardViewState = myBoardViewModel.loadMyBoardList(myBoardListLoadForm = MyBoardListLoadForm(reload = false))
            adapter.submitList(boardViewState.boardListEntity.boardList) {
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