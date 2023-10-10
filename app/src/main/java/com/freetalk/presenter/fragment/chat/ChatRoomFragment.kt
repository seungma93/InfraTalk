package com.freetalk.presenter.fragment.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.freetalk.databinding.FragmentChatBinding
import com.freetalk.databinding.FragmentChatRoomBinding
import com.freetalk.databinding.FragmentHomeBinding
import com.freetalk.domain.entity.BoardContentPrimaryKeyEntity
import com.freetalk.domain.entity.ChatPartnerEntity
import com.freetalk.presenter.fragment.board.BoardContentFragment

class ChatRoomFragment : Fragment() {
    companion object {
        const val CHAT_PARTNER_KEY = "CHAT_PARTNER_KEY"

        fun newInstance(
            chatPartnerEntity: ChatPartnerEntity
        ): ChatRoomFragment {
            return ChatRoomFragment().apply {
                arguments = bundleOf(
                    CHAT_PARTNER_KEY to chatPartnerEntity
                )
            }
        }
    }

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!
    private val chatPartnerEntity
        get() = requireArguments().getSerializable(
            CHAT_PARTNER_KEY
        ) as ChatPartnerEntity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }
}