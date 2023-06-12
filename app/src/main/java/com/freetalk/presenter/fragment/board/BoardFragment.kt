package com.freetalk.presenter.fragment.board

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freetalk.data.UserSingleton
import com.freetalk.data.remote.*
import com.freetalk.databinding.FragmentBoardBinding
import com.freetalk.di.component.DaggerBoardFragmentComponent
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.adapter.BoardListAdapter
import com.freetalk.presenter.fragment.ChildFragmentNavigable
import com.freetalk.presenter.fragment.MainChildFragmentEndPoint
import com.freetalk.presenter.fragment.MainFragment
import com.freetalk.presenter.viewmodel.BoardViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

class OnScrollListener(private val moreItems: () -> Unit, private val showToast: () -> Unit) :
    RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val lastVisibleItemPosition =
            (recyclerView.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: 0
        recyclerView.adapter?.let {
            val itemCount = it.itemCount - 1
            Log.d("BoardFragment", lastVisibleItemPosition.toString() + "  " + itemCount.toString())
            if (!recyclerView.canScrollVertically(1) && itemCount == lastVisibleItemPosition) {
                Log.d("BoardFragment", "onScrolled")
                val morePage = it.itemCount % 10 == 0
                if (morePage) moreItems() else showToast()
            }
        }
    }
}

class BoardFragment : Fragment() {
    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!
    private var _adapter: BoardListAdapter? = null
    private val adapter get() = _adapter!!
    private val onScrollListener: OnScrollListener = OnScrollListener({ moreItems() }, {
        Toast.makeText(
            requireContext(),
            "마지막 페이지 입니다.",
            Toast.LENGTH_SHORT
        ).show()
    })

    @Inject
    lateinit var boardViewModelFactory: ViewModelProvider.Factory
    private val boardViewModel: BoardViewModel by viewModels { boardViewModelFactory }


    override fun onAttach(context: Context) {
        DaggerBoardFragmentComponent.factory().create(context).inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BoardContentFragment", "갯수" + parentFragmentManager.backStackEntryCount)
        var isFabOpen = false
        _adapter = BoardListAdapter(
            itemClick = {
                val endPoint = MainChildFragmentEndPoint.BoardContent(wrapperBoardEntity = it)
                (parentFragment as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            },
            bookMarkClick = { wrapperBoardEntity ->

                val bookMarkUpdateForm = BookMarkUpdateForm(
                    wrapperBoardEntity.boardEntity.author.email,
                    wrapperBoardEntity.boardEntity.createTime
                )

                val bookMarkSelectForm = BookMarkSelectForm(
                    wrapperBoardEntity.boardEntity.author.email,
                    wrapperBoardEntity.boardEntity.createTime
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    boardViewModel.updateBookMark(
                        bookMarkUpdateForm, bookMarkSelectForm
                    )
                }

            },
            likeClick = { wrapperBoardEntity ->

                    val likeUpdateForm = LikeUpdateForm(
                        wrapperBoardEntity.boardEntity.author.email,
                        wrapperBoardEntity.boardEntity.createTime
                        )

                    val likeSelectForm = LikeSelectForm(
                        wrapperBoardEntity.boardEntity.author.email,
                        wrapperBoardEntity.boardEntity.createTime
                    )

                    val likeCountSelectForm = LikeCountSelectForm(
                        wrapperBoardEntity.boardEntity.author.email,
                        wrapperBoardEntity.boardEntity.createTime,
                    )

                        Log.v("BookSearchFragment", "세이브 람다")
                        viewLifecycleOwner.lifecycleScope.launch {
                            boardViewModel.updateLike(
                                likeUpdateForm, likeSelectForm, likeCountSelectForm
                            )
                        }
                    }

        )


        binding.apply {
            btnFabMenu.setOnClickListener {
                isFabOpen = toggleFab(isFabOpen)
            }
            btnFabWrite.setOnClickListener {
                (parentFragment as? ChildFragmentNavigable)?.navigateFragment(
                    MainChildFragmentEndPoint.BoardWrite
                )
                toggleFab(isFabOpen = true)
            }
            swipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    kotlin.runCatching {
                        boardViewModel.select(BoardSelectForm(reload = true))
                    }
                    swipeRefreshLayout.isRefreshing = false
                }

            }
            recyclerviewBoardList.adapter = adapter
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            val result = boardViewModel.select(BoardSelectForm(reload = true))
            adapter.submitList(result.boardListEntity.boardList) {
                binding.recyclerviewBoardList.scrollToPosition(0)
            }
        }
        subscribe()
        initScrollListener()


    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            boardViewModel.viewState.collect {
                Log.d("BoardFragment", "셀렉트 성공")
                it.boardListEntity.boardList.map {
                    Log.d("새로 업데이트", it.boardEntity.content)
                }
                adapter.currentList.map {
                    Log.d("기존 리스트", it.boardEntity.content)
                }
                val newList = adapter.currentList + it.boardListEntity.boardList
                newList.map {
                    Log.d("합쳐진 리스트", it.boardEntity.content)
                }

                adapter.submitList(it.boardListEntity.boardList)
            }
        }
    }

    private fun initScrollListener() {
        binding.recyclerviewBoardList.addOnScrollListener(onScrollListener)
    }

    private fun moreItems() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            boardViewModel.select(BoardSelectForm(reload = false))
        }
    }

    private fun toggleFab(isFabOpen: Boolean): Boolean {
        return if (isFabOpen) {
            AnimatorSet().apply {
                this.playTogether(
                    ObjectAnimator.ofFloat(binding.btnFabMyList, "translationY", 0f),
                    ObjectAnimator.ofFloat(binding.btnFabLike, "translationY", 0f),
                    ObjectAnimator.ofFloat(binding.btnFabWrite, "translationY", 0f)
                )
            }.start()
            false
        } else {
            AnimatorSet().apply {
                this.playTogether(
                    ObjectAnimator.ofFloat(binding.btnFabMyList, "translationY", -600f),
                    ObjectAnimator.ofFloat(binding.btnFabLike, "translationY", -400f),
                    ObjectAnimator.ofFloat(binding.btnFabWrite, "translationY", -200f)
                )
            }.start()
            true
        }
    }
}