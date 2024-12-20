package com.seungma.infratalk.presenter.board.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.seungma.infratalk.databinding.DialogBoardImageBinding
import com.seungma.infratalk.domain.image.entity.ImagesResultEntity
import com.seungma.infratalk.presenter.board.adpater.ImageAdapter

class DialogImageFragment(private val imagesResultEntity: ImagesResultEntity?) : DialogFragment() {
    private var _binding: DialogBoardImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBoardImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.rvImage // RecyclerView
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager= layoutManager
        recyclerView.adapter = ImageAdapter(imagesResultEntity?.successUris ?: emptyList()) // Adapter 설정
    }

    override fun onStart() {
        super.onStart()

    }


}