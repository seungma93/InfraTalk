package com.sm.infratalk.presenter.main.fragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.sm.infratalk.R
import com.sm.infratalk.databinding.FragmentMainBinding
import com.sm.infratalk.di.component.DaggerMyPageFragmentComponent
import com.sm.infratalk.di.component.DaggerServiceComponent
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.presenter.board.fragment.BoardFragment
import com.sm.infratalk.presenter.board.fragment.BoardWriteFragment
import com.sm.infratalk.presenter.chat.fragment.ChatRoomFragment
import com.sm.infratalk.presenter.home.HomeFragment
import com.sm.infratalk.presenter.mypage.fragment.MyAccountInfoEditFragment
import com.sm.infratalk.presenter.mypage.fragment.MyBoardFragment
import com.sm.infratalk.presenter.mypage.fragment.MyBookmarkBoardFragment
import com.sm.infratalk.presenter.mypage.fragment.MyBookmarkCommentFragment
import com.sm.infratalk.presenter.mypage.fragment.MyCommentFragment
import com.sm.infratalk.presenter.mypage.fragment.MyLikeBoardFragment
import com.sm.infratalk.presenter.mypage.fragment.MyLikeCommentFragment
import com.sm.infratalk.presenter.mypage.fragment.MyPageFragment
import com.sm.infratalk.presenter.mypage.viewmodel.MyPageViewModel
import com.sm.infratalk.presenter.service.ForegroundService
import javax.inject.Inject

interface ChildFragmentNavigable {
    fun navigateFragment(endPoint: MainChildFragmentEndPoint)
}

sealed class MainChildFragmentEndPoint {
    object Home : MainChildFragmentEndPoint()
    object Board : MainChildFragmentEndPoint()
    object ChatRoom : MainChildFragmentEndPoint()
    object MyPage : MainChildFragmentEndPoint()
    object BoardWrite : MainChildFragmentEndPoint()
    data class MyBoard(val userEntity: UserEntity) : MainChildFragmentEndPoint()
    data class MyComment(val userEntity: UserEntity) : MainChildFragmentEndPoint()
    object MyBookmarkBoard : MainChildFragmentEndPoint()
    object MyLikeBoard : MainChildFragmentEndPoint()
    object MyBookmarkComment : MainChildFragmentEndPoint()
    object MyLikeComment : MainChildFragmentEndPoint()
    object MyAccountInfoEdit : MainChildFragmentEndPoint()
    object Error : MainChildFragmentEndPoint()
}

class MainFragment : Fragment(), ChildFragmentNavigable {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startService(requireContext())


        binding.apply {

            if(childFragmentManager.fragments.isEmpty()){
                navigateFragment(MainChildFragmentEndPoint.Home)
            }

            navigation.setOnItemSelectedListener {
                when (it.itemId) {
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

    override fun onDestroy() {
        super.onDestroy()
        stopService(requireContext())
    }

    private fun setFragment(fragment: Fragment, viewId: Int, backStackToken: Boolean) {
        val transaction = childFragmentManager.beginTransaction()
        when (backStackToken) {
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

            is MainChildFragmentEndPoint.MyLikeComment -> {
                val fragment = MyLikeCommentFragment()
                setFragment(fragment, R.id.fragment_frame_layout, true)
            }

            is MainChildFragmentEndPoint.MyAccountInfoEdit -> {
                val fragment = MyAccountInfoEditFragment()
                setFragment(fragment, R.id.fragment_frame_layout, true)
            }

            is MainChildFragmentEndPoint.Error -> {
            }

            else -> {}
        }
    }

    private fun startService(context: Context) {
        Log.d("seungma", "서비스 시작 뷰모델")
        val serviceIntent = Intent(context, ForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

    }

    private fun stopService(context: Context) {
        val serviceIntent = Intent(context, ForegroundService::class.java)
        context.stopService(serviceIntent)
    }

}