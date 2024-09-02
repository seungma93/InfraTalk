package com.seungma.infratalk.presenter.main.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.seungma.infratalk.di.component.DaggerSplashActivityComponent
import com.seungma.infratalk.presenter.sign.viewmodel.SplashViewEvent
import com.seungma.infratalk.presenter.sign.viewmodel.SplashViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var splashViewModelFactory: ViewModelProvider.Factory
    private val splashViewModel: SplashViewModel by viewModels { splashViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerSplashActivityComponent.factory().create(this).inject(this)

        lifecycleScope.launch {
            splashViewModel.checkLogin()
        }

        lifecycleScope.launch {
            splashViewModel.viewEvent.collect {
                when(it) {
                    is SplashViewEvent.CheckLogin  -> {
                        Log.d("SplashActivity", "스플래시 : " + it.userEntity )
                    }
                    else -> {

                    }
                }
            }
        }
/*
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

 */
    }
}