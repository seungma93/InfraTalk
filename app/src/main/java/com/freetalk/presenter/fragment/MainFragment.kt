package com.freetalk.presenter.fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.freetalk.databinding.FragmentLoginMainBinding
import com.freetalk.databinding.FragmentMainBinding

class MainFragment: Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            var isFabOpen = false
            btnFabMenu.setOnClickListener {
                isFabOpen = toggleFab(isFabOpen)
            }
        }
    }

    private fun toggleFab(isFabOpen: Boolean): Boolean {
        return if (isFabOpen) {
            ObjectAnimator.ofFloat(binding.btnFabList, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.btnFabMessege, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.btnFabWrite, "translationY", 0f).apply { start() }
            false
        } else {
            ObjectAnimator.ofFloat(binding.btnFabList, "translationY", -600f).apply { start() }
            ObjectAnimator.ofFloat(binding.btnFabMessege, "translationY", -400f).apply { start() }
            ObjectAnimator.ofFloat(binding.btnFabWrite, "translationY", -200f).apply { start() }
            true
        }
    }
}