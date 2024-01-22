package com.seungma.infratalk.presenter.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seungma.infratalk.databinding.FragmentHomeBinding

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
                "[infratalk@home] uname -an \n[infratalk@home] 인프라 엔지니어 하던 안드로이드 개발자\n" +
                "[infratalk@home] email \n[infratalk@home] seungma93@naver.com " +
                "[infratalk@home] help \n[infratalk@home] 질문이 해결 안되면 답변 달아드립니다 \n" +
                "앱 사용시 문제가 있으면 이메일로 문의 주세요\n인프라 엔지니어 응원합니다"
        }
    }
}