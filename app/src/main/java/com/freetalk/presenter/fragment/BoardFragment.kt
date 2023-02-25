package com.freetalk.presenter.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.freetalk.data.remote.BoardRespond
import com.freetalk.data.remote.FirebaseBoardRemoteDataSourceImpl
import com.freetalk.databinding.FragmentBoardBinding
import com.freetalk.databinding.FragmentHomeBinding
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.adapter.BoardListAdapter
import com.freetalk.presenter.viewmodel.BoardViewEvent
import com.freetalk.presenter.viewmodel.BoardViewModel
import com.freetalk.presenter.viewmodel.BoardViewModelFactory
import com.freetalk.presenter.viewmodel.BoardViewState
import com.freetalk.repository.FirebaseBoardDataRepositoryImpl
import com.freetalk.usecase.BoardUseCaseImpl
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class BoardFragment : Fragment() {
    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!
    private var adapter: BoardListAdapter? = null
    private val boardViewModel: BoardViewModel by lazy {
        val firebaseBoardRemoteDataSourceImpl = FirebaseBoardRemoteDataSourceImpl(Firebase.firestore, FirebaseStorage.getInstance())
        val firebaseBoardDataRepositoryImpl =
            FirebaseBoardDataRepositoryImpl(firebaseBoardRemoteDataSourceImpl)
        val firebaseBoardCaseImpl = BoardUseCaseImpl(firebaseBoardDataRepositoryImpl)
        val factory = BoardViewModelFactory(firebaseBoardCaseImpl)
        ViewModelProvider(requireActivity(), factory).get(BoardViewModel::class.java)
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
        adapter = BoardListAdapter {  }
        binding.apply {
            btnFabMenu.setOnClickListener {
                isFabOpen = toggleFab(isFabOpen)
            }
            btnFabWrite.setOnClickListener {
                (requireActivity() as? Navigable)?.navigateFragment(EndPoint.BoardWrite(1))
                toggleFab(true)
            }
            recyclerviewBoardList.adapter = adapter
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            boardViewModel.select()
        }
        subscribe()

    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            boardViewModel.viewState.collect {
                when(it) {
                    is BoardViewState.Select -> {
                        when(it.boardData?.respond) {
                            is BoardRespond.SelectSuccess -> {
                                Log.v("BoardFragment", "셀렉트 성공")
                                //Log.v("BoardFragment", it.boardData.boardList[0].title)
                                adapter?.setItems(it.boardData.boardList)
                            }
                            else -> {}
                        }
                    }
                    else -> {}
                }
            }
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