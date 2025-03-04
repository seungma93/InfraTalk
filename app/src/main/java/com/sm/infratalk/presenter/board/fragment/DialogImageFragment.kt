package com.sm.infratalk.presenter.board.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sm.infratalk.databinding.DialogBoardImageBinding
import com.sm.infratalk.domain.image.entity.ImagesResultEntity
import com.sm.infratalk.presenter.board.adpater.ImageAdapter

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
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val recyclerView = binding.rvImage // RecyclerView
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager= layoutManager
        recyclerView.adapter = ImageAdapter(imagesResultEntity?.successUris ?: emptyList()) // Adapter 설정
        // 아이템 크기 맞춰 스크롤 이동
        val snapHelper = PagerSnapHelper() // 또는 LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val snapView =snapHelper.findSnapView(layoutManager)
                val snapPosition = layoutManager.getPosition(snapView!!)
                val totalPages = recyclerView.adapter?.itemCount
                binding.tvCount.text = "${snapPosition + 1}/$totalPages"
            }
        })

        binding.ivExit.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

    }


}