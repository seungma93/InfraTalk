package com.seungma.infratalk.presenter.chat.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.seungma.infratalk.databinding.FragmentChatRoomBinding
import com.seungma.infratalk.di.component.DaggerChatRoomFragmentComponent
import com.seungma.infratalk.domain.chat.entity.ChatPrimaryKeyEntity
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.presenter.chat.adapter.ChatRoomListAdapter
import com.seungma.infratalk.presenter.chat.viewmodel.ChatRoomViewEvent
import com.seungma.infratalk.presenter.chat.viewmodel.ChatRoomViewModel
import com.seungma.infratalk.presenter.main.activity.EndPoint
import com.seungma.infratalk.presenter.main.activity.Navigable
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatRoomFragment : Fragment() {
    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!
    private var _adapter: ChatRoomListAdapter? = null
    private val adapter get() = _adapter!!
    private lateinit var userEntity: UserEntity

    @Inject
    lateinit var chatRoomViewModelFactory: ViewModelProvider.Factory
    private val chatRoomViewModel: ChatRoomViewModel by viewModels { chatRoomViewModelFactory }

    override fun onAttach(context: Context) {
        DaggerChatRoomFragmentComponent.factory().create(context).inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            userEntity = chatRoomViewModel.getUserMe()
        }
        _adapter = ChatRoomListAdapter(itemClick = { chatRoomEntity ->
            val userEmail = userEntity.email
            val endPoint = EndPoint.Chat(
                chatPrimaryKeyEntity = ChatPrimaryKeyEntity(
                    partnerEmail = when (chatRoomEntity.leaveMember?.size) {
                        1 -> chatRoomEntity.leaveMember.first()
                        else -> chatRoomEntity.member?.find { it != userEmail } ?: error("")
                    },
                    chatRoomId = chatRoomEntity.primaryKey
                )
            )
            (requireActivity() as? Navigable)?.navigateFragment(endPoint)
        })

        viewLifecycleOwner.lifecycleScope.launch {
            showProgressBar()
            chatRoomViewModel.loadChatRoom()
        }
        binding.rvChatRoom.adapter = adapter
        subscribe()
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                chatRoomViewModel.viewState.collect {
                    Log.d("seungma", "구독" + it.chatRoomListEntity.chatRoomList.size)
                    if (it.chatRoomListEntity.chatRoomList.isNotEmpty()) {
                        if (it.chatRoomListEntity.chatRoomList.first().primaryKey.isNotEmpty()) {
                            adapter.submitList(it.chatRoomListEntity.chatRoomList) {
                                hideProgressBar()
                                binding.rvChatRoom.scrollToPosition(0)
                            }
                        }
                    } else {
                        adapter.submitList(it.chatRoomListEntity.chatRoomList) {
                            hideProgressBar()
                            binding.rvChatRoom.scrollToPosition(0)
                        }
                    }
                }
            }
            launch {
                chatRoomViewModel.viewEvent.collect {
                    when (it) {
                        is ChatRoomViewEvent.ChatStart -> {
                            when (it.chatStartEntity.isSuccess) {
                                true -> {
                                    Log.d("seungma", "채팅 시작 성공")
                                    val endPoint = EndPoint.Chat(
                                        chatPrimaryKeyEntity = ChatPrimaryKeyEntity(
                                            partnerEmail = it.chatStartEntity.chatPartner,
                                            chatRoomId = it.chatStartEntity.chatRoomId ?: error("")
                                        )
                                    )
                                    (requireActivity() as? Navigable)?.navigateFragment(endPoint)
                                }

                                false -> Log.d("seungma", "채팅방 시작 실패")
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun showProgressBar() {
        Log.d("BoardFragment", "프로그레스바 시작")
        blockLayoutTouch()
        binding.progressBar.isVisible = true
    }

    private fun blockLayoutTouch() {
        requireActivity().window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideProgressBar() {
        Log.d("BoardFragment", "프로그레스바 종료")
        clearBlockLayoutTouch()
        binding.progressBar.isVisible = false
    }

    private fun clearBlockLayoutTouch() {
        requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}
