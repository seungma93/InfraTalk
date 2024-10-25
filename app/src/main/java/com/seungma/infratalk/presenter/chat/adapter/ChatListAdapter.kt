package com.seungma.infratalk.presenter.chat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.seungma.infratalk.databinding.ListItemChatMessageDateBinding
import com.seungma.infratalk.databinding.ListItemChatMessageOwnerBinding
import com.seungma.infratalk.databinding.ListItemChatMessagePartnerBinding
import com.seungma.infratalk.domain.chat.entity.ChatMessageEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class ChatItem {
    abstract val chatMessageEntity: ChatMessageEntity

    data class Owner(override val chatMessageEntity: ChatMessageEntity) : ChatItem()

    data class Partner(override val chatMessageEntity: ChatMessageEntity) : ChatItem()

    data class Date(override val chatMessageEntity: ChatMessageEntity) : ChatItem()
}

class ChatListAdapter() : ListAdapter<ChatItem, RecyclerView.ViewHolder>(diffUtil) {

    companion object {
        private const val TYPE_OWNER = 0
        private const val TYPE_PARTNER = 1
        private const val TYPE_DATE = 2
        val diffUtil = object : DiffUtil.ItemCallback<ChatItem>() {

            // 두 아이템이 동일한 아이템인지 체크. 보통 고유한 id를 기준으로 비교
            override fun areItemsTheSame(
                oldItem: ChatItem,
                newItem: ChatItem
            ): Boolean {
                return when {
                    oldItem is ChatItem.Owner && newItem is ChatItem.Owner -> {
                        oldItem.chatMessageEntity.chatMessagePrimaryKey == newItem.chatMessageEntity.chatMessagePrimaryKey
                    }

                    oldItem is ChatItem.Partner && newItem is ChatItem.Partner -> {
                        oldItem.chatMessageEntity.chatMessagePrimaryKey == newItem.chatMessageEntity.chatMessagePrimaryKey
                    }

                    else -> false
                }
            }

            // 두 아이템이 동일한 내용을 가지고 있는지 체크. areItemsTheSame()이 true일때 호출됨
            override fun areContentsTheSame(
                oldItem: ChatItem,
                newItem: ChatItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_OWNER -> {
                val binding =
                    ListItemChatMessageOwnerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ChatMessageOwnerViewHolder(binding)
            }

            TYPE_PARTNER -> {
                val binding =
                    ListItemChatMessagePartnerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ChatMessagePartnerViewHolder(binding)
            }

            TYPE_DATE -> {
                val binding =
                    ListItemChatMessageDateBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ChatMessageDateViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChatMessageOwnerViewHolder -> {
                val item = getItem(position) as ChatItem.Owner
                holder.bind(item.chatMessageEntity)
            }

            is ChatMessagePartnerViewHolder -> {
                val item = getItem(position) as ChatItem.Partner
                holder.bind(item.chatMessageEntity)
            }

            is ChatMessageDateViewHolder -> {
                val item = getItem(position) as ChatItem.Date
                holder.bind(item.chatMessageEntity)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatItem.Owner -> TYPE_OWNER
            is ChatItem.Partner -> TYPE_PARTNER
            is ChatItem.Date -> TYPE_DATE
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    fun getItemAt(position: Int): ChatItem {
        return getItem(position)
    }
}


class ChatMessageOwnerViewHolder(
    private val binding: ListItemChatMessageOwnerBinding,
) : RecyclerView.ViewHolder(binding.root) {
    private var chatMessageEntity: ChatMessageEntity? = null

    init {
        binding.apply {

        }

    }

    fun bind(chatMessageEntity: ChatMessageEntity) {
        this.chatMessageEntity = chatMessageEntity
        binding.apply {
            chatMessageEntity.let {
                tvMessage.text = it.content
                date.text = modifiedDate(it.sendTime)
                /*
                title.text = it.boardMetaEntity.title
                date.text = it.boardMetaEntity.createTime.toString()
                author.text = it.boardMetaEntity.author.nickname
                content.text = it.boardMetaEntity.content
                btnBookmark.isSelected = it.bookmarkEntity.isBookmark
                btnLike.isSelected = it.likeEntity.isLike
                likeCount.text = it.likeCountEntity.likeCount.toString()
                btnLike.isEnabled = true
                btnBookmark.isEnabled = true

                 */
            }
        }
    }

    private fun modifiedDate(date: Date?): String {

        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }
}

class ChatMessagePartnerViewHolder(
    private val binding: ListItemChatMessagePartnerBinding
) : RecyclerView.ViewHolder(binding.root) {
    private var chatMessageEntity: ChatMessageEntity? = null

    init {
        binding.apply {

        }

    }

    fun bind(chatMessageEntity: ChatMessageEntity) {
        this.chatMessageEntity = chatMessageEntity
        binding.apply {
            chatMessageEntity.let {
                tvName.text = it.sender.nickname
                tvContent.text = it.content
                date.text = modifiedDate(it.sendTime)

                val requestOptions = RequestOptions.circleCropTransform().autoClone()
                it.sender.image?.let {
                    Glide.with(itemView.context)
                        .load(it)
                        .apply(requestOptions)
                        .into(ivProfile)

                }
                /*
                Log.d("CommentListAdapter", "바인딩")
                context.text = it.commentMetaEntity.content
                date.text = it.commentMetaEntity.createTime.toString()
                author.text = it.commentMetaEntity.author.nickname
                btnBookmark.isSelected = it.bookmarkEntity.isBookmark
                btnLike.isSelected = it.likeEntity.isLike
                likeCount.text = it.likeCountEntity.likeCount.toString()
                btnDelete.visibility =
                    when ( userEntity.email == it.commentMetaEntity.author.email) {
                        true -> View.VISIBLE
                        else -> View.GONE
                    }
                btnLike.isEnabled = true
                btnBookmark.isEnabled = true
                btnDelete.isEnabled = true

                 */
            }
        }
    }

    private fun modifiedDate(date: Date?): String {

        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }
}

class ChatMessageDateViewHolder(
    private val binding: ListItemChatMessageDateBinding
) : RecyclerView.ViewHolder(binding.root) {
    private var chatMessageEntity: ChatMessageEntity? = null

    fun bind(chatMessageEntity: ChatMessageEntity) {
        this.chatMessageEntity = chatMessageEntity
        binding.apply {
            tvDate.text = modifiedDate(date = chatMessageEntity.sendTime)
        }
    }

    private fun modifiedDate(date: Date?): String {
        // 날짜 포맷 지정
        val sdf = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        return sdf.format(date)
    }
}