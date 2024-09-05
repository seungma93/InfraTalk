package com.seungma.infratalk.presenter.main.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ReportFragment.Companion.reportFragment
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
                    is SplashViewEvent.Error -> {
                        Log.d("SplashActivity", "스플래시 에러 : " + it.errorCode.message )
                        when(it.errorCode.message) {

                            "HTTP 200" -> {

                            }

                            "HTTP 400 " -> {
                                // TODO 프리퍼런스 초기화
                                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                // TODO 홈으로 이동

                            }

                        }
                    }
                    else -> {

                    }
                }
            }
        }


    }
}