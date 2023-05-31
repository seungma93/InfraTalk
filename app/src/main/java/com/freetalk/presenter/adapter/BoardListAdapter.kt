package com.freetalk.presenter.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freetalk.data.UserSingleton
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.BoardListEntity
import com.freetalk.data.entity.BookMarkableBoardEntity
import com.freetalk.databinding.BoardListItemBinding
import com.freetalk.databinding.BoardWriteImageItemBinding
import java.security.PrivateKey


class BoardListAdapter(
    private val itemClick: (BookMarkableBoardEntity) -> Unit,
    private val bookMarkClick: (Boolean, BookMarkableBoardEntity) -> Unit
) : ListAdapter<BookMarkableBoardEntity, BoardListAdapter.ViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<BookMarkableBoardEntity>() {

            // 두 아이템이 동일한 아이템인지 체크. 보통 고유한 id를 기준으로 비교
            override fun areItemsTheSame(
                oldItem: BookMarkableBoardEntity,
                newItem: BookMarkableBoardEntity
            ): Boolean {
                return oldItem.boardEntity.createTime == newItem.boardEntity.createTime && oldItem.boardEntity.author == newItem.boardEntity.author
            }

            // 두 아이템이 동일한 내용을 가지고 있는지 체크. areItemsTheSame()이 true일때 호출됨
            override fun areContentsTheSame(
                oldItem: BookMarkableBoardEntity,
                newItem: BookMarkableBoardEntity
            ): Boolean {
                return oldItem == newItem
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            BoardListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick, bookMarkClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: BoardListItemBinding,
        private val itemClick: (BookMarkableBoardEntity) -> Unit,
        private val bookMarkClick: (Boolean, BookMarkableBoardEntity) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private var bookMarkableBoardEntity: BookMarkableBoardEntity? = null

        init {
            binding.root.setOnClickListener {
                bookMarkableBoardEntity?.let {
                    itemClick(it)
                }
            }
            binding.btnBookmark.setOnClickListener {
                bookMarkableBoardEntity?.let {
                    Log.v("BookListAdapter", "onClick 실행")
                    when (binding.btnBookmark.isSelected) {
                        true -> bookMarkClick(true, it)
                        false -> bookMarkClick(false, it)
                    }
                }
            }
        }

        fun bind(bookMarkableBoardEntity: BookMarkableBoardEntity) {
            this.bookMarkableBoardEntity = bookMarkableBoardEntity
            binding.apply {
                bookMarkableBoardEntity.let {
                    Log.v("BoardListAdpater", "셀렉트 바인딩")
                    title.text = it.boardEntity.title
                    context.text = it.boardEntity.content
                    date.text = it.boardEntity.createTime.toString()
                    author.text = it.boardEntity.author.nickname
                    btnBookmark.isSelected = it.bookMarkToken
                    //Glide.with(itemView.context).load(imgUri).into(image)
                }
            }
        }
    }
}