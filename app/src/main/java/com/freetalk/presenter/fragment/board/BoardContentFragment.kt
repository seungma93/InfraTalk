package com.freetalk.presenter.fragment.board

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.freetalk.data.entity.BoardEntity
import com.freetalk.databinding.FragmentBoardContentBinding


class BoardContentFragment : Fragment() {
    companion object {
        const val BOARD_ITEM_KEY = "BOARD_ITEM_KEY"
    }

    private var _binding: FragmentBoardContentBinding? = null
    private val binding get() = _binding!!
    private val boardEntity get() = requireArguments().getSerializable(BoardContentFragment.BOARD_ITEM_KEY) as BoardEntity
    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("BoardWriteFragment", "백스택 실행")
                parentFragmentManager.popBackStackImmediate()

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
        Log.d("BoardContentFragment", boardEntity.content)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BoardContentFragment", "onViewCreated")
        Log.d("BoardContentFragment", "갯수" + parentFragmentManager.backStackEntryCount)

    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}

