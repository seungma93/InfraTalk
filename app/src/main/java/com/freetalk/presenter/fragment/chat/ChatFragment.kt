package com.freetalk.presenter.fragment.chat

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.freetalk.databinding.FragmentChatBinding
import com.freetalk.di.component.DaggerChatFragmentComponent
import com.freetalk.domain.entity.ChatPrimaryKeyEntity
import com.freetalk.domain.entity.ChatRoomEntity
import com.freetalk.domain.entity.ChatRoomLeave
import com.freetalk.presenter.adapter.ChatItem
import com.freetalk.presenter.adapter.ChatListAdapter
import com.freetalk.presenter.form.ChatMessageListLoadForm
import com.freetalk.presenter.form.ChatMessageSendForm
import com.freetalk.presenter.form.ChatRoomLeaveForm
import com.freetalk.presenter.form.ChatRoomLoadForm
import com.freetalk.presenter.viewmodel.ChatViewEvent
import com.freetalk.presenter.viewmodel.ChatViewModel
import com.freetalk.presenter.viewmodel.ChatViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatFragment : Fragment() {
    companion object {
        const val CHAT_PRIMARY_KEY = "CHAT_PRIMARY_KEY"

        fun newInstance(
            chatPrimaryKeyEntity: ChatPrimaryKeyEntity
        ): ChatFragment {
            return ChatFragment().apply {
                arguments = bundleOf(
                    CHAT_PRIMARY_KEY to chatPrimaryKeyEntity
                )
            }
        }
    }

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private var _chatListAdapter: ChatListAdapter? = null
    private val chatListAdapter get() = _chatListAdapter!!

    private val onChatScrollListener: OnChatScrollListener = OnChatScrollListener({
        Log.d("seungma", "람다 전달")
        moreItems()
    }, {
        Toast.makeText(
            requireContext(),
            "마지막 페이지 입니다.",
            Toast.LENGTH_SHORT
        ).show()
    }, { showProgressBar() })

    private val chatPrimaryKeyEntity
        get() = requireArguments().getSerializable(
            CHAT_PRIMARY_KEY
        ) as ChatPrimaryKeyEntity

    @Inject
    lateinit var chatViewModelFactory: ChatViewModelFactory
    private val chatViewModel: ChatViewModel by viewModels { chatViewModelFactory }

    override fun onAttach(context: Context) {
        DaggerChatFragmentComponent.factory().create(context).inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _chatListAdapter = ChatListAdapter()

        binding.apply {

            chatEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // 텍스트 변경 전에 호출되는 메서드
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 텍스트가 변경될 때 호출되는 메서드
                    when (s.isNullOrBlank()) {
                        true -> btnSendChat.isEnabled = false
                        false -> btnSendChat.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    // 텍스트 변경 후에 호출되는 메서드
                }
            })

            btnSendChat.setOnClickListener {
                val inputChatMessage = binding.chatTextInput.editText!!.text.toString()
                when (inputChatMessage.isEmpty()) {
                    true -> {
                        Toast.makeText(
                            requireActivity(), "내용을 입력하세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    false -> {
                        it.isEnabled = false
                        chatEditText.text = null
                        viewLifecycleOwner.lifecycleScope.launch {
                            chatViewModel.sendChatMessage(
                                chatMessageSendForm = ChatMessageSendForm(
                                    chatRoomId = chatPrimaryKeyEntity.chatRoomId,
                                    content = inputChatMessage
                                )
                            )
                        }

                    }
                }

            }

            ivChatExit.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    chatViewModel.leaveChatRoom(
                        chatRoomLeaveForm = ChatRoomLeaveForm(
                            chatRoomId = chatPrimaryKeyEntity.chatRoomId
                        )
                    )
                }
            }

            val layoutManager = LinearLayoutManager(requireContext())
            layoutManager.reverseLayout = true;
            layoutManager.stackFromEnd = true;
            rvChat.layoutManager = layoutManager
            rvChat.adapter = chatListAdapter
        }
        viewLifecycleOwner.lifecycleScope.launch {
            hideProgressBar()

            val chatRoomEntity =
                chatViewModel.loadChatRoomName(chatRoomLoadForm = ChatRoomLoadForm(chatRoomId = chatPrimaryKeyEntity.chatRoomId)).chatRoomEntity

            chatRoomEntity?.let { binding.tvChatTitle.text = it.roomName }

            val viewState = chatViewModel.loadChatMessage(
                chatMessageListLoadForm = ChatMessageListLoadForm(
                    chatRoomId = chatPrimaryKeyEntity.chatRoomId,
                    reload = true
                )
            )
            chatListAdapter.submitList(createChatItem(viewState)) {
                binding.rvChat.scrollToPosition(0)
                hideProgressBar()
            }
        }



        chatViewModel.viewModelScope.launch {

            launch {
                /*
                chatViewModel.viewState.map {
                    it.chatRoomEntity?.roomName
                }.distinctUntilChanged().collect {
                    Log.d("seungma", "챗프레그먼트 콜렉트1")
                    binding.tvChatTitle.text = it
                }

                 */
                chatViewModel.viewState.map { it.chatRoomEntity?.roomName }
                    .stateIn(this)
                    .collect {
                        Log.d("seungma", "챗프레그먼트 콜렉트1")
                        binding.tvChatTitle.text = it
                    }
            }

            launch {
                chatViewModel.viewState.map { createChatItem(it) to it.isNewChatMessage }
                    .stateIn(this)
                    .collect { (chatList, isNewChatMessage) ->
                        Log.d("seungma", "챗프레그먼트 콜렉트2")
                        chatListAdapter.submitList(chatList) {
                            if (isNewChatMessage) binding.rvChat.scrollToPosition(0)
                        }
                    }
            }

            Log.d("seungma", "챗프레그먼트 콜렉트")
        }

        subscribe()
        initScrollListener()

    }

    private fun createChatItem(viewState: ChatViewModel.ChatViewState): List<ChatItem> =
        with(viewState) {
            return mutableListOf<ChatItem>().apply {
                chatMessageListEntity.chatMessageList.map {
                    when (it.sender.email == chatPrimaryKeyEntity.partnerEmail) {
                        true -> add(ChatItem.Partner(it))
                        false -> add(ChatItem.Owner(it))
                    }
                }
            }
        }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.viewEvent.collect {
                when (it) {
                    is ChatViewEvent.SendMessage -> {
                        when (it.chatMessageSend.isSuccess) {
                            true -> {
                                Log.d("seungma", "메시지 전송 성공")
                            }

                            false -> Log.d("seungma", "메시지 전송 실패")
                        }
                    }

                    is ChatViewEvent.LeaveChat -> {
                        when (it.chatRoomLeave.isSuccess) {
                            true -> parentFragmentManager.popBackStack()
                            false -> {}
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun initScrollListener() {
        binding.rvChat.addOnScrollListener(onChatScrollListener)
    }

    private fun moreItems() {
        Log.d("seungma", "moreItems")
        viewLifecycleOwner.lifecycleScope.launch {
            val viewState = chatViewModel.loadChatMessage(
                chatMessageListLoadForm = ChatMessageListLoadForm(
                    chatRoomId = chatPrimaryKeyEntity.chatRoomId,
                    reload = false
                )
            )
            chatListAdapter.submitList(createChatItem(viewState)) {
                hideProgressBar()
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