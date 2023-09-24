package com.freetalk.presenter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freetalk.databinding.BoardListItemBinding
import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.BoardMetaEntity


class BoardListAdapter(
    private val itemClick: (BoardMetaEntity) -> Unit,
    private val bookmarkClick: (BoardEntity) -> Unit,
    private val likeClick: (BoardEntity) -> Unit
) : ListAdapter<BoardEntity, BoardListAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            BoardListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick, bookmarkClick, likeClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: BoardListItemBinding,
        private val itemClick: (BoardMetaEntity) -> Unit,
        private val bookmarkClick: (BoardEntity) -> Unit,
        private val likeClick: (BoardEntity) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private var boardEntity: BoardEntity? = null

        init {
            binding.apply {
                root.setOnClickListener {
                    boardEntity?.let {
                        Log.d("comment","리스트어댑터 데이터" + it.boardMetaEntity.author.email)
                        itemClick(it.boardMetaEntity)
                    }
                }
                btnBookmark.setOnClickListener {
                    btnBookmark.isEnabled = false
                    boardEntity?.let {
                        bookmarkClick(it)
                    }
                }
                btnLike.setOnClickListener {
                    Log.d("board", "좋아요 람다 전달")
                    btnLike.isEnabled = false
                    boardEntity?.let {
                        likeClick(it)
                    }
                }
            }

        }

        fun bind(boardEntity: BoardEntity) {
            this.boardEntity = boardEntity
            binding.apply {
                boardEntity.let {
                    Log.d("BoardListAdpater", "셀렉트 바인딩")
                    title.text = it.boardMetaEntity.title
                    context.text = it.boardMetaEntity.content
                    date.text = it.boardMetaEntity.createTime.toString()
                    author.text = it.boardMetaEntity.author.nickname
                    btnBookmark.isSelected = it.bookmarkEntity.isBookmark
                    btnLike.isSelected = it.likeEntity.isLike
                    likeCount.text = it.likeCountEntity.likeCount.toString()
                    /*
                    Glide.with(itemView.context)
                        .load(it.boardMetaEntity.images?.successUris?.firstOrNull())
                        .into(ivSingleImage)

                     */
                    btnLike.isEnabled = true
                    btnBookmark.isEnabled = true
                }
            }
        }
    }
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<BoardEntity>() {

            // 두 아이템이 동일한 아이템인지 체크. 보통 고유한 id를 기준으로 비교
            override fun areItemsTheSame(
                oldItem: BoardEntity,
                newItem: BoardEntity
            ): Boolean {
                return oldItem.boardMetaEntity.createTime == newItem.boardMetaEntity.createTime &&
                        oldItem.boardMetaEntity.author == newItem.boardMetaEntity.author
            }

            // 두 아이템이 동일한 내용을 가지고 있는지 체크. areItemsTheSame()이 true일때 호출됨
            override fun areContentsTheSame(
                oldItem: BoardEntity,
                newItem: BoardEntity
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}