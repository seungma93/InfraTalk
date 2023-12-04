package com.freetalk.presenter.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.freetalk.R
import com.freetalk.databinding.FragmentMainBinding
import com.freetalk.domain.entity.UserEntity
import com.freetalk.presenter.fragment.board.BoardFragment
import com.freetalk.presenter.fragment.board.BoardWriteFragment
import com.freetalk.presenter.fragment.chat.ChatFragment
import com.freetalk.presenter.fragment.chat.ChatRoomFragment
import com.freetalk.presenter.fragment.home.HomeFragment
import com.freetalk.presenter.fragment.mypage.MyBoardFragment
import com.freetalk.presenter.fragment.mypage.MyBookmarkBoardFragment
import com.freetalk.presenter.fragment.mypage.MyBookmarkCommentFragment
import com.freetalk.presenter.fragment.mypage.MyCommentFragment
import com.freetalk.presenter.fragment.mypage.MyLikeBoardFragment
import com.freetalk.presenter.fragment.mypage.MyPageFragment

interface ChildFragmentNavigable {
    fun navigateFragment(endPoint: MainChildFragmentEndPoint)
}

sealed class MainChildFragmentEndPoint {
    object Home : MainChildFragmentEndPoint()
    object Board : MainChildFragmentEndPoint()
    object ChatRoom : MainChildFragmentEndPoint()
    object MyPage : MainChildFragmentEndPoint()
    object BoardWrite : MainChildFragmentEndPoint()
    data class MyBoard(val userEntity: UserEntity): MainChildFragmentEndPoint()
    data class MyComment(val userEntity: UserEntity): MainChildFragmentEndPoint()
    object MyBookmarkBoard : MainChildFragmentEndPoint()
    object MyLikeBoard : MainChildFragmentEndPoint()
    object MyBookmarkComment : MainChildFragmentEndPoint()
    object Error : MainChildFragmentEndPoint()
}

class MainFragment : Fragment(), ChildFragmentNavigable {
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
                        navigateFragment(MainChildFragmentEndPoint.Home)
                    }
                    R.id.board_fragment -> {
                        navigateFragment(MainChildFragmentEndPoint.Board)
                    }
                    R.id.chat_fragment -> {
                        navigateFragment(MainChildFragmentEndPoint.ChatRoom)
                    }
                    R.id.my_page_fragment -> {
                       navigateFragment(MainChildFragmentEndPoint.MyPage)
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

    override fun navigateFragment(endPoint: MainChildFragmentEndPoint) {
        when (endPoint) {
            is MainChildFragmentEndPoint.Home -> {
                val fragment = HomeFragment()
                setFragment(fragment, R.id.fragment_frame_layout, false)
            }
            is MainChildFragmentEndPoint.Board -> {
                val fragment = BoardFragment()
                setFragment(fragment, R.id.fragment_frame_layout, false)
            }
            is MainChildFragmentEndPoint.ChatRoom -> {
                val fragment = ChatRoomFragment()
                setFragment(fragment, R.id.fragment_frame_layout, false)
            }
            is MainChildFragmentEndPoint.MyPage -> {
                val fragment = MyPageFragment()
                setFragment(fragment, R.id.fragment_frame_layout, false)
            }
            is MainChildFragmentEndPoint.BoardWrite -> {
                val fragment = BoardWriteFragment()
                setFragment(fragment, R.id.fragment_frame_layout, true)
            }
            is MainChildFragmentEndPoint.MyBoard -> {
                val fragment = MyBoardFragment.newInstance(userEntity = endPoint.userEntity)
                setFragment(fragment, R.id.fragment_frame_layout, true)
            }
            is MainChildFragmentEndPoint.MyComment -> {
                val fragment = MyCommentFragment.newInstance(userEntity = endPoint.userEntity)
                setFragment(fragment, R.id.fragment_frame_layout, true)
            }
            is MainChildFragmentEndPoint.MyBookmarkBoard -> {
                val fragment = MyBookmarkBoardFragment()
                setFragment(fragment, R.id.fragment_frame_layout, true)
            }
            is MainChildFragmentEndPoint.MyLikeBoard -> {
                val fragment = MyLikeBoardFragment()
                setFragment(fragment, R.id.fragment_frame_layout, true)
            }
            is MainChildFragmentEndPoint.MyBookmarkComment -> {
                val fragment = MyBookmarkCommentFragment()
                setFragment(fragment, R.id.fragment_frame_layout, true)
            }
            is MainChildFragmentEndPoint.Error -> {
            }
            else -> {}
        }
    }


}