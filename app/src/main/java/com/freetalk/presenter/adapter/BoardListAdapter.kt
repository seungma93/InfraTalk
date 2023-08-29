package com.freetalk.presenter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.WrapperBoardEntity
import com.freetalk.databinding.BoardListItemBinding


class BoardListAdapter(
    private val itemClick: (BoardEntity) -> Unit,
    private val bookMarkClick: (WrapperBoardEntity) -> Unit,
    private val likeClick: (WrapperBoardEntity) -> Unit
) : ListAdapter<WrapperBoardEntity, BoardListAdapter.ViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<WrapperBoardEntity>() {

            // 두 아이템이 동일한 아이템인지 체크. 보통 고유한 id를 기준으로 비교
            override fun areItemsTheSame(
                oldItem: WrapperBoardEntity,
                newItem: WrapperBoardEntity
            ): Boolean {
                return oldItem.boardEntity.createTime == newItem.boardEntity.createTime && oldItem.boardEntity.author == newItem.boardEntity.author
            }

            // 두 아이템이 동일한 내용을 가지고 있는지 체크. areItemsTheSame()이 true일때 호출됨
            override fun areContentsTheSame(
                oldItem: WrapperBoardEntity,
                newItem: WrapperBoardEntity
            ): Boolean {
                return oldItem == newItem
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            BoardListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick, bookMarkClick, likeClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: BoardListItemBinding,
        private val itemClick: (BoardEntity) -> Unit,
        private val bookMarkClick: (WrapperBoardEntity) -> Unit,
        private val likeClick: (WrapperBoardEntity) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private var wrapperBoardEntity: WrapperBoardEntity? = null

        init {
            binding.apply {
                root.setOnClickListener {
                    wrapperBoardEntity?.let {
                        itemClick(it.boardEntity)
                    }
                }
                btnBookmark.setOnClickListener {
                    wrapperBoardEntity?.let {
                        bookMarkClick(it)
                    }
                }
                btnLike.setOnClickListener {
                    btnLike.isEnabled = false
                    wrapperBoardEntity?.let {
                            likeClick(it)
                    }
                }
            }

        }

        fun bind(wrapperBoardEntity: WrapperBoardEntity) {
            this.wrapperBoardEntity = wrapperBoardEntity
            binding.apply {
                wrapperBoardEntity.let {
                    Log.d("BoardListAdpater", "셀렉트 바인딩")
                    title.text = it.boardEntity.title
                    context.text = it.boardEntity.content
                    date.text = it.boardEntity.createTime.toString()
                    author.text = it.boardEntity.author.nickname
                    btnBookmark.isSelected = (it.bookMarkEntity != null)
                    btnLike.isSelected = (it.likeEntity != null)
                    likeCount.text = it.likeCount.toString()
                    Glide.with(itemView.context).load(it.boardEntity.images?.successUris?.firstOrNull()).into(ivSingleImage)
                    btnLike.isEnabled = true
                    btnBookmark.isEnabled = true
                }
            }
        }
    }
}