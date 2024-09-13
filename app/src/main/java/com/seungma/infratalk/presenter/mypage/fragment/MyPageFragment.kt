package com.seungma.infratalk.presenter.mypage.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.seungma.infratalk.databinding.FragmentMyPageBinding
import com.seungma.infratalk.di.component.DaggerMyPageFragmentComponent
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.presenter.main.fragment.ChildFragmentNavigable
import com.seungma.infratalk.presenter.main.fragment.MainChildFragmentEndPoint
import com.seungma.infratalk.presenter.mypage.viewmodel.MyPageViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var userEntity: UserEntity

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
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {

            viewLifecycleOwner.lifecycleScope.launch {
                userEntity = myPageViewModel.getUserMe()
                userEntity.apply {
                    Log.d("seungma", "수행")
                    tvNickname.text =
                        "[infratalk@mypage] nickname \n[infratalk@mypage] $nickname"
                    Log.d("seungma", image.toString())

                    val requestOptions = RequestOptions.circleCropTransform().autoClone()
                    image?.let {
                        Glide.with(requireContext())
                            .load(it)
                            .apply(requestOptions)
                            .into(ivProfileImage)

                    }
                }
            }


            lyEditMyInfo.setOnClickListener {
                val endPoint = MainChildFragmentEndPoint.MyAccountInfoEdit
                (requireParentFragment() as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            }
            lyMyBoard.setOnClickListener {
                val endPoint =
                    MainChildFragmentEndPoint.MyBoard(userEntity = userEntity)
                (parentFragment as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            }
            lyMyComment.setOnClickListener {
                val endPoint =
                    MainChildFragmentEndPoint.MyComment(userEntity = userEntity)
                (requireParentFragment() as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            }
            lyMyBookmarkBoard.setOnClickListener {
                val endPoint = MainChildFragmentEndPoint.MyBookmarkBoard
                (requireParentFragment() as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            }
            lyMyBookmarkComment.setOnClickListener {
                val endPoint = MainChildFragmentEndPoint.MyBookmarkComment
                (requireParentFragment() as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            }
            lyMyLikeBoard.setOnClickListener {
                val endPoint = MainChildFragmentEndPoint.MyLikeBoard
                (requireParentFragment() as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            }
            lyMyLikeComment.setOnClickListener {
                val endPoint = MainChildFragmentEndPoint.MyLikeComment
                (requireParentFragment() as? ChildFragmentNavigable)?.navigateFragment(endPoint)
            }
        }
    }

    fun ImageView.loadProfileImage(
        url: String?,
        @DrawableRes defaultImage: Int = 0
    ) {
        val requestOptions = RequestOptions.circleCropTransform().autoClone()
        Glide.with(this)
            .load(url)
            .apply(requestOptions)
            .apply {
                if (defaultImage != 0) {
                    error(
                        Glide.with(this@loadProfileImage)
                            .load(defaultImage)
                            .apply(requestOptions)
                    )
                }
            }
            .into(this)
    }
}