package com.freetalk.presenter.fragment.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.freetalk.databinding.FragmentChatRoomBinding
import com.freetalk.di.component.DaggerChatFragmentComponent
import com.freetalk.di.component.DaggerChatRoomFragmentComponent
import com.freetalk.domain.entity.ChatPrimaryKeyEntity
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.adapter.ChatRoomListAdapter
import com.freetalk.presenter.form.ChatRoomCheckForm
import com.freetalk.presenter.form.ChatRoomCreateForm
import com.freetalk.presenter.viewmodel.BoardViewEvent
import com.freetalk.presenter.viewmodel.BoardViewModel
import com.freetalk.presenter.viewmodel.ChatRoomViewEvent
import com.freetalk.presenter.viewmodel.ChatRoomViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatRoomFragment : Fragment() {

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!
    private var _adapter: ChatRoomListAdapter? = null
    private val adapter get() = _adapter!!

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
    ): View? {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _adapter = ChatRoomListAdapter(itemClick = { chatRoomEntity ->
            /*
            viewLifecycleOwner.lifecycleScope.launch {
                val member = chatRoomEntity.member
                chatRoomViewModel.startChat(
                    chatRoomCheckForm = ChatRoomCheckForm(member = member)
                )
            }

             */
        })

        viewLifecycleOwner.lifecycleScope.launch {
            chatRoomViewModel.loadChatRoom()
        }
        binding.rvChatRoom.adapter = adapter
        subscribe()
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            chatRoomViewModel.viewState.collect {
                Log.d("seungma", "구독" + it.chatRoomListEntity.chatRoomList.size)
                adapter.submitList(it.chatRoomListEntity.chatRoomList)
            }
/*
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
            */
        }
    }
}