package com.freetalk.presenter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freetalk.databinding.BoardListItemBinding
import com.freetalk.databinding.ListItemChatRoomBinding
import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.BoardMetaEntity
import com.freetalk.domain.entity.ChatRoomEntity
import com.freetalk.domain.entity.UserEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ChatRoomListAdapter(
    private val itemClick: (ChatRoomEntity) -> Unit
) : ListAdapter<ChatRoomEntity, ChatRoomListAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemChatRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemChatRoomBinding,
        private val itemClick: (ChatRoomEntity) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private var chatRoomEntity: ChatRoomEntity? = null

        init {
            binding.apply {
                root.setOnClickListener {
                    chatRoomEntity?.let {
                        itemClick(it)
                    }
                }
            }

        }

        fun bind(chatRoomEntity: ChatRoomEntity) {
            this.chatRoomEntity = chatRoomEntity
            binding.apply {
                chatRoomEntity.let {
                    Log.d("BoardListAdpater", "셀렉트 바인딩")
                    tvChatRoomTitle.text = it.roomId
                    tvLastChatContent.text = it.lastChatMessageEntity?.let { it.content } ?: "대화 없음"
                    tvLastChatTime.text = it.lastChatMessageEntity?.let { modifiedDate(it.sendTime) } ?: modifiedDate(it.createTime)
                    /*
                    context.text = it.boardMetaEntity.content
                    date.text = it.boardMetaEntity.createTime.toString()
                    author.text = it.boardMetaEntity.author.nickname
                    btnBookmark.isSelected = it.bookmarkEntity.isBookmark
                    btnLike.isSelected = it.likeEntity.isLike
                    likeCount.text = it.likeCountEntity.likeCount.toString()

                    Glide.with(itemView.context)
                        .load(it.boardMetaEntity.images?.successUris?.firstOrNull())
                        .into(ivSingleImage)


                    btnChat.visibility = when ( userEntity.email != it.boardMetaEntity.author.email) {
                        true -> View.VISIBLE
                        else -> View.GONE
                    }
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
                SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(date)
            }
            return displayText
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatRoomEntity>() {

            // 두 아이템이 동일한 아이템인지 체크. 보통 고유한 id를 기준으로 비교
            override fun areItemsTheSame(
                oldItem: ChatRoomEntity,
                newItem: ChatRoomEntity
            ): Boolean {
                return oldItem.primaryKey == newItem.primaryKey
            }

            // 두 아이템이 동일한 내용을 가지고 있는지 체크. areItemsTheSame()이 true일때 호출됨
            override fun areContentsTheSame(
                oldItem: ChatRoomEntity,
                newItem: ChatRoomEntity
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}