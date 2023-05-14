package com.freetalk.presenter.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.BoardListEntity
import com.freetalk.databinding.BoardListItemBinding
import com.freetalk.databinding.BoardWriteImageItemBinding


class BoardListAdapter(
    private val itemClick: (BoardEntity) -> Unit
) : ListAdapter<BoardEntity, BoardListAdapter.ViewHolder>(diffUtil) {
    private val datalist = mutableListOf<BoardEntity>()

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<BoardEntity>() {

            // 두 아이템이 동일한 아이템인지 체크. 보통 고유한 id를 기준으로 비교
            override fun areItemsTheSame(oldItem: BoardEntity, newItem: BoardEntity): Boolean {
                return oldItem == newItem
            }

            // 두 아이템이 동일한 내용을 가지고 있는지 체크. areItemsTheSame()이 true일때 호출됨
            override fun areContentsTheSame(oldItem: BoardEntity, newItem: BoardEntity): Boolean {
                return oldItem == newItem
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            BoardListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
/*
    override fun getItemCount(): Int {
        return datalist.size
    }

 */

    fun setItems(newItems: List<BoardEntity>) {
        // data 초기화
        //datalist.clear()
        // 모든 데이터 add

            datalist.addAll(newItems)

        // 데이터 변경을 알림
        notifyDataSetChanged()
    }
/*
    fun getItem(): BoardEntity? {
        return datalist.firstOrNull()
    }

 */



    class ViewHolder(private val binding: BoardListItemBinding,
                     private val itemClick: (BoardEntity) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        private var boardEntity: BoardEntity? = null

        init {
            binding.root.setOnClickListener{
                boardEntity?.let{
                    itemClick(it)
                }
            }
        }
        fun bind(boardEntity: BoardEntity) {
            this.boardEntity = boardEntity
            binding.apply {
                Log.v("BoardListAdpater", "셀렉트 바인딩")
                title.text = boardEntity.title
                context.text = boardEntity.content
                date.text = boardEntity.createTime.toString()
                author.text = boardEntity.author.nickname
                //Glide.with(itemView.context).load(imgUri).into(image)
            }
        }
    }
}