package com.freetalk.presenter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.freetalk.data.UserSingleton
import com.freetalk.databinding.ListItemBoardContentBinding
import com.freetalk.databinding.ListItemCommentBinding
import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.CommentEntity

sealed class ListItem {
    data class BoardItem(val boardEntity: BoardEntity) : ListItem()
    data class CommentItem(val commentEntity: CommentEntity) : ListItem()
}

class CommentListAdapter(
    private val commentItemClick: (CommentEntity) -> Unit,
    private val boardBookmarkClick: (BoardEntity) -> Unit,
    private val boardLikeClick: (BoardEntity) -> Unit,
    private val commentBookmarkClick: (CommentEntity) -> Unit,
    private val commentLikeClick: (CommentEntity) -> Unit,
    private val commentDeleteClick: (CommentEntity) -> Unit
) : ListAdapter<ListItem, RecyclerView.ViewHolder>(diffUtil) {

    companion object {
        private const val TYPE_BOARD_CONTENT = 0
        private const val TYPE_COMMENT = 1
        val diffUtil = object : DiffUtil.ItemCallback<ListItem>() {

            // 두 아이템이 동일한 아이템인지 체크. 보통 고유한 id를 기준으로 비교
            override fun areItemsTheSame(
                oldItem: ListItem,
                newItem: ListItem
            ): Boolean {
                return when {
                    oldItem is ListItem.BoardItem && newItem is ListItem.BoardItem -> {
                        oldItem.boardEntity.boardMetaEntity.createTime == newItem.boardEntity.boardMetaEntity.createTime &&
                                oldItem.boardEntity.boardMetaEntity.author.email == newItem.boardEntity.boardMetaEntity.author.email
                    }
                    oldItem is ListItem.CommentItem && newItem is ListItem.CommentItem -> {
                        oldItem.commentEntity.commentMetaEntity.createTime == newItem.commentEntity.commentMetaEntity.createTime &&
                                oldItem.commentEntity.commentMetaEntity.author.email == newItem.commentEntity.commentMetaEntity.author.email
                    }
                    else -> false
                }
            }

            // 두 아이템이 동일한 내용을 가지고 있는지 체크. areItemsTheSame()이 true일때 호출됨
            override fun areContentsTheSame(
                oldItem: ListItem,
                newItem: ListItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_BOARD_CONTENT -> {
                val binding =
                    ListItemBoardContentBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                BoardContentViewHolder(binding, boardBookmarkClick, boardLikeClick)
            }

            TYPE_COMMENT -> {
                val binding =
                    ListItemCommentBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                CommentViewHolder(binding, commentItemClick, commentBookmarkClick, commentLikeClick, commentDeleteClick)
            }

            else -> throw IllegalArgumentException("Unknown view type")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BoardContentViewHolder -> {
                val item = getItem(position) as ListItem.BoardItem
                holder.bind(item.boardEntity)
            }

            is CommentViewHolder -> {
                val item = getItem(position) as ListItem.CommentItem
                holder.bind(item.commentEntity)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.BoardItem -> TYPE_BOARD_CONTENT
            is ListItem.CommentItem -> TYPE_COMMENT
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }
}

class BoardContentViewHolder(
    private val binding: ListItemBoardContentBinding,
    private val boardBookmarkClick: (BoardEntity) -> Unit,
    private val boardLikeClick: (BoardEntity) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private var boardEntity: BoardEntity? = null

    init {
        binding.apply {
            btnBookmark.setOnClickListener {
                boardEntity?.let {
                    Log.d("board", "보드 북마크 람다 전달")
                    boardBookmarkClick(it)
                }
            }
            btnLike.setOnClickListener {
                boardEntity?.let {
                    boardLikeClick(it)
                }
            }
        }

    }

    fun bind(boardEntity: BoardEntity) {
        this.boardEntity = boardEntity
        binding.apply {
            boardEntity.let {

                title.text = it.boardMetaEntity.title
                /*
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

                 */
            }
        }
    }
}

class CommentViewHolder(
    private val binding: ListItemCommentBinding,
    private val commentItemClick: (CommentEntity) -> Unit,
    private val commentBookmarkClick: (CommentEntity) -> Unit,
    private val commentLikeClick: (CommentEntity) -> Unit,
    private val commentDeleteClick: (CommentEntity) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private var commentEntity: CommentEntity? = null

    init {
        binding.apply {
            root.setOnClickListener {
                commentEntity?.let {
                    commentItemClick(it)
                }
            }
            btnBookmark.setOnClickListener {
                commentEntity?.let {
                    commentBookmarkClick(it)
                }
            }
            btnLike.setOnClickListener {
                btnLike.isEnabled = false
                commentEntity?.let {
                    commentLikeClick(it)
                }
            }
            btnDelete.setOnClickListener {
                commentEntity?.let {
                    commentDeleteClick(it)
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