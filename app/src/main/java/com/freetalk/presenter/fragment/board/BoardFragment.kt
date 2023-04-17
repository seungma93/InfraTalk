package com.freetalk.presenter.fragment.board

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.freetalk.data.remote.BoardResponse
import com.freetalk.data.remote.FirebaseBoardRemoteDataSourceImpl
import com.freetalk.data.remote.FirebaseImageRemoteDataSourceImpl
import com.freetalk.databinding.FragmentBoardBinding
import com.freetalk.di.component.DaggerBoardFragmentComponent
import com.freetalk.di.component.DaggerSignFragmentComponent
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.adapter.BoardListAdapter
import com.freetalk.presenter.viewmodel.BoardViewModel
import com.freetalk.presenter.viewmodel.BoardViewModelFactory
import com.freetalk.presenter.viewmodel.BoardViewState
import com.freetalk.presenter.viewmodel.SignViewModel
import com.freetalk.repository.FirebaseBoardDataRepositoryImpl
import com.freetalk.repository.FirebaseImageDataRepositoryImpl
import com.freetalk.usecase.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class BoardFragment : Fragment() {
    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!
    private var adapter: BoardListAdapter? = null

    @Inject
    lateinit var boardViewModelFactory: ViewModelProvider.Factory
    private val boardViewModel: SignViewModel by viewModels { boardViewModelFactory }

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
           // boardViewModel.select()
        }
        //subscribe()
    }
/*
    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            boardViewModel.viewState.collect {
                when(it) {
                    is BoardViewState.Select -> {
                        when(it.boardSelectData?.response) {
                            is BoardResponse.SelectSuccess -> {
                                Log.v("BoardFragment", "셀렉트 성공")
                                //Log.v("BoardFragment", it.boardData.boardList[0].title)
                                it.boardSelectData.boardList?.let {
                                    adapter?.setItems(it)
                                }
                            }
                            else -> {
                                Log.v("BoardFragment", it.boardSelectData?.response.toString())
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

 */

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