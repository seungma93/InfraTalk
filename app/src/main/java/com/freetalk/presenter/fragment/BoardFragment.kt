package com.freetalk.presenter.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.freetalk.databinding.FragmentBoardBinding
import com.freetalk.databinding.FragmentHomeBinding
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable

class BoardFragment: Fragment() {
    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!

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
        binding.apply {
            btnFabMenu.setOnClickListener {
                isFabOpen = toggleFab(isFabOpen)
            }
            btnFabWrite.setOnClickListener {
                (requireActivity() as? Navigable)?.navigateFragment(EndPoint.BoardWrite(1))
                toggleFab(true)
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