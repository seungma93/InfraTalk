package com.seungma.infratalk.presenter.main.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.seungma.infratalk.R
import com.seungma.infratalk.databinding.ActivityMainBinding
import com.seungma.infratalk.domain.board.entity.BoardContentPrimaryKeyEntity
import com.seungma.infratalk.domain.chat.entity.ChatPrimaryKeyEntity
import com.seungma.infratalk.presenter.board.fragment.BoardContentFragment
import com.seungma.infratalk.presenter.chat.fragment.ChatFragment
import com.seungma.infratalk.presenter.main.fragment.MainFragment
import com.seungma.infratalk.presenter.sign.fragment.LoginMainFragment
import com.seungma.infratalk.presenter.sign.fragment.SignUpFragment

sealed class EndPoint {
    object LoginMain : EndPoint()
    object SignUp : EndPoint()
    object Main : EndPoint()
    data class BoardContent(val boardContentPrimaryKeyEntity: BoardContentPrimaryKeyEntity) :
        EndPoint()

    data class Chat(val chatPrimaryKeyEntity: ChatPrimaryKeyEntity) : EndPoint()
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

    override fun navigateFragment(endPoint: EndPoint) {
        when (endPoint) {
            is EndPoint.LoginMain -> {
                val fragment = LoginMainFragment()
                setFragment(fragment, R.id.activity_frame_layout, true)
            }

            is EndPoint.SignUp -> {
                val fragment = SignUpFragment()
                setFragment(fragment, R.id.activity_frame_layout, true)
            }

            is EndPoint.Main -> {
                val fragment = MainFragment()
                setFragment(fragment, R.id.activity_frame_layout, false)
            }

            is EndPoint.BoardContent -> {
                val fragment =
                    BoardContentFragment.newInstance(endPoint.boardContentPrimaryKeyEntity)
                setFragment(fragment, R.id.activity_frame_layout, true)
            }

            is EndPoint.Chat -> {
                val fragment = ChatFragment.newInstance(endPoint.chatPrimaryKeyEntity)
                setFragment(fragment, R.id.activity_frame_layout, true)
            }

            is EndPoint.Error -> {
            }
        }
    }

}