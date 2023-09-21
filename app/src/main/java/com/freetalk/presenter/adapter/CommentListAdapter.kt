package com.freetalk.presenter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.freetalk.data.UserSingleton
import com.freetalk.databinding.ListItemCommentBinding
import com.freetalk.domain.entity.CommentEntity


class CommentListAdapter(
    private val itemClick: (CommentEntity) -> Unit,
    private val bookmarkClick: (CommentEntity) -> Unit,
    private val likeClick: (CommentEntity) -> Unit,
    private val deleteClick: (CommentEntity) -> Unit
) : ListAdapter<CommentEntity, CommentListAdapter.ViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CommentEntity>() {

            // 두 아이템이 동일한 아이템인지 체크. 보통 고유한 id를 기준으로 비교
            override fun areItemsTheSame(
                oldItem: CommentEntity,
                newItem: CommentEntity
            ): Boolean {
                return oldItem.commentMetaEntity.createTime == newItem.commentMetaEntity.createTime &&
                        oldItem.commentMetaEntity.author.email == newItem.commentMetaEntity.author.email
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick, bookmarkClick, likeClick, deleteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemCommentBinding,
        private val itemClick: (CommentEntity) -> Unit,
        private val bookmarkClick: (CommentEntity) -> Unit,
        private val likeClick: (CommentEntity) -> Unit,
        private val deleteClick: (CommentEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var commentEntity: CommentEntity? = null

        init {
            binding.apply {
                root.setOnClickListener {
                    commentEntity?.let {
                        itemClick(it)
                    }
                }
                btnBookmark.setOnClickListener {
                    commentEntity?.let {
                        bookmarkClick(it)
                    }
                }
                btnLike.setOnClickListener {
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
                    Log.d("CommentListAdapter", "바인딩")
                    context.text = it.commentMetaEntity.content
                    date.text = it.commentMetaEntity.createTime.toString()
                    author.text = it.commentMetaEntity.author.nickname
                    btnBookmark.isSelected = it.bookmarkEntity.isBookmark
                    btnLike.isSelected = it.likeEntity.isLike
                    likeCount.text = it.likeCountEntity.likeCount.toString()
                    btnDelete.visibility =
                        when (UserSingleton.userEntity.email == it.commentMetaEntity.author.email) {
                            true -> View.VISIBLE
                            else -> View.GONE
                        }
                    btnLike.isEnabled = true
                    btnBookmark.isEnabled = true
                }
            }
        }
    }
}