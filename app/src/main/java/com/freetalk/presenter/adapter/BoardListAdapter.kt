package com.freetalk.presenter.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freetalk.data.entity.BoardEntity
import com.freetalk.databinding.BoardListItemBinding
import com.freetalk.databinding.BoardWriteImageItemBinding

class BoardListAdapter(
    private val itemClick: (BoardEntity) -> Unit
) : RecyclerView.Adapter<BoardListAdapter.ViewHolder>() {
    private val datalist = mutableListOf<BoardEntity>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            BoardListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datalist[position])
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    fun setItems(newItems: List<BoardEntity>) {
        // data 초기화
        datalist.clear()
        // 모든 데이터 add
        datalist.addAll(newItems)
        // 데이터 변경을 알림
        notifyDataSetChanged()
    }


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
                //Glide.with(itemView.context).load(imgUri).into(image)
            }
        }
    }
}