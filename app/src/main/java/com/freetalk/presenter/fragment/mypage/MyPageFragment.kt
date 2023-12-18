package com.freetalk.presenter.fragment.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.freetalk.R
import com.freetalk.databinding.FragmentHomeBinding
import com.freetalk.databinding.FragmentMyPageBinding
import com.freetalk.di.component.DaggerBoardFragmentComponent
import com.freetalk.di.component.DaggerMyPageFragmentComponent
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
        DaggerMyPageFragmentComponent.factory().create(context).inject(this)
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

        val userEntity = myPageViewModel.getUserInfo()

        binding.apply {

            userEntity.apply {
                Log.d("seungma", "수행")
                tvEmail.text = email
                tvNickname.text = nickname
                Log.d("seungma", image.toString())

                image?.let {
                    Glide.with(requireContext())
                        .load(it)
                        .into(profileImage)

                } ?: run {
                    val resourceId = R.drawable.ic_baseline_person_24
                    profileImage.setImageResource(resourceId)
                }


            }

            btnEditMyInfo.setOnClickListener {
                val endPoint = MainChildFragmentEndPoint.MyAccountInfoEdit
                (requireParentFragment() as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            }
            btnMyBoard.setOnClickListener {
                val endPoint = MainChildFragmentEndPoint.MyBoard(userEntity = myPageViewModel.getUserInfo())
                (requireParentFragment() as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            }
            btnMyComment.setOnClickListener {
                val endPoint = MainChildFragmentEndPoint.MyComment(userEntity = myPageViewModel.getUserInfo())
                (requireParentFragment() as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            }
            btnMyBookmarkBoard.setOnClickListener {
                val endPoint = MainChildFragmentEndPoint.MyBookmarkBoard
                (requireParentFragment() as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            }
            btnMyLikeBoard.setOnClickListener {
                val endPoint = MainChildFragmentEndPoint.MyLikeBoard
                (requireParentFragment() as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            }
            btnMyBookmarkComment.setOnClickListener {
                val endPoint = MainChildFragmentEndPoint.MyBookmarkComment
                (requireParentFragment() as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            }
            btnMyLikeComment.setOnClickListener {
                val endPoint = MainChildFragmentEndPoint.MyLikeComment
                (requireParentFragment() as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            }
        }
    }
}