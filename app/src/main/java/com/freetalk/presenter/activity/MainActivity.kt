package com.freetalk.presenter.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.freetalk.R
import com.freetalk.databinding.ActivityMainBinding
import com.freetalk.presenter.fragment.LoginMainFragment
import com.freetalk.presenter.fragment.MainFragment
import com.freetalk.presenter.fragment.SignUpFragment
import kotlin.math.log

sealed class EndPoint {
    data class LoginMain(val token: Int) : EndPoint()
    data class SignUp(val token: Int): EndPoint()
    data class Main(val token: Int): EndPoint()
    object Error : EndPoint()
}

interface MainActivityNavigation {
    fun navigateFragment(endPoint: EndPoint)
}

class MainActivity() : AppCompatActivity(), MainActivityNavigation {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val endPointLoginMain = EndPoint.LoginMain(1)
        navigateFragment(endPointLoginMain)
    }

    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun navigateFragment(endPoint: EndPoint) {
        Bundle().let {
            when (endPoint) {
                is EndPoint.LoginMain -> {
                    val fragment = LoginMainFragment()
                    setFragment(fragment)
                }
                is EndPoint.SignUp -> {
                    val fragment = SignUpFragment()
                    setFragment(fragment)
                }
                is EndPoint.Main -> {
                    val fragment = MainFragment()
                    setFragment(fragment)
                }
                is EndPoint.Error -> {
                }
            }
        }
    }
}