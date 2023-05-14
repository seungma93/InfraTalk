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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freetalk.databinding.FragmentBoardBinding
import com.freetalk.di.component.DaggerBoardFragmentComponent
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.adapter.BoardListAdapter
import com.freetalk.presenter.viewmodel.BoardViewModel
import com.freetalk.presenter.viewmodel.BoardViewState
import com.freetalk.presenter.viewmodel.SignViewModel
import com.freetalk.usecase.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnScrollListener(private val boardFragment: BoardFragment) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val lastVisibleItemPosition =
            (recyclerView.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: 0
        val itemCount = (recyclerView.adapter?.itemCount?.minus(1) ?: 0)
        Log.d("BoardFragment", lastVisibleItemPosition.toString() + "  " + itemCount.toString())
        if (!recyclerView.canScrollVertically(1) && itemCount == lastVisibleItemPosition) {
            Log.d("BoardFragment", "onScrolled")
            if ((recyclerView.adapter?.itemCount)?.rem(10) == 0) {
                boardFragment.moreItems()
            } else {
                Toast.makeText(boardFragment.requireContext(), "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show()
                //binding.bookList.post {
                //  adapter?.unsetLoading()
                //}
            }

        }
    }
}

class BoardFragment : Fragment() {
    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!
    private var adapter: BoardListAdapter? = null
    private val onScrollListener: OnScrollListener = OnScrollListener(this)

    @Inject
    lateinit var boardViewModelFactory: ViewModelProvider.Factory
    private val boardViewModel: BoardViewModel by viewModels { boardViewModelFactory }

    /*
    private val boardViewModel: BoardViewModel by lazy {

        // dataSource
        val firebaseBoardRemoteDataSourceImpl = FirebaseBoardRemoteDataSourceImpl(Firebase.firestore)
        val firebaseImageDataSourceImpl = FirebaseImageRemoteDataSourceImpl(FirebaseStorage.getInstance())
        // repository
        val firebaseBoardDataRepositoryImpl =
            FirebaseBoardDataRepositoryImpl(firebaseBoardRemoteDataSourceImpl)
        val firebaseImageDataRepositoryImpl = FirebaseImageDataRepositoryImpl(firebaseImageDataSourceImpl)
        // usecase
        val writeContentUseCaseImpl = WriteContentUseCaseImpl(firebaseBoardDataRepositoryImpl)
        val uploadImagesUseCaseImpl = UploadImagesUseCaseImpl(firebaseImageDataRepositoryImpl)
        val updateContentUseCaseImpl = UpdateContentUseCaseImpl(firebaseBoardDataRepositoryImpl)
        val updateImagesContentUseCaseImpl = UpdateImageContentUseCaseImpl(updateContentUseCaseImpl, uploadImagesUseCaseImpl)
        val factory = BoardViewModelFactory(writeContentUseCaseImpl, updateImagesContentUseCaseImpl)
        ViewModelProvider(requireActivity(), factory).get(BoardViewModel::class.java)
    }

     */
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
        var isFabOpen = false
        adapter = BoardListAdapter {}
        binding.apply {
            btnFabMenu.setOnClickListener {
                isFabOpen = toggleFab(isFabOpen)
            }
            btnFabWrite.setOnClickListener {
                (requireActivity() as? Navigable)?.navigateFragment(EndPoint.BoardWrite)
                toggleFab(true)
            }
            swipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    boardViewModel.select(null)
                }
                swipeRefreshLayout.isRefreshing = false
            }
            recyclerviewBoardList.adapter = adapter
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            boardViewModel.select(null)
        }
        subscribe()
        initScrollListener()
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            boardViewModel.viewState.collect {
                when (it) {
                    is BoardViewState.Select -> {
                        when (it.boardListEntity) {
                            null -> {

                            }
                            else -> {
                                Log.d("BoardFragment", "셀렉트 성공")
                                it.boardListEntity.boardList?.let {
                                    adapter?.submitList(it)
                                }

                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun initScrollListener() {
        binding.recyclerviewBoardList.addOnScrollListener(onScrollListener)
    }


    fun moreItems() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
          boardViewModel.select(adapter?.currentList?.firstOrNull()?.lastDocument)
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