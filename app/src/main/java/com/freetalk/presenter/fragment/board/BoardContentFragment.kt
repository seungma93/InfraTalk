package com.freetalk.presenter.fragment.board

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withResumed
import com.bumptech.glide.Glide
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.data.remote.*
import com.freetalk.databinding.FragmentBoardContentBinding
import com.freetalk.di.component.DaggerBoardFragmentComponent
import com.freetalk.presenter.adapter.BoardContentImageAdapter
import com.freetalk.presenter.viewmodel.BoardContentViewModel
import com.freetalk.presenter.viewmodel.BoardViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


class BoardContentFragment : Fragment() {
    companion object {
        const val BOARD_ITEM_KEY = "BOARD_ITEM_KEY"

        fun newInstance(
            boardEntity: BoardEntity
        ): BoardContentFragment {
            return BoardContentFragment().apply {
                arguments = bundleOf(
                    BOARD_ITEM_KEY to boardEntity
                )
            }
        }
    }

    private var _binding: FragmentBoardContentBinding? = null
    private val binding get() = _binding!!
    private val boardEntity get() = requireArguments().getSerializable(BOARD_ITEM_KEY) as BoardEntity
    private lateinit var callback: OnBackPressedCallback
    private var _adapter: BoardContentImageAdapter? = null
    private val adapter get() = _adapter!!

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
        //Log.d("BoardContentFragment", wrapperBoardEntity.boardEntity.content)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _adapter = BoardContentImageAdapter { }


        binding.apply {
            btnBookmark.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val bookMarkUpdateForm = BookMarkUpdateForm(
                        boardEntity.author.email,
                        boardEntity.createTime
                    )

                    val bookMarkSelectForm = BookMarkSelectForm(
                        boardEntity.author.email,
                        boardEntity.createTime
                    )



                    boardContentViewModel.updateBookMarkContent(bookMarkUpdateForm, bookMarkSelectForm)
                }
            }
            btnLike.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val likeUpdateForm = LikeUpdateForm(
                        boardEntity.author.email,
                        boardEntity.createTime
                    )

                    val likeSelectForm = LikeSelectForm(
                        boardEntity.author.email,
                        boardEntity.createTime
                    )

                    val likeCountSelectForm = LikeCountSelectForm(
                        boardEntity.author.email,
                        boardEntity.createTime
                    )


                    boardContentViewModel.updateLikeContent(likeUpdateForm, likeSelectForm, likeCountSelectForm)
                }
            }
        }
        subscribe()
        printContent()
        binding.recyclerviewImage.adapter = adapter


    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            boardContentViewModel.viewState.collect {
                    it.wrapperBoardEntity.let {
                        binding.apply {
                            Log.v("BoardListAdpater", "셀렉트 바인딩")
                            contentTitle.text = it.boardEntity.title
                            contentContext.text = it.boardEntity.content
                            contentCreateTime.text = it.boardEntity.createTime.toString()
                            contentAuthor.text = it.boardEntity.author.nickname
                            btnBookmark.isSelected = it.isBookMark
                            btnLike.isSelected = it.isLike
                            likeCount.text = it.likeCount.toString()
                            it.boardEntity.images?.let {
                                adapter.setItems(it.successUris)
                            }
                        }
                    }
            }
        }
    }

    private fun printContent() = with(binding) {

        val bookMarkSelectForm = BookMarkSelectForm(
            boardEntity.author.email,
            boardEntity.createTime
        )

        val boardContentSelectForm = BoardContentSelectForm(
            boardEntity.author.email,
            boardEntity.createTime
        )

        val likeSelectForm = LikeSelectForm(
            boardEntity.author.email,
            boardEntity.createTime
        )

        val likeCountSelectForm = LikeCountSelectForm(
            boardEntity.author.email,
            boardEntity.createTime
        )
        viewLifecycleOwner.lifecycleScope.launch {
            boardContentViewModel.select(
                boardContentSelectForm,
                bookMarkSelectForm,
                likeSelectForm,
                likeCountSelectForm
            )
        }
    }


    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}

