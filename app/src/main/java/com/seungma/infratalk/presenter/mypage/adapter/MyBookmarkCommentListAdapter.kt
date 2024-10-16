package com.seungma.infratalk.presenter.mypage.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seungma.infratalk.databinding.ListItemMyBookmarkCommentBinding
import com.seungma.infratalk.domain.comment.entity.CommentEntity
import com.seungma.infratalk.domain.comment.entity.CommentMetaEntity
import com.seungma.infratalk.domain.user.entity.UserEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MyBookmarkCommentListAdapter(
    private val itemClick: (CommentMetaEntity) -> Unit,
    private val bookmarkClick: (CommentEntity) -> Unit,
    private val likeClick: (CommentEntity) -> Unit,
    private val deleteClick: (CommentEntity) -> Unit,
    private val userEntity: UserEntity
) : ListAdapter<CommentEntity, MyBookmarkCommentListAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemMyBookmarkCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding, itemClick, bookmarkClick, likeClick, deleteClick, userEntity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemMyBookmarkCommentBinding,
        private val itemClick: (CommentMetaEntity) -> Unit,
        private val bookmarkClick: (CommentEntity) -> Unit,
        private val likeClick: (CommentEntity) -> Unit,
        private val deleteClick: (CommentEntity) -> Unit,
        private val userEntity: UserEntity
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private var commentEntity: CommentEntity? = null

        init {
            binding.apply {
                root.setOnClickListener {
                    commentEntity?.let {

                        itemClick(it.commentMetaEntity)
                    }
                }
                btnBookmark.setOnClickListener {
                    btnBookmark.isEnabled = false
                    commentEntity?.let {
                        bookmarkClick(it)
                    }
                }
                btnLike.setOnClickListener {
                    Log.d("board", "좋아요 람다 전달")
                    btnLike.isEnabled = false
                    commentEntity?.let {
                        likeClick(it)
                    }
                }
                btnDelete.setOnClickListener {
                    commentEntity?.let {
                        deleteClick(it)
                    }
                }
            }

        }

        fun bind(commentEntity: CommentEntity) {
            this.commentEntity = commentEntity
            binding.apply {
                commentEntity.let {
                    context.text = it.commentMetaEntity.content
                    date.text = modifiedDate(it.commentMetaEntity.createTime)
                    author.text = it.commentMetaEntity.author.nickname
                    btnBookmark.isSelected = it.bookmarkEntity.isBookmark
                    btnLike.isSelected = it.likeEntity.isLike
                    likeCount.text = it.likeCountEntity.likeCount.toString()
                    btnDelete.visibility =
                        when (userEntity.email == it.commentMetaEntity.author.email) {
                            true -> View.VISIBLE
                            else -> View.GONE
                        }
                    btnLike.isEnabled = true
                    btnBookmark.isEnabled = true
                    btnDelete.isEnabled = true
                }
            }
        }

        private fun modifiedDate(date: Date?): String {

            // 현재 날짜
            val currentDate = Date()

            // 날짜 포맷 지정
            val sdf = SimpleDateFormat("MM월 dd일", Locale.getDefault())

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

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CommentEntity>() {

            // 두 아이템이 동일한 아이템인지 체크. 보통 고유한 id를 기준으로 비교
            override fun areItemsTheSame(
                oldItem: CommentEntity,
                newItem: CommentEntity
            ): Boolean {
                return oldItem.commentMetaEntity.commentPrimaryKey == newItem.commentMetaEntity.commentPrimaryKey
            }

            // 두 아이템이 동일한 내용을 가지고 있는지 체크. areItemsTheSame()이 true일때 호출됨
            override fun areContentsTheSame(
                oldItem: CommentEntity,
                newItem: CommentEntity
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}