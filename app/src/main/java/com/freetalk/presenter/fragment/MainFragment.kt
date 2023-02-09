package com.freetalk.presenter.fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.freetalk.R
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
        (activity as AppCompatActivity).setSupportActionBar(binding.appBar)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
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