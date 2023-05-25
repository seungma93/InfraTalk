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
import com.freetalk.presenter.fragment.board.BoardContentFragment
import com.freetalk.presenter.fragment.board.BoardFragment
import com.freetalk.presenter.fragment.board.BoardWriteFragment
import com.freetalk.presenter.fragment.chat.ChatFragment
import com.freetalk.presenter.fragment.home.HomeFragment
import com.freetalk.presenter.fragment.mypage.MyPageFragment
import com.freetalk.presenter.fragment.sign.LoginMainFragment
import com.freetalk.presenter.fragment.sign.SignUpFragment

class MainFragment : Fragment(), Navigable {
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
                        navigateFragment(EndPoint.Home)
                    }
                    R.id.board_fragment -> {
                        navigateFragment(EndPoint.Board)
                    }
                    R.id.chat_fragment -> {
                        navigateFragment(EndPoint.Chat)
                    }
                    R.id.my_page_fragment -> {
                       navigateFragment(EndPoint.MyPage)
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

    private fun setFragment(fragment: Fragment, viewId: Int, backStackToken: Boolean) {
        val transaction = childFragmentManager.beginTransaction()
        when(backStackToken){
            true -> {
                transaction.replace(viewId, fragment)
                    .addToBackStack(null)
                    .commit()
            }
            false -> {
                transaction.replace(viewId, fragment)
                    .commit()
            }
        }

    }

    override fun navigateFragment(endPoint: EndPoint) {
        when (endPoint) {
            is EndPoint.Home -> {
                val fragment = HomeFragment()
                setFragment(fragment, R.id.fragment_frame_layout, true)
            }
            is EndPoint.Board -> {
                val fragment = BoardFragment()
                setFragment(fragment, R.id.fragment_frame_layout, true)
            }
            is EndPoint.Chat -> {
                val fragment = ChatFragment()
                setFragment(fragment, R.id.fragment_frame_layout, true)
            }
            is EndPoint.MyPage -> {
                val fragment = MyPageFragment()
                setFragment(fragment, R.id.fragment_frame_layout, true)
            }
            is EndPoint.BoardWrite -> {
                val fragment = BoardWriteFragment()
                setFragment(fragment, R.id.fragment_frame_layout, true)
            }
            is EndPoint.BoardContent -> {
                val fragment = BoardContentFragment()
                val bundle = Bundle()
                bundle.putSerializable(BoardContentFragment.BOARD_ITEM_KEY, endPoint.boardEntity)
                fragment.arguments = bundle
                setFragment(fragment, R.id.fragment_frame_layout, true)
            }
            is EndPoint.Error -> {
            }
            else -> {}
        }
    }


}