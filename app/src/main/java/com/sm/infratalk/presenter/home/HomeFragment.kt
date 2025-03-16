package com.sm.infratalk.presenter.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sm.infratalk.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvDeveloperInfo.text =
                "[infratalk@home] uname -an \n[infratalk@home] 안드로이드 개발자 전직 미들웨어 엔지니어\n" +
                        "[infratalk@home] help \n[infratalk@home] 앱 관련 문제 발생시 이메일 문의 \n" +
                        "[infratalk@home] email \n[infratalk@home] seungma93@naver.com "



            lyOracle.setOnClickListener {
                Log.d("seungma","오라클")
                // 정해진 URL을 여기에 넣어주세요.
                val url = "https://docs.oracle.com/en/"

                // 브라우저를 열기 위한 Intent 생성
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                // 액티비티가 있는지 확인 후 실행
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                } else {
                    Log.d("seungma", "브라우저 없음")
                    val chooser = Intent.createChooser(intent, "브라우저를 선택하세요")
                    startActivity(chooser)
                }
            }

            lyIbm.setOnClickListener {
                // 정해진 URL을 여기에 넣어주세요.
                val url = "https://www.ibm.com/docs/en"

                // 브라우저를 열기 위한 Intent 생성
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                // 액티비티가 있는지 확인 후 실행
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                } else {
                    Log.d("seungma", "브라우저 없음")
                    val chooser = Intent.createChooser(intent, "브라우저를 선택하세요")
                    startActivity(chooser)
                }
            }

            lyAws.setOnClickListener {
                // 정해진 URL을 여기에 넣어주세요.
                val url = "https://docs.aws.amazon.com/"

                // 브라우저를 열기 위한 Intent 생성
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                // 액티비티가 있는지 확인 후 실행
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                } else {
                    Log.d("seungma", "브라우저 없음")
                    val chooser = Intent.createChooser(intent, "브라우저를 선택하세요")
                    startActivity(chooser)
                }
            }

            lyApache.setOnClickListener {
                // 정해진 URL을 여기에 넣어주세요.
                val url = "https://httpd.apache.org/docs/"

                // 브라우저를 열기 위한 Intent 생성
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                // 액티비티가 있는지 확인 후 실행
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                } else {
                    Log.d("seungma", "브라우저 없음")
                    val chooser = Intent.createChooser(intent, "브라우저를 선택하세요")
                    startActivity(chooser)
                }
            }

            lyTomcat.setOnClickListener {
                // 정해진 URL을 여기에 넣어주세요.
                val url = "https://tomcat.apache.org/"

                // 브라우저를 열기 위한 Intent 생성
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                // 액티비티가 있는지 확인 후 실행
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                } else {
                    Log.d("seungma", "브라우저 없음")
                    val chooser = Intent.createChooser(intent, "브라우저를 선택하세요")
                    startActivity(chooser)
                }
            }

            lyThread.setOnClickListener {
                // 정해진 URL을 여기에 넣어주세요.
                val url = "https://fastthread.io/"

                // 브라우저를 열기 위한 Intent 생성
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                // 액티비티가 있는지 확인 후 실행
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                } else {
                    Log.d("seungma", "브라우저 없음")
                    val chooser = Intent.createChooser(intent, "브라우저를 선택하세요")
                    startActivity(chooser)
                }
            }
        }
    }
}