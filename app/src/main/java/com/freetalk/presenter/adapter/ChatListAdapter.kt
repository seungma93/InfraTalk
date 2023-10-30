package com.freetalk.presenter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.freetalk.data.UserSingleton
import com.freetalk.data.datasource.remote.UserDataSource
import com.freetalk.databinding.ListItemBoardContentBinding
import com.freetalk.databinding.ListItemChatMessageOwnerBinding
import com.freetalk.databinding.ListItemChatMessagePartnerBinding
import com.freetalk.databinding.ListItemCommentBinding
import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.ChatMessageEntity
import com.freetalk.domain.entity.CommentEntity
import com.freetalk.domain.entity.UserEntity
import com.freetalk.presenter.viewmodel.BoardContentViewModel
import com.google.firebase.firestore.auth.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class ChatItem {
    abstract val chatMessageEntity: ChatMessageEntity

    data class Owner(override val chatMessageEntity: ChatMessageEntity) : ChatItem()

    data class Partner(override val chatMessageEntity: ChatMessageEntity) : ChatItem()
}

class ChatListAdapter() : ListAdapter<ChatItem, RecyclerView.ViewHolder>(diffUtil) {

    companion object {
        private const val TYPE_OWNER = 0
        private const val TYPE_PARTNER = 1
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
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatItem.Owner -> TYPE_OWNER
            is ChatItem.Partner -> TYPE_PARTNER
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
                content.text = it.content
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

        // 현재 날짜
        val currentDate = Date()

        // 날짜 포맷 지정
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 날짜를 문자열로 변환
        val dateFromDatabaseString = sdf.format(date)
        val currentDateString = sdf.format(currentDate)

        // 날짜를 비교하여 표시할 내용 결정
        val displayText = if (dateFromDatabaseString == currentDateString) {
            // 같은 날짜인 경우, 시간으로 표시
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        } else {
            // 하루가 지났으면 일자로 표시
            dateFromDatabaseString
        }
        return displayText
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
                content.text = it.content
                date.text = modifiedDate(it.sendTime)
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

        // 현재 날짜
        val currentDate = Date()

        // 날짜 포맷 지정
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 날짜를 문자열로 변환
        val dateFromDatabaseString = sdf.format(date)
        val currentDateString = sdf.format(currentDate)

        // 날짜를 비교하여 표시할 내용 결정
        val displayText = if (dateFromDatabaseString == currentDateString) {
            // 같은 날짜인 경우, 시간으로 표시
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        } else {
            // 하루가 지났으면 일자로 표시
            dateFromDatabaseString
        }
        return displayText
    }
}