package com.freetalk.presenter.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.freetalk.R
import com.freetalk.databinding.FragmentMainBinding
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable

class MainFragment : Fragment() {
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

            navigation.setOnItemSelectedListener {
                when(it.itemId) {
                    R.id.home_fragment -> {
                        Log.v("MainFragment", "홈 버튼 클릭")
                        (requireActivity() as? Navigable)?.navigateFragment(EndPoint.Home)
                    }
                    R.id.board_fragment -> {
                        (requireActivity() as? Navigable)?.navigateFragment(EndPoint.Board)
                    }
                    R.id.chat_fragment -> {
                        (requireActivity() as? Navigable)?.navigateFragment(EndPoint.Chat)
                    }
                    R.id.my_page_fragment -> {
                        (requireActivity() as? Navigable)?.navigateFragment(EndPoint.MyPage)
                    }
                }
                true
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


}