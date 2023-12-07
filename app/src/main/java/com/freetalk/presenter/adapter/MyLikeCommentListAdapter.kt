package com.freetalk.presenter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freetalk.data.UserSingleton.userEntity
import com.freetalk.databinding.BoardListItemBinding
import com.freetalk.databinding.ListItemMyBoardBinding
import com.freetalk.databinding.ListItemMyBookmarkCommentBinding
import com.freetalk.databinding.ListItemMyCommentBinding
import com.freetalk.databinding.ListItemMyLikeCommentBinding
import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.BoardMetaEntity
import com.freetalk.domain.entity.CommentEntity
import com.freetalk.domain.entity.CommentMetaEntity
import com.freetalk.domain.entity.UserEntity


class MyLikeCommentListAdapter(
    private val itemClick: (CommentMetaEntity) -> Unit,
    private val bookmarkClick: (CommentEntity) -> Unit,
    private val likeClick: (CommentEntity) -> Unit,
    private val deleteClick: (CommentEntity) -> Unit
) : ListAdapter<CommentEntity, MyLikeCommentListAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemMyLikeCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick, bookmarkClick, likeClick, deleteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemMyLikeCommentBinding,
        private val itemClick: (CommentMetaEntity) -> Unit,
        private val bookmarkClick: (CommentEntity) -> Unit,
        private val likeClick: (CommentEntity) -> Unit,
        private val deleteClick: (CommentEntity) -> Unit
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
                }
            }
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