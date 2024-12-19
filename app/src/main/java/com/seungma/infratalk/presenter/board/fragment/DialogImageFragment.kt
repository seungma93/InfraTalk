package com.seungma.infratalk.presenter.board.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.seungma.infratalk.databinding.DialogBoardImageBinding
import com.seungma.infratalk.domain.image.entity.ImagesResultEntity

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
        imagesResultEntity?.successUris?.map {
            Log.d("seungma", "이미지 : " + it)
            val imageView = ImageView(requireContext())
            Glide.with(imageView.context)
                .load(it)
                .centerCrop()
                .into(imageView)
            binding.rvImage.addView(imageView)
        }
    }

    override fun onStart() {
        super.onStart()

    }


}