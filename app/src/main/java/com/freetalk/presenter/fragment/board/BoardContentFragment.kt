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
import com.bumptech.glide.Glide
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.databinding.FragmentBoardContentBinding
import com.freetalk.presenter.adapter.BoardContentImageAdapter


class BoardContentFragment : Fragment() {
    companion object {
        const val BOARD_ITEM_KEY = "BOARD_ITEM_KEY"

        fun newInstance(
            wrapperBoardEntity: WrapperBoardEntity
        ): BoardContentFragment {
            return BoardContentFragment().apply {
                arguments = bundleOf(
                    BOARD_ITEM_KEY to wrapperBoardEntity
                )
            }
        }
    }

    private var _binding: FragmentBoardContentBinding? = null
    private val binding get() = _binding!!
    private val wrapperBoardEntity get() = requireArguments().getSerializable(BOARD_ITEM_KEY) as WrapperBoardEntity
    private lateinit var callback: OnBackPressedCallback
    private var _adapter: BoardContentImageAdapter? = null
    private val adapter get() = _adapter!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

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
        Log.d("BoardContentFragment", wrapperBoardEntity.boardEntity.content)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _adapter = BoardContentImageAdapter {  }

        printContent()
        binding.recyclerviewImage.adapter = adapter


    }

    private fun printContent() = with(binding) {

        wrapperBoardEntity.let {
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

            //Glide.with(itemView.context).load(it.boardEntity.images?.successUris?.firstOrNull()).into(image)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}

