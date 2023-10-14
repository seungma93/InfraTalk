package com.freetalk.presenter.fragment.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.freetalk.databinding.FragmentChatBinding
import com.freetalk.databinding.FragmentHomeBinding
import com.freetalk.di.component.DaggerBoardFragmentComponent
import com.freetalk.di.component.DaggerChatFragmentComponent
import com.freetalk.domain.entity.ChatPrimaryKeyEntity
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.presenter.activity.Navigable
import com.freetalk.presenter.form.ChatMessageSendForm
import com.freetalk.presenter.viewmodel.BoardViewEvent
import com.freetalk.presenter.viewmodel.BoardViewModel
import com.freetalk.presenter.viewmodel.ChatViewEvent
import com.freetalk.presenter.viewmodel.ChatViewModel
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

    private val chatPrimaryKeyEntity
        get() = requireArguments().getSerializable(
            ChatFragment.CHAT_PRIMARY_KEY
        ) as ChatPrimaryKeyEntity

    @Inject
    lateinit var chatViewModelFactory: ViewModelProvider.Factory
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

        binding.apply {
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
        }
        subscribe()
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.viewEvent.collect {
                when(it) {
                    is ChatViewEvent.SendMessage -> {
                        when(it.chatMessageSend.isSuccess) {
                            true -> { Log.d("seungma", "메시지 전송 성공")
                                binding.btnSendChat.isEnabled = true
                            }
                            false -> Log.d("seungma", "메시지 전송 실패")
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}