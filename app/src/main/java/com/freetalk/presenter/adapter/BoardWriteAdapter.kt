package com.freetalk.presenter.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freetalk.databinding.BoardWriteImageItemBinding

class BoardWriteAdapter(
    private val itemClick: (Uri) -> Unit
) : RecyclerView.Adapter<BoardWriteAdapter.ViewHolder>() {
    private val datalist = mutableListOf<Uri>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            BoardWriteImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datalist[position])
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    fun setItems(newItems: List<Uri>) {
        // data 초기화
        datalist.clear()
        // 모든 데이터 add
        datalist.addAll(newItems)
        // 데이터 변경을 알림
        notifyDataSetChanged()
    }


    class ViewHolder(private val binding: BoardWriteImageItemBinding,
                     private val itemClick: (Uri) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        private var imgUri: Uri? = null

        init {
            binding.root.setOnClickListener{
                imgUri?.let{
                    itemClick(it)
                }
            }
        }
        fun bind(imgUri: Uri) {
            this.imgUri = imgUri
            binding.apply {
                Glide.with(itemView.context).load(imgUri).into(image)
            }
        }
    }
}