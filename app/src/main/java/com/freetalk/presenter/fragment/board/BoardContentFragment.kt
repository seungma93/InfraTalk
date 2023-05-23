package com.freetalk.presenter.fragment.board

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.freetalk.data.entity.BoardEntity
import com.freetalk.databinding.FragmentBoardBinding
import com.freetalk.databinding.FragmentBoardContentBinding


class BoardContentFragment : Fragment() {
    companion object {
        const val BOARD_ITEM_KEY = "BOARD_ITEM_KEY"
    }

    private var _binding: FragmentBoardContentBinding? = null
    private val binding get() = _binding!!
    private val boardEntity get() = requireArguments().getSerializable(BoardContentFragment.BOARD_ITEM_KEY) as BoardEntity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardContentBinding.inflate(inflater, container, false)
        Log.d("BoardContentFragment", boardEntity.content)
        return binding.root
    }
}