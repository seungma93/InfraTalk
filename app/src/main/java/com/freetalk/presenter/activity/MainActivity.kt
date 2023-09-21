package com.freetalk.presenter.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.freetalk.R
import com.freetalk.domain.entity.BoardEntity
import com.freetalk.databinding.ActivityMainBinding
import com.freetalk.domain.entity.BoardContentPrimaryKeyEntity
import com.freetalk.presenter.fragment.*
import com.freetalk.presenter.fragment.board.BoardContentFragment
import com.freetalk.presenter.fragment.sign.LoginMainFragment
import com.freetalk.presenter.fragment.sign.SignUpFragment
import com.freetalk.presenter.fragment.board.BoardFragment
import com.freetalk.presenter.fragment.board.BoardWriteFragment
import com.freetalk.presenter.fragment.chat.ChatFragment
import com.freetalk.presenter.fragment.home.HomeFragment
import com.freetalk.presenter.fragment.mypage.MyPageFragment

sealed class EndPoint {
    object LoginMain : EndPoint()
    object SignUp : EndPoint()
    object Main: EndPoint()
    data class BoardContent(val boardContentPrimaryKeyEntity: BoardContentPrimaryKeyEntity): EndPoint()
    object Error : EndPoint()
}

interface Navigable {
    fun navigateFragment(endPoint: EndPoint)
}

class MainActivity() : AppCompatActivity(), Navigable {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigateFragment(EndPoint.LoginMain)
    }

    private fun setFragment(fragment: Fragment, viewId: Int, backStackToken: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
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
                is EndPoint.LoginMain -> {
                    val fragment = LoginMainFragment()
                    setFragment(fragment, R.id.activity_frame_layout, true)
                }
                is EndPoint.SignUp -> {
                    val fragment = SignUpFragment()
                    setFragment(fragment, R.id.activity_frame_layout,true)
                }
                is EndPoint.Main -> {
                    val fragment = MainFragment()
                    setFragment(fragment, R.id.activity_frame_layout, false)
                }
                is EndPoint.BoardContent -> {
                    val fragment = BoardContentFragment.newInstance(endPoint.boardContentPrimaryKeyEntity)
                    setFragment(fragment, R.id.activity_frame_layout, true)
                }
                is EndPoint.Error -> {
                }
            }
    }

}