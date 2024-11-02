package com.seungma.infratalk.presenter.chat.fragment

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.seungma.infratalk.databinding.FragmentChatBinding
import com.seungma.infratalk.di.component.DaggerChatFragmentComponent
import com.seungma.infratalk.domain.chat.entity.ChatPrimaryKeyEntity
import com.seungma.infratalk.presenter.chat.adapter.ChatItem
import com.seungma.infratalk.presenter.chat.adapter.ChatListAdapter
import com.seungma.infratalk.presenter.chat.form.ChatMessageListLoadForm
import com.seungma.infratalk.presenter.chat.form.ChatMessageSendForm
import com.seungma.infratalk.presenter.chat.form.ChatRoomLeaveForm
import com.seungma.infratalk.presenter.chat.listener.OnChatScrollListener
import com.seungma.infratalk.presenter.chat.viewmodel.ChatViewEvent
import com.seungma.infratalk.presenter.chat.viewmodel.ChatViewModel
import com.seungma.infratalk.presenter.chat.viewmodel.ChatViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
            //layoutManager.stackFromEnd = true;
            rvChat.layoutManager = layoutManager
            rvChat.adapter = chatListAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {

            val loadMessage = chatViewModel.loadChatMessage(
                chatMessageListLoadForm = ChatMessageListLoadForm(
                    chatRoomId = chatPrimaryKeyEntity.chatRoomId,
                    reload = true
                )
            )

            val chatItemList = createChatItem(loadMessage)

            val resultList = sortMessage(list = chatItemList)

            chatListAdapter.submitList(resultList) {
                binding.rvChat.scrollToPosition(0)
            }
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
            launch {
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
            /*
            launch {
                // 실시간 로드
                chatViewModel.viewState.collect {
                    // 방 이름
                    val roomName = it.chatRoomEntity?.roomName
                    if (roomName != binding.tvChatTitle.text.toString()) binding.tvChatTitle.text =
                        roomName

                    // 채팅
                    val chatItemList = createChatItem(it)
                    val dateMessageList = chatItemList.mapIndexed { index, current ->

                        if(index == 0) listOf(current) else {
                            if (index > 0 && checkDate(
                                    chatItemList[index - 1].chatMessageEntity.sendTime,
                                    current.chatMessageEntity.sendTime
                                )
                            ) {
                                listOf(current)
                            } else {
                                listOf(ChatItem.Date(chatItemList[index - 1].chatMessageEntity), current)
                            }
                        }
                    }.flatten()
                    dateMessageList.map {
                        Log.d("seungma", "메세지 소팅: " + it)
                    }
                    chatListAdapter.submitList(dateMessageList) {
                        if (it.isNewChatMessage) binding.rvChat.scrollToPosition(0)
                    }

                }
            }
            */

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

    private fun checkDate(first: Date, second: Date): Boolean {

        val sdf = SimpleDateFormat("MM월 dd일", Locale.getDefault())
        val firstDate = sdf.format(first)
        val secondDate = sdf.format(second)

        Log.d("seungma", "첫번째 아이템 :" + firstDate + "두번쨰 아이템 :" + secondDate)

        return firstDate == secondDate
    }

    private fun groupMessageType(list : List<ChatItem> ):List<List<ChatItem>> {
        if (list.isEmpty()) return emptyList()

        val result = mutableListOf<MutableList<ChatItem>>()
        var currentGroup = mutableListOf(list[0])

        for (i in 1 until list.size) {
            if (list[i]::class == currentGroup.last()::class) {
                currentGroup.add(list[i])
            } else {
                result.add(currentGroup)
                currentGroup = mutableListOf(list[i])
            }
        }
        result.add(currentGroup) // 마지막 그룹 추가

        return result
    }

    private fun groupMessageTime(list: List<ChatItem>): List<List<ChatItem>> {
        if (list.isEmpty()) return emptyList()

        val result = mutableListOf<MutableList<ChatItem>>()
        var currentGroup = mutableListOf(list[0])

        for (i in 1 until list.size) {
            if (modifiedTime(list[i].chatMessageEntity.sendTime) == modifiedTime(currentGroup.last().chatMessageEntity.sendTime)) {
                currentGroup.add(list[i])
            } else {
                result.add(currentGroup)
                currentGroup = mutableListOf(list[i])
            }
        }
        result.add(currentGroup) // 마지막 그룹 추가

        return result
    }

    private fun modifiedTime(date: Date): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }

    private fun resortMessageList(list: List<List<ChatItem>>): List<List<ChatItem>> {
        if (list.isEmpty()) return emptyList()

        val result = mutableListOf<List<ChatItem>>()

        for (i in 0 until list.size) {

            when (list[i].size) {
                1 -> {
                    result.add(list[i])
                }

                else -> {
                    groupMessageTime(list[i]).map {
                        result.add(it)
                    }
                }
            }
        }

        return result
    }

    private fun sortMessage(list: List<ChatItem>) : List<ChatItem> {
        val dateAddList = list.mapIndexed { index, current ->

            if(index != list.size -1) {
                if (checkDate(
                        current.chatMessageEntity.sendTime,
                        list[index + 1].chatMessageEntity.sendTime
                    )
                ) {
                    listOf(current)
                } else {
                    listOf(current,ChatItem.Date(current.chatMessageEntity))
                }
            } else {
                listOf(current)
            }
        }.flatten()

        val groupList = groupMessageType(list = dateAddList)

        val resultList = resortMessageList(groupList).map { list ->
            if (list.size > 1) { // 메시지 두개 이상 일때
                list.mapIndexed { index, item ->
                    when (item) {
                        is ChatItem.Owner -> {
                            if (index == 0) { // 첫번째 아이템(마지막 메세지)
                                item
                            } else { // 첫번째 아이템이 아닐떄(마지막이 아닐때)
                                item.apply {
                                    item.isLast = false
                                }
                            }
                        }

                        is ChatItem.Partner -> {
                            if (index == 0) { // 첫번째 아이템(마지막 메세지)
                                item.apply {
                                    isFirst = false
                                }
                            } else if (index == list.size - 1) { // 마지막 아이템(첫번째 메시지)
                                item.apply {
                                    isLast = false
                                }
                            } else { // 첫번째 && 마지막 아닐때
                                item.apply {
                                    isFirst = false
                                    isLast = false
                                }
                            }
                        }

                        else -> {
                            item
                        }
                    }
                }

            } else { // 메시지 하나 일 때
                list
            }
        }.flatten()

        return resultList
    }
}