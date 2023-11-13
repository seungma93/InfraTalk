package com.freetalk.presenter.fragment.mypage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.freetalk.databinding.FragmentHomeBinding
import com.freetalk.databinding.FragmentMyPageBinding
import com.freetalk.di.component.DaggerBoardFragmentComponent
import com.freetalk.presenter.fragment.ChildFragmentNavigable
import com.freetalk.presenter.fragment.MainChildFragmentEndPoint
import com.freetalk.presenter.viewmodel.BoardViewModel
import com.freetalk.presenter.viewmodel.MyPageViewModel
import javax.inject.Inject

class MyPageFragment: Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var myPageViewModelFactory: ViewModelProvider.Factory
    private val myPageViewModel: MyPageViewModel by viewModels { myPageViewModelFactory }


    override fun onAttach(context: Context) {
        //Dagger.factory().create(context).inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnMyBoard.setOnClickListener {
            val endPoint = MainChildFragmentEndPoint.MyBoard
            (requireParentFragment() as? ChildFragmentNavigable)?.navigateFragment(endPoint)
        }
    }
}